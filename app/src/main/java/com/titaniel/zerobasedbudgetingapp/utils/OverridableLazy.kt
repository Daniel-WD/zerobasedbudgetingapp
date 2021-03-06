package com.titaniel.zerobasedbudgetingapp.utils

/**
 * Like [Lazy], with ability to change [implementation]
 * (source: https://proandroiddev.com/testing-the-untestable-the-case-of-the-viewmodel-delegate-975c09160993)
 */
class OverridableLazy<T>(var implementation: Lazy<T>) : Lazy<T> {
    override val value
        get() = implementation.value

    override fun isInitialized() = implementation.isInitialized()
}