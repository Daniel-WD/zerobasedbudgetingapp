package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.PayeeDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to interact with payee data
 */
@Singleton
class PayeeRepository @Inject constructor(
    private val payeeDao: PayeeDao
) {

    /**
     * Add [payees]
     */
    suspend fun addPayees(vararg payees: Payee): Array<Long> {
        return payeeDao.add(*payees)
    }

    /**
     * Get all payees
     */
    fun getAllPayees(): Flow<List<Payee>> {
        return payeeDao.getAll()
    }

    /**
     * Get payee by [payeeId]
     */
    fun getPayeeById(payeeId: Long): Flow<Payee> {
        return payeeDao.getById(payeeId)
    }

}