package com.titaniel.zerobasedbudgetingapp.api

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import org.junit.After
import org.junit.Before
import org.junit.Test

class DatabaseModuleTest {

    // DatabaseModule
    private val databaseModule = DatabaseModule()

    // Database
    private lateinit var database: Database

    @Before
    fun setup() {

        // Create database
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().targetContext,
                Database::class.java
        ).build()
    }

    @After
    fun tearDown() {
        // Close database
        database.close()
    }

    @Test
    fun provides_transaction_dao_correctly() {
        assertThat(databaseModule.provideTransactionDao(database)).isEqualTo(database.transactionDao())
    }

    @Test
    fun provides_payee_dao_correctly() {
        assertThat(databaseModule.providePayeeDao(database)).isEqualTo(database.payeeDao())
    }

    @Test
    fun provides_category_dao_correctly() {
        assertThat(databaseModule.provideCategoryDao(database)).isEqualTo(database.categoryDao())
    }

    @Test
    fun provides_budget_dao_correctly() {
        assertThat(databaseModule.provideBudgetDao(database)).isEqualTo(database.budgetDao())
    }


}