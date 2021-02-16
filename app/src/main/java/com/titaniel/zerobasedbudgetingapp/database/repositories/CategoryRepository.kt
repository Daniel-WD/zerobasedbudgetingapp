package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.CategoryDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetsOfCategory
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionsOfCategory
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