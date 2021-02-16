package com.titaniel.zerobasedbudgetingapp.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class UtilsTest {

    @Test
    fun converts_utc_timestamp_to_humanly_readable_date_string_correctly() {
        // Set timezone to GMT
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

        // Assert timestamps get formatted correctly
        assertThat(Utils.convertLocalDateToString(1612476124000L)).isEqualTo("04.02.2021")
        assertThat(Utils.convertLocalDateToString(1579561324000L)).isEqualTo("20.01.2020")
        assertThat(Utils.convertLocalDateToString(1767222124000L)).isEqualTo("31.12.2025")
    }

}