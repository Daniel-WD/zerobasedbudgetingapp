package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Payee(
    @PrimaryKey val name: String
)