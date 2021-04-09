package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionWithCategoryAndPayee
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
    private val transaction1 = Transaction(1, 1, 2, "", LocalDate.now(), 1)
    private val transaction2 = Transaction(2, 2, 3, "", LocalDate.now(), 2)
    private val transaction3 = Transaction(3, 2, 1, "", LocalDate.now(), 3)
    private val transaction4 = Transaction(4, 1, -1, "", LocalDate.now(), 4)

    /**
     * Example payees
     */
    private val payee1 = Payee("payee1", 1)
    private val payee2 = Payee("payee2", 2)

    /**
     * Example categories
     */
    private val category1 = Category("cat1", 1, 1)
    private val category2 = Category("cat2", 2, 2)
    private val category3 = Category("cat3", 3, 3)

    /**
     * TransactionWithCategoryAndPayee's derived from other example data
     */
    private val tWithCatAPayee1 = TransactionWithCategoryAndPayee(transaction1, category2, payee1)
    private val tWithCatAPayee2 = TransactionWithCategoryAndPayee(transaction2, category3, payee2)
    private val tWithCatAPayee3 = TransactionWithCategoryAndPayee(transaction3, category1, payee2)
    private val tWithCatAPayee4 = TransactionWithCategoryAndPayee(transaction4, null, payee1)

    @Before
    fun setup(): Unit = runBlocking {

        // Create database
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            Database::class.java
        ).build()

        // Add example payees and categories
        database.payeeDao().add(payee1, payee2)
        database.categoryDao().add(category1, category2, category3)

        // Get payee dao
        transactionDao = database.transactionDao()

        // Add example transactions
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
        transaction1.categoryId = 99
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

    @Test
    fun gets_transaction_with_category_and_payee_by_id_correctly(): Unit = runBlocking {

        assertThat(transactionDao.getTransactionWithCategoryAndPayeeById(1).first()).isEqualTo(
            tWithCatAPayee1
        )
        assertThat(transactionDao.getTransactionWithCategoryAndPayeeById(2).first()).isEqualTo(
            tWithCatAPayee2
        )
        assertThat(transactionDao.getTransactionWithCategoryAndPayeeById(3).first()).isEqualTo(
            tWithCatAPayee3
        )
        assertThat(transactionDao.getTransactionWithCategoryAndPayeeById(4).first()).isEqualTo(
            tWithCatAPayee4
        )
        assertThat(transactionDao.getTransactionWithCategoryAndPayeeById(5).first()).isEqualTo(null)

    }

    @Test
    fun gets_all_transactions_with_category_and_payee_correctly(): Unit = runBlocking {
        assertThat(transactionDao.getAllTransactionsWithCategoryAndPayee().first()).isEqualTo(
            listOf(
                tWithCatAPayee1,
                tWithCatAPayee2,
                tWithCatAPayee3,
                tWithCatAPayee4
            )
        )
    }

}