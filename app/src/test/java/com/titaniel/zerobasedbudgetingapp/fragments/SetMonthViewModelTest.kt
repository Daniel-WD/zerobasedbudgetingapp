package com.titaniel.zerobasedbudgetingapp.fragments

import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.SettingRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
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
import java.time.YearMonth

@RunWith(MockitoJUnitRunner::class)
class SetMonthViewModelTest : CoroutinesAndLiveDataTest() {
//
//    /**
//     * BudgetRepository mock
//     */
//    @Mock
//    private lateinit var budgetRepositoryMock: BudgetRepository
//
//    /**
//     * CategoryRepository mock
//     */
//    @Mock
//    private lateinit var categoryRepositoryMock: CategoryRepository
//
//    /**
//     * SettingRepository mock
//     */
//    @Mock
//    private lateinit var settingRepositoryMock: SettingRepository
//
//    /**
//     * UpdateBudgetViewModel to test
//     */
//    private lateinit var viewModel: SetMonthViewModel
//
//    /**
//     * Test month
//     */
//    private val month = YearMonth.of(2020, 5)
//
//    /**
//     * Test availableMonths
//     */
//    private val availableMonths = listOf(
//        YearMonth.of(2020, 4),
//        YearMonth.of(2020, 5),
//        YearMonth.of(2020, 6)
//    )
//
//    /**
//     * Test categories
//     */
//    private val categories = listOf(
//        Category("cat1", 1, 1),
//        Category("cat2", 2, 2),
//        Category("cat3", 3, 3),
//        Category("cat4", 4, 4)
//    )
//
//    /**
//     * Test budgets of first budget in [availableMonths].
//     */
//    private val budgetsOfMonth0 = emptyList<Budget>()
//
//    /**
//     * Test budgets of second budget in [availableMonths].
//     */
//    private val budgetsOfMonth1 = listOf(
//        Budget(1, availableMonths[1], 123, 1),
//        Budget(4, availableMonths[1], 345, 3)
//    )
//
//    /**
//     * Test budgets of third budget in [availableMonths].
//     */
//    private val budgetsOfMonth2 = listOf(
//        Budget(1, availableMonths[2], 123, 1),
//        Budget(2, availableMonths[2], 234, 2),
//        Budget(3, availableMonths[2], 345, 4),
//        Budget(4, availableMonths[2], 345, 3)
//    )
//
//    @ExperimentalCoroutinesApi
//    @Before
//    override fun setup() {
//        super.setup()
//
//        // Stub getMonth()
//        `when`(settingRepositoryMock.getMonth()).thenReturn(flow { emit(month) })
//
//        // Stub availableMonths
//        `when`(settingRepositoryMock.availableMonths).thenReturn(flow { emit(availableMonths) })
//
//        // Stub getAllCategories()
//        `when`(categoryRepositoryMock.getAllCategories()).thenReturn(flow { emit(categories) })
//
//        // Stub getBudgetsByMonth(availableMonths[0])
//        `when`(budgetRepositoryMock.getBudgetsByMonth(availableMonths[0])).thenReturn(flow {
//            emit(
//                budgetsOfMonth0
//            )
//        })
//
//        // Stub getBudgetsByMonth(availableMonths[1])
//        `when`(budgetRepositoryMock.getBudgetsByMonth(availableMonths[1])).thenReturn(flow {
//            emit(
//                budgetsOfMonth1
//            )
//        })
//
//        // Stub getBudgetsByMonth(availableMonths[2])
//        `when`(budgetRepositoryMock.getBudgetsByMonth(availableMonths[2])).thenReturn(flow {
//            emit(
//                budgetsOfMonth2
//            )
//        })
//
//        // Create ViewModel instance
//        viewModel = SetMonthViewModel(
//            settingRepositoryMock, categoryRepositoryMock, budgetRepositoryMock
//        )
//
//    }
//
//    @Test
//    fun sets_selectable_months_correctly() {
//
//        assertThat(viewModel.selectableMonths.value).isEqualTo(availableMonths)
//
//    }
//
//    @Test
//    fun adds_missing_budgets_on_start_up_correctly(): Unit = runBlocking {
//
//        // Check budgets for first month inserted
//        verify(budgetRepositoryMock).addBudgets(
//            Budget(1, availableMonths[0], 0),
//            Budget(2, availableMonths[0], 0),
//            Budget(3, availableMonths[0], 0),
//            Budget(4, availableMonths[0], 0)
//        )
//
//        // Check budgets for second month inserted
//        verify(budgetRepositoryMock).addBudgets(
//            Budget(2, availableMonths[1], 0),
//            Budget(3, availableMonths[1], 0)
//        )
//
//    }
//
//    @Test
//    fun sets_month_correctly(): Unit = runBlocking {
//
//        // Define index of month to set
//        val index = 1
//
//        // Set month
//        viewModel.setMonth(index)
//
//        // Check correct month has been set
//        verify(settingRepositoryMock).setMonth(availableMonths[index])
//
//    }
//
//    @Test
//    fun gets_index_of_month_correctly(): Unit = runBlocking {
//
//        // Check if returns correct index
//        assertThat(viewModel.getIndexOfMonth()).isEqualTo(availableMonths.indexOf(month))
//
//    }


}