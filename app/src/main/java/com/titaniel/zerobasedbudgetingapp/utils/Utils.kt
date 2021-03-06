package com.titaniel.zerobasedbudgetingapp.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Object for utility methods
 */
object Utils {

    /**
     * Convert [localDate] to its string representation
     */
    fun convertLocalDateToString(localDate: LocalDate): String {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(localDate)
    }

}