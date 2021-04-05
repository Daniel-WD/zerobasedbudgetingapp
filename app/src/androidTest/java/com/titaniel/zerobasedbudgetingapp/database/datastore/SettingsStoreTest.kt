package com.titaniel.zerobasedbudgetingapp.database.datastore

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.time.YearMonth

class SettingsStoreTest {

    private lateinit var settingStore: SettingStore

    @Before
    fun setup() {

        settingStore = SettingStore(InstrumentationRegistry.getInstrumentation().targetContext, "testSettings")

    }

    @Test
    fun sets_and_gets_month_correctly(): Unit = runBlocking(GlobalScope.coroutineContext) {

        // Define month
        val m = YearMonth.of(1999, 5)

        // Set month
        settingStore.setMonth(m)

        assertThat(settingStore.getMonth().first()).isEqualTo(m)

    }

}