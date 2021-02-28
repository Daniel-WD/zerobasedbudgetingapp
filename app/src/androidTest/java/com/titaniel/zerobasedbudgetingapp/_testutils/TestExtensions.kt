package com.titaniel.zerobasedbudgetingapp._testutils

import androidx.lifecycle.ViewModel
import com.titaniel.zerobasedbudgetingapp.utils.OverridableLazy
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

/**
 * Replaces ViewModels
 * (source: https://proandroiddev.com/testing-the-untestable-the-case-of-the-viewmodel-delegate-975c09160993)
 */
fun <VM: ViewModel, T> T.replace(
        viewModelDelegate: KProperty1<T, VM>, viewModel: VM) {
    viewModelDelegate.isAccessible = true
    (viewModelDelegate.getDelegate(this) as
            OverridableLazy<VM>).implementation = lazy { viewModel }
}