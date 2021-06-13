package com.titaniel.zerobasedbudgetingapp.activities

import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import com.titaniel.zerobasedbudgetingapp._testutils.TestUtils
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.SettingRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
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

@RunWith(MockitoJUnitRunner::class)
class ManageCategoriesViewModelTest : CoroutinesAndLiveDataTest() {

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
     * ManageCategoriesViewModel to test
     */
    private lateinit var testViewModel: ManageCategoriesViewModel

    /**
     * Test availableMonths
     */
    private val availableMonths = listOf(
        YearMonth.of(2020, 9),
        YearMonth.of(2020, 10),
        YearMonth.of(2020, 11),
        YearMonth.of(2020, 12),
        YearMonth.of(2021, 1)
    )

    /**
     * Fake category data
     */
    private val exampleCategories = listOf(
        Category("cat1", 0, 9, 1), // 0
        Category("cat2", 0, 8, 2), // 1
        Category("cat3", 0, 7, 3), // 2
        Category("cat4", 0, 6, 4), // 3
        Category("cat5", 0, 5, 5), // 4
        Category("cat6", 0, 4, 6) // 5
    )

    /**
     * Fake transaction data
     */
    private val exampleTransactions = listOf(
        Transaction(1, 0, 6, "", LocalDate.now(), 1),
        Transaction(2, 0, 6, "", LocalDate.now(), 2),
        Transaction(3, 0, 1, "", LocalDate.now(), 3),
        Transaction(4, 0, -1, "", LocalDate.now(), 4),
        Transaction(5, 0, 4, "", LocalDate.now(), 5),
    )

    /**
     * Sorted fake categories
     */
    private val sortedExampleCategories = exampleCategories.sortedBy { it.positionInGroup }

    @ExperimentalCoroutinesApi
    @Before
    override fun setup() {
        super.setup()

        // Stub getMonth()
        `when`(settingRepositoryMock.availableMonths).thenReturn(flow {
            emit(availableMonths)
        })

        // Stub getAllCategories()
        `when`(categoryRepositoryMock.getAllCategories()).thenReturn(flow {
            emit(exampleCategories)
        })

        // Stub getAllTransactions()
        `when`(transactionRepositoryMock.getAllTransactions()).thenReturn(flow {
            emit(exampleTransactions)
        })

        testViewModel = ManageCategoriesViewModel(
            settingRepositoryMock,
            categoryRepositoryMock,
            transactionRepositoryMock,
            budgetRepositoryMock
        )

    }

    @Test
    fun new_categories_gets_initialized_correctly() {
        assertThat(testViewModel.newCategories.value).isEqualTo(sortedExampleCategories)
    }

    @Test
    fun performs_categories_changed_correctly() {

        // Get newCategories
        val newCats = testViewModel.newCategories.value!!

        assertThat(testViewModel.categoriesChanged()).isFalse()

        // Add category
        val cat = Category("ajsdfölks", 0, 7, 1)
        newCats.add(cat)

        assertThat(testViewModel.categoriesChanged()).isTrue()

        // Remove previously added category
        newCats.remove(cat)

        assertThat(testViewModel.categoriesChanged()).isFalse()

        // Change category
        newCats[4].name = "new name"

        assertThat(testViewModel.categoriesChanged()).isTrue()

        // Revert previous change
        newCats[4].name = "cat2"

        assertThat(testViewModel.categoriesChanged()).isFalse()

        // Swap elements
        newCats[0] = newCats[2].also {
            newCats[2] = newCats[0]
        }

        assertThat(testViewModel.categoriesChanged()).isTrue()

        // Revert previous swap
        newCats[2] = newCats[0].also {
            newCats[0] = newCats[2]
        }

        assertThat(testViewModel.categoriesChanged()).isFalse()

    }

    @Test
    fun performs_add_edit_category_correctly() {

        // Get newCategories
        var newCats = testViewModel.newCategories.value!!

        // Change category name with valid name
        assertThat(
            testViewModel.addEditCategory(3, "valid name")
        ).isTrue()

        assertThat(newCats.find { it.id == 3L }!!.name).isEqualTo("valid name")

        newCats = sortedExampleCategories.toMutableList()

        // Change category name with invalid name I
        assertThat(
            testViewModel.addEditCategory(3, "cat5")
        ).isFalse()

        assertThat(newCats.find { it.id == 3L }!!.name).isEqualTo("cat3")

        newCats = sortedExampleCategories.toMutableList()

        // Change category name with invalid name II
        assertThat(
            testViewModel.addEditCategory(3, "")
        ).isFalse()

        assertThat(newCats.find { it.id == 3L }!!.name).isEqualTo("cat3")

        newCats = sortedExampleCategories.toMutableList()

        // Change category name with same name
        assertThat(
            testViewModel.addEditCategory(3, "cat3")
        ).isTrue()

        assertThat(newCats).isEqualTo(sortedExampleCategories)

        // Add new category with valid name
        assertThat(
            testViewModel.addEditCategory(null, "valid name")
        ).isTrue()

        assertThat(testViewModel.newCategories.value!!.find { it.name == "valid name" }).isNotNull()

    }

    @Test
    fun performs_save_new_categories_correctly(): Unit = runBlocking {

        // Stub addCategories()
        `when`(categoryRepositoryMock.addCategories(TestUtils.any())).thenReturn(arrayOf(7L))

        // Old cats
//            Category("cat6", 4, 6),
//            Category("cat5", 5, 5),
//            Category("cat4", 6, 4),
//            Category("cat3", 7, 3),
//            Category("cat2", 8, 2),
//            Category("cat1", 9, 1)

        // Set new cats
        testViewModel.newCategories.value = mutableListOf(
            Category("cat1", 0, 9, 1),
            Category("cat4", 0, 6, 4),
            Category("newcat", 0, 6, 0),
            Category("newnamecat2", 0, 8, 2),
            Category("cat3", 0, 7, 3),
            Category("newnamecat5", 0, 5, 5)
        )

        testViewModel.saveNewCategories()

        verify(transactionRepositoryMock).updateTransactions(
            *exampleTransactions.take(2).map { it.apply { categoryId = -1 } }.toTypedArray()
        )

        verify(categoryRepositoryMock).deleteCategories(Category("cat6", 0, 4, 6))

        verify(categoryRepositoryMock).addCategories(Category("newcat", 0, 2, 0))

        verify(categoryRepositoryMock).updateCategories(
            Category("cat1", 0, 0, 1),
            Category("cat4", 0, 1, 4),
            Category("newnamecat2", 0, 3, 2),
            Category("cat3", 0, 4, 3),
            Category("newnamecat5", 0, 5, 5)
        )

        val expectedNewBudgets = availableMonths.map { month ->
            Budget(7, month, 0)
        }.toTypedArray()

        verify(budgetRepositoryMock).addBudgets(*expectedNewBudgets)

    }

}