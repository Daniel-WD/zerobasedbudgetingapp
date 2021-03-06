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
     * Add [payees]
     */
    suspend fun addPayees(vararg payees: Payee) {
        payeeDao.add(*payees)
    }

    /**
     * Get all payees
     */
    fun getAllPayees(): Flow<List<Payee>> {
        return payeeDao.getAll()
    }

}