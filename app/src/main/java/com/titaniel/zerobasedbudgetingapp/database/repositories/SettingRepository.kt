package com.titaniel.zerobasedbudgetingapp.database.repositories

import androidx.lifecycle.viewModelScope
import com.titaniel.zerobasedbudgetingapp.database.datastore.SettingStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Month
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

}