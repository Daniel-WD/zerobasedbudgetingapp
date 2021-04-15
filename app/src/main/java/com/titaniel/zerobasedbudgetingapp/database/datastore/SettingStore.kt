package com.titaniel.zerobasedbudgetingapp.database.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.YearMonth

/**
 * Class to persist simple values. Needs [applicationContext].
 */
class SettingStore constructor(private val applicationContext: Context, dataStoreName: String) {

    /**
     * DataStore
     */
    private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(dataStoreName)

    companion object {

        /**
         * Key for saving month value of month
         */
        private val MONTH_M_KEY = intPreferencesKey("month_of_month")

        /**
         * Key for saving year value of month
         */
        private val MONTH_Y_KEY = intPreferencesKey("year_of_month")

        /**
         * Key for saving month value of month
         */
        private val START_MONTH_M_KEY = intPreferencesKey("start_month_of_month")

        /**
         * Key for saving year value of month
         */
        private val START_MONTH_Y_KEY = intPreferencesKey("start_year_of_month")

    }

    init { // TODO Flag for first start, instead of nullable flows.

        val curYearMonth = YearMonth.now()

        // Set default values
        GlobalScope.launch {
            // Set current month as default month
            getMonth().first() ?: setMonth(curYearMonth)

            // Set current month as start month, when no start month is existing
            getStartMonth().first() ?: run {
                applicationContext.settingsStore.edit { settings ->
                    // Set start month values
                    settings[START_MONTH_M_KEY] = curYearMonth.monthValue
                    settings[START_MONTH_Y_KEY] = curYearMonth.year
                }
            }
        }
    }

    /**
     * Gets selected month.
     */
    fun getMonth(): Flow<YearMonth?> {
        return applicationContext.settingsStore.data
            .map { preferences ->
                // Create YearMonth, return  when at least one atomic value is null
                YearMonth.of(
                    preferences[MONTH_Y_KEY] ?: return@map null,
                    preferences[MONTH_M_KEY] ?: return@map null
                )
            }
    }

    /**
     * Sets selected [month].
     */
    suspend fun setMonth(month: YearMonth) {
        applicationContext.settingsStore.edit { settings ->
            // Set month values in settings
            settings[MONTH_M_KEY] = month.monthValue
            settings[MONTH_Y_KEY] = month.year
        }
    }

    /**
     * Gets start month.
     */
    fun getStartMonth(): Flow<YearMonth?> {
        return applicationContext.settingsStore.data
            .map { preferences ->
                // Create YearMonth, return  when at least one atomic value is null
                YearMonth.of(
                    preferences[START_MONTH_Y_KEY] ?: return@map null,
                    preferences[START_MONTH_M_KEY] ?: return@map null
                )
            }
    }

}
