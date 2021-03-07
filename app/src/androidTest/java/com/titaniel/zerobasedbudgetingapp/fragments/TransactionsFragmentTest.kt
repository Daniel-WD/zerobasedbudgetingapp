package com.titaniel.zerobasedbudgetingapp.fragments

import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.atPosition
import com.titaniel.zerobasedbudgetingapp._testutils.checkRecyclerViewContentHasCorrectData
import com.titaniel.zerobasedbudgetingapp._testutils.launchFragmentInHiltContainer
import com.titaniel.zerobasedbudgetingapp._testutils.replace
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions.TransactionsFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions.TransactionsViewModel
import com.titaniel.zerobasedbudgetingapp.utils.Utils
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate

@RunWith(MockitoJUnitRunner::class)
class TransactionsFragmentTest {

    /**
     * Fragment scenario
     */
    private lateinit var testFragment: TransactionsFragment

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockViewModel: TransactionsViewModel

    /**
     * Example transactions
     */
    private val exampleTransactions = listOf(
        Transaction(1, "payee1", "cat1", "", LocalDate.of(2020, 12, 1)),
        Transaction(2, "payee1", "cat1", "", LocalDate.of(2020, 10, 2)),
        Transaction(3, "payee2", "cat1", "hallo welt", LocalDate.of(2010, 1, 20)),
        Transaction(4, "payee4", "cat2", "", LocalDate.of(2023, 5, 12)),
        Transaction(5, "payee2", "cat2", "woowhoehwoe", LocalDate.of(2010, 11, 1)),
    )

    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockViewModel.transactions).thenReturn(MutableLiveData(exampleTransactions))

        // Launch scenario
        launchFragmentInHiltContainer<TransactionsFragment> {
            (this as TransactionsFragment).apply {
                replace(TransactionsFragment::viewModel, mockViewModel)
                testFragment = this
            }
        }
    }

    @Test
    fun starts_correctly() {
        checkTransactionListContent()
    }

    @Test
    fun handles_data_changes_correctly() {

        // Change data
        exampleTransactions[2].payeeName = "newPayee"

        checkTransactionListContent()
    }

    private fun checkTransactionListContent() {
        checkRecyclerViewContentHasCorrectData(R.id.transactionsList, exampleTransactions,
            { hasDescendant(withText(it.pay.toString())) },
            { hasDescendant(withText(it.categoryName)) },
            { hasDescendant(withText(it.payeeName)) },
            { hasDescendant(withText(Utils.convertLocalDateToString(it.date))) })
    }
}