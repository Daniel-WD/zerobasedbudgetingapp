package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [Payee] with [name]
 */
@Entity
data class Payee(
        @PrimaryKey val name: String
)