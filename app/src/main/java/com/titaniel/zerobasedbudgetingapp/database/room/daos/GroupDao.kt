package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Group
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for everything concerning groups
 */
@Dao
interface GroupDao {

    /**
     * Add [groups]
     */
    @Insert(onConflict = REPLACE)
    suspend fun add(vararg groups: Group): Array<Long>

    /**
     * Get all groups
     */
    @Query("SELECT * FROM `group`")
    fun getAll(): Flow<List<Group>>

    /**
     * Get group by [groupId]
     */
    @Query("SELECT * FROM `Group` WHERE `Group`.id = :groupId")
    fun getById(groupId: Long): Flow<Group>

}