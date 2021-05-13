package com.titaniel.zerobasedbudgetingapp.database.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
//
//        /**
//         * Key for saving if this app is started the first time
//         */
//        private val FIRST_START_KEY = booleanPreferencesKey("first_start")

    }

    init {

        // First call to make sure default values get set correctly
        getMonth()
        getStartMonth()

    }

//
//    /**
//     * Returns if the app is started fot the first time.
//     */
//    private suspend fun firstStart(): Boolean {
//        return applicationContext.settingsStore.data
//            .map { preferences ->
//                preferences[FIRST_START_KEY] ?: true
//            }.first()
//    }
//
//    /**
//     * Sets value of [FIRST_START_KEY] to false
//     */
//    private suspend fun setFirstStartFalse() {
//        applicationContext.settingsStore.edit { settings ->
//            // Set month values in settings
//            settings[FIRST_START_KEY] = false
//        }
//    }

    /**
     * Gets selected month.
     */
    fun getMonth(): Flow<YearMonth> {
        return applicationContext.settingsStore.data
            .map { preferences ->

                // Gather year and month
                val year = preferences[MONTH_Y_KEY]
                val month = preferences[MONTH_M_KEY]

                // Check year or month null
                if (year == null || month == null) {

                    // Set current real world month and return it
                    YearMonth.now().let {
                        setMonth(it)
                        it
                    }

                } else {

                    // Create YearMonth
                    YearMonth.of(year, month)
                }


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
    fun getStartMonth(): Flow<YearMonth> {
        return applicationContext.settingsStore.data
            .map { preferences ->

                // Gather year and month
                val year = preferences[START_MONTH_Y_KEY]
                val month = preferences[START_MONTH_M_KEY]

                // Check year or month null
                if (year == null || month == null) {

                    // Set current real world month and return it
                    YearMonth.now().let {

                        // Set current month as start month
                        applicationContext.settingsStore.edit { settings ->
                            settings[START_MONTH_M_KEY] = it.monthValue
                            settings[START_MONTH_Y_KEY] = it.year
                        }

                        it
                    }

                } else {

                    // Create YearMonth
                    YearMonth.of(year, month)
                }
            }
    }

}
