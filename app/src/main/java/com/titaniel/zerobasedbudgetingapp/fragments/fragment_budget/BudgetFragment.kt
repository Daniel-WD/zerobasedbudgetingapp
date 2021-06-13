package com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget

import android.content.Intent
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activities.ManageCategoriesActivity
import com.titaniel.zerobasedbudgetingapp.compose.dialog_select_month.SelectMonthDialogFragment
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.SettingRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetFragment
import com.titaniel.zerobasedbudgetingapp.utils.createSimpleMediatorLiveData
import com.titaniel.zerobasedbudgetingapp.utils.moneyFormat
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * [BudgetViewModel] for [BudgetFragment].
 */
@HiltViewModel
class BudgetViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    budgetRepository: BudgetRepository,
    settingRepository: SettingRepository
) : ViewModel() {

    /**
     * Month
     */
    private val month = settingRepository.getMonth().asLiveData()

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
     * All budgetsWithCategory
     */
    private val allBudgetsWithCategory = budgetRepository.getAllBudgetsWithCategory().asLiveData()

    /**
     * To be budgeted
     */
    val toBeBudgeted: MutableLiveData<Long> = MutableLiveData()

    /**
     * All budgetsWithCategory of selected month
     */
    val budgetsWithCategoryOfMonth: MutableLiveData<List<BudgetWithCategory>> = MutableLiveData()

    /**
     * Available money per budget
     */
    val availableMoney: MutableLiveData<Map<BudgetWithCategory, Long>> = MutableLiveData(emptyMap())

    /**
     * MediatorLiveData for [budgetsWithCategoryOfMonth], [transactionsOfCategories], [budgetsOfCategories], [month]
     */
    private val updateAvailableMoneyMediator = createSimpleMediatorLiveData(
        budgetsWithCategoryOfMonth,
        transactionsOfCategories,
        budgetsOfCategories,
        month
    )

    /**
     * MediatorLiveData for [transactions], [allBudgets]
     */
    private val updateToBeBudgetedMediator = createSimpleMediatorLiveData(transactions, allBudgets)

    /**
     * MediatorLiveData for [month], [allBudgetsWithCategory]
     */
    private val budgetsWithCategoryUpdateMediator =
        createSimpleMediatorLiveData(month, allBudgetsWithCategory)

    /**
     * Observer to update [budgetsWithCategoryOfMonth]
     */
    private val budgetsWithCategoryUpdateObserver: Observer<Unit> = Observer {
        val mon = month.value
        val budsWithCat = allBudgetsWithCategory.value

        // Check non null
        if (mon != null && budsWithCat != null) {
            // Filter all budgetsWithCategory of currently selected month
            budgetsWithCategoryOfMonth.value =
                budsWithCat.filter { it.budget.month == mon }.sortedBy { it.category.index }
        }

    }

    /**
     * Observer to update [availableMoney]
     */
    private val updateAvailableMoneyObserver: Observer<Unit> = Observer {
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
                            ?.filter { transaction -> transaction.date.year < mon.year || (transaction.date.year == mon.year && transaction.date.month <= mon.month) }
                            ?.fold(0L, { acc, transaction -> acc + transaction.pay }) ?: 0) +

                        // Added with sum of all budgets with same category before this budget (inclusive)
                        budsOfCats.find { budgetsOfCategory -> budgetsOfCategory.category.id == budgetWithCategory.category.id }!!.budgets
                            .filter { bud -> bud.month <= mon }
                            .fold(0L, { acc, bud -> acc + bud.budgeted })
            }.toMap()
        }
    }

    /**
     * Observer to update [toBeBudgeted]
     */
    private val updateToBeBudgetedObserver: Observer<Unit> = Observer {
        val trans = transactions.value
        val buds = allBudgets.value

        // Check non null
        if (trans != null && buds != null) {
            // Filter all transaction for to be budgeted, sum pays up. Subtract all budget values.
            toBeBudgeted.value = trans.filter { it.categoryId == Category.TO_BE_BUDGETED.id }
                .fold(0L, { acc, transaction -> acc + transaction.pay }) -
                    buds.fold(0L, { acc, budget -> acc + budget.budgeted })
        }
    }

    init {
        // Register all observers
        updateAvailableMoneyMediator.observeForever(updateAvailableMoneyObserver)
        updateToBeBudgetedMediator.observeForever(updateToBeBudgetedObserver)
        budgetsWithCategoryUpdateMediator.observeForever(budgetsWithCategoryUpdateObserver)
    }

    override fun onCleared() {
        super.onCleared()

        // Remove all observers
        updateAvailableMoneyMediator.removeObserver(updateAvailableMoneyObserver)
        updateToBeBudgetedMediator.removeObserver(updateToBeBudgetedObserver)
        budgetsWithCategoryUpdateMediator.removeObserver(budgetsWithCategoryUpdateObserver)
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
    private lateinit var tvToBeBudgeted: AppCompatTextView

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

        // Toolbar click listener
        toolbar.setOnClickListener {
            SelectMonthDialogFragment()
                .show(childFragmentManager, "SelectMonthFragment")
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
        if (listBudgeting.itemDecorationCount == 0) {
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
            tvToBeBudgeted.text = it.moneyFormat()
        }
    }

}