package com.titaniel.zerobasedbudgetingapp.activities

import com.google.common.truth.Truth.assertThat
import com.jraska.livedata.test
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import com.titaniel.zerobasedbudgetingapp._testutils.TestUtils
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.SettingRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetsOfCategory
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionsOfCategory
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.BudgetViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class BudgetViewModelTest : CoroutinesAndLiveDataTest() {

    /**
     * SettingRepository mock
     */
    @Mock
    private lateinit var settingRepositoryMock: SettingRepository

    /**
     * CategoryRepository mock
     */
    @Mock
    private lateinit var categoryRepositoryMock: CategoryRepository

    /**
     * TransactionRepository mock
     */
    @Mock
    private lateinit var transactionRepositoryMock: TransactionRepository

    /**
     * BudgetRepository mock
     */
    @Mock
    private lateinit var budgetRepositoryMock: BudgetRepository

    /**
     * BudgetViewModel to test
     */
    private lateinit var budgetViewModel: BudgetViewModel

    /**
     * Test month
     */
    private val month: YearMonth = YearMonth.of(2020, 9)

    /**
     * Fake category data
     */
    private val exampleCategories = listOf(
        Category("cat1", 0, 0), // 0
        Category("cat2", 1, 1), // 1
        Category("cat3", 2, 2), // 2
        Category("cat4", 3, 3), // 3
        Category("cat5", 4, 4), // 4
        Category("cat6", 5, 5) // 5
    )

    /**
     * Fake budget data
     */
    private val exampleBudgets = listOf(
        Budget(0, YearMonth.of(2020, 9), 200, 0), // 0
        Budget(1, YearMonth.of(2020, 9), 500, 1), // 1
        Budget(3, YearMonth.of(2020, 9), -1000, 2), // 2

        Budget(1, YearMonth.of(2020, 10), 200, 3), // 3
        Budget(2, YearMonth.of(2020, 10), 500, 4), // 4
        Budget(3, YearMonth.of(2020, 10), -1000, 5), // 5
        Budget(4, YearMonth.of(2010, 3), 200, 6), // 6
        Budget(1, YearMonth.of(2020, 1), 500, 7), // 7
        Budget(3, YearMonth.of(1999, 12), -1000, 8) // 8
    )

    /**
     * Fake budgets with category data
     */
    private val exampleBudgetsWithCategory = mutableListOf(
        BudgetWithCategory(exampleBudgets[0], exampleCategories[0]),
        BudgetWithCategory(exampleBudgets[1], exampleCategories[1]),
        BudgetWithCategory(exampleBudgets[2], exampleCategories[3]),

        BudgetWithCategory(exampleBudgets[3], exampleCategories[1]),
        BudgetWithCategory(exampleBudgets[4], exampleCategories[2]),
        BudgetWithCategory(exampleBudgets[5], exampleCategories[3]),
        BudgetWithCategory(exampleBudgets[6], exampleCategories[4]),
        BudgetWithCategory(exampleBudgets[7], exampleCategories[1]),
        BudgetWithCategory(exampleBudgets[8], exampleCategories[3])
    )

    /**
     * Fake transaction data
     */
    private val exampleTransactions = listOf(
        Transaction(-100, 1, 0, "", LocalDate.of(2020, 9, 1), 0), // 0
        Transaction(-190, 1, 0, "", LocalDate.of(2020, 9, 23), 1), // 1
        Transaction(1000, 1, 0, "", LocalDate.of(2020, 9, 10), 2), // 2
        Transaction(300, 1, 1, "", LocalDate.of(2020, 9, 15), 3), // 3
        Transaction(-50, 1, 1, "", LocalDate.of(2020, 9, 10), 4), // 4
            Transaction(-10, 1, 4, "", LocalDate.of(2020, 9, 9), 5), // 5
            Transaction(-100, 1, 4, "", LocalDate.of(2020, 9, 30), 6), // 6

        Transaction(-100, 1, 0, "", LocalDate.of(2020, 5, 1), 7), // 7
        Transaction(-190, 1, 1, "", LocalDate.of(2020, 4, 23), 8), // 8
        Transaction(1000, 1, 0, "", LocalDate.of(2020, 10, 10), 9), // 9
        Transaction(300, 1, 2, "", LocalDate.of(2020, 12, 15), 10), // 10
        Transaction(-50, 1, 1, "", LocalDate.of(2020, 6, 10), 11), // 11
            Transaction(-10, 1, 4, "", LocalDate.of(2010, 9, 9), 12), // 12
        Transaction(-100, 1, 0, "", LocalDate.of(2021, 9, 30), 13), // 13
        Transaction(-100, 1, 4, "", LocalDate.of(2023, 10, 1), 14), // 14
        Transaction(-190, 1, 1, "", LocalDate.of(2011, 6, 23), 15), // 15
        Transaction(1000, 1, 0, "", LocalDate.of(2020, 2, 10), 16), // 16
        Transaction(300, 1, 3, "", LocalDate.of(2016, 12, 15), 17), // 17
        Transaction(-50, 1, 4, "", LocalDate.of(2020, 11, 10), 18), // 18
        Transaction(-10, 1, 1, "", LocalDate.of(2035, 10, 9), 19), // 19
        Transaction(-100, 1, 0, "", LocalDate.of(2020, 8, 30), 20), // 20
        Transaction(9999, 1, Category.TO_BE_BUDGETED.id, "", LocalDate.of(2020, 8, 21)), // 21
        Transaction(-2, 1, Category.TO_BE_BUDGETED.id, "", LocalDate.of(2020, 12, 22)) // 22
    )

    /**
     * Fake budgetsOfCategories data
     */
    private val exampleBudgetsOfCategories = listOf(
        BudgetsOfCategory(
            exampleCategories[0],
            listOf(
                exampleBudgets[0]
            )
        ),
        BudgetsOfCategory(
            exampleCategories[1],
            listOf(
                exampleBudgets[1],
                exampleBudgets[3],
                exampleBudgets[7],
            )
        ),
        BudgetsOfCategory(
            exampleCategories[2],
            listOf(
                exampleBudgets[4]
            )
        ),
        BudgetsOfCategory(
            exampleCategories[3],
            listOf(
                exampleBudgets[2],
                exampleBudgets[5],
                exampleBudgets[8]
            )
        ),
        BudgetsOfCategory(
            exampleCategories[4],
            listOf(
                exampleBudgets[6]
            )
        ),
        BudgetsOfCategory(
            exampleCategories[5],
            emptyList()
        )
    )

    /**
     * Fake transactionsOfCategories data
     */
    private val exampleTransactionsOfCategories = listOf(
        TransactionsOfCategory(
            exampleCategories[0],
            listOf(
                exampleTransactions[0],
                exampleTransactions[1],
                exampleTransactions[2],
                exampleTransactions[7],
                exampleTransactions[9],
                exampleTransactions[13],
                exampleTransactions[16],
                exampleTransactions[20]
            )
        ),
        TransactionsOfCategory(
            exampleCategories[1],
            listOf(
                exampleTransactions[3],
                exampleTransactions[4],
                exampleTransactions[8],
                exampleTransactions[11],
                exampleTransactions[15],
                exampleTransactions[19]
            )
        ),
        TransactionsOfCategory(
            exampleCategories[2],
            listOf(
                exampleTransactions[10]
            )
        ),
        TransactionsOfCategory(
            exampleCategories[3],
            listOf(
                exampleTransactions[17]
            )
        ),
        TransactionsOfCategory(
            exampleCategories[4],
            listOf(
                exampleTransactions[5],
                exampleTransactions[6],
                exampleTransactions[12],
                exampleTransactions[14],
                exampleTransactions[18],
            )
        ),
        TransactionsOfCategory(
            exampleCategories[5],
            emptyList()
        )
    )

    @ExperimentalCoroutinesApi
    @Before
    override fun setup() {
        super.setup()

        // Stub getBudgetsOfCategories()
        `when`(categoryRepositoryMock.getBudgetsOfCategories()).thenReturn(flow {
            emit(exampleBudgetsOfCategories)
        })

        // Stub getTransactionsOfCategories()
        `when`(categoryRepositoryMock.getTransactionsOfCategories()).thenReturn(flow {
            emit(exampleTransactionsOfCategories)
        })

        // Stub getBudgetsByMonth()
        `when`(budgetRepositoryMock.getAllBudgetsWithCategory()).thenReturn(flow {
            emit(exampleBudgetsWithCategory)
        })

        // Stub getAllBudgets()
        `when`(budgetRepositoryMock.getAllBudgets()).thenReturn(flow {
            emit(exampleBudgets)
        })

        // Stub getAllTransactions()
        `when`(transactionRepositoryMock.getAllTransactions()).thenReturn(flow {
            emit(exampleTransactions)
        })

        // Stub getMonth()
        `when`(settingRepositoryMock.getMonth()).thenReturn(flow {
            emit(month)
        })

        createViewModel()

    }

    /**
     * Creates the test ViewModel
     */
    private fun createViewModel() {

        // Create ViewModel to test
        budgetViewModel = BudgetViewModel(
            settingRepositoryMock,
            categoryRepositoryMock,
            transactionRepositoryMock,
            budgetRepositoryMock
        )
    }

    @Test
    fun updates_available_money_correctly() {

        // Missing budgets with category
        val missingBudgetsWithCategory =
            listOf(
                BudgetWithCategory(Budget(2, month, 0, 9), exampleCategories[2]),
                BudgetWithCategory(Budget(4, month, 0, 10), exampleCategories[4]),
                BudgetWithCategory(Budget(5, month, 0, 11), exampleCategories[5])
            )

        // Expected output: transactions + budgets (with corresponding category and <= month)
        val expectedAvailableMoney = mapOf(
            exampleBudgetsWithCategory[0] to 1510L + 200L,
            exampleBudgetsWithCategory[1] to -180L + 1000L,
            exampleBudgetsWithCategory[2] to 300L - 2000L,
            missingBudgetsWithCategory[0] to 0L + 0L,
            missingBudgetsWithCategory[1] to 0L + 80L,
            missingBudgetsWithCategory[2] to 0L + 0L
        )

        // Add missing budgets
        exampleBudgetsWithCategory.addAll(
            listOf(
                BudgetWithCategory(Budget(2, month, 0, 9), exampleCategories[2]),
                BudgetWithCategory(Budget(4, month, 0, 10), exampleCategories[4]),
                BudgetWithCategory(Budget(5, month, 0, 11), exampleCategories[5])
            )
        )

        // Re-Stub getBudgetsByMonth()
        `when`(budgetRepositoryMock.getAllBudgetsWithCategory()).thenReturn(flow {
            emit(exampleBudgetsWithCategory)
        })

        // Re-create ViewModel
        createViewModel()

        assertThat(budgetViewModel.availableMoney.value).isEqualTo(expectedAvailableMoney)

    }

    @Test
    fun updates_to_be_budgeted_correctly() {

        // Expected output: to be budgeted transactions - all budget values summed up
        val expectedToBeBudgeted = 9997L + 900L

        assertThat(budgetViewModel.toBeBudgeted.value).isEqualTo(expectedToBeBudgeted)
    }

}