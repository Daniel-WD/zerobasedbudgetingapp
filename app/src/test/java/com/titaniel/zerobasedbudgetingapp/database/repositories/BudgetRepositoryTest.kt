package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.BudgetDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate

@RunWith(MockitoJUnitRunner::class)
class BudgetRepositoryTest {

    /**
     * BudgetDao mock
     */
    @Mock
    private lateinit var budgetDaoMock: BudgetDao

    /**
     * BudgetRepository to test
     */
    private lateinit var budgetRepository: BudgetRepository

    /**
     * Example budget mock
     */
    @Mock
    private lateinit var budgetMock: Budget

    @Before
    fun setup() {
        budgetRepository = BudgetRepository(budgetDaoMock)
    }

    @Test
    fun performs_add_budgets_correctly(): Unit = runBlocking {
        // Add budget
        budgetRepository.addBudgets(budgetMock)

        // Verify add budget on dao
        verify(budgetDaoMock).add(budgetMock)
    }

    @Test
    fun performs_update_budgets_correctly(): Unit = runBlocking {
        // Update budget
        budgetRepository.updateBudgets(budgetMock)

        // Verify update budget on dao
        verify(budgetDaoMock).update(budgetMock)
    }

    @Test
    fun performs_get_budgets_by_id_correctly() {
        // Set id
        val id = 2L

        // Get budget by id
        budgetRepository.getBudgetById(id)

        // Verify get budget by id on dao
        verify(budgetDaoMock).getById(id)
    }

    @Test
    fun performs_get_budgets_by_month_correctly() {
        // Set id
        val month = LocalDate.now()

        // Get budgets by month
        budgetRepository.getBudgetsByMonth(month)

        // Verify get budgets by month on dao
        verify(budgetDaoMock).getByMonth(month)
    }
    
    @Test
    fun performs_get_all_budgets_correctly() {

        // Get all budgets
        budgetRepository.getAllBudgets()

        // Verify get budgets on dao
        verify(budgetDaoMock).getAll()

    }

}