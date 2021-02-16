package com.titaniel.zerobasedbudgetingapp.repositories

import com.titaniel.zerobasedbudgetingapp.database.daos.CategoryDao
import com.titaniel.zerobasedbudgetingapp.database.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.relations.BudgetsOfCategory
import com.titaniel.zerobasedbudgetingapp.database.relations.TransactionsOfCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {

    suspend fun addCategory(category: Category) {
        categoryDao.add(category)
    }

    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAll()
    }

    fun getTransactionsOfCategories(): Flow<List<TransactionsOfCategory>> {
        return categoryDao.getTransactionsOfCategories()
    }

    fun getBudgetsOfCategories(): Flow<List<BudgetsOfCategory>> {
        return categoryDao.getBudgetsOfCategories()
    }

}