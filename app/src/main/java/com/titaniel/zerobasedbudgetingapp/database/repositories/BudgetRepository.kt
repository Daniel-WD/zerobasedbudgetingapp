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
     * Add [budgets] to database
     */
    suspend fun addBudgets(vararg budgets: Budget) {
        budgetDao.add(*budgets)
    }

    /**
     * Updates [budget] in database
     */
    suspend fun updateBudget(budget: Budget) {
        budgetDao.update(budget)
    }

    /**
     * Get budget with [id]
     */
    fun getBudgetById(id: Long): Flow<Budget> {
        return budgetDao.getBudgetById(id)
    }

    /**
     * Get budgets with [month]
     */
    fun getBudgetsByMonth(month: LocalDate): Flow<List<Budget>> {
        return budgetDao.getBudgetsByMonth(month)
    }

}