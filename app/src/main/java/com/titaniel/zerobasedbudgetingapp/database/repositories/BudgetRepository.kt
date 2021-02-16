package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.BudgetDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class BudgetRepository @Inject constructor(
    private val budgetDao: BudgetDao
) {

    suspend fun addBudgets(vararg budget: Budget) {
        budgetDao.add(*budget)
    }

    suspend fun updateBudget(budget: Budget) {
        budgetDao.update(budget)
    }

    fun getBudgetById(id: Long): Flow<Budget> {
        return budgetDao.getBudgetById(id)
    }

    fun getBudgetsByCategory(categoryName: String): Flow<List<Budget>> {
        return budgetDao.getBudgetsByCategory(categoryName)
    }

    fun getBudgetsByMonth(month: LocalDate): Flow<List<Budget>> {
        return budgetDao.getBudgetsByMonth(month)
    }

    fun getAllBudgetes(): Flow<List<Budget>> {
        return budgetDao.getAll()
    }

}