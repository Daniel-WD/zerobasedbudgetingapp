package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetsOfCategory
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionsOfCategory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class CategoryDaoTest {

    /**
     * Database
     */
    private lateinit var database: Database

    /**
     * CategoryDao to test
     */
    private lateinit var categoryDao: CategoryDao

    /**
     * Example categories
     */
    private val category1 = Category("cat1")
    private val category2 = Category("cat2")
    private val category3 = Category("cat3")
    private val category4 = Category("cat4")

    @Before
    fun setup() {

        // Create database
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                Database::class.java
        ).build()

        // Get category dao
        categoryDao = database.categoryDao()

        GlobalScope.launch {
            // Add example budgets
            categoryDao.add(category1, category2, category3, category4)
        }
    }

    @After
    fun tearDown() {
        // Close database
        database.close()
    }

    @Test
    fun gets_categories_correctly() {
        GlobalScope.launch {
            assertThat(categoryDao.getAll().first()).isEqualTo(listOf(category1, category2, category3, category4))
        }
    }

    @Test
    fun gets_transactions_of_categories_correctly() {
        GlobalScope.launch {

            // Create example transactions
            val transaction1 = Transaction(1, "payee1", "cat1", "", LocalDate.now())
            val transaction2 = Transaction(2, "payee1", "cat2", "", LocalDate.now())
            val transaction3 = Transaction(3, "payee1", "cat2", "", LocalDate.now())
            val transaction4 = Transaction(4, "payee1", "cat1", "", LocalDate.now())
            val transaction5 = Transaction(5, "payee1", "cat3", "", LocalDate.now())
            val transaction6 = Transaction(6, "payee1", "cat5", "", LocalDate.now())

            // Define expected TransactionsOfCategories
            val transactionsOfCategory1 = TransactionsOfCategory(category1, listOf(transaction1, transaction4))
            val transactionsOfCategory2 = TransactionsOfCategory(category2, listOf(transaction2, transaction3))
            val transactionsOfCategory3 = TransactionsOfCategory(category3, listOf(transaction5))
            val transactionsOfCategory4 = TransactionsOfCategory(category4, emptyList())

            // Add transactions to database
            database.transactionDao().add(transaction1, transaction2, transaction3, transaction4, transaction5, transaction6)

            assertThat(categoryDao.getTransactionsOfCategories().first()).isEqualTo(
                    listOf(
                            transactionsOfCategory1,
                            transactionsOfCategory2,
                            transactionsOfCategory3,
                            transactionsOfCategory4
                    )
            )
        }
    }

    @Test
    fun gets_budgets_of_categories_correctly() {
        GlobalScope.launch {

            // Create example budgets
            val budget1 = Budget("cat1", LocalDate.now(), 1)
            val budget2 = Budget("cat2", LocalDate.now(), 2)
            val budget3 = Budget("cat2", LocalDate.now(), 3)
            val budget4 = Budget("cat1", LocalDate.now(), 4)
            val budget5 = Budget("cat3", LocalDate.now(), 5)
            val budget6 = Budget("cat5", LocalDate.now(), 6)

            // Define expected BudgetsOfCategories
            val budgetsOfCategory1 = BudgetsOfCategory(category1, listOf(budget1, budget4))
            val budgetsOfCategory2 = BudgetsOfCategory(category2, listOf(budget2, budget3))
            val budgetsOfCategory3 = BudgetsOfCategory(category3, listOf(budget5))
            val budgetsOfCategory4 = BudgetsOfCategory(category4, emptyList())

            // Add transactions to database
            database.budgetDao().add(budget1, budget2, budget3, budget4, budget5, budget6)

            assertThat(categoryDao.getBudgetsOfCategories().first()).isEqualTo(
                    listOf(
                            budgetsOfCategory1,
                            budgetsOfCategory2,
                            budgetsOfCategory3,
                            budgetsOfCategory4
                    )
            )
        }
    }

}