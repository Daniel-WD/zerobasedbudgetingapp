package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = REPLACE)
    suspend fun add(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Update
    suspend fun update(transaction: Transaction)

    @Query("SELECT * FROM `transaction` WHERE id = :transactionId")
    fun getById(transactionId: Long): Flow<Transaction>

    @Query("SELECT * FROM `transaction` WHERE categoryName = :categoryName")
    fun getByCategory(categoryName: String): Flow<List<Transaction>>

    @Query("SELECT * FROM `transaction`")
    fun getAll(): Flow<List<Transaction>>

}