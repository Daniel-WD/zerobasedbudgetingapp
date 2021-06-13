package com.titaniel.zerobasedbudgetingapp.database.room.daos

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.database.room.Database
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Group
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

class GroupDaoTest {

    /**
     * Database
     */
    private lateinit var database: Database

    /**
     * GroupDao to test
     */
    private lateinit var groupDao: GroupDao

    /**
     * Test groups
     */
    private val group1 = Group("group1", 1, 1)
    private val group2 = Group("group2", 2, 2)
    private val group3 = Group("group3", 3, 3)
    private val group4 = Group("group4", 4, 4)

    @Before
    fun setup(): Unit = runBlocking {

        // Create database
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            Database::class.java
        ).build()

        // Get group dao
        groupDao = database.groupDao()

        // Add example budgets
        groupDao.add(group1, group2, group3, group4)
    }

    @After
    fun tearDown() {
        // Close database
        database.close()
    }

    @Test
    fun gets_groups_correctly(): Unit = runBlocking {
        assertThat(groupDao.getAll().first()).isEqualTo(listOf(group1, group2, group3, group4))
    }

}