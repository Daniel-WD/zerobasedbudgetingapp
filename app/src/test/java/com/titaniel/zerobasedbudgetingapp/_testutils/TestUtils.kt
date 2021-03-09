package com.titaniel.zerobasedbudgetingapp._testutils

import org.mockito.Mockito

object TestUtils {

    /**
     * Mockito.any not null workaround
     */
    fun <T> any(): T {
        return Mockito.any()
    }

}