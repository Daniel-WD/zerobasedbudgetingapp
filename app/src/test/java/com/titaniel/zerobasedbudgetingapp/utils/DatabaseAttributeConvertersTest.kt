package com.titaniel.zerobasedbudgetingapp.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class DatabaseAttributeConvertersTest {

    /**
     * DatabaseAttributeConverters to test
     */
    private val converters = DatabaseAttributeConverters()

    @Test
    fun converts_epoch_day_to_date_correctly() {
        assertThat(converters.epochDayToDate(18678)).isEqualTo(LocalDate.of(2021, 2, 20))
    }

    @Test
    fun converts_date_to_epoch_day_correctly() {
        assertThat(converters.dateToEpochDay(LocalDate.of(2021, 2, 20))).isEqualTo(18678)
    }

}