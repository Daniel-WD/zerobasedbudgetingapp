package com.titaniel.zerobasedbudgetingapp.fragments

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.atPosition
import com.titaniel.zerobasedbudgetingapp._testutils.launchFragmentInHiltContainer
import com.titaniel.zerobasedbudgetingapp._testutils.replace
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.CategoriesListAdapter
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryViewModel
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.PayeesListAdapter
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeViewModel
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
    private lateinit var mockViewModel: SelectPayeeViewModel

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
        `when`(mockViewModel.payees).thenReturn(MutableLiveData(examplePayees))

        // Launch scenario
        launchFragmentInHiltContainer<SelectPayeeFragment> {
            (this as SelectPayeeFragment).apply {
                replace(SelectPayeeFragment::viewModel, mockViewModel)
                testFragment = this
            }
        }
    }

    @Test
    fun starts_correctly() {

        // Payee list content is correct
        // Entry 1
        onView(withId(R.id.listPayees)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(withText("payee1"))
                )
            )
        )

        // Entry 2
        onView(withId(R.id.listPayees)).check(
            matches(
                atPosition(
                    1,
                    hasDescendant(withText("payee2"))
                )
            )
        )

        // Entry 3
        onView(withId(R.id.listPayees)).check(
            matches(
                atPosition(
                    2,
                    hasDescendant(withText("payee3"))
                )
            )
        )

        // Entry 4
        onView(withId(R.id.listPayees)).check(
            matches(
                atPosition(
                    3,
                    hasDescendant(withText("payee4"))
                )
            )
        )

        // Entry 5
        onView(withId(R.id.listPayees)).check(
            matches(
                atPosition(
                    4,
                    hasDescendant(withText("payee5"))
                )
            )
        )

    }

    @Test
    fun handles_data_change_correctly() {

        // Change data
        examplePayees.add(Payee("newPayee"))

        // Assert data correct
        // Entry 6
        onView(withId(R.id.listPayees)).check(
            matches(
                atPosition(
                    5,
                    hasDescendant(withText("newPayee"))
                )
            )
        )

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
}