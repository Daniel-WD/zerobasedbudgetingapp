package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.daos.TransactionDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
/**
 * Repository to interact with transaction data
 */
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    /**
     * Add transaction
     * @param transaction Transaction
     */
    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.add(transaction)
    }

    /**
     * Delete transaction
     * @param transaction Transaction
     */
    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    /**
     * Update transaction
     * @param transaction Transaction
     */
    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    /**
     * Get transaction by id
     * @param transactionId Id of transaction to get
     */
    fun getTransactionById(transactionId: Long): Flow<Transaction> {
        return transactionDao.getById(transactionId)
    }

    /**
     * Get transactions by category
     * @param categoryName Category name of transaction to get
     */
    fun getTransactionsByCategory(categoryName: String): Flow<List<Transaction>> {
        return transactionDao.getByCategory(categoryName)
    }

    /**
     * Get all transactions
     */
    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAll()
    }

}