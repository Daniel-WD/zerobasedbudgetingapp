package com.titaniel.zerobasedbudgetingapp.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

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