package com.titaniel.zerobasedbudgetingapp.database.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class to persist simple values.
 */
@Singleton
class SettingStore @Inject constructor(@ApplicationContext private val context: Context) {

    /**
     * DataStore
     */
    private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore("settings")

    companion object {

        /**
         * Key for saving month value of month
         */
        private val MONTH_M_KEY = intPreferencesKey("month_of_month")

        /**
         * Key for saving year value of month
         */
        private val MONTH_Y_KEY = intPreferencesKey("year_of_month")

    }

    /**
     * Gets selected month.
     */
    fun getMonth(): Flow<YearMonth?> {
        return context.settingsStore.data
            .map { preferences ->
                // Create YearMonth, return null when at least one atomic value is null
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
        context.settingsStore.edit { settings ->
            // Set month values in settings
            settings[MONTH_M_KEY] = month.monthValue
            settings[MONTH_Y_KEY] = month.year
        }
    }

}
