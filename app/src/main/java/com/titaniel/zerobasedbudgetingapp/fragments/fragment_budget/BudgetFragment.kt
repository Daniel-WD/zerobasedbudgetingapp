package com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetsOfCategory
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionsOfCategory
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetFragment
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * [BudgetViewModel] with [categoryRepository], [transactionsRepository] and [mBudgetRepository]
 */
@HiltViewModel
class BudgetViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    transactionRepository: TransactionRepository,
    private val mBudgetRepository: BudgetRepository
) : ViewModel() {

    /**
     * Month
     */
    val month = MutableLiveData(LocalDate.of(2021, 2, 1))

    /**
     * To be budgeted
     */
    val toBeBudgeted: MutableLiveData<Long> = MutableLiveData()

    /**
     * All budgets of selected month
     */
    val budgets = mBudgetRepository.getBudgetsByMonth(month.value!!).asLiveData()

    /**
     * All categories
     */
    val categories = categoryRepository.getAllCategories().asLiveData()

    /**
     * All transactions
     */
    val transactions = transactionRepository.getAllTransactions().asLiveData()

    /**
     * Budgets by categories
     */
    private val mBudgetsOfCategories = categoryRepository.getBudgetsOfCategories().asLiveData()

    /**
     * Transactions by categories
     */
    private val mTransactionsOfCategories = categoryRepository.getTransactionsOfCategories().asLiveData()

    /**
     * Available money per category
     */
    val availableMoney: MutableLiveData<Map<Category, Long>> = MutableLiveData(emptyMap())

    /**
     * Viewmodel observer for categories
     */
    private val mCategoriesObserver: Observer<List<Category>> =
        Observer {
            checkBudgets()
            updateAvailableMoney()
        }

    /**
     * Viewmodel observer for budgetsOfCategories
     */
    private val mBudgetsOfCategoriesObserver: Observer<List<BudgetsOfCategory>> =
        Observer {
            updateAvailableMoney()
        }

    /**
     * Viewmodel observer for transactionsOfCategories
     */
    private val mTransactionsOfCategoriesObserver: Observer<List<TransactionsOfCategory>> =
        Observer {
            updateAvailableMoney()
        }

    /**
     * Viewmodel observer for transactions
     */
    private val mTransactionsObserver: Observer<List<Transaction>> =
        Observer {
            updateToBeBudgeted()
        }

    /**
     * Viewmodel observer for budgets
     */
    private val mBudgetsObserver: Observer<List<Budget>> =
        Observer {
            updateToBeBudgeted()
        }

    init {
        // Register all observers
        categories.observeForever(mCategoriesObserver)
        mBudgetsOfCategories.observeForever(mBudgetsOfCategoriesObserver)
        mTransactionsOfCategories.observeForever(mTransactionsOfCategoriesObserver)
        transactions.observeForever(mTransactionsObserver)
        budgets.observeForever(mBudgetsObserver)
    }

    override fun onCleared() {
        super.onCleared()

        // Remove all observers
        categories.removeObserver(mCategoriesObserver)
        mBudgetsOfCategories.removeObserver(mBudgetsOfCategoriesObserver)
        mTransactionsOfCategories.removeObserver(mTransactionsOfCategoriesObserver)
        transactions.removeObserver(mTransactionsObserver)
        budgets.removeObserver(mBudgetsObserver)
    }

    /**
     * Updates [availableMoney]
     */
    private fun updateAvailableMoney() {
        val cats = categories.value
        val transOfCats = mTransactionsOfCategories.value
        val budsOfCats = mBudgetsOfCategories.value

        if (cats != null && transOfCats != null && budsOfCats != null) {
            // Update available money per category
            availableMoney.value = cats.map { category ->
                category to
                        // Sum of all transactions of this category until selected month (inclusive)
                        (transOfCats.find { transactionsOfCategory -> transactionsOfCategory.category == category }?.transactions
                            ?.filter { transaction -> month.value!!.let { transaction.date.year <= it.year && transaction.date.month <= it.month } }
                            ?.fold(0L, { acc, transaction -> acc + transaction.pay }) ?: 0) +

                        // Added with sum of all budgets of this category until selected month (inclusive)
                        budsOfCats.find { budgetsOfCategory -> budgetsOfCategory.category == category }!!.budgets
                            .filter { budget -> month.value!!.let { budget.month.year <= it.year && budget.month.month <= it.month } }
                            .fold(0L, { acc, budget -> acc + budget.budgeted })

            }.toMap()
        }

    }

    /**
     * Update [toBeBudgeted]
     */
    private fun updateToBeBudgeted() {
        val transactions = transactions.value
        val budgets = budgets.value

        if (transactions != null && budgets != null) {
            toBeBudgeted.value = transactions.filter { it.categoryName == Category.TO_BE_BUDGETED }
                .fold(0L, { acc, transaction -> acc + transaction.pay }) -
                    budgets.fold(0L, { acc, budget -> acc + budget.budgeted })

        }
    }

    /**
     * Checks if for every category in [categories] and [month] combination, exists a budget. If not, then create missing [Budget]s.
     */
    private fun checkBudgets() {
        val cats = categories.value
        val buds = budgets.value
        if (cats != null && buds != null) {
            val missingBudgets =
                cats.filter { category -> buds.find { budget -> budget.categoryName == category.name } == null }
                    .map { category -> Budget(category.name, month.value!!, 0) }
                    .toTypedArray()
            viewModelScope.launch {
                mBudgetRepository.addBudgets(*missingBudgets)
            }
        }
    }

}

/**
 * [BudgetFragment] to show a list of categories. Each item contains budgeting information, which can be edited.
 */
@AndroidEntryPoint
class BudgetFragment : Fragment(R.layout.fragment_budget) {

    /**
     * Toolbar
     */
    private lateinit var mToolbar: MaterialToolbar

    /**
     * "To be budgeted" Text
     */
    private lateinit var mTvToBeBudgeted: TextView

    /**
     * Budgeting list
     */
    private lateinit var mListBudgeting: RecyclerView

    /**
     * View model
     */
    private val mViewModel: BudgetViewModel by viewModels()

    override fun onStart() {
        super.onStart()

        // Init
        mToolbar = requireView().findViewById(R.id.toolbar)
        mTvToBeBudgeted = requireView().findViewById(R.id.tvToBeBudgeted)
        mListBudgeting = requireView().findViewById(R.id.listBudgeting)

        // Setup toolbar
        mToolbar.menu

        // Init list categories
        // Set layout manager
        mListBudgeting.layoutManager = LinearLayoutManager(requireContext())

        // Add adapter
        mListBudgeting.adapter = BudgetingListAdapter(
            mViewModel.categories,
            mViewModel.budgets,
            mViewModel.availableMoney,
            { budget -> // Category click

                // Create update budget fragment
                val updateBudgetFragment = UpdateBudgetFragment()

                // Category name,  budgeted value as arguments
                updateBudgetFragment.arguments =
                    bundleOf(
                        UpdateBudgetFragment.BUDGET_ID_KEY to budget.id
                    )

                // Show update budget fragment
                updateBudgetFragment.show(childFragmentManager, "UpdateBudgetFragment")

            },
            requireContext(),
            viewLifecycleOwner
        )

        // Add horizontal dividers
        mListBudgeting.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        // Observe to be budgeted value
        mViewModel.toBeBudgeted.observe(viewLifecycleOwner) {

            // Update to be budgeted text
            mTvToBeBudgeted.text = it.toString()
        }
    }

}