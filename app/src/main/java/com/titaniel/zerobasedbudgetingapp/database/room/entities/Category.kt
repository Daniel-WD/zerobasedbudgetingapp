package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [Category] with [name] and [id]
 */
@Entity
data class Category(
    var name: String,
    var index: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
) {

    companion object {
        /**
         * Category that represents [TO_BE_BUDGETED]
         */
        val TO_BE_BUDGETED = Category("\nTO_BE_BUDGETED", -1, -1)
    }

}