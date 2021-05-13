package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.YearMonth

/**
 * [Budget] with [categoryId], [month], [budgeted] value and [id]
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("categoryId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION
    )]
)
data class Budget(
    val categoryId: Long,
    val month: YearMonth,
    var budgeted: Long,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)