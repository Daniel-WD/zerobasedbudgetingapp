package com.titaniel.zerobasedbudgetingapp.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Object for utility methods
 */
object Utils {

    /**
     * Convert local date to date string
     */
    fun convertLocalDateToString(localDate: LocalDate): String {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(localDate)
    }

}