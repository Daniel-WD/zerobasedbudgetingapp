package com.titaniel.zerobasedbudgetingapp.compose.assets

import androidx.compose.ui.text.input.TextFieldValue
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TextOnValueChangeListenersTest {

    @Test
    fun money_on_value_change_returns_zero_on_empty_input() {

        assertThat(moneyOnValueChange(TextFieldValue(""), 10)).isEqualTo("0")
    }

    @Test
    fun money_on_value_change_returns_zero_when_last_character_is_a_negation_character_and_length_of_input_is_1() {

        assertThat(moneyOnValueChange(TextFieldValue(""), 10)).isEqualTo("0")
        assertThat(moneyOnValueChange(TextFieldValue(""), 10)).isEqualTo("0")
    }

    @Test
    fun money_on_value_change_ignores_invalid_characters_at_invalid_positions() {

        assertThat(moneyOnValueChange(TextFieldValue("298.3"), 10)).isEqualTo("2983")
        assertThat(moneyOnValueChange(TextFieldValue("2a983"), 10)).isEqualTo("2983")
        assertThat(moneyOnValueChange(TextFieldValue(" 2a983"), 10)).isEqualTo("2983")
        assertThat(moneyOnValueChange(TextFieldValue(" 29d.-a.sffgdg83"), 10)).isEqualTo("2983")
        assertThat(moneyOnValueChange(TextFieldValue(" - 29d.-a.sffgdg83"), 10)).isEqualTo("-2983")
        assertThat(moneyOnValueChange(TextFieldValue(" - 2983d.-a.sffgdg"), 10)).isEqualTo("2983")
    }

    @Test
    fun money_on_value_change_negates_input_when_negation_character_is_at_last_position() {

        assertThat(moneyOnValueChange(TextFieldValue("-2983."), 10)).isEqualTo("2983")
        assertThat(moneyOnValueChange(TextFieldValue("-2983-"), 10)).isEqualTo("2983")

        assertThat(moneyOnValueChange(TextFieldValue("2983456."), 10)).isEqualTo("-2983456")
        assertThat(moneyOnValueChange(TextFieldValue("2983-"), 10)).isEqualTo("-2983")
    }

    @Test
    fun money_on_value_change_cuts_input_to_max_characters_correctly() {
        assertThat(moneyOnValueChange(TextFieldValue("298399999999991"), 10)).isEqualTo("2983999999")

    }

}