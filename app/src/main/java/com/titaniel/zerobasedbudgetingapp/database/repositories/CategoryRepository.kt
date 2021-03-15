package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.CategoryDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetsOfCategory
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionsOfCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository to interact with category data
 */
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {

    /**
     * Add [categories]
     */
    suspend fun addCategories(vararg categories: Category) {
        categoryDao.add(*categories)
    }

    /**
     * Delete [categories]
     */
    suspend fun deleteCategories(vararg categories: Category) {
        categoryDao.delete(*categories)
    }

    /**
     * Update [categories]
     */
    suspend fun updateCategories(vararg categories: Category) {
        categoryDao.update(*categories)
    }

    /**
     * Get all categories
     */
    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAll()
    }

    /**
     * Get category by [categoryId]
     */
    fun getCategoryById(categoryId: Long): Flow<Category> {
        return categoryDao.getById(categoryId)
    }

    /**
     * Get all TransactionsOfCategory relations
     */
    fun getTransactionsOfCategories(): Flow<List<TransactionsOfCategory>> {
        return categoryDao.getTransactionsOfCategories()
    }

    /**
     * Get all BudgetsOfCategory relations
     */
    fun getBudgetsOfCategories(): Flow<List<BudgetsOfCategory>> {
        return categoryDao.getBudgetsOfCategories()
    }

}