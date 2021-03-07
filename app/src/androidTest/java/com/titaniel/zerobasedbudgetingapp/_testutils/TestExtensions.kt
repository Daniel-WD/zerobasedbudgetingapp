package com.titaniel.zerobasedbudgetingapp._testutils

import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.titaniel.zerobasedbudgetingapp.utils.OverridableLazy
import org.hamcrest.Matcher
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

/**
 * Replaces ViewModels
 * (source: https://proandroiddev.com/testing-the-untestable-the-case-of-the-viewmodel-delegate-975c09160993)
 */
fun <VM : ViewModel, T> T.replace(
    viewModelDelegate: KProperty1<T, VM>, viewModel: VM
) {
    viewModelDelegate.isAccessible = true
    (viewModelDelegate.getDelegate(this) as
            OverridableLazy<VM>).implementation = lazy { viewModel }
}