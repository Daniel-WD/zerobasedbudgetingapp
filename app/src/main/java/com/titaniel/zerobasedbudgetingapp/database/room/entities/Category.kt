package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey val name: String
) {
    companion object {
        const val TO_BE_BUDGETED = "TO_BE_BUDGETED"
    }
}