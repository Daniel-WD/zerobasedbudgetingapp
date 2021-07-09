package com.titaniel.zerobasedbudgetingapp.compose.assets

import androidx.compose.ui.text.AnnotatedString
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class VisualTransformationsTest {

    @Test
    fun money_transformation_formats_text_correctly() {

        // Create transformation
        val moneyTransformation = MoneyVisualTransformation

        // Test different cases
        assertThat(moneyTransformation.filter(AnnotatedString("-0")).text.text).isEqualTo("0,00 €")
        assertThat(moneyTransformation.filter(AnnotatedString("-1")).text.text).isEqualTo("-0,01 €")
        assertThat(moneyTransformation.filter(AnnotatedString("12")).text.text).isEqualTo("0,12 €")
        assertThat(moneyTransformation.filter(AnnotatedString("-12")).text.text).isEqualTo("-0,12 €")
        assertThat(moneyTransformation.filter(AnnotatedString("123")).text.text).isEqualTo("1,23 €")
        assertThat(moneyTransformation.filter(AnnotatedString("-123")).text.text).isEqualTo("-1,23 €")
        assertThat(moneyTransformation.filter(AnnotatedString("123456")).text.text).isEqualTo("1.234,56 €")
        assertThat(moneyTransformation.filter(AnnotatedString("-123456")).text.text).isEqualTo("-1.234,56 €")

    }

    @Test
    fun money_transformation_maps_cursor_from_original_to_transformed_correctly() {

        // Offset mapping for 1 digit
        val offsetMapping1 = MoneyVisualTransformation.filter(AnnotatedString("1" /* "0,01 €" */)).offsetMapping

        assertThat(offsetMapping1.originalToTransformed(0)).isEqualTo(3)
        assertThat(offsetMapping1.originalToTransformed(1)).isEqualTo(4)

        // Offset mapping for 1 digit signed
        val offsetMapping1Signed = MoneyVisualTransformation.filter(AnnotatedString("-1" /* "-0,01 €" */)).offsetMapping

        assertThat(offsetMapping1Signed.originalToTransformed(0)).isEqualTo(0)
        assertThat(offsetMapping1Signed.originalToTransformed(1)).isEqualTo(1)
        assertThat(offsetMapping1Signed.originalToTransformed(2)).isEqualTo(5)

        // Offset mapping for 2 digit
        val offsetMapping2 = MoneyVisualTransformation.filter(AnnotatedString("12" /* "0,12 €" */)).offsetMapping

        assertThat(offsetMapping2.originalToTransformed(0)).isEqualTo(2)
        assertThat(offsetMapping2.originalToTransformed(1)).isEqualTo(3)
        assertThat(offsetMapping2.originalToTransformed(2)).isEqualTo(4)

        // Offset mapping for 2 digit signed
        val offsetMapping2Signed = MoneyVisualTransformation.filter(AnnotatedString("-12" /* "-0,12 €" */)).offsetMapping

        assertThat(offsetMapping2Signed.originalToTransformed(0)).isEqualTo(0)
        assertThat(offsetMapping2Signed.originalToTransformed(1)).isEqualTo(1)
        assertThat(offsetMapping2Signed.originalToTransformed(2)).isEqualTo(4)
        assertThat(offsetMapping2Signed.originalToTransformed(3)).isEqualTo(5)

        // Offset mapping for 7 digit
        val offsetMapping7 = MoneyVisualTransformation.filter(AnnotatedString("1234567" /* "12.345,67 €" */)).offsetMapping

        assertThat(offsetMapping7.originalToTransformed(0)).isEqualTo(0)
        assertThat(offsetMapping7.originalToTransformed(1)).isEqualTo(1)
        assertThat(offsetMapping7.originalToTransformed(2)).isEqualTo(2)
        assertThat(offsetMapping7.originalToTransformed(3)).isEqualTo(4)
        assertThat(offsetMapping7.originalToTransformed(4)).isEqualTo(5)
        assertThat(offsetMapping7.originalToTransformed(5)).isEqualTo(6)
        assertThat(offsetMapping7.originalToTransformed(6)).isEqualTo(8)
        assertThat(offsetMapping7.originalToTransformed(7)).isEqualTo(9)

        // Offset mapping for 7 digit signed
//        val offsetMapping7Signed = MoneyVisualTransformation.filter(AnnotatedString("-1234567" /* "-12.345,67 €" */)).offsetMapping
//
//        assertThat(offsetMapping7Signed.originalToTransformed(0)).isEqualTo(0)
//        assertThat(offsetMapping7Signed.originalToTransformed(1)).isEqualTo(1)
//        assertThat(offsetMapping7Signed.originalToTransformed(2)).isEqualTo(4)
//        assertThat(offsetMapping7Signed.originalToTransformed(3)).isEqualTo(5)

    }

}