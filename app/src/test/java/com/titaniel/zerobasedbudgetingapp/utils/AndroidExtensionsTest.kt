package com.titaniel.zerobasedbudgetingapp.utils

import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import org.junit.Test
import java.time.YearMonth

class AndroidExtensionsTest : CoroutinesAndLiveDataTest() {

    @Test
    fun performs_re_emit_correctly() {

        // Create MutableLiveData
        val mutableLiveData = MutableLiveData("")

        // Flag for observer called
        var observerCalledCount = 0

        // Observer
        mutableLiveData.observeForever {
            observerCalledCount++
        }

        mutableLiveData.reEmit()

        // Check that observer has been called
        assertThat(observerCalledCount).isEqualTo(2)

    }

}