package com.titaniel.zerobasedbudgetingapp.datamanager

import android.content.Context
import androidx.lifecycle.Lifecycle
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class DataManagerUnitTest {

    private val mCategory = Category(
        mutableMapOf(),
        mutableMapOf(1577836800000 /* 01.01.2020 */ to 50, 1588291200000 /* 01.05.2020 */ to 0),
        "fakeCategory"
    )

    private lateinit var mDataManager: DataManager

    @Before
    fun setup() {

        // Set timezone to GMT
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

        // DataManager
        mDataManager = DataManager(
            Mockito.mock(Context::class.java),
            Mockito.mock(Lifecycle::class.java)
        )

        mDataManager.state = DataManager.STATE_LOADED
        mDataManager.toBeBudgeted = 100
        mDataManager.categories.add(mCategory)

    }

    @Test
    fun updates_category_correctly_with_positive_transaction_and_known_month() {

        val transaction = Transaction(
            100,
            "fakePayee",
            "fakeCategory",
            "fakeDescription",
            1579561324000L // 20.01.2020
        )

        // Add transaction correctly, when its month is known
        mDataManager.updateCategoryTransactionSums(transaction)
        Truth.assertThat(mCategory.transactionSums[1577836800000 /* 01.01.2020*/]).isEqualTo(150)

    }

    @Test
    fun update_category_correctly_with_negative_transaction_and_known_month() {

        // negative Transaction, month known
        val transaction = Transaction(
            -400,
            "fakePayee",
            "fakeCategory",
            "fakeDescription",
            1579561324000L // 20.01.2020
        )

        // Add negative transaction correctly, when its month is known
        mDataManager.updateCategoryTransactionSums(transaction)
        Truth.assertThat(mCategory.transactionSums[1577836800000 /* 01.01.2020*/]).isEqualTo(-350)

    }

    @Test
    fun update_category_correctly_with_positive_transaction_and_unknown_month() {

        // positive Transaction, month unknown
        val transaction = Transaction(
            800,
            "fakePayee",
            "fakeCategory",
            "fakeDescription",
            1585526400000L // 30.03.2020
        )

        // Add transaction correctly, when its month is unknown
        mDataManager.updateCategoryTransactionSums(transaction)
        Truth.assertThat(mCategory.transactionSums[1583020800000 /* 01.03.2020*/]).isEqualTo(800)

    }

    @Test
    fun update_category_correctly_with_negative_transaction_and_unknown_month() {


        // negative Transaction, month unknown
        val transaction = Transaction(
            -1200,
            "fakePayee",
            "fakeCategory",
            "fakeDescription",
            1585526400000L // 30.03.2020
        )

        // Add negative transaction correctly, when its month is unknown
        mDataManager.updateCategoryTransactionSums(transaction)
        Truth.assertThat(mCategory.transactionSums[1583020800000 /* 01.03.2020*/]).isEqualTo(-1200)

    }

    @Test
    fun update_category_correctly_with_negative_transaction_and_known_month_that_should_be_removed() {


        // negative Transaction, month unknown
        val transaction = Transaction(
            -1200,
            "fakePayee",
            "fakeCategory",
            "fakeDescription",
            1579561324000L // 20.01.2020
        )

        // Remove transaction correctly, when its month is known
        mDataManager.updateCategoryTransactionSums(transaction, true)
        Truth.assertThat(mCategory.transactionSums[1577836800000 /* 01.01.2020*/]).isEqualTo(1250)

    }

    @Test
    fun update_category_correctly_with_negative_transaction_and_unknown_month_that_should_be_removed() {

        // negative Transaction, month unknown
        val transaction = Transaction(
            -1200,
            "fakePayee",
            "fakeCategory",
            "fakeDescription",
            1585526400000L // 30.03.2020
        )

        // Remove transaction correctly, when its month is unknown
        mDataManager.updateCategoryTransactionSums(transaction, true)
        Truth.assertThat(mCategory.transactionSums[1583020800000 /* 01.03.2020*/]).isEqualTo(1200)

    }

    @Test(expected = NullPointerException::class)
    fun update_category_should_throw_null_pointer_exception_when_category_is_unknown() {

        // Transaction category unknown
        val transaction = Transaction(
            -1200,
            "fakePayee",
            "unknownCategory",
            "fakeDescription",
            1585526400000L // 30.03.2020
        )

        // Category not found -> exception
        mDataManager.updateCategoryTransactionSums(transaction)
    }

    @Test
    fun update_to_be_budgeted_value_correctly() {

        // Transaction, to be budgeted
        val transaction = Transaction(
            700,
            "fakePayee",
            Category.TO_BE_BUDGETED,
            "fakeDescription",
            1585526400000L // 30.03.2020
        )

        // Add transaction correctly, when category is TO_BE_BUDGETED
        mDataManager.updateCategoryTransactionSums(transaction)
        Truth.assertThat(mDataManager.toBeBudgeted).isEqualTo(800)
    }

    @Test
    fun update_to_be_budgeted_value_correctly_when_it_should_be_removed() {

        // Transaction, to be budgeted
        val transaction = Transaction(
            700,
            "fakePayee",
            Category.TO_BE_BUDGETED,
            "fakeDescription",
            1585526400000L // 30.03.2020
        )

        // Remove transaction correctly, when category is TO_BE_BUDGETED
        mDataManager.updateCategoryTransactionSums(transaction, true)
        Truth.assertThat(mDataManager.toBeBudgeted).isEqualTo(-600)
    }

    @Test(expected = IllegalStateException::class)
    fun update_category_should_throw_illegal_state_exception_when_state_is_not_loaded() {
        mDataManager.state = DataManager.STATE_NOT_LOADED
        mDataManager.updateCategoryTransactionSums(Transaction(0, "", "", "", 0))
    }

}