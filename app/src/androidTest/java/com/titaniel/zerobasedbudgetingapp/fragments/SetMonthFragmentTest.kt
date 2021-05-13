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
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_set_month.SetMonthFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_set_month.SetMonthViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import java.time.YearMonth

@RunWith(MockitoJUnitRunner::class)
class SetMonthFragmentTest {

    /**
     * Fragment scenario
     */
    private lateinit var testFragment: SetMonthFragment

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockViewModel: SetMonthViewModel

    /**
     * Example selectable months
     */
    private val exampleSelectableMonths = listOf(
        YearMonth.of(2020, 2),
        YearMonth.of(2020, 3),
        YearMonth.of(2020, 4),
        YearMonth.of(2020, 5),
        YearMonth.of(2020, 6)
    )

    /**
     * Example index of selected month
     */
    private val indexOfSelectedMonth = 3

    @Before
    fun setup(): Unit = runBlocking {

        // Mock selectableMonths
        `when`(mockViewModel.selectableMonths).thenReturn(MutableLiveData(exampleSelectableMonths))

        // Mock blocking getIndexOfMonth()
        mockViewModel.stub {
            onBlocking {
                getIndexOfMonth()
            }.doReturn(indexOfSelectedMonth)
        }

        // Launch scenario
        launchFragmentInHiltContainer<SetMonthFragment> {
            (this as SetMonthFragment).apply {
                replace(SetMonthFragment::viewModel, mockViewModel)
                testFragment = this
            }
        }
    }

    @Test
    fun starts_correctly() {

        // Spinner has correct text
        onView(withId(android.R.id.content)).check(matches(hasDescendant(withText("Mai 2020"))))

    }

}