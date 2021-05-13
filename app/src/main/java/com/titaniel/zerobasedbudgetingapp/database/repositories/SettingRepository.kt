package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.datastore.SettingStore
import com.titaniel.zerobasedbudgetingapp.utils.rangeTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
     * Months that are selectable by the user
     */
    val availableMonths: Flow<List<YearMonth>> by lazy {

        // Create flow, because we need to wait for start month
        flow {

            // Emit list of YearMonth range, form start month to current month +1
            emit(
                settingStore.getStartMonth().first()
                    .let { (it..YearMonth.now().plusMonths(1)).toList() })

        }

    }

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