package com.titaniel.zerobasedbudgetingapp.utils

import com.titaniel.zerobasedbudgetingapp.datamanager.Category
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
     * Updates transaction sums in category, or to be budgeted value
     * @param transaction Transaction to update its category with
     * @param dataManager Data manager
     * @param remove If trnasaction should be removed
     */
    fun updateTransactionSums(
        transaction: Transaction,
        dataManager: DataManager,
        remove: Boolean = false
    ) {
        // Find month timestamp
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = transaction.utcTimestamp
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // Set month timestamp
        val monthTimestmap = calendar.timeInMillis

        // Value operation
        val value = if (remove) -transaction.value else transaction.value

        // If category is TO_BE_BUDGETED, change to be budgeted value
        if (transaction.category == Category.TO_BE_BUDGETED) {
            dataManager.toBeBudgeted = dataManager.toBeBudgeted.plus(value)
            return
        }

        // Find transaction category
        val category =
            dataManager.categories.find { category -> category.name == transaction.category }

        // Update transaction sums for month. Create key if necessary.
        category!!.transactionSums[monthTimestmap] = category.transactionSums[monthTimestmap]?.plus(
            value
        ) ?: value

    }

}