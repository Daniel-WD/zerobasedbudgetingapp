package com.titaniel.zerobasedbudgetingapp.activities

import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import com.titaniel.zerobasedbudgetingapp._testutils.TestUtils
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
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

@RunWith(MockitoJUnitRunner::class)
class BudgetViewModelTest : CoroutinesAndLiveDataTest() {

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
    val month: LocalDate = LocalDate.of(2020, 9, 1)

    /**
     * Fake category data
     */
    val exampleCategories = listOf(
        Category("cat1"), // 0
        Category("cat2"), // 1
        Category("cat3"), // 2
        Category("cat4"), // 3
        Category("cat5"), // 4
        Category("cat6") // 5
    )

    /**
     * Fake budget data
     */
    val exampleBudgets = listOf(
        Budget("cat1", LocalDate.of(2020, 9, 1), 200), // 0
        Budget("cat2", LocalDate.of(2020, 9, 1), 500), // 1
        Budget("cat4", LocalDate.of(2020, 9, 1), -1000), // 2

        Budget("cat2", LocalDate.of(2020, 10, 1), 200), // 3
        Budget("cat3", LocalDate.of(2020, 10, 1), 500), // 4
        Budget("cat4", LocalDate.of(2020, 10, 1), -1000), // 5
        Budget("cat5", LocalDate.of(2010, 3, 1), 200), // 6
        Budget("cat2", LocalDate.of(2020, 1, 1), 500), // 7
        Budget("cat4", LocalDate.of(1999, 12, 1), -1000) // 8
    )

    /**
     * Fake transaction data
     */
    val exampleTransactions = listOf(
        Transaction(-100, "payee", "cat1", "", LocalDate.of(2020, 9, 1)), // 0
        Transaction(-190, "payee", "cat1", "", LocalDate.of(2020, 9, 23)), // 1
        Transaction(1000, "payee", "cat1", "", LocalDate.of(2020, 9, 10)), // 2
        Transaction(300, "payee", "cat2", "", LocalDate.of(2020, 9, 15)), // 3
        Transaction(-50, "payee", "cat2", "", LocalDate.of(2020, 9, 10)), // 4
        Transaction(-10, "payee", "cat5", "", LocalDate.of(2020, 9, 9)), // 5
        Transaction(-100, "payee", "cat5", "", LocalDate.of(2020, 9, 30)), // 6

        Transaction(-100, "payee", "cat1", "", LocalDate.of(2020, 5, 1)), // 7
        Transaction(-190, "payee", "cat2", "", LocalDate.of(2020, 4, 23)), // 8
        Transaction(1000, "payee", "cat1", "", LocalDate.of(2020, 10, 10)), // 9
        Transaction(300, "payee", "cat3", "", LocalDate.of(2020, 12, 15)), // 10
        Transaction(-50, "payee", "cat2", "", LocalDate.of(2020, 6, 10)), // 11
        Transaction(-10, "payee", "cat5", "", LocalDate.of(2010, 9, 9)), // 12
        Transaction(-100, "payee", "cat1", "", LocalDate.of(2021, 9, 30)), // 13
        Transaction(-100, "payee", "cat5", "", LocalDate.of(2023, 10, 1)), // 14
        Transaction(-190, "payee", "cat2", "", LocalDate.of(2011, 6, 23)), // 15
        Transaction(1000, "payee", "cat1", "", LocalDate.of(2020, 2, 10)), // 16
        Transaction(300, "payee", "cat4", "", LocalDate.of(2016, 12, 15)), // 17
        Transaction(-50, "payee", "cat5", "", LocalDate.of(2020, 11, 10)), // 18
        Transaction(-10, "payee", "cat2", "", LocalDate.of(2035, 10, 9)), // 19
        Transaction(-100, "payee", "cat1", "", LocalDate.of(2020, 8, 30)), // 20
        Transaction(9999, "payee", Category.TO_BE_BUDGETED, "", LocalDate.of(2020, 8, 30)), // 21
        Transaction(-2, "payee", Category.TO_BE_BUDGETED, "", LocalDate.of(2020, 12, 30)) // 22
    )

    /**
     * Fake budgetsOfCategories data
     */
    val exampleBudgetsOfCategories = listOf(
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
    val exampleTransactionsOfCategories = listOf(
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

        // Stub getAllCategories()
        `when`(categoryRepositoryMock.getAllCategories()).thenReturn(flow {
            emit(exampleCategories)
        })

        // Stub getBudgetsOfCategories()
        `when`(categoryRepositoryMock.getBudgetsOfCategories()).thenReturn(flow {
            emit(exampleBudgetsOfCategories)
        })

        // Stub getTransactionsOfCategories()
        `when`(categoryRepositoryMock.getTransactionsOfCategories()).thenReturn(flow {
            emit(exampleTransactionsOfCategories)
        })

        // Stub getBudgetsByMonth()
        `when`(budgetRepositoryMock.getBudgetsByMonth(TestUtils.any())).thenReturn(flow {
            emit(exampleBudgets.filter { it.month == month })
        })

        // Stub getAllBudgets()
        `when`(budgetRepositoryMock.getAllBudgets()).thenReturn(flow {
            emit(exampleBudgets)
        })

        // Stub getAllTransactions()
        `when`(transactionRepositoryMock.getAllTransactions()).thenReturn(flow {
            emit(exampleTransactions)
        })

        // Create ViewModel to test
        budgetViewModel = BudgetViewModel(
            categoryRepositoryMock,
            transactionRepositoryMock,
            budgetRepositoryMock
        )

        // Set month
        budgetViewModel.month.value = month

    }

    @Test
    fun updates_available_money_correctly() {

        // Expected output: transactions + budgets (with corresponding category and <= month)
        val expectedAvailableMoney = mapOf(
            exampleBudgets[0] to 1510L + 200L,
            exampleBudgets[1] to -180L + 1000L,
            exampleBudgets[2] to 300L - 2000L
        )

        assertThat(budgetViewModel.availableMoney.value).isEqualTo(expectedAvailableMoney)

    }

    @Test
    fun updates_to_be_budgeted_correctly() {

        // Expected output: to be budgeted transactions - all budget values summed up
        val expectedToBeBudgeted = 9997L + 900L

        assertThat(budgetViewModel.toBeBudgeted.value).isEqualTo(expectedToBeBudgeted)
    }

    @Test
    fun checks_budgets_correctly() = runBlocking {

        // Expected missing budgets
        val expectedMissingBudgets = listOf(
            Budget("cat3", month, 0),
            Budget("cat5", month, 0),
            Budget("cat6", month, 0)
        ).toTypedArray()

        // Verify addBudgets has been called
        verify(budgetRepositoryMock).addBudgets(*expectedMissingBudgets)
    }

}