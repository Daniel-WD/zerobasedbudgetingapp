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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

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
    private val category1 = Category("cat1", 0, 1)
    private val category2 = Category("cat2", 1, 2)
    private val category3 = Category("cat3", 2, 3)
    private val category4 = Category("cat4", 3, 4)

    @Before
    fun setup(): Unit = runBlocking {

        // Create database
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            Database::class.java
        ).build()

        // Get category dao
        categoryDao = database.categoryDao()

        // Add example budgets
        categoryDao.add(category1, category2, category3, category4)
    }

    @After
    fun tearDown() {
        // Close database
        database.close()
    }

    @Test
    fun gets_categories_correctly(): Unit = runBlocking {
        assertThat(categoryDao.getAll().first()).isEqualTo(
            listOf(
                category1,
                category2,
                category3,
                category4
            )
        )
    }

    @Test
    fun gets_transactions_of_categories_correctly(): Unit = runBlocking {

        // Create example transactions
        val transaction1 = Transaction(1, 0, 1, "", LocalDate.now(), 1)
        val transaction2 = Transaction(2, 0, 2, "", LocalDate.now(), 2)
        val transaction3 = Transaction(3, 0, 2, "", LocalDate.now(), 3)
        val transaction4 = Transaction(4, 0, 1, "", LocalDate.now(), 4)
        val transaction5 = Transaction(5, 0, 3, "", LocalDate.now(), 5)
        val transaction6 = Transaction(6, 0, 5, "", LocalDate.now(), 6)

        // Define expected TransactionsOfCategories
        val transactionsOfCategory1 =
            TransactionsOfCategory(category1, listOf(transaction1, transaction4))
        val transactionsOfCategory2 =
            TransactionsOfCategory(category2, listOf(transaction2, transaction3))
        val transactionsOfCategory3 = TransactionsOfCategory(category3, listOf(transaction5))
        val transactionsOfCategory4 = TransactionsOfCategory(category4, emptyList())

        // Add transactions to database
        database.transactionDao()
            .add(transaction1, transaction2, transaction3, transaction4, transaction5, transaction6)

        assertThat(categoryDao.getTransactionsOfCategories().first()).isEqualTo(
            listOf(
                transactionsOfCategory1,
                transactionsOfCategory2,
                transactionsOfCategory3,
                transactionsOfCategory4
            )
        )
    }

    @Test
    fun gets_budgets_of_categories_correctly(): Unit = runBlocking {

        // Create example budgets
        val budget1 = Budget(1, YearMonth.now(), 1, 1)
        val budget2 = Budget(2, YearMonth.now(), 2, 2)
        val budget3 = Budget(2, YearMonth.now(), 3, 3)
        val budget4 = Budget(1, YearMonth.now(), 4, 4)
        val budget5 = Budget(3, YearMonth.now(), 5, 5)
        val budget6 = Budget(3, YearMonth.now(), 6, 6)

        // Define expected BudgetsOfCategories
        val budgetsOfCategory1 = BudgetsOfCategory(category1, listOf(budget1, budget4))
        val budgetsOfCategory2 = BudgetsOfCategory(category2, listOf(budget2, budget3))
        val budgetsOfCategory3 = BudgetsOfCategory(category3, listOf(budget5, budget6))
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

    @Test
    fun gets_category_by_id_correctly(): Unit = runBlocking {
        assertThat(categoryDao.getById(1).first()).isEqualTo(category1)
        assertThat(categoryDao.getById(2).first()).isEqualTo(category2)
        assertThat(categoryDao.getById(3).first()).isEqualTo(category3)
        assertThat(categoryDao.getById(4).first()).isEqualTo(category4)
        assertThat(categoryDao.getById(5).first()).isEqualTo(null)
    }

    @Test
    fun updates_categories_correctly(): Unit = runBlocking {

        // Change categories
        category4.name = "aölskjfalskdföa"
        category2.index = 10
        category3.index = 1

        // Update changed categories in db
        categoryDao.update(category2, category3, category4)

        assertThat(categoryDao.getById(1).first()).isEqualTo(category1)
        assertThat(categoryDao.getById(2).first()).isEqualTo(category2)
        assertThat(categoryDao.getById(3).first()).isEqualTo(category3)

    }

    @Test
    fun deletes_categories_correctly(): Unit = runBlocking {

        // Delete cats
        categoryDao.delete(category4, category1)

        assertThat(categoryDao.getById(1).first()).isEqualTo(null)
        assertThat(categoryDao.getById(4).first()).isEqualTo(null)

    }

}