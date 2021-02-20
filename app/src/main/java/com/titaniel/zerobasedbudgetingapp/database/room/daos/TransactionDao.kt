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
     * Delete transaction
     * @param transaction Transaction to delete
     */
    @Delete
    suspend fun delete(transaction: Transaction)

    /**
     * Update transaction
     * @param transaction Transaction to update
     */
    @Update
    suspend fun update(transaction: Transaction)

    /**
     * Get transaction by id
     * @param transactionId Id of transaction to get
     */
    @Query("SELECT * FROM `transaction` WHERE id = :transactionId")
    fun getById(transactionId: Long): Flow<Transaction>

    /**
     * Get all transactions of category
     * @param categoryName Category name of transactions to get
     */
    @Query("SELECT * FROM `transaction` WHERE categoryName = :categoryName")
    fun getByCategory(categoryName: String): Flow<List<Transaction>>

    /**
     * Get all transactions
     */
    @Query("SELECT * FROM `transaction`")
    fun getAll(): Flow<List<Transaction>>

}