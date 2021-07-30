package com.titaniel.zerobasedbudgetingapp.compose.assets

import androidx.compose.ui.text.input.TextFieldValue

/**
 * Method for handling value changes in TextField with a money value. [oldValue] being the previous
 * text, [newValue] being the new text value and [maxCharacters] being the maximum amount of digits
 * the money text should have. This excludes the minus symbol.
 *
 * The value can be negated by a new character of '.' or '-'.
 *
 * Characters that are not negation characters or digits are ignored.
 */
fun moneyOnValueChange(newValue: TextFieldValue, maxCharacters: Int): String {

    // New value text
    val text = newValue.text
        // Filter valid characters
        .filter { char -> char.isDigit() || ".-".contains(char) }
        .let { textValidCharacters ->
            // Filter '.' and '-' that are on valid positions
            textValidCharacters.filterIndexed { i, char -> if (".-".contains(char)) (i == textValidCharacters.length - 1) || (char == '-' && i == 0) else true }
        }

    // If text is empty or length is 1 with that one character not being digit...
    return if (text == "" || (text.length == 1 && !text[0].isDigit()) /* e.g. '-' */) {
        //... return 0
        "0"
    } else if (!text[text.length - 1].isDigit() /* e.g. '-' */) { // If last typed in character is not a digit...
        // Negate current value
        (-text.take(text.length - 1).toLong()).toString()

    } else { // Otherwise...
        // Have a maximum of maxCharacters characters + sign
        text.take(text.length.coerceAtMost(maxCharacters + if (text[0] == '-') 1 else 0)).toLong()
            .toString()
    }
}