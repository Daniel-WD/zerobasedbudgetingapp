package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.TransactionDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionWithCategoryAndPayee
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to interact with transaction data
 */
@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    /**
     * Add [transactions]
     */
    suspend fun addTransactions(vararg transactions: Transaction): Array<Long> {
        return transactionDao.add(*transactions)
    }

    /**
     * Delete [transactions]
     */
    suspend fun deleteTransactions(vararg transactions: Transaction): Int {
        return transactionDao.delete(*transactions)
    }

    /**
     * Update [transactions]
     */
    suspend fun updateTransactions(vararg transactions: Transaction): Int {
        return transactionDao.update(*transactions)
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
     * Get all transactions until [date]
     */
    fun getTransactionsUntilDate(date: LocalDate): Flow<List<Transaction>> {
        return transactionDao.getUntilDate(date)
    }

    /**
     * Get all TransactionWithCategoryAndPayee
     */
    fun getAllTransactionsWithCategoryAndPayee(): Flow<List<TransactionWithCategoryAndPayee>> {
        return transactionDao.getAllTransactionsWithCategoryAndPayee()
    }

    /**
     * Get TransactionWithCategoryAndPayee by [transactionId]
     */
    fun getTransactionWithCategoryAndPayeeById(transactionId: Long): Flow<TransactionWithCategoryAndPayee> {
        return transactionDao.getTransactionWithCategoryAndPayeeById(transactionId)
    }

}