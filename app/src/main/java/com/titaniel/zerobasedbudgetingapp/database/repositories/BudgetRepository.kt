package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.BudgetDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Repository to interact with budgeting data
 */
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {

    /**
     * Add budgets to database
     * @param budgets Budgets to add
     */
    suspend fun addBudgets(vararg budgets: Budget) {
        budgetDao.add(*budgets)
    }

    /**
     * Updates budget in database
     * @param budget Budget to update
     */
    suspend fun updateBudget(budget: Budget) {
        budgetDao.update(budget)
    }

    /**
     * Get budget
     * @param id Id of budget to get
     */
    fun getBudgetById(id: Long): Flow<Budget> {
        return budgetDao.getBudgetById(id)
    }

    /**
     * Get budgets with specific category
     * @param categoryName Name of category
     */
    fun getBudgetsByCategory(categoryName: String): Flow<List<Budget>> {
        return budgetDao.getBudgetsByCategory(categoryName)
    }

    /**
     * Get budgets with specific month
     * @param month Month
     */
    fun getBudgetsByMonth(month: LocalDate): Flow<List<Budget>> {
        return budgetDao.getBudgetsByMonth(month)
    }

    /**
     * Get all budgets
     */
    fun getAllBudgets(): Flow<List<Budget>> {
        return budgetDao.getAll()
    }

}