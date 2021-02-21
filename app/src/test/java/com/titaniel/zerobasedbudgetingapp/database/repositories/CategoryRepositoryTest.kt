package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.CategoryDao
import com.titaniel.zerobasedbudgetingapp.database.room.daos.PayeeDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
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

}