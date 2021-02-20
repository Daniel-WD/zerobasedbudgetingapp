package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.PayeeDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
/**
 * Repository to interact with payee data
 */
class PayeeRepository @Inject constructor(
    private val payeeDao: PayeeDao
) {

    /**
     * Add payee
     * @param payee Payee
     */
    suspend fun addPayee(payee: Payee) {
        payeeDao.add(payee)
    }

    /**
     * Get all payees
     */
    fun getAllPayees(): Flow<List<Payee>> {
        return payeeDao.getAll()
    }

}