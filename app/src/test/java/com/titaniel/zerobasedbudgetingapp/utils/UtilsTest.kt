package com.titaniel.zerobasedbudgetingapp.utils

import android.content.Context
import androidx.lifecycle.Lifecycle
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.datamanager.Category
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager
import com.titaniel.zerobasedbudgetingapp.datamanager.Transaction
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.lang.NullPointerException
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class UtilsTest {

    @Test
    fun converts_utc_timestamp_to_humanly_readable_date_string_correctly() {
        // Set timezone to GMT
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

        // Assert timestamps get formatted correctly
        assertThat(Utils.convertUtcToString(1612476124000L)).isEqualTo("04.02.2021")
        assertThat(Utils.convertUtcToString(1579561324000L)).isEqualTo("20.01.2020")
        assertThat(Utils.convertUtcToString(1767222124000L)).isEqualTo("31.12.2025")
    }

    @Test(expected = NullPointerException::class)
    fun updates_transaction_sums_correctly() {

        // Set timezone to GMT
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

        // positive Transaction, month known
        val transaction1 = Transaction(
            100,
            "fakePayee",
            "fakeCategory",
            "fakeDescription",
            1579561324000L // 20.01.2020
        )

        // negative Transaction, month known
        val transaction2 = Transaction(
            -400,
            "fakePayee",
            "fakeCategory",
            "fakeDescription",
            1579561324000L // 20.01.2020
        )

        // positive Transaction, month unknown
        val transaction3 = Transaction(
            800,
            "fakePayee",
            "fakeCategory",
            "fakeDescription",
            1585526400000L // 30.03.2020
        )

        // negative Transaction, month unknown
        val transaction4 = Transaction(
            -1200,
            "fakePayee",
            "fakeCategory",
            "fakeDescription",
            1585526400000L // 30.03.2020
        )

        // Transaction category unknown
        val transaction5 = Transaction(
            -1200,
            "fakePayee",
            "unknownCategory",
            "fakeDescription",
            1585526400000L // 30.03.2020
        )

        // Transaction, to be budgeted
        val transaction6 = Transaction(
            700,
            "fakePayee",
            Category.TO_BE_BUDGETED,
            "fakeDescription",
            1585526400000L // 30.03.2020
        )

        // Category
        val category = Category(
            mutableMapOf(),
            mutableMapOf(1577836800000 /* 01.01.2020 */ to 50, 1588291200000 /* 01.05.2020 */ to 0),
            "fakeCategory"
        )

        // DataManager
        val dataManager =
            spy(DataManager(mock(Context::class.java), mock(Lifecycle::class.java)))
        dataManager.toBeBudgeted = 100
        doReturn(mutableListOf(category)).`when`(dataManager).categories

        // Add transaction correctly, when its month is known
        Utils.updateTransactionSums(transaction1, dataManager)
        assertThat(category.transactionSums[1577836800000 /* 01.01.2020*/]).isEqualTo(150)

        // Add negative transaction correctly, when its month is known
        Utils.updateTransactionSums(transaction2, dataManager)
        assertThat(category.transactionSums[1577836800000 /* 01.01.2020*/]).isEqualTo(-250)

        // Add transaction correctly, when its month is unknown
        Utils.updateTransactionSums(transaction3, dataManager)
        assertThat(category.transactionSums[1583020800000 /* 01.03.2020*/]).isEqualTo(800)

        // Add negative transaction correctly, when its month is unknown
        Utils.updateTransactionSums(transaction4, dataManager)
        assertThat(category.transactionSums[1583020800000 /* 01.03.2020*/]).isEqualTo(-400)

        // Remove transaction correctly, when its month is known
        Utils.updateTransactionSums(transaction4, dataManager, true)
        assertThat(category.transactionSums[1583020800000 /* 01.03.2020*/]).isEqualTo(800)

        // Clear transaction sums
        category.transactionSums.clear()

        // Remove transaction correctly, when its month is unknown
        Utils.updateTransactionSums(transaction4, dataManager, true)
        assertThat(category.transactionSums[1583020800000 /* 01.03.2020*/]).isEqualTo(1200)

        // Category not found -> exception
        Utils.updateTransactionSums(transaction5, dataManager)

        // Add transaction correctly, when category is TO_BE_BUDGETED
        Utils.updateTransactionSums(transaction6, dataManager)
        assertThat(dataManager.toBeBudgeted).isEqualTo(700)

        // Remove transaction correctly, when category is TO_BE_BUDGETED
        Utils.updateTransactionSums(transaction6, dataManager, true)
        assertThat(dataManager.toBeBudgeted).isEqualTo(0)

    }

}