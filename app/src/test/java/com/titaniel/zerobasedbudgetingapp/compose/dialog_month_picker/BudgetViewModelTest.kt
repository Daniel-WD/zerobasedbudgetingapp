package com.titaniel.zerobasedbudgetingapp.compose.dialog_month_picker

import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import com.titaniel.zerobasedbudgetingapp.compose.screen_budget.BudgetViewModel
import com.titaniel.zerobasedbudgetingapp.compose.screen_budget.CategoryItemData
import com.titaniel.zerobasedbudgetingapp.compose.screen_budget.GroupData
import com.titaniel.zerobasedbudgetingapp.database.repositories.*
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Group
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import com.titaniel.zerobasedbudgetingapp.utils.asLocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import java.time.YearMonth

@RunWith(MockitoJUnitRunner::class)
class BudgetViewModelTest : CoroutinesAndLiveDataTest() {

    /**
     * SettingRepository mock
     */
    @Mock
    private lateinit var settingRepositoryMock: SettingRepository

    /**
     * TransactionRepository mock
     */
    @Mock
    private lateinit var transactionRepositoryMock: TransactionRepository

    /**
     * CategoryRepository mock
     */
    @Mock
    private lateinit var categoryRepositoryMock: CategoryRepository

    /**
     * BudgetRepository mock
     */
    @Mock
    private lateinit var budgetRepositoryMock: BudgetRepository

    /**
     * GroupRepository mock
     */
    @Mock
    private lateinit var groupRepositoryMock: GroupRepository

    /**
     * UpdateBudgetViewModel to test
     */
    private lateinit var viewModel: BudgetViewModel

    /**
     * Transactions until date
     */
    private val transactionsUntilDate = listOf(
        Transaction(1, 1, Category.TO_BE_BUDGETED.id, "", LocalDate.of(2020, 6, 13)),
        Transaction(2, 1, Category.TO_BE_BUDGETED.id, "", LocalDate.of(2020, 4, 11)),
        Transaction(3, 1, Category.TO_BE_BUDGETED.id, "", LocalDate.of(2019, 5, 10)),

        Transaction(4, 1, 1, "", LocalDate.of(2020, 6, 10)),
        Transaction(5, 1, 1, "", LocalDate.of(2020, 5, 10)),

        Transaction(6, 1, 2, "", LocalDate.of(2020, 5, 1)),
        Transaction(7, 1, 2, "", LocalDate.of(2020, 5, 10)),
    )

    /**
     * All categories
     */
    private val allCategories = listOf(
        Category("x", 1, 2, 1),
        Category("y", 1, 1, 2),
        Category("z", 2, 1, 3)
    )

    /**
     * Budgets until month (every category is represented by a budget in this month)
     */
    private val budgetsUntilMonthComplete = listOf(
        Budget(1, YearMonth.of(2020, 6), 10, 1),
        Budget(2, YearMonth.of(2020, 6), 20, 2),
        Budget(3, YearMonth.of(2020, 6), 30, 3),
        Budget(1, YearMonth.of(2020, 5), 40, 4),
        Budget(2, YearMonth.of(2020, 5), 50, 5),
        Budget(3, YearMonth.of(2020, 5), 60, 6)
    )

    /**
     * Budgets until month (not every category has a budget in this month)
     */
    private val budgetsUntilMonthUnderrepresented = listOf(
        Budget(1, YearMonth.of(2020, 6), 10, 1),
        Budget(1, YearMonth.of(2020, 5), 40, 4),
        Budget(2, YearMonth.of(2020, 5), 50, 5),
        Budget(3, YearMonth.of(2020, 5), 60, 6)
    )

    /**
     * Budgets with category by month (every category is represented by a budget in this month)
     */
    private val budgetsWithCategoryByMonthComplete = listOf(
        BudgetWithCategory(budgetsUntilMonthComplete[0], allCategories[0]),
        BudgetWithCategory(budgetsUntilMonthComplete[1], allCategories[1]),
        BudgetWithCategory(budgetsUntilMonthComplete[2], allCategories[2])
    )

    /**
     * Budgets with category by month (not every category has a budget in this month)
     */
    private val budgetsWithCategoryByMonthUnderrepresented = listOf(
        BudgetWithCategory(budgetsUntilMonthUnderrepresented[0], allCategories[0])
    )

    /**
     * Month
     */
    private val month = YearMonth.of(2020, 6)

    /**
     * All groups
     */
    private val allGroups = listOf(
        Group("g1", 2, 1),
        Group("g2", 1, 2)
    )

    @ExperimentalCoroutinesApi
    @Before
    override fun setup() {
        super.setup()

        // Stub transactions until date
        `when`(transactionRepositoryMock.getTransactionsUntilDate(month.asLocalDate())).thenReturn(
            flow { emit(transactionsUntilDate) })

        // Stub all categories
        `when`(categoryRepositoryMock.getAllCategories()).thenReturn(flow { emit(allCategories) })

        // Stub budgets until month
        `when`(budgetRepositoryMock.getBudgetsUntilMonth(month)).thenReturn(flow {
            emit(
                budgetsUntilMonthComplete
            )
        })

        // Stub budgets with category by month
        `when`(budgetRepositoryMock.getBudgetsWithCategoryByMonth(month)).thenReturn(flow {
            emit(
                budgetsWithCategoryByMonthComplete
            )
        })

        // Stub month
        `when`(settingRepositoryMock.getMonth()).thenReturn(flow { emit(month) })

        // Stub all groups
        `when`(groupRepositoryMock.getAllGroups()).thenReturn(flow { emit(allGroups) })

        // Create ViewModel instance
        viewModel = BudgetViewModel(
            transactionRepositoryMock,
            categoryRepositoryMock,
            budgetRepositoryMock,
            settingRepositoryMock,
            groupRepositoryMock
        )

    }

    @Test
    fun calls_get_transactions_until_date_with_correct_date() {

        // TODO need to mock LocalDate.now()

    }

    @Test
    fun returns_month_correctly() {

        viewModel.month.test().awaitValue()

        assertThat(viewModel.month.value).isEqualTo(month)

    }

    @Test
    fun calculates_to_be_budgeted_correctly() {

        viewModel.toBeBudgeted.test().awaitValue()

        assertThat(viewModel.toBeBudgeted.value).isEqualTo(6)

    }

    @Test
    fun calculates_group_list_correctly_when_budgets_complete() {

        val expectedGroupList = listOf(
            GroupData(
                "g2", listOf(
                    CategoryItemData(
                        "z",
                        30,
                        0 + 90 /*transaction sum, budget sum*/
                    )
                )
            ),
            GroupData(
                "g1", listOf(
                    CategoryItemData("y", 20, 13 + 70),
                    CategoryItemData("x", 10, 9 + 50)
                )
            ),
        )

        viewModel.groupList.test().awaitValue()

        assertThat(viewModel.groupList.value).isEqualTo(expectedGroupList)

    }

    @Test
    fun calculates_group_list_correctly_when_budgets_underrepresented(): Unit = runBlocking {

        val expectedNewBudgets = listOf(
            Budget(2, month, 0),
            Budget(3, month, 0)
        )

        val expectedGroupList = listOf(
            GroupData(
                "g2", listOf(
                    CategoryItemData(
                        "z",
                        0,
                        0 + 60 /*transaction sum, budget sum*/
                    )
                )
            ),
            GroupData(
                "g1", listOf(
                    CategoryItemData("y", 0, 13 + 50),
                    CategoryItemData("x", 10, 9 + 50)
                )
            ),
        )

        val budgetsWithCategoryByMonthFlow =
            MutableStateFlow(budgetsWithCategoryByMonthUnderrepresented)

        // Stub budgets with category by month
        `when`(budgetRepositoryMock.getBudgetsWithCategoryByMonth(month)).thenReturn(
            budgetsWithCategoryByMonthFlow
        )

        // Stub budgets until month
        `when`(budgetRepositoryMock.getBudgetsUntilMonth(month)).thenReturn(flow {
            emit(
                budgetsUntilMonthUnderrepresented
            )
        })

        // Re-create ViewModel instance
        viewModel = BudgetViewModel(
            transactionRepositoryMock,
            categoryRepositoryMock,
            budgetRepositoryMock,
            settingRepositoryMock,
            groupRepositoryMock
        )

        viewModel.groupList.test().awaitValue()

        // Verify new budgets added
        verify(budgetRepositoryMock).addBudgets(*expectedNewBudgets.toTypedArray())

        // Create new value of budgetsWithCategoriesWithMissingBudgets
        val budsWithCatsWithMissingBudgets =
            budgetsWithCategoryByMonthUnderrepresented.toMutableList()

        budsWithCatsWithMissingBudgets.addAll(
            listOf(
                BudgetWithCategory(expectedNewBudgets[0], allCategories[1]),
                BudgetWithCategory(expectedNewBudgets[1], allCategories[2])
            )
        )

        // Re-trigger budgetsWithCategoryByMonthFlow
        budgetsWithCategoryByMonthFlow.value = budsWithCatsWithMissingBudgets

        viewModel.groupList.test().awaitValue()

        assertThat(viewModel.groupList.value).isEqualTo(expectedGroupList)

    }


}