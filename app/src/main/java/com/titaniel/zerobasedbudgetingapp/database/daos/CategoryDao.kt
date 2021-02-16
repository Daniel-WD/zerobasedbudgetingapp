package com.titaniel.zerobasedbudgetingapp.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.titaniel.zerobasedbudgetingapp.database.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.relations.BudgetsOfCategory
import com.titaniel.zerobasedbudgetingapp.database.relations.TransactionsOfCategory
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