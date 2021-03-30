package com.titaniel.zerobasedbudgetingapp.utils

import androidx.lifecycle.MutableLiveData

/**
 * Add [element] to the list, if the value of the property returned by [uniquePropertyDelegate] will be unique.
 */
fun <E, P> MutableList<E>.addUnique(element: E, uniquePropertyDelegate: (E) -> P): Boolean {
    find { uniquePropertyDelegate(it) == uniquePropertyDelegate(element) }?.let {
        add(element)
        return true
    }
    return false
}

/**
 * Re emits value, so that all observers get called again.
 */
fun <T> MutableLiveData<T>.reEmit() {
    this.value = this.value
}