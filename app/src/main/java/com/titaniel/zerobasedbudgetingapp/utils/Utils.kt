package com.titaniel.zerobasedbudgetingapp.utils

import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager
import com.titaniel.zerobasedbudgetingapp.datamanager.Transaction
import java.text.SimpleDateFormat
import java.util.*

/**
 * Object for utility methods
 */
object Utils {

    /**
     * Convert utc timestamp to date string
     */
    fun convertUtcToString(utcTimestamp: Long): String {
        val timezone = Calendar.getInstance().timeZone
        val dateFormat = SimpleDateFormat.getDateInstance()
        dateFormat.timeZone = timezone
        return dateFormat.format(utcTimestamp)
    }

    /**
     * Updates transaction sums in category
     * @param transaction Transaction to update its category with
     * @param dataManager Data manager
     * @param remove If trnasaction should be removed
     */
    fun updateTransactionSums(
        transaction: Transaction,
        dataManager: DataManager,
        remove: Boolean = false
    ) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = transaction.utcTimestamp
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val monthTimestmap = calendar.timeInMillis
        val category =
            dataManager.categories.find { category -> category.name == transaction.category }

        val value = if (remove) -transaction.value else transaction.value

        category!!.transactionSums[monthTimestmap] = category.transactionSums[monthTimestmap]?.plus(
            value
        ) ?: value

    }

}