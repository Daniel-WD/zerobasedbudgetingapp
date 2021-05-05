package com.titaniel.zerobasedbudgetingapp.utils

import androidx.lifecycle.MutableLiveData
import java.time.YearMonth

/**
 * Add [element] to the list, if the value of the property returned by [uniquePropertyDelegate] will be unique.
 * Null is treated as normal value.
 */
fun <E, P> MutableList<E>.addUnique(element: E, uniquePropertyDelegate: (E) -> P): Boolean {
    find { uniquePropertyDelegate(it) == uniquePropertyDelegate(element) } ?: run {
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

/**
 * Iterator for [YearMonth] to iterate form [startMonth] to [endMonthInclusive] by [monthStep].
 */
class YearMonthIterator(
    private val startMonth: YearMonth,
    private val endMonthInclusive: YearMonth,
    private val monthStep: Long = 1
) : Iterator<YearMonth> {

    /**
     * The iterators current value
     */
    private var current = startMonth

    override fun hasNext() = current <= endMonthInclusive

    override fun next() = current.also { current = current.plusMonths(monthStep) }

}

/**
 * Progression for [YearMonth] with [start], [endInclusive] and [monthStep].
 */
class YearMonthProgression(
    override val start: YearMonth,
    override val endInclusive: YearMonth,
    private val monthStep: Long = 1
) : Iterable<YearMonth>, ClosedRange<YearMonth> {

    /**
     * Create new YearMonthProgression with [monthStep]
     */
    infix fun step(monthStep: Long) = YearMonthProgression(start, endInclusive, monthStep)

    override fun iterator() = YearMonthIterator(start, endInclusive, monthStep)

}

/**
 * Override range operator to return [YearMonthProgression].
 */
operator fun YearMonth.rangeTo(other: YearMonth) = YearMonthProgression(this, other)