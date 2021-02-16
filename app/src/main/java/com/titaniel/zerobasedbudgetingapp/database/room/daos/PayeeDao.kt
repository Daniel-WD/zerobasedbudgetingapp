package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import kotlinx.coroutines.flow.Flow

@Dao
interface PayeeDao {

    @Insert(onConflict = REPLACE)
    suspend fun add(payee: Payee)

    @Query("SELECT * FROM payee")
    fun getAll(): Flow<List<Payee>>

}