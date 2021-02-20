package com.titaniel.zerobasedbudgetingapp.utils

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * [DatabaseAttributeConverters].
 */
class DatabaseAttributeConverters {

    @TypeConverter
    fun epochDayToDate(value: Long): LocalDate {
        return LocalDate.ofEpochDay(value)
    }

    @TypeConverter
    fun dateToEpochDay(date: LocalDate): Long {
        return date.toEpochDay()
    }

}