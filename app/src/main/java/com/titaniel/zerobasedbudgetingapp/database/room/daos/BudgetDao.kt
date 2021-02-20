package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data access object for everything concerning budgets
 */
@Dao
interface BudgetDao {

    /**
     * Add budget
     * @param budgets Budgets to add
     */
    @Insert(onConflict = REPLACE)
    suspend fun add(vararg budgets: Budget)

    /**
     * Update budget
     * @param budget Budget to update
     */
    @Update
    suspend fun update(budget: Budget)

    /**
     * Get budget by id
     * @param id Budget id
     */
    @Query("SELECT * FROM budget WHERE id = :id")
    fun getBudgetById(id: Long) : Flow<Budget>

    /**
     * Get budgets by category
     * @param categoryName Name of category
     */
    @Query("SELECT * FROM budget WHERE categoryName = :categoryName")
    fun getBudgetsByCategory(categoryName: String): Flow<List<Budget>>

    /**
     * Get budgets by month
     * @param month Month
     */
    @Query("SELECT * FROM budget WHERE month = :month")
    fun getBudgetsByMonth(month: LocalDate): Flow<List<Budget>>

    /**
     * Get all budgets
     */
    @Query("SELECT * FROM budget")
    fun getAll(): Flow<List<Budget>>

}