package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.TransactionDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TransactionRepositoryTest {

    /**
     * TransactionDao mock
     */
    @Mock
    private lateinit var transactionDaoMock: TransactionDao

    /**
     * Example transaction mock
     */
    @Mock
    private lateinit var transactionMock: Transaction

    /**
     * TransactionRepository to test
     */
    private lateinit var transactionRepository: TransactionRepository

    @Before
    fun setup() {
        transactionRepository = TransactionRepository(transactionDaoMock)
    }

    @Test
    fun performs_add_transactions_correctly(): Unit = runBlocking {
        // Add transaction
        transactionRepository.addTransactions(transactionMock)

        // Verify add transaction on dao
        verify(transactionDaoMock).add(transactionMock)
    }

    @Test
    fun performs_delete_transactions_correctly(): Unit = runBlocking {
        // Delete transaction
        transactionRepository.deleteTransactions(transactionMock)

        // Verify delete transaction on dao
        verify(transactionDaoMock).delete(transactionMock)
    }

    @Test
    fun performs_update_transactions_correctly(): Unit = runBlocking {
        // Update transaction
        transactionRepository.updateTransactions(transactionMock)

        // Verify update transaction on dao
        verify(transactionDaoMock).update(transactionMock)
    }

    @Test
    fun performs_get_transaction_by_id_correctly() {
        // Define id
        val id = 5L

        // Get transaction by id
        transactionRepository.getTransactionById(id)

        // Verify get transaction by id on dao
        verify(transactionDaoMock).getById(id)
    }

    @Test
    fun performs_get_all_transactions_correctly() {

        // Get all transactions
        transactionRepository.getAllTransactions()

        // Verify get all transaction on dao
        verify(transactionDaoMock).getAll()
    }

    @Test
    fun performs_get_all_transactions_with_category_and_payee_correctly() {

        // Call method
        transactionRepository.getAllTransactionsWithCategoryAndPayee()

        // Very dao call
        verify(transactionDaoMock).getAllTransactionsWithCategoryAndPayee()

    }

    @Test
    fun performs_get_transaction_with_category_and_payee_by_id_correctly() {

        val id = 32L

        // Call method
        transactionRepository.getTransactionWithCategoryAndPayeeById(id)

        // Very dao call
        verify(transactionDaoMock).getTransactionWithCategoryAndPayeeById(id)

    }

}