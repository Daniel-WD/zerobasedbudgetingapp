package com.titaniel.zerobasedbudgetingapp.fragments

import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.checkRecyclerViewContentHasCorrectData
import com.titaniel.zerobasedbudgetingapp._testutils.launchFragmentInHiltContainer
import com.titaniel.zerobasedbudgetingapp._testutils.replace
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionViewModel
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.PayeesListAdapter
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SelectPayeeFragmentTest {

    /**
     * Fragment scenario
     */
    private lateinit var testFragment: SelectPayeeFragment

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockParentViewModel: AddEditTransactionViewModel

    /**
     * Example categories
     */
    private val examplePayees = mutableListOf(
        Payee("payee1"),
        Payee("payee2"),
        Payee("payee3"),
        Payee("payee4"),
        Payee("payee5")
    )

    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockParentViewModel.allPayees).thenReturn(MutableLiveData(examplePayees))

        // Launch scenario
        launchFragmentInHiltContainer<SelectPayeeFragment> {
            (this as SelectPayeeFragment).apply {
                replace(SelectPayeeFragment::parentViewModel, mockParentViewModel)
                testFragment = this
            }
        }
    }

    @Test
    fun starts_correctly() {

        checkPayeeListContent()

    }

    @Test
    fun handles_data_change_correctly() {

        // Change data
        examplePayees.add(Payee("newPayee"))

        checkPayeeListContent()

    }

    @Test
    fun performs_item_click_correctly() {

        // Set fragment result listener
        testFragment.requireActivity().runOnUiThread {
            testFragment.setFragmentResultListener(AddEditTransactionActivity.PAYEE_REQUEST_KEY) { _, bundle ->
                assertThat(bundle[SelectPayeeFragment.PAYEE_KEY]).isEqualTo("payee5")
            }
        }

        // Click item
        onView(withId(R.id.listPayees))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<PayeesListAdapter.PayeeItem>(
                    4,
                    click()
                )
            )

        // Check if fragment finishes
        assertThat(testFragment.isAdded).isFalse()

    }

    @Test
    fun adds_new_payee_correctly() {

        // Expected new payee
        val newPayee = "newPayee"

        // Set fragment result listener
        testFragment.requireActivity().runOnUiThread {
            testFragment.setFragmentResultListener(AddEditTransactionActivity.PAYEE_REQUEST_KEY) { _, bundle ->
                assertThat(bundle[SelectPayeeFragment.PAYEE_KEY]).isEqualTo(newPayee)
            }
        }

        // Type new payee
        onView(withId(R.id.etNewPayee)).perform(typeText(newPayee))

        // Confirm new payee
        onView(withId(R.id.ivAddPayee)).perform(click())

        // Check if fragment finishes
        assertThat(testFragment.isAdded).isFalse()

    }

    private fun checkPayeeListContent() {
        checkRecyclerViewContentHasCorrectData(R.id.listPayees, examplePayees,
            { hasDescendant(withText(it.name)) })
    }
}