package com.titaniel.zerobasedbudgetingapp.fragments

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.atPosition
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
    private val budgetsOfMonth = listOf(
        Budget("sex", LocalDate.of(2020, 10, 1), 20),
        Budget("girlfriend", LocalDate.of(2020, 10, 1), 100),
        Budget("computer", LocalDate.of(2020, 10, 1), 202342),
        Budget("ide", LocalDate.of(2020, 10, 1), 203),
        Budget("groceries", LocalDate.of(2020, 10, 1), 21230)
    )

    /**
     * AvailableMoney example data
     */
    private val availableMoney = mutableMapOf(
        budgetsOfMonth[0] to 100L,
        budgetsOfMonth[1] to 200L,
        budgetsOfMonth[2] to 0L,
        budgetsOfMonth[3] to 4L,
        budgetsOfMonth[4] to -10000L
    )

    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockViewModel.toBeBudgeted).thenReturn(MutableLiveData(toBeBudgeted))
        `when`(mockViewModel.budgetsOfMonth).thenReturn(MutableLiveData(budgetsOfMonth))
        `when`(mockViewModel.availableMoney).thenReturn(MutableLiveData(availableMoney))

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

        // budget list content is correct
        // Entry 1
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(withText("sex"))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(withText("20"))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    0,
                    hasDescendant(withText("100"))
                )
            )
        )

        // Entry 2
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    1,
                    hasDescendant(withText("girlfriend"))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    1,
                    hasDescendant(withText("100"))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    1,
                    hasDescendant(withText("200"))
                )
            )
        )

        // Entry 3
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    2,
                    hasDescendant(withText("computer"))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    2,
                    hasDescendant(withText("202342"))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    2,
                    hasDescendant(withText("0"))
                )
            )
        )

        // Entry 4
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    3,
                    hasDescendant(withText("ide"))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    3,
                    hasDescendant(withText("203"))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    3,
                    hasDescendant(withText("4"))
                )
            )
        )

        // Entry 5
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    4,
                    hasDescendant(withText("groceries"))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    4,
                    hasDescendant(withText("21230"))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    4,
                    hasDescendant(withText("-10000"))
                )
            )
        )

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
        budgetsOfMonth[2].categoryName = newCat
        budgetsOfMonth[2].budgeted = newBudgetedValue

        // Check if new values are displayed

        // Entry 3
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    2,
                    hasDescendant(withText(newCat))
                )
            )
        )
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    2,
                    hasDescendant(withText(newBudgetedValue.toString()))
                )
            )
        )

    }

    @Test
    fun handles_available_money_change_correctly() {

        // Define new available money value
        val newAvailableMoneyValue = 234234999L

        // Set new values
        availableMoney[budgetsOfMonth[2]] = newAvailableMoneyValue

        // Check if new values are displayed

        // Entry 3
        onView(withId(R.id.listBudgeting)).check(
            matches(
                atPosition(
                    2,
                    hasDescendant(withText(newAvailableMoneyValue.toString()))
                )
            )
        )

    }

}