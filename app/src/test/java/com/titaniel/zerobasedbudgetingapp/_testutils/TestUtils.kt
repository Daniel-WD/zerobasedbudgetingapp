package com.titaniel.zerobasedbudgetingapp._testutils

import org.mockito.Mockito

object TestUtils {

    /**
     * Mockit.any not null workaround
     */
    fun <T> any(): T {
        return Mockito.any<T>()
    }

}