package com.titaniel.zerobasedbudgetingapp.database.repositories

import com.titaniel.zerobasedbudgetingapp.database.datastore.SettingStore
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

        flow<List<YearMonth>> {

            // List for result
            val result = mutableListOf<YearMonth>()

            // Get nextMonth
            val nextMonth = YearMonth.now().plusMonths(1)

            settingStore.getStartMonth().first().let { startMonth ->

                // Set last month to startMonth
                var last = startMonth

                // Iterate from startMonth to nextMonth
                while (last <= nextMonth) {

                    // Add last
                    result.add(last)

                    // Increase last by 1 month
                    last = last.plusMonths(1)

                }

            }

            emit(result)
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