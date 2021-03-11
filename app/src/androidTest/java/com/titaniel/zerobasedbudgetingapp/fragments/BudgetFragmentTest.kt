package com.titaniel.zerobasedbudgetingapp.fragments

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.checkRecyclerViewContentHasCorrectData
import com.titaniel.zerobasedbudgetingapp._testutils.launchFragmentInHiltContainer
import com.titaniel.zerobasedbudgetingapp._testutils.replace
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.BudgetFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.BudgetListAdapter
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.BudgetViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate

@RunWith(MockitoJUnitRunner::class)
class BudgetFragmentTest {

    /**
     * Fragment scenario
     */
    private lateinit var testFragment: BudgetFragment

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockViewModel: BudgetViewModel

    /**
     * ToBeBudgeted example data
     */
    private val toBeBudgeted = 1000L

    /**
     * BudgetsOfMonth example data
     */
    private val exampleBudgetsOfMonth = listOf(
        Budget("sex", LocalDate.of(2020, 10, 1), 20),
        Budget("girlfriend", LocalDate.of(2020, 10, 1), 100),
        Budget("computer", LocalDate.of(2020, 10, 1), 202342),
        Budget("ide", LocalDate.of(2020, 10, 1), 203),
        Budget("groceries", LocalDate.of(2020, 10, 1), 21230)
    )

    /**
     * AvailableMoney example data
     */
    private var exampleAvailableMoney = mutableMapOf(
        exampleBudgetsOfMonth[0] to 100L,
        exampleBudgetsOfMonth[1] to 200L,
        exampleBudgetsOfMonth[2] to 0L,
        exampleBudgetsOfMonth[3] to 4L,
        exampleBudgetsOfMonth[4] to -10000L
    )

    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockViewModel.toBeBudgeted).thenReturn(MutableLiveData(toBeBudgeted))
        `when`(mockViewModel.budgetsWithCategoryOfMonth).thenReturn(MutableLiveData(exampleBudgetsOfMonth))
        `when`(mockViewModel.availableMoney).thenReturn(MutableLiveData(exampleAvailableMoney))

        // Launch scenario
        launchFragmentInHiltContainer<BudgetFragment> {
            (this as BudgetFragment).apply {
                replace(BudgetFragment::viewModel, mockViewModel)
                testFragment = this
            }
        }
    }

    @Test
    fun starts_correctly() {

        // ToBeBudgeted text is correct
        onView(withId(R.id.tvToBeBudgeted)).check(matches(withText(toBeBudgeted.toString())))

        checkBudgetListContent()

    }

    @Test
    fun performs_item_click_correctly() {

        // Click on second item
        onView(withId(R.id.listBudgeting))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<BudgetListAdapter.BudgetingItem>(
                    0,
                    click()
                )
            )

        // Check if UpdateBudgetFragment has been opened
        onView(withId(R.id.etBudgeted)).check(matches(isDisplayed()))

    }

    @Test
    fun handles_to_be_budgeted_change_correctly() {

        // Define new to be budgeted value
        val newToBeBudgeted = 5000L

        // Change to be budgeted value
        testFragment.requireActivity().runOnUiThread {
            mockViewModel.toBeBudgeted.value = newToBeBudgeted
        }

        // Check if new value is displayed
        onView(withId(R.id.tvToBeBudgeted)).check(matches(withText(newToBeBudgeted.toString())))

    }

    @Test
    fun handles_budgets_of_month_change_correctly() {

        // Define new category name, and budgeted value
        val newCat = "superAwesomeNewCat"
        val newBudgetedValue = 234234L

        // Set new values
        exampleBudgetsOfMonth[2].categoryName = newCat
        exampleBudgetsOfMonth[2].budgeted = newBudgetedValue

        // Reset available money
        exampleAvailableMoney = mutableMapOf(
            exampleBudgetsOfMonth[0] to 100L,
            exampleBudgetsOfMonth[1] to 200L,
            exampleBudgetsOfMonth[2] to 0L,
            exampleBudgetsOfMonth[3] to 4L,
            exampleBudgetsOfMonth[4] to -10000L
        )

        // Set budgetsOfMonth value in ViewModel
        testFragment.requireActivity().runOnUiThread {
            mockViewModel.availableMoney.value = exampleAvailableMoney
        }

        checkBudgetListContent()

    }

    @Test
    fun handles_available_money_change_correctly() {

        // Define new available money value
        val newAvailableMoneyValue = 234234999L

        // Set new values
        exampleAvailableMoney[exampleBudgetsOfMonth[2]] = newAvailableMoneyValue

        checkBudgetListContent()

    }

    private fun checkBudgetListContent() {
        checkRecyclerViewContentHasCorrectData(R.id.listBudgeting, exampleBudgetsOfMonth,
            { hasDescendant(withText(it.budgeted.toString())) },
            { hasDescendant(withText(it.categoryName)) })
        checkRecyclerViewContentHasCorrectData(R.id.listBudgeting, exampleAvailableMoney.toList(),
            { hasDescendant(withText(it.second.toString())) })
    }

}