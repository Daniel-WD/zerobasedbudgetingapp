package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.BudgetDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to interact with budgeting data
 */
@Singleton
class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {

    /**
     * Add [budgets] to database
     */
    suspend fun addBudgets(vararg budgets: Budget): Array<Long> {
        return budgetDao.add(*budgets)
    }

    /**
     * Updates [budgets] in database
     */
    suspend fun updateBudgets(vararg budgets: Budget): Int {
        return budgetDao.update(*budgets)
    }

    /**
     * Delete [budgets] in database
     */
    suspend fun deleteBudgets(vararg budgets: Budget): Int {
        return budgetDao.delete(*budgets)
    }

    /**
     * Get all budgets
     */
    fun getAllBudgets(): Flow<List<Budget>> {
        return budgetDao.getAll()
    }

    /**
     * Get all budgets until [month]
     */
    fun getBudgetsUntilMonth(month: YearMonth): Flow<List<Budget>> {
        return budgetDao.getUntilMonth(month)
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
    fun getBudgetsByMonth(month: YearMonth): Flow<List<Budget>> {
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
    fun getBudgetsWithCategoryByMonth(month: YearMonth): Flow<List<BudgetWithCategory>> {
        return budgetDao.getBudgetsWithCategoryByMonth(month)
    }

    /**
     * Get BudgetWithCategory with [budgetId]
     */
    fun getBudgetWithCategoryById(budgetId: Long): Flow<BudgetWithCategory> {
        return budgetDao.getBudgetWithCategoryById(budgetId)
    }

}