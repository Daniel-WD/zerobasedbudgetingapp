package com.titaniel.zerobasedbudgetingapp.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.titaniel.zerobasedbudgetingapp.database.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.entities.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface BudgetDao {

    @Insert(onConflict = REPLACE)
    suspend fun add(vararg budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Query("SELECT * FROM budget WHERE id = :id")
    fun getBudgetById(id: Long) : Flow<Budget>

    @Query("SELECT * FROM budget WHERE categoryName = :categoryName")
    fun getBudgetsByCategory(categoryName: String): Flow<List<Budget>>

    @Query("SELECT * FROM budget WHERE month = :month")
    fun getBudgetsByMonth(month: LocalDate): Flow<List<Budget>>

    @Query("SELECT * FROM budget")
    fun getAll(): Flow<List<Budget>>

}