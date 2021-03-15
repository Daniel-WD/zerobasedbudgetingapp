package com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget

import android.content.Intent
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activities.ManageCategoriesActivity
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetsOfCategory
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionsOfCategory
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetFragment
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * [BudgetViewModel] with [categoryRepository], [transactionsRepository] and [budgetRepository]
 */
@HiltViewModel
class BudgetViewModel @Inject constructor(
    categoryRepository: CategoryRepository,
    transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    // FIXME -> should be private after month can be set by the user
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
            /**
             * Month
             */
    val month = MutableLiveData(LocalDate.of(2021, 2, 1))

    /**
     * All categories
     */
    val categories = categoryRepository.getAllCategories().asLiveData()

    /**
     * Budgets by categories
     */
    private val budgetsOfCategories = categoryRepository.getBudgetsOfCategories().asLiveData()

    /**
     * Transactions by categories
     */
    private val transactionsOfCategories =
        categoryRepository.getTransactionsOfCategories().asLiveData()

    /**
     * All budgets
     */
    private val allBudgets = budgetRepository.getAllBudgets().asLiveData()

    /**
     * All transactions
     */
    private val transactions = transactionRepository.getAllTransactions().asLiveData()

    /**
     * To be budgeted
     */
    val toBeBudgeted: MutableLiveData<Long> = MutableLiveData()

    /**
     * All budgetsWithCategory of selected month
     */
    val budgetsWithCategoryOfMonth = budgetRepository.getBudgetsWithCategoryByMonth(month.value!!).map { list -> list.sortedBy { it.category.index } }.asLiveData()

    /**
     * Available money per budget
     */
    val availableMoney: MutableLiveData<Map<BudgetWithCategory, Long>> = MutableLiveData(emptyMap())

    /**
     * ViewModel observer for categories
     */
    private val categoriesObserver: Observer<List<Category>> =
        Observer {
            //checkBudgets() //I
        }

    /**
     * ViewModel observer for budgetsOfCategories
     */
    private val budgetsOfCategoriesObserver: Observer<List<BudgetsOfCategory>> =
        Observer {
            updateAvailableMoney()
        }

    /**
     * ViewModel observer for transactionsOfCategories
     */
    private val transactionsOfCategoriesObserver: Observer<List<TransactionsOfCategory>> =
        Observer {
            updateAvailableMoney()
        }

    /**
     * ViewModel observer for transactions
     */
    private val transactionsObserver: Observer<List<Transaction>> =
        Observer {
            updateToBeBudgeted()
        }

    /**
     * ViewModel observer for budgetsWithCategoryOfMonth
     */
    private val budgetsWithCategoryOfMonthObserver: Observer<List<BudgetWithCategory>> =
        Observer {
            checkBudgets() //IIII
            updateAvailableMoney()
        }

    /**
     * ViewModel observer for allBudgets
     */
    private val allBudgetsObserver: Observer<List<Budget>> =
        Observer {
            updateToBeBudgeted()
        }

    /**
     * ViewModel observer for month
     */
    private val monthObserver: Observer<LocalDate> =
        Observer {
            checkBudgets()
            updateAvailableMoney()
        }

    init {
        // Register all observers
        categories.observeForever(categoriesObserver)
        budgetsOfCategories.observeForever(budgetsOfCategoriesObserver)
        transactionsOfCategories.observeForever(transactionsOfCategoriesObserver)
        transactions.observeForever(transactionsObserver)
        budgetsWithCategoryOfMonth.observeForever(budgetsWithCategoryOfMonthObserver)
        allBudgets.observeForever(allBudgetsObserver)
        month.observeForever(monthObserver)
    }

    override fun onCleared() {
        super.onCleared()

        // Remove all observers
        categories.removeObserver(categoriesObserver)
        budgetsOfCategories.removeObserver(budgetsOfCategoriesObserver)
        transactionsOfCategories.removeObserver(transactionsOfCategoriesObserver)
        transactions.removeObserver(transactionsObserver)
        budgetsWithCategoryOfMonth.removeObserver(budgetsWithCategoryOfMonthObserver)
        month.removeObserver(monthObserver)
        allBudgets.removeObserver(allBudgetsObserver)
    }

    /**
     * Updates [availableMoney]
     */
    private fun updateAvailableMoney() {
        val budsWithCatMon = budgetsWithCategoryOfMonth.value
        val transOfCats = transactionsOfCategories.value
        val budsOfCats = budgetsOfCategories.value
        val mon = month.value

        if (budsWithCatMon != null && transOfCats != null && budsOfCats != null && mon != null && budsOfCats.size == budsWithCatMon.size) {
            // Update available money per budget
            availableMoney.value = budsWithCatMon.map { budgetWithCategory ->
                budgetWithCategory to
                        // Sum of all transactions of the category of this budget until selected month (inclusive)
                        (transOfCats.find { transactionsOfCategory -> transactionsOfCategory.category.id == budgetWithCategory.category.id }?.transactions
                            ?.filter { transaction -> transaction.date.withDayOfMonth(1) <= mon }
                            ?.fold(0L, { acc, transaction -> acc + transaction.pay }) ?: 0) +

                        // Added with sum of all budgets with same category before this budget (inclusive)
                        budsOfCats.find { budgetsOfCategory -> budgetsOfCategory.category.id == budgetWithCategory.category.id }!!.budgets
                            .filter { bud -> bud.month <= mon }
                            .fold(0L, { acc, bud -> acc + bud.budgeted })
            }.toMap()
        }

    }

    /**
     * Update [toBeBudgeted]
     */
    private fun updateToBeBudgeted() {
        val trans = transactions.value
        val buds = allBudgets.value

        if (trans != null && buds != null) {
            toBeBudgeted.value = trans.filter { it.categoryId == Category.TO_BE_BUDGETED.id }
                .fold(0L, { acc, transaction -> acc + transaction.pay }) -
                    buds.fold(0L, { acc, budget -> acc + budget.budgeted })
        }
    }

    /**
     * Checks if for every category in [categories] and [month] combination, exists a budget. If not, then create missing [Budget]s.
     */
    private fun checkBudgets() {
        val cats = categories.value
        val budgetsWithCategory = budgetsWithCategoryOfMonth.value
        val mon = month.value

        if(cats != null && budgetsWithCategory != null && mon != null) {
            val missingBudgets =
                // Find categories that have no budget in selected month
                cats.filter { category -> budgetsWithCategory.find { budgetWithCategory -> budgetWithCategory.category == category } == null }
                    // Create budgets for filtered categories
                    .map { category -> Budget(category.id, mon, 0) }
                    .toTypedArray()

            // Add missing budgets
            viewModelScope.launch {
                budgetRepository.addBudgets(*missingBudgets)
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
    private lateinit var toolbar: MaterialToolbar

    /**
     * "To be budgeted" Text
     */
    private lateinit var tvToBeBudgeted: TextView

    /**
     * Budgeting list
     */
    private lateinit var listBudgeting: RecyclerView

    /**
     * View model
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: BudgetViewModel by provideViewModel()

    override fun onStart() {
        super.onStart()

        // Init
        toolbar = requireView().findViewById(R.id.toolbar)
        tvToBeBudgeted = requireView().findViewById(R.id.tvToBeBudgeted)
        listBudgeting = requireView().findViewById(R.id.listBudgeting)

        // Setup menu item click listener
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                // Manage categories click
                R.id.categoryManagement -> {
                    // Start ManageCategoriesActivity
                    startActivity(Intent(requireContext(), ManageCategoriesActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Init list categories
        // Set layout manager
        listBudgeting.layoutManager = LinearLayoutManager(requireContext())

        // Add adapter
        listBudgeting.adapter = BudgetListAdapter(
            viewModel.budgetsWithCategoryOfMonth,
            viewModel.availableMoney,
            { budget -> // budget click

                // Create update budget fragment
                val updateBudgetFragment = UpdateBudgetFragment()

                // Budget id as argument
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

        // Add horizontal dividers, if not already there
        if(listBudgeting.itemDecorationCount == 0) {
            listBudgeting.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        // Observe to be budgeted value
        viewModel.toBeBudgeted.observe(viewLifecycleOwner) {

            // Update to be budgeted text
            tvToBeBudgeted.text = it.toString()
        }
    }

}