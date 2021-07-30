package com.titaniel.zerobasedbudgetingapp.compose.screen_budget

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.test.espresso.Espresso
import com.titaniel.zerobasedbudgetingapp.utils.moneyFormat
import kotlinx.coroutines.runBlocking
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
    private val groupsLiveData = MutableLiveData(
        listOf(
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
    )

    /**
     * Test inBudgetChangeMode
     */
    private val inBudgetChangeMode = MutableLiveData(false)

    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockViewModel.toBeBudgeted).thenReturn(liveData { emit(toBeBudgeted) })
        `when`(mockViewModel.month).thenReturn(liveData { emit(month) })
        `when`(mockViewModel.groupList).thenReturn(groupsLiveData)
        `when`(mockViewModel.inBudgetChangeMode).thenReturn(inBudgetChangeMode)

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

    @Test
    fun performs_clear_all_budgets_correctly() {

        // Open context menu
        composeTestRule.onNodeWithTag("MenuButton").performClick()

        // Click 'Clear All Budgets' btn
        composeTestRule.onNodeWithTag("ClearAllBudgets").performClick()

        verify(mockViewModel).onClearAllBudgets()

    }

    @Test
    fun performs_item_click_correctly() {

        // Click item
        composeTestRule.onNodeWithTag("GroupList").onChildAt(4).performClick()

        verify(mockViewModel).onItemClick(2)

    }

    @Test
    fun performs_budget_confirmation_correctly() {

        // Group list when item 'Investment' in 'Persönlich' is editable
        val clickedGroupList = listOf(
            GroupData(
                "Persönlich", listOf(
                    CategoryItemData(
                        categoryName = "Lebensmittel",
                        budgetedAmount = 1000000,
                        availableAmount = 1200,
                        budgetId = 1,
                        state = CategoryItemState.CHANGE_UNSELECTED
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0,
                        budgetId = 2,
                        state = CategoryItemState.CHANGE_SELECTED
                    ),
                    CategoryItemData(
                        categoryName = "Bücher",
                        budgetedAmount = 10000,
                        availableAmount = -1412,
                        budgetId = 3,
                        state = CategoryItemState.CHANGE_UNSELECTED
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
                        state = CategoryItemState.CHANGE_UNSELECTED
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0,
                        budgetId = 5,
                        state = CategoryItemState.CHANGE_UNSELECTED
                    )
                )
            )
        )

        // Set clickedGroupList
        composeTestRule.runOnUiThread {
            groupsLiveData.value = clickedGroupList
        }

        composeTestRule.onNodeWithTag("GroupList").onChildAt(4).let {
            // Change budget
            it.onChildAt(0).performTextInput("123")

            // Confirm budget
            it.onChildAt(1).performClick()
        }


        verify(mockViewModel).onBudgetConfirmationClick(123)
    }

    @Test
    fun budget_editing_gets_canceled_on_cancel_button_click() = runBlocking {

        // Group list when item 'Investment' in 'Persönlich' is editable
        val clickedGroupList = listOf(
            GroupData(
                "Persönlich", listOf(
                    CategoryItemData(
                        categoryName = "Lebensmittel",
                        budgetedAmount = 1000000,
                        availableAmount = 1200,
                        budgetId = 1,
                        state = CategoryItemState.CHANGE_UNSELECTED
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0,
                        budgetId = 2,
                        state = CategoryItemState.CHANGE_SELECTED
                    ),
                    CategoryItemData(
                        categoryName = "Bücher",
                        budgetedAmount = 10000,
                        availableAmount = -1412,
                        budgetId = 3,
                        state = CategoryItemState.CHANGE_UNSELECTED
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
                        state = CategoryItemState.CHANGE_UNSELECTED
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0,
                        budgetId = 5,
                        state = CategoryItemState.CHANGE_UNSELECTED
                    )
                )
            )
        )

        composeTestRule.runOnUiThread {
            // Set clickedGroupList
            groupsLiveData.value = clickedGroupList

            // Set mode to be in budget change mode
            inBudgetChangeMode.value = true
        }

        // Click 'cancel budget editing' button
        composeTestRule.onNodeWithTag("AbortBudgetChange").performClick()

        verify(mockViewModel).onAbortBudgetChange()

    }

    @Test
    fun budget_editing_gets_canceled_on_double_back_press() {

        // Group list when item 'Investment' in 'Persönlich' is editable
        val clickedGroupList = listOf(
            GroupData(
                "Persönlich", listOf(
                    CategoryItemData(
                        categoryName = "Lebensmittel",
                        budgetedAmount = 1000000,
                        availableAmount = 1200,
                        budgetId = 1,
                        state = CategoryItemState.CHANGE_UNSELECTED
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0,
                        budgetId = 2,
                        state = CategoryItemState.CHANGE_SELECTED
                    ),
                    CategoryItemData(
                        categoryName = "Bücher",
                        budgetedAmount = 10000,
                        availableAmount = -1412,
                        budgetId = 3,
                        state = CategoryItemState.CHANGE_UNSELECTED
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
                        state = CategoryItemState.CHANGE_UNSELECTED
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0,
                        budgetId = 5,
                        state = CategoryItemState.CHANGE_UNSELECTED
                    )
                )
            )
        )

        composeTestRule.runOnUiThread {
            // Set clickedGroupList
            groupsLiveData.value = clickedGroupList

            // Set mode to be in budget change mode
            inBudgetChangeMode.value = true
        }

        Espresso.pressBack()

        Espresso.pressBack()

        verify(mockViewModel).onAbortBudgetChange()
    }

}