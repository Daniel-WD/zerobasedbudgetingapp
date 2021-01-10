package com.titaniel.zerobasedbudgetingapp.utils

import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun convertUtcToString(utcTimestamp: Long): String {
        val timezone = Calendar.getInstance().timeZone
        val dateFormat = SimpleDateFormat.getDateInstance()
        dateFormat.timeZone = timezone
        return dateFormat.format(utcTimestamp)
    }

}