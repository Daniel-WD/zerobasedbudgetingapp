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
 * Visual transformation for a text field to format a text containing a money value that is a full
 * number of cents, into a usual money format.
 * 0 -> 0,00 €
 * -123 -> -1,23 €
 */

val MoneyVisualTransformation = VisualTransformation { text ->

    // Get plain string
    val plainText = text.toString()

    // Format text to money format
    val formattedText = plainText.toLong().moneyFormat()

    // Indexes of formattedText that is delimiter: ,.[space] and the currency sign
    val delimiterIndexes =
        formattedText.mapIndexed { index, c -> if (",. €".contains(c)) index else -1 }
            .filterNot { it == -1 }

    // Cursor offset mapping between original and transformed text
    val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {

            return when (plainText.filter { it.isDigit() }.length /* Amount of digits */) {
                0 -> 0
                1 ->
                    if (plainText.contains('-')) { // Is input negative?
                        when (offset) {
                            0 -> 0
                            1 -> 1
                            else -> 5
                        }
                    } else {
                        when (offset) {
                            0 -> 3
                            else -> 4
                        }
                    }
                2 ->
                    if (plainText.contains('-')) { // Is input negative?
                        when (offset) {
                            0 -> 0
                            1 -> 1
                            2 -> 4
                            else -> 5
                        }
                    } else {
                        when (offset) {
                            0 -> 2
                            1 -> 3
                            else -> 4
                        }
                    }
                else -> offset + delimiterIndexes.filter { it <= offset }.size /* Add count of delimiters until offset */
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return when (plainText.filter { it.isDigit() }.length /* Amount of digits */) {
                0 -> 0
                1 ->
                    if (plainText.contains('-')) { // Is input negative?
                        when {
                            offset == 0 -> 0
                            offset <= 4 -> 1
                            else -> 2
                        }
                    } else {
                        when {
                            offset <= 3 -> 0
                            else -> 1
                        }
                    }
                2 ->
                    if (plainText.contains('-')) { // Is input negative?
                        when {
                            offset == 0 -> 0
                            offset <= 3 -> 1
                            offset == 4 -> 2
                            else -> 3
                        }
                    } else {
                        when {
                            offset <= 2 -> 0
                            offset == 3 -> 1
                            else -> 2
                        }
                    }
                else -> offset - delimiterIndexes.filter { it < offset }.size /* Remove count of delimiters until offset */
            }
        }
    }

    TransformedText(AnnotatedString(formattedText), offsetMapping)
}