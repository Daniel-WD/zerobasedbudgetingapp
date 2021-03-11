package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [Category] with [name]
 */
@Entity
data class Category(
    var name: String
) {

    companion object {
        /**
         * Category that represents [TO_BE_BUDGETED]
         */
        val TO_BE_BUDGETED = Category("\nTO_BE_BUDGETED").apply { id = -1 }
    }

    /**
     * [Transaction]s [id]
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}