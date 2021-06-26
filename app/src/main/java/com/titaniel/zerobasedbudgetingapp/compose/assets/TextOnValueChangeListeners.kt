package com.titaniel.zerobasedbudgetingapp.compose.assets

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.titaniel.zerobasedbudgetingapp.utils.moneyFormat
import android.R.string.no
import java.util.*

/**
 * Method for onValueChange for money text fields
 */
val moneyOnValueChange: (oldValue: String, newValue: TextFieldValue, maxCharacters: Int) -> String =
    { oldValue, newValue, maxCharacters ->

        // New value text
        val text = newValue.text

        if (text == "" || (text.length == 1 && !text[0].isDigit())) { // If text is empty or length is 1 with that one character not being digit...
            //... return 0
            "0"
        } else if (!text[text.length - 1].isDigit()) { // If last typed in character is not a digit...
            // Negate current value
            (-oldValue.toLong()).toString()
        } else { // Otherwise...
            // Have a maximum of maxCharacters characters + sign
            text.substring(
                0,
                text.length.coerceAtMost(maxCharacters + if (text[0] == '-') 1 else 0)
            ).toLong().toString()
        }
    }