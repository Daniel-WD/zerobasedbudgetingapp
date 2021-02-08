package com.titaniel.zerobasedbudgetingapp.utils

import android.content.Context
import androidx.lifecycle.Lifecycle
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.datamanager.Category
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager
import com.titaniel.zerobasedbudgetingapp.datamanager.Transaction
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.lang.NullPointerException
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class UtilsTest {

    @Test
    fun converts_utc_timestamp_to_humanly_readable_date_string_correctly() {
        // Set timezone to GMT
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

        // Assert timestamps get formatted correctly
        assertThat(Utils.convertUtcToString(1612476124000L)).isEqualTo("04.02.2021")
        assertThat(Utils.convertUtcToString(1579561324000L)).isEqualTo("20.01.2020")
        assertThat(Utils.convertUtcToString(1767222124000L)).isEqualTo("31.12.2025")
    }

}