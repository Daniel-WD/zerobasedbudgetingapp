package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * [Transaction] with [pay], [payeeId], [categoryId], [description], [date] and [id]
 */
@Entity
data class Transaction(
    var pay: Long,
    var payeeId: Long,
    var categoryId: Long,
    var description: String,
    var date: LocalDate,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)