package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for everything concerning transactions
 */
@Dao
interface TransactionDao {

    /** Add [transaction] */
    @Insert(onConflict = REPLACE)
    suspend fun add(transaction: Transaction)

    /**
     * Delete [transaction]
     */
    @Delete
    suspend fun delete(transaction: Transaction)

    /**
     * Update [transaction]
     */
    @Update
    suspend fun update(transaction: Transaction)

    /**
     * Get transaction with [transactionId]
     */
    @Query("SELECT * FROM `transaction` WHERE id = :transactionId")
    fun getById(transactionId: Long): Flow<Transaction>

    /**
     * Get all transactions with [categoryName]
     */
    @Query("SELECT * FROM `transaction` WHERE categoryName = :categoryName")
    fun getByCategory(categoryName: String): Flow<List<Transaction>>

    /**
     * Get all transactions
     */
    @Query("SELECT * FROM `transaction`")
    fun getAll(): Flow<List<Transaction>>

}