package com.titaniel.zerobasedbudgetingapp.fragments

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.launchFragmentInHiltContainer
import com.titaniel.zerobasedbudgetingapp._testutils.replace
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetViewModel
import com.titaniel.zerobasedbudgetingapp.utils.moneyFormat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.time.YearMonth

@RunWith(MockitoJUnitRunner::class)
class UpdateBudgetFragmentTest {

    /**
     * Fragment scenario
     */
    private lateinit var testFragment: UpdateBudgetFragment

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockViewModel: UpdateBudgetViewModel

    /**
     * Example budget
     */
    private val exampleBudgetWithCategory = BudgetWithCategory(
        Budget(1, YearMonth.of(2020, 12), 100),
        Category("sex", 0, 1)
    )

    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockViewModel.budgetWithCategory).thenReturn(
            MutableLiveData(
                exampleBudgetWithCategory
            )
        )

        // Launch scenario
        launchFragmentInHiltContainer<UpdateBudgetFragment> {
            (this as UpdateBudgetFragment).apply {
                replace(UpdateBudgetFragment::viewModel, mockViewModel)
                testFragment = this
            }
        }
    }

    @Test
    fun starts_correctly() {

        // Category text correct
        onView(withId(R.id.tvCategory)).check(matches(withText(exampleBudgetWithCategory.category.name)))

        // Budgeted text correct
        onView(withId(R.id.etBudgeted)).check(matches(withText(exampleBudgetWithCategory.budget.budgeted.moneyFormat())))

        // Budgeted text is focused
        onView(withId(R.id.etBudgeted)).check(matches(hasFocus()))

    }

    @Test
    fun performs_done_btn_click_correctly() {

        val newBudgetedValue = 45324532L

        // Change budgeted value
        onView(withId(R.id.etBudgeted)).perform(replaceText(""))
        onView(withId(R.id.etBudgeted)).perform(typeText("dsfs $newBudgetedValue \n sadf"))

        // Click done button
        onView(withId(R.id.ivDone)).perform(click())

        // Calls updateBudget()
        verify(mockViewModel).updateBudget(newBudgetedValue)

        // Dismisses fragment
        assertThat(testFragment.isAdded).isFalse()

    }

    @Test
    fun performs_ime_action_done_correctly() {

        val newBudgetedValue = 45324532L

        // Change budgeted value
        onView(withId(R.id.etBudgeted)).perform(replaceText(""))
        onView(withId(R.id.etBudgeted)).perform(typeText("dsfs $newBudgetedValue \n sadf"))

        // Press ime btn
        onView(withId(R.id.etBudgeted)).perform(pressImeActionButton())

        // Calls updateBudget()
        verify(mockViewModel).updateBudget(newBudgetedValue)

        // Dismisses fragment
        assertThat(testFragment.isAdded).isFalse()

    }

}