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
class DoubleLiveData<A, B>(a: LiveData<A>, b: LiveData<B>) : MediatorLiveData<Pair<A?, B?>>() {
    init {
        addSource(a) { value = it to b.value }
        addSource(b) { value = a.value to it }
    }
}

/**
 * Class to combine 3 livedata objects.
 */
class TripleLiveData<A, B, C>(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>) : MediatorLiveData<Triple<A?, B?, C?>>() {
    init {
        addSource(a) { value = Triple(it, b.value, c.value) }
        addSource(b) { value = Triple(a.value, it, c.value) }
        addSource(c) { value = Triple(a.value, b.value, it) }
    }
}

/**
 * Class to combine 4 livedata objects.
 */
class QuadrupleLiveData<A, B, C, D>(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>, d: LiveData<D>) : MediatorLiveData<Quadruple<A?, B?, C?, D?>>() {
    init {
        addSource(a) { value = Quadruple(it, b.value, c.value, d.value) }
        addSource(b) { value = Quadruple(a.value, it, c.value, d.value) }
        addSource(c) { value = Quadruple(a.value, b.value, it, d.value) }
        addSource(d) { value = Quadruple(a.value, b.value, c.value, it) }
    }
}

/**
 * Class to combine 5 livedata objects.
 */
class QuintupleLiveData<A, B, C, D, E>(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>, d: LiveData<D>, e: LiveData<E>) : MediatorLiveData<Quintuple<A?, B?, C?, D?, E?>>() {
    init {
        addSource(a) { value = Quintuple(it, b.value, c.value, d.value, e.value) }
        addSource(b) { value = Quintuple(a.value, it, c.value, d.value, e.value) }
        addSource(c) { value = Quintuple(a.value, b.value, it, d.value, e.value) }
        addSource(d) { value = Quintuple(a.value, b.value, c.value, it, e.value) }
        addSource(e) { value = Quintuple(a.value, b.value, c.value, d.value, it) }
    }
}

/**
 * Class to combine 6 livedata objects.
 */
class SextupleLiveData<A, B, C, D, E, F>(a: LiveData<A>, b: LiveData<B>, c: LiveData<C>, d: LiveData<D>, e: LiveData<E>, f: LiveData<F>) : MediatorLiveData<Sextuple<A?, B?, C?, D?, E?, F?>>() {
    init {
        addSource(a) { value = Sextuple(it, b.value, c.value, d.value, e.value, f.value) }
        addSource(b) { value = Sextuple(a.value, it, c.value, d.value, e.value, f.value) }
        addSource(c) { value = Sextuple(a.value, b.value, it, d.value, e.value, f.value) }
        addSource(d) { value = Sextuple(a.value, b.value, c.value, it, e.value, f.value) }
        addSource(e) { value = Sextuple(a.value, b.value, c.value, d.value, it, f.value) }
        addSource(f) { value = Sextuple(a.value, b.value, c.value, d.value, e.value, it) }
    }
}

//2 = double, 3 = triple, 4 = quadruple, 5 = quintuple, 6 = sextuple, 7 = septuple, 8 = octuple
/**
 * Like [Pair] with 5 parameters
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