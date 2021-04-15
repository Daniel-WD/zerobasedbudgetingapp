package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.datastore.SettingStore
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository to interact with simple app data
 */
@Singleton
class SettingRepository @Inject constructor(
    private val settingStore: SettingStore
) {

    /**
     * Gets selected month
     */
    fun getMonth(): Flow<YearMonth?> {
        return settingStore.getMonth()
    }

    /**
     * Sets selected [month]
     */
    suspend fun setMonth(month: YearMonth) {
        settingStore.setMonth(month)
    }

    /**
     * Gets start month
     */
    fun getStartMonth(): Flow<YearMonth?> {
        return settingStore.getStartMonth()
    }

}