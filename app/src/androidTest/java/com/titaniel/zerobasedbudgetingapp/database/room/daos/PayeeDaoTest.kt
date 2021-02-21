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

class PayeeDaoTest {

    // Database
    private lateinit var database: Database

    // Category dao
    private lateinit var categoryDao: CategoryDao

    // Example budgets
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
            // Check that all expected elements are in database
            assertThat(categoryDao.getAll().first()).isEqualTo(listOf(category1, category2, category3, category4))
        }
    }

}