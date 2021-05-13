package com.titaniel.zerobasedbudgetingapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Convert [localDate] to its string representation
 */
fun convertLocalDateToString(localDate: LocalDate): String {
    return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(localDate)
}

/**
 * Builds a MediatorLiveData that fires when on of [liveData] gets changed
 */
fun createSimpleMediatorLiveData(vararg liveData: LiveData<*>): MediatorLiveData<Unit> {
    val mediator: MediatorLiveData<Unit> = MediatorLiveData()
    liveData.forEach {
        mediator.addSource(it) { mediator.value = Unit }
    }
    return mediator
}