package com.titaniel.zerobasedbudgetingapp.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate

@RunWith(MockitoJUnitRunner::class)
class UtilsTest {

    @Test
    fun converts_utc_timestamp_to_humanly_readable_date_string_correctly() {
        assertThat(convertLocalDateToString(LocalDate.of(2021, 2, 4))).isEqualTo("04.02.2021")
        assertThat(
            convertLocalDateToString(
                LocalDate.of(
                    2020,
                    1,
                    20
                )
            )
        ).isEqualTo("20.01.2020")
        assertThat(
            convertLocalDateToString(
                LocalDate.of(
                    2025,
                    12,
                    31
                )
            )
        ).isEqualTo("31.12.2025")
    }

}