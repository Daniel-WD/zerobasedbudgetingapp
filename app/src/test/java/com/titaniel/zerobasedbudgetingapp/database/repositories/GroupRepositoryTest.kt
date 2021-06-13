package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.room.daos.GroupDao
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Group
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GroupRepositoryTest {

    /**
     * GroupDao mock
     */
    @Mock
    private lateinit var groupDaoMock: GroupDao

    /**
     * GroupRepository to test
     */
    private lateinit var groupRepository: GroupRepository

    @Before
    fun setup() {
        groupRepository = GroupRepository(groupDaoMock)
    }

    @Test
    fun performs_add_groups_correctly(): Unit = runBlocking {
        // Example group
        val group = mock(Group::class.java)

        // Add group
        groupRepository.addGroups(group)

        // Verify add group on dao
        verify(groupDaoMock).add(group)
    }

    @Test
    fun performs_get_all_groups_correctly() {

        // Get all groups
        groupRepository.getAllGroups()

        // Verify get all groups on dao
        verify(groupDaoMock).getAll()
    }

}