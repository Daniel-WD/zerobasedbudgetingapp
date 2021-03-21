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
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.BudgetFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.BudgetListAdapter
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.BudgetViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.time.YearMonth

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
    private val exampleBudgetsWithCategoryOfMonth = listOf(
        BudgetWithCategory(Budget(0, YearMonth.of(2020, 10), 20), Category("a", 0, 0)),
        BudgetWithCategory(Budget(1, YearMonth.of(2020, 10), 100), Category("b", 1, 1)),
        BudgetWithCategory(Budget(2, YearMonth.of(2020, 10), 202342), Category("c", 2, 2)),
        BudgetWithCategory(Budget(3, YearMonth.of(2020, 10), 203), Category("d", 3, 3)),
        BudgetWithCategory(Budget(4, YearMonth.of(2020, 10), 21230), Category("e", 4, 4))
    )

    /**
     * AvailableMoney example data
     */
    private var exampleAvailableMoney = mutableMapOf(
        exampleBudgetsWithCategoryOfMonth[0] to 100L,
        exampleBudgetsWithCategoryOfMonth[1] to 200L,
        exampleBudgetsWithCategoryOfMonth[2] to 0L,
        exampleBudgetsWithCategoryOfMonth[3] to 4L,
        exampleBudgetsWithCategoryOfMonth[4] to -10000L
    )

    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockViewModel.toBeBudgeted).thenReturn(MutableLiveData(toBeBudgeted))
        `when`(mockViewModel.budgetsWithCategoryOfMonth).thenReturn(MutableLiveData(exampleBudgetsWithCategoryOfMonth))
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
        exampleBudgetsWithCategoryOfMonth[2].category.name = newCat
        exampleBudgetsWithCategoryOfMonth[2].budget.budgeted = newBudgetedValue

        // Reset available money
        exampleAvailableMoney = mutableMapOf(
            exampleBudgetsWithCategoryOfMonth[0] to 100L,
            exampleBudgetsWithCategoryOfMonth[1] to 200L,
            exampleBudgetsWithCategoryOfMonth[2] to 0L,
            exampleBudgetsWithCategoryOfMonth[3] to 4L,
            exampleBudgetsWithCategoryOfMonth[4] to -10000L
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
        exampleAvailableMoney[exampleBudgetsWithCategoryOfMonth[2]] = newAvailableMoneyValue

        checkBudgetListContent()

    }

    private fun checkBudgetListContent() {
        checkRecyclerViewContentHasCorrectData(R.id.listBudgeting, exampleBudgetsWithCategoryOfMonth,
            { hasDescendant(withText(it.budget.budgeted.toString())) },
            { hasDescendant(withText(it.category.name)) })
        checkRecyclerViewContentHasCorrectData(R.id.listBudgeting, exampleAvailableMoney.toList(),
            { hasDescendant(withText(it.second.toString())) })
    }

}