package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.TransactionDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionWithCategoryAndPayee
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repository to interact with transaction data
 */
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    /**
     * Add [transactions]
     */
    suspend fun addTransactions(vararg transactions: Transaction) {
        transactionDao.add(*transactions)
    }

    /**
     * Delete [transactions]
     */
    suspend fun deleteTransactions(vararg transactions: Transaction) {
        transactionDao.delete(*transactions)
    }

    /**
     * Update [transactions]
     */
    suspend fun updateTransactions(vararg transactions: Transaction) {
        transactionDao.update(*transactions)
    }

    /**
     * Get transaction with [transactionId]
     */
    fun getTransactionById(transactionId: Long): Flow<Transaction> {
        return transactionDao.getById(transactionId)
    }

    /**
     * Get all transactions
     */
    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAll()
    }

    /**
     * Get all TransactionWithCategory
     */
    fun getAllTransactionsWithCategoryAndPayee(): Flow<List<TransactionWithCategoryAndPayee>> {
        return transactionDao.getAllTransactionsWithCategoryAndPayee()
    }

}