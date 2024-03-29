package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class PayeeDaoTest {

    /**
     * Database
     */
    private lateinit var database: Database

    /**
     * PayeeDao to test
     */
    private lateinit var payeeDao: PayeeDao

    /**
     * Example payees
     */
    private val payee1 = Payee("payee1", 1)
    private val payee2 = Payee("payee2", 2)
    private val payee3 = Payee("payee3", 3)
    private val payee4 = Payee("payee4", 4)

    @Before
    fun setup(): Unit = runBlocking {

        // Create database
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            Database::class.java
        ).build()

        // Get payee dao
        payeeDao = database.payeeDao()

        // Add example budgets
        payeeDao.add(payee1, payee2, payee3, payee4)
    }

    @After
    fun tearDown() {
        // Close database
        database.close()
    }

    @Test
    fun gets_payees_correctly(): Unit = runBlocking {
        assertThat(payeeDao.getAll().first()).isEqualTo(listOf(payee1, payee2, payee3, payee4))
    }

    @Test
    fun gets_payee_by_id_correctly(): Unit = runBlocking {
        assertThat(payeeDao.getById(1).first()).isEqualTo(payee1)
        assertThat(payeeDao.getById(2).first()).isEqualTo(payee2)
        assertThat(payeeDao.getById(3).first()).isEqualTo(payee3)
        assertThat(payeeDao.getById(4).first()).isEqualTo(payee4)
        assertThat(payeeDao.getById(5).first()).isEqualTo(null)
    }

}