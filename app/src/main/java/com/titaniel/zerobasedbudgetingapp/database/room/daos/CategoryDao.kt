package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetsOfCategory
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionsOfCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = REPLACE)
    suspend fun add(category: Category)

    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<Category>>

    @Transaction
    @Query("SELECT * FROM category")
    fun getTransactionsOfCategories(): Flow<List<TransactionsOfCategory>>

    @Transaction
    @Query("SELECT * FROM category")
    fun getBudgetsOfCategories(): Flow<List<BudgetsOfCategory>>

}