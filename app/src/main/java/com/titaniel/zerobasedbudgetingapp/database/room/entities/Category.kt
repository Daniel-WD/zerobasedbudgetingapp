package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [Category] with [name]
 */
@Entity
data class Category(
        @PrimaryKey val name: String
) {
    companion object {
        /**
         * Indicator for [TO_BE_BUDGETED] transactions
         */
        const val TO_BE_BUDGETED = "TO_BE_BUDGETED"
    }
}