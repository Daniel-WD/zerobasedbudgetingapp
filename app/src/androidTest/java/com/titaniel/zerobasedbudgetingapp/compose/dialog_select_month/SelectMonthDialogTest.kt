package com.titaniel.zerobasedbudgetingapp.compose.dialog_select_month

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.liveData
import com.titaniel.zerobasedbudgetingapp.utils.monthName
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import java.time.YearMonth

@RunWith(MockitoJUnitRunner::class)
class SelectMonthDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockViewModel: MonthPickerViewModel

    /**
     * Dismiss callback
     */
    private val dismissCallback: () -> Unit = {}

    /**
     * Example selectable months
     */
    private val selectableMonths = listOf(
        YearMonth.of(2020, 2),
        YearMonth.of(2020, 3),
        YearMonth.of(2020, 4),
        YearMonth.of(2020, 5),
        YearMonth.of(2020, 6)
    )

    @Before
    fun setup() {

        // Mock selectableMonths
        `when`(mockViewModel.selectableMonths).thenReturn(liveData { emit(selectableMonths) })

        // Start the app
        composeTestRule.setContent {
            MonthPickerDialogWrapper(mockViewModel, dismissCallback)
        }

    }

    @Test
    fun month_list_contains_correct_values_on_start() {

        // On List
        composeTestRule.onNodeWithTag("List").let { content ->

            // For each selectable month
            selectableMonths.forEachIndexed { i, yearMonth ->

                // Assert list item contains correct representation of yearMonth
                content.onChildAt(i).assertTextEquals(
                    yearMonth.monthName(),
                    yearMonth.year.toString()
                )

            }
        }

    }

    @Test
    fun performs_list_item_click_correctly() {

        val childIndex = 2

        // Click list item at childIndex
        composeTestRule.onNodeWithTag("List").onChildAt(childIndex).performClick()

        verify(mockViewModel).onMonthClick(selectableMonths[childIndex])

    }

}