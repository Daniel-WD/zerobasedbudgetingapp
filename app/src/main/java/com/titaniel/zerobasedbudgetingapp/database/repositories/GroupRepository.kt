package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.GroupDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Group
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to interact with group data
 */
@Singleton
class GroupRepository @Inject constructor(
    private val groupDao: GroupDao
) {

    /**
     * Add [groups]
     */
    suspend fun addGroups(vararg groups: Group): Array<Long> {
        return groupDao.add(*groups)
    }

    /**
     * Get all groups
     */
    fun getAllGroups(): Flow<List<Group>> {
        return groupDao.getAll()
    }

}