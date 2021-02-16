package com.titaniel.zerobasedbudgetingapp.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Budget(
    var categoryName: String,
    val month: LocalDate,
    var budgeted: Long
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}