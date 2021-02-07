package com.titaniel.zerobasedbudgetingapp

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OperationsTest {

    @Test
    fun addition_performs_correctly() {
        assertThat(addition(1, 2)).isEqualTo(3)
        assertThat(addition(-101, 2)).isEqualTo(-99)
        assertThat(addition(10, -2)).isEqualTo(8)
        assertThat(addition(-101, -10)).isEqualTo(-111)
    }

    @Test
    fun subtraction_performs_correctly() {
        assertThat(subtraction(1, 2)).isEqualTo(-1)
        assertThat(subtraction(-101, 2)).isEqualTo(-103)
        assertThat(subtraction(10, -2)).isEqualTo(12)
        assertThat(subtraction(-101, -10)).isEqualTo(-91)
    }

}