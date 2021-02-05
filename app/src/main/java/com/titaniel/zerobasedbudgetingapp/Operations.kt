package com.titaniel.zerobasedbudgetingapp

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

/**
 * Addition operation
 */
val addition = { a: Long, b: Long -> a.plus(b) }

/**
 * Suptraction operation
 */
val subtraction = { a: Long, b: Long -> a.minus(b) }