package com.titaniel.zerobasedbudgetingapp.compose.screen_budget

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.liveData
import com.titaniel.zerobasedbudgetingapp.utils.moneyFormat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.time.YearMonth

@RunWith(MockitoJUnitRunner::class)
class BudgetScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockViewModel: BudgetViewModel

    /**
     * Test month
     */
    private val month = YearMonth.now()

    /**
     * Test toBeBudgeted
     */
    private val toBeBudgeted = 100000L

    /**
     * Test groups
     */
    private val groups = listOf(
        GroupData(
            "Persönlich", listOf(
                CategoryItemData(
                    categoryName = "Lebensmittel",
                    budgetedAmount = 1000000,
                    availableAmount = 1200,
                    budgetId = 1,
                    state = CategoryItemState.NORMAL
                ),
                CategoryItemData(
                    categoryName = "Investment",
                    budgetedAmount = 0,
                    availableAmount = 0,
                    budgetId = 2,
                    state = CategoryItemState.NORMAL
                ),
                CategoryItemData(
                    categoryName = "Bücher",
                    budgetedAmount = 10000,
                    availableAmount = -1412,
                    budgetId = 3,
                    state = CategoryItemState.NORMAL
                )
            )
        ),
        GroupData(
            "Haushalt", listOf()
        ),
        GroupData(
            "Anderes", listOf(
                CategoryItemData(
                    categoryName = "Lebensmittel",
                    budgetedAmount = 1000000,
                    availableAmount = 1200,
                    budgetId = 4,
                    state = CategoryItemState.NORMAL
                ),
                CategoryItemData(
                    categoryName = "Investment",
                    budgetedAmount = 0,
                    availableAmount = 0,
                    budgetId = 5,
                    state = CategoryItemState.NORMAL
                )
            )
        )
    )

    /**
     * Test inBudgetChangeMode
     */
    private val inBudgetChangeMode = false

    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockViewModel.toBeBudgeted).thenReturn(liveData { emit(toBeBudgeted) })
        `when`(mockViewModel.month).thenReturn(liveData { emit(month) })
        `when`(mockViewModel.groupList).thenReturn(liveData { emit(groups) })
        `when`(mockViewModel.inBudgetChangeMode).thenReturn(liveData { emit(inBudgetChangeMode) })

        // Create ui
        composeTestRule.setContent {
            BudgetScreenWrapper(mockViewModel)
        }
    }

    @Test
    fun starts_correctly() {

        val expectedListValues = listOf(
            arrayOf("Persönlich"),
            arrayOf(1010000L.moneyFormat()),
            arrayOf((-212L).moneyFormat()),

            arrayOf("Lebensmittel", 1000000L.moneyFormat(), 1200L.moneyFormat()),

            arrayOf("Investment", 0L.moneyFormat(), 0L.moneyFormat()),

            arrayOf("Bücher", 10000L.moneyFormat(), (-1412L).moneyFormat()),

            arrayOf("Haushalt"),
            arrayOf(0L.moneyFormat()),
            arrayOf(0L.moneyFormat()),

            arrayOf("Anderes"),
            arrayOf(1000000L.moneyFormat()),
            arrayOf(1200L.moneyFormat()),

            arrayOf("Lebensmittel", 1000000L.moneyFormat(), 1200L.moneyFormat()),

            arrayOf("Investment", 0L.moneyFormat(), 0L.moneyFormat())
        )

        // On List
        composeTestRule.onNodeWithTag("GroupList").let { content ->

            // For each selectable month
            expectedListValues.forEachIndexed { i, value ->

                content.onChildAt(i).assertTextEquals(*value)

            }
        }

    }

    @Test
    fun dropdown_menu_opens_on_menu_btn_click() {
        composeTestRule.onNodeWithTag("DropdownMenu").assertDoesNotExist()

        composeTestRule.onNodeWithTag("MenuButton").performClick()

        composeTestRule.onNodeWithTag("DropdownMenu").assertExists()

    }

    @Test
    fun month_picker_opens_on_month_btn_click() {

        // TODO --> still experimental

//        composeTestRule.onNodeWithTag("MonthPickerDialog").assertIsNotDisplayed()
//
//        composeTestRule.onNodeWithTag("MonthButton").performClick()
//
//        composeTestRule.onNodeWithTag("MonthPickerDialog").assertIsDisplayed()

    }

}