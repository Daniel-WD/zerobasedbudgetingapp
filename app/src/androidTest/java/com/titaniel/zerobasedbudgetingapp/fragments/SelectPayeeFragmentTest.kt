package com.titaniel.zerobasedbudgetingapp.fragments

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
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionViewModel
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.PayeesListAdapter
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
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
        `when`(mockParentViewModel.payee).thenReturn(MutableLiveData())

        // Launch scenario
        launchFragmentInHiltContainer<SelectPayeeFragment> {
            (this as SelectPayeeFragment).apply {
                replace(SelectPayeeFragment::parentViewModel, mockParentViewModel)
                testFragment = spy(this)
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

        // Check payee value in view model
        assertThat(mockParentViewModel.payee.value).isEqualTo(examplePayees[4])

    }

    @Test
    fun adds_new_payee_correctly() {

        // Expected new payee
        val newPayee = "newPayee"

        // Type new payee
        onView(withId(R.id.etNewPayee)).perform(typeText(newPayee))

        // Confirm new payee
        onView(withId(R.id.ivAddPayee)).perform(click())

        // Check payee value in view model
        verify(mockParentViewModel).setNewPayee(newPayee)

    }

    private fun checkPayeeListContent() {
        checkRecyclerViewContentHasCorrectData(R.id.listPayees, examplePayees,
            { hasDescendant(withText(it.name)) })
    }
}