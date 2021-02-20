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
     * Add [budgets]
     */
    @Insert(onConflict = REPLACE)
    suspend fun add(vararg budgets: Budget)

    /**
     * Update [budgets]
     */
    @Update
    suspend fun update(vararg budgets: Budget)

    /**
     * Get budget with [id]
     */
    @Query("SELECT * FROM budget WHERE id = :id")
    fun getBudgetById(id: Long): Flow<Budget>

    /**
     * Get budgets with [month]
     */
    @Query("SELECT * FROM budget WHERE month = :month")
    fun getBudgetsByMonth(month: LocalDate): Flow<List<Budget>>

}