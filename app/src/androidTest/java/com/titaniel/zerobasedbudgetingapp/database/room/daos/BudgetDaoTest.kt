package com.titaniel.zerobasedbudgetingapp.database.room.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class BudgetDaoTest {

    /**
     * Database
     */
    private lateinit var database: Database

    /**
     * BudgetDao to test
     */
    private lateinit var budgetDao: BudgetDao

    /**
     * Example budgets
     */
    private val budget1 = Budget(1, YearMonth.of(1999, 5), 100, 1)
    private val budget2 = Budget(2, YearMonth.of(1999, 5), 100, 2)
    private val budget3 = Budget(3, YearMonth.of(2000, 12), 100, 3)

    /**
     * Categories to meet foreign key constraints.
     */
    private val category1 = Category("name", 0, 1)
    private val category2 = Category("name", 1, 2)
    private val category3 = Category("name", 2, 3)

    @Before
    fun setup(): Unit = runBlocking {

        // Create database
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            Database::class.java
        ).build()

        // Add categories
        database.categoryDao().add(category1, category2, category3)

        // Set budget dao
        budgetDao = database.budgetDao()

        // Add example budgets
        budgetDao.add(budget1, budget2, budget3)

    }

    @After
    fun tearDown() {
        // Close database
        database.close()
    }

    @Test
    fun gets_budgets_correctly(): Unit = runBlocking {
        assertThat(budgetDao.getAll().first()).isEqualTo(listOf(budget1, budget2, budget3))
    }

    @Test
    fun updates_budgets_correctly(): Unit = runBlocking {

        // Change budget1
        budget1.budgeted = 5000

        // Update budget1
        budgetDao.update(budget1)

        assertThat(budgetDao.getAll().first()).isEqualTo(listOf(budget1, budget2, budget3))
    }

    @Test
    fun gets_budget_by_id_correctly(): Unit = runBlocking {
        assertThat(budgetDao.getById(1).first()).isEqualTo(budget1)
        assertThat(budgetDao.getById(2).first()).isEqualTo(budget2)
        assertThat(budgetDao.getById(3).first()).isEqualTo(budget3)
    }

    @Test
    fun gets_budgets_by_month_correctly(): Unit = runBlocking {
        assertThat(budgetDao.getByMonth(YearMonth.of(1999, 5)).first())
            .isEqualTo(listOf(budget1, budget2))
        assertThat(budgetDao.getByMonth(YearMonth.of(2000, 12)).first())
            .isEqualTo(listOf(budget3))
    }

    @Test(expected = SQLiteConstraintException::class)
    fun throws_exception_when_no_category_for_budget_exists(): Unit = runBlocking {

        // Add budget that has no category
        budgetDao.add(Budget(4, YearMonth.of(2000, 12), 100, 4))

    }

    @Test
    fun budget_gets_deleted_when_its_category_gets_deleted(): Unit = runBlocking {

        // Delete category
        database.categoryDao().delete(category2)

        // Check if respective budget is missing
        assertThat(budgetDao.getAll().first()).isEqualTo(listOf(budget1, budget3))

    }

}