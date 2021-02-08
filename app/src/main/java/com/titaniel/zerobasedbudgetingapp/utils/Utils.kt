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

}