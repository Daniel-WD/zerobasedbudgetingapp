package com.titaniel.zerobasedbudgetingapp.activities

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.replace
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionWithCategoryAndPayee
import com.titaniel.zerobasedbudgetingapp.utils.convertLocalDateToString
import com.titaniel.zerobasedbudgetingapp.utils.moneyFormat
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.hamcrest.text.IsEmptyString.isEmptyString
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import kotlin.math.absoluteValue

@RunWith(MockitoJUnitRunner::class)
class AddEditTransactionActivityTest {

    /**
     * Activity scenario
     */
    private lateinit var scenario: ActivityScenario<AddEditTransactionActivity>

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockViewModel: AddEditTransactionViewModel

    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockViewModel.payee).thenReturn(MutableLiveData())
        `when`(mockViewModel.pay).thenReturn(MutableLiveData(0L))
        `when`(mockViewModel.category).thenReturn(MutableLiveData())
        `when`(mockViewModel.description).thenReturn(MutableLiveData(""))
        `when`(mockViewModel.date).thenReturn(MutableLiveData())
        `when`(mockViewModel.editTransactionWithCategoryAndPayee).thenReturn(MutableLiveData(null))
        `when`(mockViewModel.positive).thenReturn(false)

        // Add lifecycle callback
        ActivityLifecycleMonitorRegistry.getInstance()
            .addLifecycleCallback { activity: Activity, stage: Stage ->
                if (stage == Stage.PRE_ON_CREATE && activity is AddEditTransactionActivity) {
                    // Set mockViewModel before onCreate()
                    activity.apply {
                        replace(AddEditTransactionActivity::viewModel, mockViewModel)
                    }
                }
            }

        // Launch scenario
        scenario = launchActivity()
    }

    @After
    fun tearDown() {
        // Close scenario
        scenario.close()
    }

    @Test
    fun starts_correctly() {

        // Value empty
        onView(withId(R.id.etPay)).check(matches(withText(0L.moneyFormat())))

        // Payee empty
        onView(withId(R.id.tvPayee)).check(matches(withText(isEmptyString())))

        // Category empty
        onView(withId(R.id.tvCategory)).check(matches(withText(isEmptyString())))

        // Date empty
        onView(withId(R.id.tvDate)).check(matches(withText(isEmptyString())))

        // Description empty
        onView(withId(R.id.etDescription)).check(matches(withText(isEmptyString())))

        // Title for transaction creation
        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.activity_add_edit_transaction_create_transaction))))

        // Button text is create
        onView(withId(R.id.fabCreateApply)).check(matches(withText(R.string.activity_add_edit_transaction_create)))

        // Pos/neg switch is unchecked
        onView(withId(R.id.switchPosNeg)).check(matches(isNotChecked()))

    }

    @Test
    fun starts_correctly_with_positive_edit_transaction() {

        // Create editTransaction data
        val pay = 120L
        val payee = Payee("payee", 1)
        val category = Category("category", 0, 1)
        val description = "description"
        val date = LocalDate.of(1998, 1, 12)

        // Set editTransaction
        `when`(mockViewModel.editTransactionWithCategoryAndPayee).thenReturn(
            MutableLiveData(
                TransactionWithCategoryAndPayee(
                    Transaction(pay, payee.id, category.id, description, date),
                    category, payee
                )
            )
        )

        // Stub data validity check to return true
        `when`(mockViewModel.isDataValid()).thenReturn(true)

        // Create ownScenario (recreate didn't work)

        val ownScenario = launchActivity<AddEditTransactionActivity>()

        // Check pay
        onView(withId(R.id.etPay)).check(matches(withText(pay.moneyFormat())))

        // Check payeeName
        onView(withId(R.id.tvPayee)).check(matches(withText(payee.name)))

        // Check categoryName
        onView(withId(R.id.tvCategory)).check(matches(withText(category.name)))

        // Check date
        onView(withId(R.id.tvDate)).check(matches(withText(convertLocalDateToString(date))))

        // Check description
        onView(withId(R.id.etDescription)).check(matches(withText(description)))

        // Open date picker, confirm date (to check if correct date was selected)
        onView(withId(R.id.layoutDate)).perform(click())
        onView(withId(R.id.confirm_button)).perform(click())

        // Title for edit transaction
        onView(withId(android.R.id.content)).check(matches(hasDescendant(withText(R.string.activity_add_edit_transaction_edit_transaction))))

        // Button text is apply
        onView(withId(R.id.fabCreateApply)).check(matches(withText(R.string.activity_add_edit_transaction_apply)))

        // Apply btn enabled
        onView(withId(R.id.fabCreateApply)).check(matches(isEnabled()))

        // Assert transaction values in ViewModel correct
        assertThat(mockViewModel.pay.value).isEqualTo(pay)
        assertThat(mockViewModel.payee.value).isEqualTo(payee)
        assertThat(mockViewModel.category.value).isEqualTo(category)
        assertThat(mockViewModel.description.value).isEqualTo(description)
        assertThat(mockViewModel.date.value).isEqualTo(date)
        verify(mockViewModel).positive = true

        ownScenario.close()
    }

    @Test
    fun starts_correctly_with_negative_edit_transaction_and_to_be_budgeted_as_category() {
        // NOTE: only negativity and to_be_budgeted as category is tested here. Rest is already verified in 'starts_correctly_with_positive_edit_transaction()'

        // Create editTransaction data
        val pay = -120L
        val payee = Payee("payee", 1)
        val category = Category.TO_BE_BUDGETED
        val description = "description"
        val date = LocalDate.of(1998, 1, 12)

        // Set editTransaction
        `when`(mockViewModel.editTransactionWithCategoryAndPayee).thenReturn(
            MutableLiveData(
                TransactionWithCategoryAndPayee(
                    Transaction(pay, payee.id, category.id, description, date),
                    category, payee
                )
            )
        )

        // Create ownScenario (recreate didn't work)

        val ownScenario = launchActivity<AddEditTransactionActivity>()

        // Check categoryName
        onView(withId(R.id.tvCategory)).check(matches(withText(R.string.activity_add_edit_transaction_to_be_budgeted)))

        // Assert transaction values in ViewModel correct
        assertThat(mockViewModel.category.value).isEqualTo(category)
        verify(mockViewModel).positive = false


        ownScenario.close()
    }

    @Test
    fun handles_pos_neg_switch_click_correctly() {

        // Reset mock to invalidate previous positive = ...
        Mockito.reset(mockViewModel)

        // Pos/neg switch is unchecked
        onView(withId(R.id.switchPosNeg)).perform(click())

        // Check positive has been set to true
        verify(mockViewModel).positive = true

        // Reset mock to invalidate previous positive = ...
        Mockito.reset(mockViewModel)

        // Pos/neg switch is unchecked
        onView(withId(R.id.switchPosNeg)).perform(click())

        // Check positive has been set to false
        verify(mockViewModel).positive = false

    }

    @Test
    fun handles_delete_click_correctly_on_new_transaction_mode() = runBlocking {

        // Click delete
        onView(withId(R.id.delete)).perform(click())

        // (Makes it work on GitHub actions :D)
//        delay(1000)

        // Check activity finishing
        assertThat(scenario.state == Lifecycle.State.DESTROYED).isTrue()

        // Verify deleteEditTransaction() called
        verify(mockViewModel).deleteEditTransaction()

    }

    @Test
    fun displays_correct_date_after_it_has_been_set_with_date_picker() {

        // Click date layout
        onView(withId(R.id.layoutDate)).perform(click())

        // Click ok on date picker
        onView(withId(R.id.confirm_button)).perform(click())

        // Check if correct date is selected, and is correctly formatted
        val dateString = convertLocalDateToString(LocalDate.now())
        onView(withId(R.id.tvDate)).check(matches(withText(dateString)))

    }

    @Test
    fun shows_correct_payee_when_new_payee_is_set() {

        val expectedPayee = "aPayee"

        // Change payee
        scenario.onActivity {
            mockViewModel.payee.value = Payee(expectedPayee)
        }

        // Check correct payee
        onView(withId(R.id.tvPayee)).check(matches(withText(expectedPayee)))

    }

    @Test
    fun shows_nothing_when_payee_null() {

        // Change payee
        scenario.onActivity {
            mockViewModel.payee.value = null
        }

        // Check payee text empty
        onView(withId(R.id.tvPayee)).check(matches(withText(isEmptyString())))

    }

    @Test
    fun shows_correct_category_when_new_category_is_set() {

        val expectedCategory = "aCategory"

        // Change category
        scenario.onActivity {
            mockViewModel.category.value = Category(expectedCategory, 0)
        }

        // Check correct category
        onView(withId(R.id.tvCategory)).check(matches(withText(expectedCategory)))

    }

    @Test
    fun shows_nothing_when_category_null() {

        // Change category
        scenario.onActivity {
            mockViewModel.category.value = null
        }

        // Check category text empty
        onView(withId(R.id.tvCategory)).check(matches(withText(isEmptyString())))

    }

    @Test
    fun shows_to_be_budgeted_text_when_category_is_to_be_budgeted() {

        // Change category
        scenario.onActivity {
            mockViewModel.category.value = Category.TO_BE_BUDGETED
        }

        // Check category text
        onView(withId(R.id.tvCategory)).check(matches(withText(R.string.activity_add_edit_transaction_to_be_budgeted)))

    }

    @Test
    fun opens_payee_picker_on_payee_layout_click() {

        // Click payee layout
        onView(withId(R.id.layoutPayee)).perform(click())

        // Payee picker opens
        onView(withId(R.id.listPayees)).check(matches(isDisplayed()))

    }

    @Test
    fun opens_category_picker_on_category_layout_click() {

        // Click category layout
        onView(withId(R.id.layoutCategory)).perform(click())

        // Category picker opens
        onView(withId(R.id.listCategories)).check(matches(isDisplayed()))

    }

    @Test
    fun creates_transaction_without_description_and_negative_pay_correctly() {

        // Setup expected values
        val pay = -1234L
        val payee = Payee("fakePayee")
        val category = Category("fakeCategory", 1)
        val description = ""

        // Type value
        onView(withId(R.id.etPay)).perform(typeText(pay.toString()))

        // Select payee
        scenario.onActivity {
            mockViewModel.payee.value = payee
        }

        // Select category
        scenario.onActivity {
            mockViewModel.category.value = category
        }

        // Create btn not enabled
        onView(withId(R.id.fabCreateApply)).check(matches(not(isEnabled())))

        // Stub data validity check to return true
        `when`(mockViewModel.isDataValid()).thenReturn(true)

        // Select today as date
        onView(withId(R.id.layoutDate)).perform(click())
        onView(withId(R.id.confirm_button)).perform(click())

        // Check create enabled
        onView(withId(R.id.fabCreateApply)).check(matches(isEnabled()))

        // Create transaction
        onView(withId(R.id.fabCreateApply)).perform(click())

        // Verify applyData() on ViewModel called
        verify(mockViewModel).applyData()

        // Assert transaction values correct
        assertThat(mockViewModel.pay.value).isEqualTo(pay.absoluteValue)
        assertThat(mockViewModel.payee.value).isEqualTo(payee)
        assertThat(mockViewModel.category.value).isEqualTo(category)
        assertThat(mockViewModel.description.value).isEqualTo(description)
        assertThat(mockViewModel.date.value).isEqualTo(LocalDate.now())
        assertThat(mockViewModel.positive).isFalse()

    }

    @Test
    fun creates_transaction_with_description_and_positive_pay_correctly() {

        // Setup expected values
        val pay = 1234L
        val payee = Payee("fakePayee")
        val category = Category("fakeCategory", 1)
        val description = "     fake Desc ription   "

        // Type value
        onView(withId(R.id.etPay)).perform(typeText(pay.toString()))

        // Make pay positive
        onView(withId(R.id.switchPosNeg)).perform(click())

        // Type description
        onView(withId(R.id.etDescription)).perform(typeText(description))

        // Select payee
        scenario.onActivity {
            mockViewModel.payee.value = payee
        }

        // Select today as date
        onView(withId(R.id.layoutDate)).perform(click())
        onView(withId(R.id.confirm_button)).perform(click())

        // Create btn not enabled
        onView(withId(R.id.fabCreateApply)).check(matches(not(isEnabled())))

        // Stub data validity check to return true
        `when`(mockViewModel.isDataValid()).thenReturn(true)

        // Select category
        scenario.onActivity {
            mockViewModel.category.value = category
        }

        // Check create enabled
        onView(withId(R.id.fabCreateApply)).check(matches(isEnabled()))

        // Create transaction
        onView(withId(R.id.fabCreateApply)).perform(click())

        // Verify applyData() on ViewModel called
        verify(mockViewModel).applyData()

        // Assert transaction values correct
        assertThat(mockViewModel.pay.value).isEqualTo(pay)
        assertThat(mockViewModel.payee.value).isEqualTo(payee)
        assertThat(mockViewModel.category.value).isEqualTo(category)
        assertThat(mockViewModel.description.value).isEqualTo(description)
        assertThat(mockViewModel.date.value).isEqualTo(LocalDate.now())
        verify(mockViewModel).positive = true

    }

}