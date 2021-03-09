package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class TransactionDaoTest {

    /**
     * Database
     */
    private lateinit var database: Database

    /**
     * TransactionDao to test
     */
    private lateinit var transactionDao: TransactionDao

    /**
     * Example transactions
     */
    private val transaction1 = Transaction(1, "payee1", "cat1", "", LocalDate.now())
        .apply { id = 1 }
    private val transaction2 = Transaction(2, "payee1", "cat2", "", LocalDate.now())
        .apply { id = 2 }
    private val transaction3 = Transaction(3, "payee1", "cat2", "", LocalDate.now())
        .apply { id = 3 }
    private val transaction4 = Transaction(4, "payee1", "cat1", "", LocalDate.now())
        .apply { id = 4 }

    @Before
    fun setup(): Unit = runBlocking {

        // Create database
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            Database::class.java
        ).build()

        // Get payee dao
        transactionDao = database.transactionDao()

        // Add example budgets
        transactionDao.add(transaction1, transaction2, transaction3, transaction4)
    }

    @After
    fun tearDown() {
        // Close database
        database.close()
    }

    @Test
    fun gets_transactions_correctly(): Unit = runBlocking {
        assertThat(transactionDao.getAll().first()).isEqualTo(
            listOf(
                transaction1,
                transaction2,
                transaction3,
                transaction4
            )
        )
    }

    @Test
    fun deletes_transactions_correctly(): Unit = runBlocking {

        // Delete transactions
        transactionDao.delete(transaction4, transaction1)

        assertThat(transactionDao.getAll().first()).isEqualTo(listOf(transaction2, transaction3))
    }

    @Test
    fun updates_transactions_correctly(): Unit = runBlocking {

        // Change some transactions
        transaction1.categoryName = "cat99"
        transaction1.description = "description"
        transaction3.date = LocalDate.of(1989, 3, 23)

        // Update transactions
        transactionDao.update(transaction1, transaction3)

        assertThat(transactionDao.getAll().first()).isEqualTo(
            listOf(
                transaction1,
                transaction2,
                transaction3,
                transaction4
            )
        )
    }

    @Test
    fun gets_transaction_by_id_correctly(): Unit = runBlocking {
        assertThat(transactionDao.getById(1).first()).isEqualTo(transaction1)
        assertThat(transactionDao.getById(2).first()).isEqualTo(transaction2)
        assertThat(transactionDao.getById(3).first()).isEqualTo(transaction3)
        assertThat(transactionDao.getById(4).first()).isEqualTo(transaction4)
    }

}