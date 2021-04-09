package com.titaniel.zerobasedbudgetingapp.utils

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.YearMonth

/**
 * [DatabaseAttributeConverters].
 */
class DatabaseAttributeConverters {

    /**
     * Converts [epochDay] to LocalDate instance.
     */
    @TypeConverter
    fun epochDayToLocalDate(epochDay: Long): LocalDate {
        return LocalDate.ofEpochDay(epochDay)
    }

    /**
     * Converts [date] to epoch day.
     */
    @TypeConverter
    fun localDateToEpochDay(date: LocalDate): Long {
        return date.toEpochDay()
    }

    /**
     * Converts [epochDay] to YearMonth instance.
     */
    @TypeConverter
    fun epochDayToYearMonth(epochDay: Long): YearMonth {
        val ld = LocalDate.ofEpochDay(epochDay)
        return YearMonth.of(ld.year, ld.month)
    }

    /**
     * Converts [yearMonth] to epoch day. Day of month will be the last day of the month.
     */
    @TypeConverter
    fun yearMonthToEpochDay(yearMonth: YearMonth): Long {
        return LocalDate.of(yearMonth.year, yearMonth.month, yearMonth.lengthOfMonth()).toEpochDay()
    }

}