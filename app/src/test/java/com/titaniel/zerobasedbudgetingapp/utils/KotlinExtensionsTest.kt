package com.titaniel.zerobasedbudgetingapp.utils

import androidx.lifecycle.MutableLiveData
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import org.junit.Test

class KotlinExtensionsTest : CoroutinesAndLiveDataTest() {

    @Test
    fun performs_add_unique_correctly() {

        // Element class for our list
        data class Element(val a: Int, val b: String, val c: Element? = null)

        // Example list
        val list = listOf(
            Element(1, "1"),
            Element(2, "1", Element(123, "lkj", Element(1, "1"))),
            Element(5, "2", Element(54, "sdf")),
            Element(5, "3"),
            Element(2, "3"),
        )

        // List to test with
        var testList = list.toMutableList()

        // Define values to add
        val e1 = Element(1, "5")
        val e2 = Element(123, "lkj", Element(1, "1"))
        val e3 = Element(2354, "ölajsdfa", Element(54, "sdf"))
        val e4 = Element(0, "3534")

        // #1
        // Add element
        assertThat(
            testList.addUnique(e1) { it.b }
        ).isTrue()

        assertThat(testList).contains(e1)

        // Revert testList
        testList = list.toMutableList()

        // #2
        // Add element
        assertThat(
            testList.addUnique(e1) { it.a }
        ).isFalse()

        assertThat(testList).doesNotContain(e1)

        // Revert testList
        testList = list.toMutableList()

        // #3
        // Add element
        assertThat(
            testList.addUnique(e2) { it.c }
        ).isTrue()

        assertThat(testList).contains(e2)

        // Revert testList
        testList = list.toMutableList()

        // #4
        // Add element
        assertThat(
            testList.addUnique(e3) { it.c }
        ).isFalse()

        assertThat(testList).doesNotContain(e3)

        // Revert testList
        testList = list.toMutableList()

        // #5
        // Add element
        assertThat(
            testList.addUnique(e4) { it.c }
        ).isFalse()

        assertThat(testList).doesNotContain(e4)

        // Revert testList
        testList = list.toMutableList()

        // #6
        // Add element
        assertThat(
            testList.addUnique(e4) { it.a }
        ).isTrue()

        assertThat(testList).contains(e4)

    }

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