package com.titaniel.zerobasedbudgetingapp.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class UtilsTest {

    @Test
    fun test_convertUtcToString() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

        val timestamp1 = 1612476124000L
        val timestamp2 = 1579561324000L
        val timestamp3 = 1767222124000L

        val expectedDate1 = "04.02.2021"
        val expectedDate2 = "20.01.2020"
        val expectedDate3 = "31.12.2025"

        assertThat(Utils.convertUtcToString(timestamp1)).isEqualTo(expectedDate1)
        assertThat(Utils.convertUtcToString(timestamp2)).isEqualTo(expectedDate2)
        assertThat(Utils.convertUtcToString(timestamp3)).isEqualTo(expectedDate3)
    }

}