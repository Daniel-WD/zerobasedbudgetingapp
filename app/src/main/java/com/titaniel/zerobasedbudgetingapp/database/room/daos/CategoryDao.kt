package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
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
     * Add [categories]
     */
    @Insert(onConflict = REPLACE)
    suspend fun add(vararg categories: Category): Array<Long>

    /**
     * Update [categories]
     */
    @Update
    suspend fun update(vararg categories: Category): Int

    /**
     * Delete [categories]
     */
    @Delete
    suspend fun delete(vararg categories: Category): Int

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

    /**
     * Get category by [categoryId]
     */
    @Query("SELECT * FROM category WHERE category.id == :categoryId")
    fun getById(categoryId: Long): Flow<Category>

}