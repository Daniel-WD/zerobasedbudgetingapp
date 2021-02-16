package com.titaniel.zerobasedbudgetingapp.repositories

import com.titaniel.zerobasedbudgetingapp.database.daos.BudgetDao
import com.titaniel.zerobasedbudgetingapp.database.daos.PayeeDao
import com.titaniel.zerobasedbudgetingapp.database.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.entities.Payee
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