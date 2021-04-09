package com.titaniel.zerobasedbudgetingapp.utils

import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate

@RunWith(MockitoJUnitRunner::class)
class UtilsTest : CoroutinesAndLiveDataTest() {

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

    @Test
    fun creates_simple_mediator_live_data_correctly() {

        // Create some live data instances
        val ld1 = MutableLiveData("hi")
        val ld2 = MutableLiveData(10)
        val ld3: MutableLiveData<Char> = MutableLiveData()

        // Create mediator live data
        val mediator = createSimpleMediatorLiveData(ld1, ld2, ld3)

        // Observer called counter
        var observerCalledCount = 0

        // Add mediator observer
        mediator.observeForever {

            // Increase counter
            observerCalledCount++

            assertThat(it).isEqualTo(Unit)
        }

        // Edit some live data's
        ld1.value = "aöslkdfja"
        ld2.value = null
        ld3.value = null

        assertThat(observerCalledCount).isEqualTo(5)


    }

}