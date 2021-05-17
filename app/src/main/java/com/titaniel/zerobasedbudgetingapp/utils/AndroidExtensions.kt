package com.titaniel.zerobasedbudgetingapp.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import java.text.NumberFormat

/**
 * Show keyboard for activities
 */
fun AppCompatActivity.forceShowSoftKeyboard() {
    val inputMethodManager: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}


/**
 * Hide keyboard for activities
 */
fun AppCompatActivity.forceHideSoftKeyboard() {
    val inputMethodManager: InputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(
        findViewById<View>(android.R.id.content).windowToken,
        0
    )
}

/**
 * Like [viewModels], with replaceable lazy
 * (source: https://proandroiddev.com/testing-the-untestable-the-case-of-the-viewmodel-delegate-975c09160993)
 */
inline fun <reified VM : ViewModel> Fragment.provideViewModel(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> =
    OverridableLazy(viewModels(ownerProducer, factoryProducer))


/**
 * Like [viewModels], with replaceable lazy
 * (source: https://proandroiddev.com/testing-the-untestable-the-case-of-the-viewmodel-delegate-975c09160993)
 */
inline fun <reified VM : ViewModel> AppCompatActivity.provideViewModel(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> =
    OverridableLazy(viewModels(factoryProducer))

/**
 * Like [viewModels], with replaceable lazy
 * (source: https://proandroiddev.com/testing-the-untestable-the-case-of-the-viewmodel-delegate-975c09160993)
 */
inline fun <reified VM : ViewModel> Fragment.provideActivityViewModel(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> =
    OverridableLazy(activityViewModels(factoryProducer))

fun TextView.setMoneyValue(cents: Long) {
    text = NumberFormat.getCurrencyInstance().format(cents.toDouble()/100)
}

/**
 * Returns all digits in text. Not spaced.
 */
fun TextView.number() = text.filter { Character.isDigit(it) }.toString().toLong()