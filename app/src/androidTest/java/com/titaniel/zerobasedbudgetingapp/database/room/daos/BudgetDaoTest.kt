package com.titaniel.zerobasedbudgetingapp.database.room.daos

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Group
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
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
    private val budget4 = Budget(3, YearMonth.of(2000, 11), 100, 4)

    /**
     * Categories to meet foreign key constraints.
     */
    private val category1 = Category("name", 1, 0, 1)
    private val category2 = Category("name", 1, 1, 2)
    private val category3 = Category("name", 1, 2, 3)

    /**
     * Example group
     */
    private val group = Group("hello", 3, 1)

    /**
     * BudgetsWithCategory derived from example budgets, categories
     */
    private val budgetWithCategory1 = BudgetWithCategory(budget1, category1)
    private val budgetWithCategory2 = BudgetWithCategory(budget2, category2)
    private val budgetWithCategory3 = BudgetWithCategory(budget3, category3)
    private val budgetWithCategory4 = BudgetWithCategory(budget4, category3)

    @Before
    fun setup(): Unit = runBlocking {

        // Create database
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            Database::class.java
        ).build()

        // Add categories, group
        database.groupDao().add(group)
        database.categoryDao().add(category1, category2, category3)

        // Set budget dao
        budgetDao = database.budgetDao()

        // Add example budgets
        budgetDao.add(budget1, budget2, budget3, budget4)

    }

    @After
    fun tearDown() {
        // Close database
        database.close()
    }

    @Test
    fun gets_budgets_correctly(): Unit = runBlocking {
        assertThat(budgetDao.getAll().first()).isEqualTo(listOf(budget1, budget2, budget3, budget4))
    }

    @Test
    fun updates_budgets_correctly(): Unit = runBlocking {

        // Change budget1
        budget1.budgeted = 5000

        // Update budget1
        budgetDao.update(budget1)

        assertThat(budgetDao.getAll().first()).isEqualTo(listOf(budget1, budget2, budget3, budget4))
    }

    @Test
    fun gets_budget_by_id_correctly(): Unit = runBlocking {
        assertThat(budgetDao.getById(1).first()).isEqualTo(budget1)
        assertThat(budgetDao.getById(2).first()).isEqualTo(budget2)
        assertThat(budgetDao.getById(3).first()).isEqualTo(budget3)
        assertThat(budgetDao.getById(4).first()).isEqualTo(budget4)
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
        assertThat(budgetDao.getAll().first()).isEqualTo(listOf(budget1, budget3, budget4))

    }

    @Test
    fun gets_budget_with_category_by_id_correctly(): Unit = runBlocking {
        assertThat(budgetDao.getBudgetWithCategoryById(1).first()).isEqualTo(budgetWithCategory1)
        assertThat(budgetDao.getBudgetWithCategoryById(2).first()).isEqualTo(budgetWithCategory2)
        assertThat(budgetDao.getBudgetWithCategoryById(3).first()).isEqualTo(budgetWithCategory3)
    }

    @Test
    fun gets_budgets_with_category_by_month_correctly(): Unit = runBlocking {
        assertThat(budgetDao.getBudgetsWithCategoryByMonth(YearMonth.of(1999, 5)).first())
            .isEqualTo(
                listOf(
                    budgetWithCategory1, budgetWithCategory2
                )
            )
        assertThat(budgetDao.getBudgetsWithCategoryByMonth(YearMonth.of(2000, 12)).first())
            .isEqualTo(
                listOf(
                    budgetWithCategory3
                )
            )
        assertThat(budgetDao.getBudgetsWithCategoryByMonth(YearMonth.of(2010, 12)).first())
            .isEmpty()
    }

    @Test
    fun gets_all_budgets_with_category_correctly(): Unit = runBlocking {
        assertThat(budgetDao.getAllBudgetsWithCategory().first()).isEqualTo(
            listOf(
                budgetWithCategory1, budgetWithCategory2, budgetWithCategory3, budgetWithCategory4
            )
        )
    }

    @Test
    fun deletes_budgets_correctly(): Unit = runBlocking {

        // Delete budgets
        budgetDao.delete(budget2)

        assertThat(budgetDao.getById(2).first()).isEqualTo(null)
    }

    @Test
    fun gets_budgets_until_month_correctly(): Unit = runBlocking {

        assertThat(budgetDao.getUntilMonth(YearMonth.of(2000, 11)).first()).isEqualTo(
            listOf(
                budget1, budget2, budget4
            )
        )

    }

    @Test
    fun gets_budget_by_category_id_and_month_correctly(): Unit = runBlocking {

        // Gets correct budget
        assertThat(budgetDao.getByCategoryIdAndMonth(3, YearMonth.of(2000, 12)).first()).isEqualTo(
            budget3
        )

        // Returns null when no such item
        assertThat(budgetDao.getByCategoryIdAndMonth(4, YearMonth.of(2000, 12)).firstOrNull()).isEqualTo(
            null
        )

    }

}