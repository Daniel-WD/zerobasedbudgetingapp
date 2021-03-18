package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.datastore.PreferenceStore
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val preferenceStore: PreferenceStore
) {

    /**
     * Gets selected month
     */
    fun getMonth(): Flow<YearMonth?> {
        return preferenceStore.getMonth()
    }

    /**
     * Sets selected [month]
     */
    suspend fun setMonth(month: YearMonth) {
        preferenceStore.setMonth(month)
    }

}