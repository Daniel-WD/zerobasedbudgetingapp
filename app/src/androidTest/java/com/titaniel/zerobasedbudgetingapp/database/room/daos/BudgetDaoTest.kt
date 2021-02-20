package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class BudgetDaoTest {

    // Database
    lateinit var database: Database

    // Example budgets
    private val budget1 = Budget("cat1", LocalDate.of(1999, 5, 1), 100)
    private val budget2 = Budget("cat99", LocalDate.of(1999, 5, 1), 100)
    private val budget3 = Budget("cat2", LocalDate.of(2000, 12, 1), 100)

    @Before
    fun setup() {

        // Create database
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                Database::class.java
        ).build()

        GlobalScope.launch {
            // Add example budgets
            database.budgetDao().add(budget1, budget2, budget3)
        }
    }

    @After
    fun tearDown() {
        // Close database
        database.close()
    }

    @Test
    fun adds_gets_budgets_correctly() {
        GlobalScope.launch {
            // Check that all expected elements are in database
            assertThat(database.budgetDao().getAll().first()).isEqualTo(listOf(budget1, budget2, budget3))
        }
    }

    @Test
    fun updates_budgets_correctly() {
        GlobalScope.launch {

            // Change budget1
            budget1.budgeted = 5000
            budget1.categoryName = "cat10"

            // Update budget1
            database.budgetDao().update(budget1)

            assertThat(database.budgetDao().getAll().first()).isEqualTo(listOf(budget1, budget2, budget3))
        }
    }

    @Test
    fun gets_budget_by_id_correctly() {
        GlobalScope.launch {
            assertThat(database.budgetDao().getBudgetById(1).first()).isEqualTo(budget1)
            assertThat(database.budgetDao().getBudgetById(2).first()).isEqualTo(budget2)
            assertThat(database.budgetDao().getBudgetById(3).first()).isEqualTo(budget3)
        }
    }

    @Test
    fun gets_budgets_by_month_correctly() {
        GlobalScope.launch {
            assertThat(database.budgetDao().getBudgetsByMonth(LocalDate.of(1999, 5, 1)).first())
                    .isEqualTo(listOf(budget1, budget2))
            assertThat(database.budgetDao().getBudgetsByMonth(LocalDate.of(2000, 12, 1)).first())
                    .isEqualTo(listOf(budget3))
        }
    }

}