package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.daos.TransactionDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.add(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.delete(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.update(transaction)
    }

    fun getTransactionById(transactionId: Long): Flow<Transaction> {
        return transactionDao.getById(transactionId)
    }

    fun getTransactionsByCategory(categoryName: String): Flow<List<Transaction>> {
        return transactionDao.getByCategory(categoryName)
    }

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAll()
    }

}