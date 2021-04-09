package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.CategoryDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CategoryRepositoryTest {

    /**
     * CategoryDao mock
     */
    @Mock
    private lateinit var categoryDaoMock: CategoryDao

    /**
     * CategoryRepository to test
     */
    private lateinit var categoryRepository: CategoryRepository

    /**
     * Example categories
     */
    @Mock
    private lateinit var category1: Category

    @Before
    fun setup() {
        categoryRepository = CategoryRepository(categoryDaoMock)
    }

    @Test
    fun performs_get_all_categories_correctly() {
        // Get all categories
        categoryRepository.getAllCategories()

        // Verify get all categories on dao
        verify(categoryDaoMock).getAll()
    }

    @Test
    fun performs_get_transactions_of_categories_correctly() {
        // Get all TransactionsOfCategories
        categoryRepository.getTransactionsOfCategories()

        // Verify get all TransactionsOfCategories on dao
        verify(categoryDaoMock).getTransactionsOfCategories()
    }

    @Test
    fun performs_get_budgets_of_categories_correctly() {
        // Get all BudgetsOfCategories
        categoryRepository.getBudgetsOfCategories()

        // Verify get all BudgetsOfCategories on dao
        verify(categoryDaoMock).getBudgetsOfCategories()
    }

    @Test
    fun performs_add_categories_correctly(): Unit = runBlocking {

        // Call method
        categoryRepository.addCategories(category1)

        // Verify correct dao method called
        verify(categoryDaoMock).add(category1)

    }

    @Test
    fun performs_delete_categories_correctly(): Unit = runBlocking {

        // Call method
        categoryRepository.deleteCategories(category1)

        // Verify correct dao method called
        verify(categoryDaoMock).delete(category1)

    }

    @Test
    fun performs_update_categories_correctly(): Unit = runBlocking {

        // Call method
        categoryRepository.updateCategories(category1)

        // Verify correct dao method called
        verify(categoryDaoMock).update(category1)

    }

    @Test
    fun performs_get_category_by_id_correctly() {

        val id = 213423L

        // Call method
        categoryRepository.getCategoryById(id)

        // Verify correct dao method called
        verify(categoryDaoMock).getById(id)

    }

}