package com.titaniel.zerobasedbudgetingapp.database.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
         * Key for saving month value of start month
         */
        private val START_MONTH_M_KEY = intPreferencesKey("month_of_start_month")

        /**
         * Key for saving year value of start month
         */
        private val START_MONTH_Y_KEY = intPreferencesKey("year_of_start_month")

        /**
         * Key for saving if this app is started the first time
         */
        private val FIRST_START_KEY = booleanPreferencesKey("first_start")

    }

    init {

        val curYearMonth = YearMonth.now()

        // Set default values
        GlobalScope.launch {

            // If this is the first start of the application...
            if(firstStart()) {

                // Set current month as default month
                setMonth(curYearMonth)

                // Set current month as start month, when no start month is existing
                applicationContext.settingsStore.edit { settings ->
                    // Set start month values
                    settings[START_MONTH_M_KEY] = curYearMonth.monthValue
                    settings[START_MONTH_Y_KEY] = curYearMonth.year
                }

            }

            setFirstStartFalse()
        }

    }

    /**
     * Returns if the app is started fot the first time.
     */
    private suspend fun firstStart(): Boolean {
        return applicationContext.settingsStore.data
            .map { preferences ->
                preferences[FIRST_START_KEY] ?: true
            }.first()
    }

    /**
     * Sets value of [FIRST_START_KEY] to false
     */
    private suspend fun setFirstStartFalse() {
        applicationContext.settingsStore.edit { settings ->
            // Set month values in settings
            settings[FIRST_START_KEY] = false
        }
    }

    /**
     * Gets selected month.
     */
    fun getMonth(): Flow<YearMonth> {
        return applicationContext.settingsStore.data
            .map { preferences ->
                // Create YearMonth
                YearMonth.of(
                    preferences[MONTH_Y_KEY]!!,
                    preferences[MONTH_M_KEY]!!
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
    suspend fun getStartMonth(): YearMonth {
        return applicationContext.settingsStore.data
            .map { preferences ->
                // Create YearMonth
                YearMonth.of(
                    preferences[START_MONTH_Y_KEY]!!,
                    preferences[START_MONTH_M_KEY]!!
                )
            }.first()
    }

}
