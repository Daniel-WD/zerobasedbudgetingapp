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
     * Get all groups TODO: can be dropped if not in use and dataintegrity can be tested through other queries (i. e. relation)
     */
    @Query("SELECT * FROM `group`")
    fun getAll(): Flow<List<Group>>

}