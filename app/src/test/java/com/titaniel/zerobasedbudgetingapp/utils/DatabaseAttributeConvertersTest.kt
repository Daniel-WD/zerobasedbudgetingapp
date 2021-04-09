package com.titaniel.zerobasedbudgetingapp.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class DatabaseAttributeConvertersTest {

    /**
     * DatabaseAttributeConverters to test
     */
    private val converters = DatabaseAttributeConverters()

    @Test
    fun converts_epoch_day_to_date_correctly() {
        assertThat(converters.epochDayToLocalDate(18678)).isEqualTo(LocalDate.of(2021, 2, 20))
    }

    @Test
    fun converts_date_to_epoch_day_correctly() {
        assertThat(converters.localDateToEpochDay(LocalDate.of(2021, 2, 20))).isEqualTo(18678)
    }

    @Test
    fun converts_epoch_day_to_year_month_correctly() {
        assertThat(converters.epochDayToYearMonth(18710)).isEqualTo(YearMonth.of(2021, 3))
    }

    @Test
    fun converts_year_month_to_epoch_day_correctly() {
        assertThat(converters.yearMonthToEpochDay(YearMonth.of(2021, 2))).isEqualTo(18686)
    }

}