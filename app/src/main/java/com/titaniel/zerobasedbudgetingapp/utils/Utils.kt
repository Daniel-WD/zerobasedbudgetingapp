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

/**
 * Class to combine 2 livedata objects.
 */
class DoubleLiveData<A, B>(a: LiveData<A>, b: LiveData<B>, fireOnlyOnChange: Boolean = true) :
    MediatorLiveData<Pair<A?, B?>>() {

    /**
     * Last a value
     */
    var lastA: A? = a.value

    /**
     * Last b value
     */
    var lastB: B? = b.value

    init {
        addSource(a) {
            if (!fireOnlyOnChange || it != a.value) value = it to lastB
            lastA = it
        }
        addSource(b) {
            if (!fireOnlyOnChange || it != b.value) value = lastA to it
            lastB = it
        }
    }
}

/**
 * Class to combine 3 livedata objects.
 */
class TripleLiveData<A, B, C>(
    a: LiveData<A>,
    b: LiveData<B>,
    c: LiveData<C>,
    fireOnlyOnChange: Boolean = true
) : MediatorLiveData<Triple<A?, B?, C?>>() {

    /**
     * Last a value
     */
    var lastA: A? = a.value

    /**
     * Last b value
     */
    var lastB: B? = b.value

    /**
     * Last c value
     */
    var lastC: C? = c.value

    init {
        addSource(a) {
            if (!fireOnlyOnChange || it != lastA) value = Triple(it, lastB, lastC)
            lastA = it
        }
        addSource(b) {
            if (!fireOnlyOnChange || it != lastB) value = Triple(lastA, it, lastC)
            lastB = it
        }
        addSource(c) {
            if (!fireOnlyOnChange || it != lastC) value = Triple(lastA, lastB, it)
            lastC = it
        }
    }
}

/**
 * Class to combine 4 livedata objects.
 */
class QuadrupleLiveData<A, B, C, D>(
    a: LiveData<A>,
    b: LiveData<B>,
    c: LiveData<C>,
    d: LiveData<D>,
    fireOnlyOnChange: Boolean = true
) : MediatorLiveData<Quadruple<A?, B?, C?, D?>>() {

    /**
     * Last a value
     */
    var lastA: A? = a.value

    /**
     * Last b value
     */
    var lastB: B? = b.value

    /**
     * Last c value
     */
    var lastC: C? = c.value

    /**
     * Last d value
     */
    var lastD: D? = d.value

    init {
        addSource(a) {
            if (!fireOnlyOnChange || it != lastA) value = Quadruple(it, lastB, lastC, lastD)
            lastA = it
        }
        addSource(b) {
            if (!fireOnlyOnChange || it != lastB) value = Quadruple(lastA, it, lastC, lastD)
            lastB = it
        }
        addSource(c) {
            if (!fireOnlyOnChange || it != lastC) value = Quadruple(lastA, lastB, it, lastD)
            lastC = it
        }
        addSource(d) {
            if (!fireOnlyOnChange || it != lastD) value = Quadruple(lastA, lastB, lastC, it)
            lastD = it
        }
    }
}

/**
 * Class to combine 5 livedata objects.
 */
class QuintupleLiveData<A, B, C, D, E>(
    a: LiveData<A>,
    b: LiveData<B>,
    c: LiveData<C>,
    d: LiveData<D>,
    e: LiveData<E>,
    fireOnlyOnChange: Boolean = true
) : MediatorLiveData<Quintuple<A?, B?, C?, D?, E?>>() {

    /**
     * Last a value
     */
    var lastA: A? = a.value

    /**
     * Last b value
     */
    var lastB: B? = b.value

    /**
     * Last c value
     */
    var lastC: C? = c.value

    /**
     * Last d value
     */
    var lastD: D? = d.value

    /**
     * Last e value
     */
    var lastE: E? = e.value

    init {
        addSource(a) {
            if (!fireOnlyOnChange || it != lastA) value = Quintuple(it, lastB, lastC, lastD, lastE)
            lastA = it
        }
        addSource(b) {
            if (!fireOnlyOnChange || it != lastB) value = Quintuple(lastA, it, lastC, lastD, lastE)
            lastB = it
        }
        addSource(c) {
            if (!fireOnlyOnChange || it != lastC) value = Quintuple(lastA, lastB, it, lastD, lastE)
            lastC = it
        }
        addSource(d) {
            if (!fireOnlyOnChange || it != lastD) value = Quintuple(lastA, lastB, lastC, it, lastE)
            lastD = it
        }
        addSource(e) {
            if (!fireOnlyOnChange || it != lastE) value = Quintuple(lastA, lastB, lastC, lastD, it)
            lastE = it
        }
    }
}

/**
 * Class to combine 6 livedata objects.
 */
class SextupleLiveData<A, B, C, D, E, F>(
    a: LiveData<A>,
    b: LiveData<B>,
    c: LiveData<C>,
    d: LiveData<D>,
    e: LiveData<E>,
    f: LiveData<F>,
    fireOnlyOnChange: Boolean = true
) : MediatorLiveData<Sextuple<A?, B?, C?, D?, E?, F?>>() {

    /**
     * Last a value
     */
    var lastA: A? = a.value

    /**
     * Last b value
     */
    var lastB: B? = b.value

    /**
     * Last c value
     */
    var lastC: C? = c.value

    /**
     * Last d value
     */
    var lastD: D? = d.value

    /**
     * Last e value
     */
    var lastE: E? = e.value

    /**
     * Last f value
     */
    var lastF: F? = f.value

    init {
        addSource(a) {
            if (!fireOnlyOnChange || it != lastA) value =
                Sextuple(it, lastB, lastC, lastD, lastE, lastF)
            lastA = it
        }
        addSource(b) {
            if (!fireOnlyOnChange || it != lastB) value =
                Sextuple(lastA, it, lastC, lastD, lastE, lastF)
            lastB = it
        }
        addSource(c) {
            if (!fireOnlyOnChange || it != lastC) value =
                Sextuple(lastA, lastB, it, lastD, lastE, lastF)
            lastC = it
        }
        addSource(d) {
            if (!fireOnlyOnChange || it != lastD) value =
                Sextuple(lastA, lastB, lastC, it, lastE, lastF)
            lastD = it
        }
        addSource(e) {
            if (!fireOnlyOnChange || it != lastE) value =
                Sextuple(lastA, lastB, lastC, lastD, it, lastF)
            lastE = it
        }
        addSource(f) {
            if (!fireOnlyOnChange || it != lastF) value =
                Sextuple(lastA, lastB, lastC, lastD, lastE, it)
            lastF = it
        }
    }
}

/**
 * Like [Pair] with 4 parameters
 */
data class Quadruple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

/**
 * Like [Pair] with 5 parameters
 */
data class Quintuple<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E)

/**
 * Like [Pair] with 6 parameters
 */
data class Sextuple<A, B, C, D, E, F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F)