package com.titaniel.zerobasedbudgetingapp.activities

import androidx.lifecycle.SavedStateHandle
import com.jraska.livedata.test
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
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
     * Test budget
     */
    private val budget = Budget(1, YearMonth.now(), 234)

    @ExperimentalCoroutinesApi
    @Before
    override fun setup() {
        super.setup()

        // Set budget id
        savedStateHandleSpy.set(UpdateBudgetFragment.BUDGET_ID_KEY, budgetId)

        // Stub getBudgetById
        `when`(budgetRepositoryMock.getBudgetById(budgetId)).thenReturn(flow { emit(budget) })

        // Create ViewModel instance
        updateBudgetViewModel = UpdateBudgetViewModel(
            savedStateHandleSpy,
            budgetRepositoryMock
        )

    }

    @Test
    fun performs_update_budget_correctly() = runBlocking {

        // New budgeted value
        val newBudgeted = 42L

        // Wait for budget to be present
        updateBudgetViewModel.budgetWithCategory.test().awaitValue(1000, TimeUnit.SECONDS)

        // Call updateBudget()
        updateBudgetViewModel.updateBudget(newBudgeted)

        // Expected budget
        val expectedBudgetToUpdate = budget.copy(budgeted = newBudgeted)

        // Verify updateBudgets() called
        verify(budgetRepositoryMock).updateBudgets(expectedBudgetToUpdate)

    }


}