package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * [Transaction] with [pay], [payeeName], [categoryName], [description] and a [date]
 */
@Entity
data class Transaction(
    var pay: Long,
    var payeeName: String,
    var categoryName: String,
    var description: String,
    var date: LocalDate
) {
    /**
     * [Transaction] [id]
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}