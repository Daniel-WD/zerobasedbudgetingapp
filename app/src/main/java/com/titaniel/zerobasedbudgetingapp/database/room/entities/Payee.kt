package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [Payee] with [name] and [id]
 */
@Entity
data class Payee(
    var name: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)