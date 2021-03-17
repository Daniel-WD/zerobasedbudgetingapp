package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionWithCategoryAndPayee
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for everything concerning transactions
 */
@Dao
interface TransactionDao {

    /**
     * Add [transactions]
     * */
    @Insert(onConflict = REPLACE)
    suspend fun add(vararg transactions: Transaction)

    /**
     * Delete [transactions]
     */
    @Delete
    suspend fun delete(vararg transactions: Transaction)

    /**
     * Update [transactions]
     */
    @Update
    suspend fun update(vararg transactions: Transaction)

    /**
     * Get transaction with [transactionId]
     */
    @Query("SELECT * FROM `transaction` WHERE id = :transactionId")
    fun getById(transactionId: Long): Flow<Transaction>

    /**
     * Get all transactions
     */
    @Query("SELECT * FROM `transaction`")
    fun getAll(): Flow<List<Transaction>>

    /**
     * Get all TransactionWithCategoryAndPayee
     */
    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction`")
    fun getAllTransactionsWithCategoryAndPayee(): Flow<List<TransactionWithCategoryAndPayee>>

}