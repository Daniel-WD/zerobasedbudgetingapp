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

/**
 * Data access object for everything concerning categories
 */
@Dao
interface CategoryDao {

    /**
     * Add [category]
     */
    @Insert(onConflict = REPLACE)
    suspend fun add(category: Category)

    /**
     * Get all categories
     */
    @Query("SELECT * FROM category")
    fun getAll(): Flow<List<Category>>

    /**
     * Get all TransactionsOfCategories
     */
    @Transaction
    @Query("SELECT * FROM category")
    fun getTransactionsOfCategories(): Flow<List<TransactionsOfCategory>>

    /**
     * Get all BudgetsOfCategories
     */
    @Transaction
    @Query("SELECT * FROM category")
    fun getBudgetsOfCategories(): Flow<List<BudgetsOfCategory>>

}