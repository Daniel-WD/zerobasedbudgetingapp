package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * [Budget] with [categoryName], [month] and [budgeted] value
 */
@Entity
data class Budget(
    var categoryId: Long,
    val month: LocalDate,
    var budgeted: Long
) {
    /** [id] of this [Budget] */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}