package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import kotlinx.coroutines.flow.Flow
import java.time.Year
import java.time.YearMonth

/**
 * Data access object for everything concerning budgets
 */
@Dao
interface BudgetDao {

    /**
     * Add [budgets]
     */
    @Insert(onConflict = REPLACE)
    suspend fun add(vararg budgets: Budget): Array<Long>

    /**
     * Update [budgets]
     */
    @Update
    suspend fun update(vararg budgets: Budget): Int

    /**
     * Delete [budgets]
     */
    @Delete
    suspend fun delete(vararg budgets: Budget): Int

    /**
     * Get budget with [id]
     */
    @Query("SELECT * FROM budget WHERE id = :id")
    fun getById(id: Long): Flow<Budget>

    /**
     * Get budgets with [month]
     */
    @Query("SELECT * FROM budget WHERE month = :month")
    fun getByMonth(month: YearMonth): Flow<List<Budget>>

    /**
     * Get budgets until [month]
     */
    @Query("SELECT * FROM budget WHERE month <= :month")
    fun getUntilMonth(month: YearMonth): Flow<List<Budget>>

    /**
     * Get all budgets
     */
    @Query("SELECT * FROM budget")
    fun getAll(): Flow<List<Budget>>

    /**
     * Get all BudgetWithCategory
     */
    @Transaction
    @Query("SELECT * FROM budget")
    fun getAllBudgetsWithCategory(): Flow<List<BudgetWithCategory>>

    /**
     * Get BudgetWithCategory with [month]
     */
    @Transaction
    @Query("SELECT * FROM budget WHERE month = :month")
    fun getBudgetsWithCategoryByMonth(month: YearMonth): Flow<List<BudgetWithCategory>>

    /**
     * Get BudgetWithCategory by [id]
     */
    @Transaction
    @Query("SELECT * FROM budget WHERE budget.id = :id")
    fun getBudgetWithCategoryById(id: Long): Flow<BudgetWithCategory>

}