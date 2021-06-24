package com.titaniel.zerobasedbudgetingapp.compose.assets

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.titaniel.zerobasedbudgetingapp.utils.moneyFormat

val MoneyVisualTransformation = VisualTransformation { text ->
    val stringText = text.toString()

    val formattedText = stringText.toLong().moneyFormat()

    val delimiterIndexes =
        formattedText.mapIndexed { index, c -> if (",. €".contains(c)) index else -1 }
            .filterNot { it == -1 }

    val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {

            return when (text.filter { it.isDigit() }.length) {
                0 -> 0
                1 ->
                    if (text.contains('-')) {
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
                    if (text.contains('-')) {
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
                else -> offset + delimiterIndexes.filter { it < offset }.size
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return when (text.length) {
                0 -> 0
                1 ->
                    if (text.contains('-')) {
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
                    if (text.contains('-')) {
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
                else -> offset - delimiterIndexes.filter { it < offset }.size
            }
        }
    }

    TransformedText(AnnotatedString(formattedText), offsetMapping)
}

fun moneyOnValueChange(oldValue: String, newValue: TextFieldValue): String {
    val text = newValue.text

    return if (text == "" || (text.length == 1 && !text[0].isDigit())) {
        "0"
    } else if (!text[text.length - 1].isDigit()) {
        (-oldValue.toLong()).toString()
    } else {
        text.substring(
            0,
            text.length.coerceAtMost(6 + if (text[0] == '-') 1 else 0)
        ).toLong().toString()
    }
}