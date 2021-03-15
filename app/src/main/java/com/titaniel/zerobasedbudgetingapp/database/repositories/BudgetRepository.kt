package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.BudgetDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
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
     * Add [budgets] to database
     */
    suspend fun addBudgets(vararg budgets: Budget) {
        budgetDao.add(*budgets)
    }

    /**
     * Updates [budgets] in database
     */
    suspend fun updateBudgets(vararg budgets: Budget) {
        budgetDao.update(*budgets)
    }

    /**
     * Delete [budgets] in database
     */
    suspend fun deleteBudgets(vararg budgets: Budget) {
        budgetDao.delete(*budgets)
    }

    /**
     * Get all budgets
     */
    fun getAllBudgets(): Flow<List<Budget>> {
        return budgetDao.getAll()
    }

    /**
     * Get budget with [id]
     */
    fun getBudgetById(id: Long): Flow<Budget> {
        return budgetDao.getById(id)
    }

    /**
     * Get budgets with [month]
     */
    fun getBudgetsByMonth(month: LocalDate): Flow<List<Budget>> {
        return budgetDao.getByMonth(month)
    }

    /**
     * Get all BudgetWithCategory
     */
    fun getAllBudgetsWithCategory(): Flow<List<BudgetWithCategory>> {
        return budgetDao.getAllBudgetsWithCategory()
    }

    /**
     * Get BudgetWithCategory with [month]
     */
    fun getBudgetsWithCategoryByMonth(month: LocalDate): Flow<List<BudgetWithCategory>> {
        return budgetDao.getBudgetsWithCategoryByMonth(month)
    }

    /**
     * Get BudgetWithCategory with [budgetId]
     */
    fun getBudgetWithCategoryById(budgetId: Long): Flow<BudgetWithCategory> {
        return budgetDao.getBudgetWithCategoryById(budgetId)
    }

}