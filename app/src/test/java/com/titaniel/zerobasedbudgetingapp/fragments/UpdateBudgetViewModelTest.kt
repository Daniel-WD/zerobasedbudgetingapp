package com.titaniel.zerobasedbudgetingapp.fragments

import androidx.lifecycle.SavedStateHandle
import com.jraska.livedata.test
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import java.time.YearMonth
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class UpdateBudgetViewModelTest : CoroutinesAndLiveDataTest() {

    /**
     * SavedStateHandle spy
     */
    @Spy
    private lateinit var savedStateHandleSpy: SavedStateHandle

    /**
     * BudgetRepository mock
     */
    @Mock
    private lateinit var budgetRepositoryMock: BudgetRepository

    /**
     * UpdateBudgetViewModel to test
     */
    private lateinit var updateBudgetViewModel: UpdateBudgetViewModel

    /**
     * Budget id
     */
    private val budgetId = 1029L

    /**
     * Test budgetWithCategory
     */
    private val budgetWithCategory =
        BudgetWithCategory(Budget(1, YearMonth.now(), 234), Category("cat", 0, 2, 1))

    @ExperimentalCoroutinesApi
    @Before
    override fun setup() {
        super.setup()

        // Set budget id
        savedStateHandleSpy.set(UpdateBudgetFragment.BUDGET_ID_KEY, budgetId)

        // Stub getBudgetById
        `when`(budgetRepositoryMock.getBudgetWithCategoryById(budgetId)).thenReturn(flow {
            emit(
                budgetWithCategory
            )
        })

        // Create ViewModel instance
        updateBudgetViewModel = UpdateBudgetViewModel(
            savedStateHandleSpy,
            budgetRepositoryMock
        )

    }

    @Test
    fun performs_update_budget_correctly(): Unit = runBlocking {

        // New budgeted value
        val newBudgeted = 42L

        // Wait for budget to be present
        updateBudgetViewModel.budgetWithCategory.test().awaitValue(1000, TimeUnit.SECONDS)

        // Call updateBudget()
        updateBudgetViewModel.updateBudget(newBudgeted)

        // Expected budget
        val expectedBudgetToUpdate = budgetWithCategory.budget.let {
            it.budgeted = newBudgeted
            it
        }

        // Verify updateBudgets() called
        verify(budgetRepositoryMock).updateBudgets(expectedBudgetToUpdate)

    }


}