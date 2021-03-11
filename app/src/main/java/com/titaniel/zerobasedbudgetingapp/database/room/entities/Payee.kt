package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [Payee] with [name]
 */
@Entity
data class Payee(
    var name: String
) {
    /**
     * [Payee]s [id]
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}