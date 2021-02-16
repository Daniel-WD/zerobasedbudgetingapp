package com.titaniel.zerobasedbudgetingapp.database

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun epochDayToDate(value: Long): LocalDate {
        return LocalDate.ofEpochDay(value)
    }

    @TypeConverter
    fun dateToEpochDay(date: LocalDate): Long {
        return date.toEpochDay()
    }

}