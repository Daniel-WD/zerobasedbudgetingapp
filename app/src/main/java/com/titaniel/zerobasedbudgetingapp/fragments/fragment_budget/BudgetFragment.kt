package com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget

import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetsOfCategory
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionsOfCategory
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetFragment
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
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
    @VisibleForTesting()
    /**
     * Month
     */
    val month = MutableLiveData(LocalDate.of(2021, 2, 1))

    /**
     * All categories
     */
    private val categories = categoryRepository.getAllCategories().asLiveData()

    /**
     * Budgets by categories
     */
    private val budgetsOfCategories = categoryRepository.getBudgetsOfCategories().asLiveData()

    /**
     * Transactions by categories
     */
    private val transactionsOfCategories = categoryRepository.getTransactionsOfCategories().asLiveData()

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
     * All budgets of selected month TODO -> update when month is set, could be erased....
     */
    val budgetsOfMonth = budgetRepository.getBudgetsByMonth(month.value!!).asLiveData()

    /**
     * Available money per budget
     */
    val availableMoney: MutableLiveData<Map<Budget, Long>> = MutableLiveData(emptyMap())

    /**
     * ViewModel observer for categories
     */
    private val categoriesObserver: Observer<List<Category>> =
            Observer {
                checkBudgets()
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
     * ViewModel observer for budgetsOfMonth
     */
    private val budgetsOfMonthObserver: Observer<List<Budget>> =
            Observer {
                checkBudgets()
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
        budgetsOfMonth.observeForever(budgetsOfMonthObserver)
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
        budgetsOfMonth.removeObserver(budgetsOfMonthObserver)
        month.removeObserver(monthObserver)
        allBudgets.removeObserver(allBudgetsObserver)
    }

    /**
     * Updates [availableMoney]
     */
    private fun updateAvailableMoney() { //TODO After first test iteration month filter to database?
        val budsMon = budgetsOfMonth.value
        val transOfCats = transactionsOfCategories.value
        val budsOfCats = budgetsOfCategories.value
        val mon = month.value

        if (budsMon != null && transOfCats != null && budsOfCats != null && mon != null) {
            // Update available money per budget
            availableMoney.value = budsMon.map { budget ->
                budget to
                        // Sum of all transactions of the category of this budget until selected month (inclusive)
                        (transOfCats.find { transactionsOfCategory -> transactionsOfCategory.category.name == budget.categoryName }?.transactions
                                ?.filter { transaction -> transaction.date.withDayOfMonth(1) <= mon }
                                ?.fold(0L, { acc, transaction -> acc + transaction.pay }) ?: 0) +

                        // Added with sum of all budgets with same category before this budget (inclusive)
                        budsOfCats.find { budgetsOfCategory -> budgetsOfCategory.category.name == budget.categoryName }!!.budgets
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
            toBeBudgeted.value = trans.filter { it.categoryName == Category.TO_BE_BUDGETED }
                    .fold(0L, { acc, transaction -> acc + transaction.pay }) -
                    buds.fold(0L, { acc, budget -> acc + budget.budgeted })
        }
    }

    /**
     * Checks if for every category in [categories] and [month] combination, exists a budget. If not, then create missing [Budget]s.
     */
    private fun checkBudgets() {
        val cats = categories.value
        val budsMon = budgetsOfMonth.value
        val mon = month.value

        if (cats != null && budsMon != null && mon != null) {
            val missingBudgets =
                    // TODO doc
                    cats.filter { category -> budsMon.find { budget -> budget.categoryName == category.name } == null }
                            .map { category -> Budget(category.name, mon, 0) }
                            .toTypedArray()
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

        // Setup toolbar
        toolbar.menu //TODO ???

        // Init list categories
        // Set layout manager
        listBudgeting.layoutManager = LinearLayoutManager(requireContext())

        // Add adapter
        listBudgeting.adapter = BudgetListAdapter(
                viewModel.budgetsOfMonth,
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

        // Add horizontal dividers
        listBudgeting.addItemDecoration(
                DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                )
        )

        // Observe to be budgeted value
        viewModel.toBeBudgeted.observe(viewLifecycleOwner) {

            // Update to be budgeted text
            tvToBeBudgeted.text = it.toString()
        }
    }

}