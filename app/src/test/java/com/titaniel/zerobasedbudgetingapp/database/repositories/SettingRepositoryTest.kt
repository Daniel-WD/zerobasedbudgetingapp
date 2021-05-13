package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.datastore.SettingStore
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.time.YearMonth

@RunWith(MockitoJUnitRunner::class)
class SettingRepositoryTest {

    /**
     * SettingStore mock
     */
    @Mock
    private lateinit var settingStoreMock: SettingStore

    /**
     * SettingRepository to test
     */
    private lateinit var settingRepository: SettingRepository

    @Before
    fun setup() {

        // Create test object
        settingRepository = SettingRepository(settingStoreMock)
    }

    @Test
    fun performs_get_month_correctly() {

        // Call method
        settingRepository.getMonth()

        // Verify called on store
        verify(settingStoreMock).getMonth()

    }

    @Test
    fun performs_set_month_correctly(): Unit = runBlocking {

        // Example month
        val month = YearMonth.now()

        // Call method
        settingRepository.setMonth(month)

        // Verify called on store
        verify(settingStoreMock).setMonth(month)

    }

}