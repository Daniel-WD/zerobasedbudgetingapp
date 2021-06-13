package com.titaniel.zerobasedbudgetingapp.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a group. Each category is assigned to a group.
 */
@Entity
data class Group(
    var name: String,
    var position: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)