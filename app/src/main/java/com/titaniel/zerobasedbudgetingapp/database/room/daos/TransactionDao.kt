package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionWithCategoryAndPayee
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data access object for everything concerning transactions
 */
@Dao
interface TransactionDao {

    /**
     * Add [transactions]
     * */
    @Insert(onConflict = REPLACE)
    suspend fun add(vararg transactions: Transaction): Array<Long>

    /**
     * Delete [transactions]
     */
    @Delete
    suspend fun delete(vararg transactions: Transaction): Int

    /**
     * Update [transactions]
     */
    @Update
    suspend fun update(vararg transactions: Transaction): Int

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
     * Get all transactions until [date]
     */
    @Query("SELECT * FROM `transaction` WHERE `transaction`.date <= :date")
    fun getUntilDate(date: LocalDate): Flow<List<Transaction>>

    /**
     * Get all TransactionWithCategoryAndPayee
     */
    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction`")
    fun getAllTransactionsWithCategoryAndPayee(): Flow<List<TransactionWithCategoryAndPayee>>

    /**
     * Get TransactionWithCategoryAndPayee by [transactionId]
     */
    @androidx.room.Transaction
    @Query("SELECT * FROM `transaction` WHERE `transaction`.id == :transactionId")
    fun getTransactionWithCategoryAndPayeeById(transactionId: Long): Flow<TransactionWithCategoryAndPayee>

}