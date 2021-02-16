package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.PayeeDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PayeeRepository @Inject constructor(
    private val payeeDao: PayeeDao
) {

    suspend fun addPayee(payee: Payee) {
        payeeDao.add(payee)
    }

    fun getAllPayees(): Flow<List<Payee>> {
        return payeeDao.getAll()
    }

}