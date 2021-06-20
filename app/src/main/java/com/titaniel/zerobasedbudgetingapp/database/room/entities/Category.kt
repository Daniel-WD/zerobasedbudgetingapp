package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Category a user can budget for.
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = Group::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("groupId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION
    )]
)
data class Category(
    var name: String,
    var groupId: Long,
    var positionInGroup: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
) {

    companion object {
        /**
         * Category that represents [TO_BE_BUDGETED]
         */
        val TO_BE_BUDGETED = Category("\nTO_BE_BUDGETED", 0, -1, -1)
    }

}