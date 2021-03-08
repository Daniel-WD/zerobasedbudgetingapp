package com.titaniel.zerobasedbudgetingapp.activities

import android.app.Activity
import androidx.core.os.bundleOf
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
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.utils.Utils
import org.hamcrest.CoreMatchers.not
import org.hamcrest.text.IsEmptyString.isEmptyString
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate

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
        `when`(mockViewModel.payeeName).thenReturn(MutableLiveData(""))
        `when`(mockViewModel.pay).thenReturn(MutableLiveData(0L))
        `when`(mockViewModel.categoryName).thenReturn(MutableLiveData(""))
        `when`(mockViewModel.description).thenReturn(MutableLiveData(""))
        `when`(mockViewModel.date).thenReturn(MutableLiveData())
        `when`(mockViewModel.editTransaction).thenReturn(MutableLiveData(null))

        // Add lifecycle callback
        ActivityLifecycleMonitorRegistry.getInstance()
            .addLifecycleCallback { activity: Activity, stage: Stage ->
                if (stage == Stage.PRE_ON_CREATE) {
                    // Set mockViewModel before onCreate()
                    (activity as AddEditTransactionActivity).apply {
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
        onView(withId(R.id.etPay)).check(matches(withText(isEmptyString())))

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

    }

    @Test
    fun starts_correctly_with_edit_transaction() {

        // Create editTransaction data
        val pay = -120L
        val payeeName = "payee"
        val categoryName = "category"
        val description = "description"
        val date = LocalDate.of(1998, 1, 12)

        // Set editTransaction
        `when`(mockViewModel.editTransaction).thenReturn(
            MutableLiveData(
                Transaction(pay, payeeName, categoryName, description, date)
            )
        )

        // Stub data validity check to return true
        `when`(mockViewModel.isDataValid()).thenReturn(true)

        // Recreate scenario (recreate didn't work)
        scenario.close()
        scenario = launchActivity()

        // Check pay
        onView(withId(R.id.etPay)).check(matches(withText(pay.toString())))

        // Check payeeName
        onView(withId(R.id.tvPayee)).check(matches(withText(payeeName)))

        // Check categoryName
        onView(withId(R.id.tvCategory)).check(matches(withText(categoryName)))

        // Check date
        onView(withId(R.id.tvDate)).check(matches(withText(Utils.convertLocalDateToString(date))))

        // Check description
        onView(withId(R.id.etDescription)).check(matches(withText(description)))

        // Open date picker, confirm date (to check if correct date was selected)
        onView(withId(R.id.layoutDate)).perform(click())
        onView(withId(R.id.confirm_button)).perform(click())

        // Title for transaction creation
        onView(withId(android.R.id.content)).check(matches(hasDescendant(withText(R.string.activity_add_edit_transaction_edit_transaction))))

        // Button text is create
        onView(withId(R.id.fabCreateApply)).check(matches(withText(R.string.activity_add_edit_transaction_apply)))

        // Create btn enabled
        onView(withId(R.id.fabCreateApply)).check(matches(isEnabled()))

        // Assert transaction values in ViewModel correct
        assertThat(mockViewModel.pay.value).isEqualTo(pay)
        assertThat(mockViewModel.payeeName.value).isEqualTo(payeeName)
        assertThat(mockViewModel.categoryName.value).isEqualTo(categoryName)
        assertThat(mockViewModel.description.value).isEqualTo(description)
        assertThat(mockViewModel.date.value).isEqualTo(date)

    }

    @Test
    fun handles_delete_click_correctly_on_new_transaction_mode() {

        // Click delete
        onView(withId(R.id.delete)).perform(click())

        scenario.onActivity { activity ->
            // Finishes
            assertThat(activity.isFinishing).isTrue()
        }

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
        val dateString = Utils.convertLocalDateToString(LocalDate.now())
        onView(withId(R.id.tvDate)).check(matches(withText(dateString)))

    }

    @Test
    fun displays_correct_payee_on_valid_fragment_result() {

        val expectedPayee = "aPayee"

        scenario.onActivity { activity ->
            // Fake fragment result from payee picker
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(SelectPayeeFragment.PAYEE_KEY to expectedPayee)
            )
        }

        // Check correct payee
        onView(withId(R.id.tvPayee)).check(matches(withText(expectedPayee)))

    }

    @Test
    fun displays_correct_payee_on_invalid_fragment_result() {

        scenario.onActivity { activity ->
            // Fake fragment result from payee picker, invalid
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(SelectPayeeFragment.PAYEE_KEY to null)
            )
        }

        // Payee text empty
        onView(withId(R.id.tvPayee)).check(matches(withText("")))

        scenario.onActivity { activity ->
            // Fake fragment result from payee picker, empty
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(SelectPayeeFragment.PAYEE_KEY to "")
            )
        }

        // Payee text empty
        onView(withId(R.id.tvPayee)).check(matches(withText("")))

    }

    @Test
    fun displays_correct_category_on_valid_fragment_result() {

        val expectedCategory = "aCategory"

        scenario.onActivity { activity ->
            // Fake fragment result from category picker
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
                bundleOf(SelectCategoryFragment.CATEGORY_KEY to expectedCategory)
            )
        }

        // Correct category text
        onView(withId(R.id.tvCategory)).check(matches(withText(expectedCategory)))

    }

    @Test
    fun displays_correct_category_on_invalid_fragment_result() {

        scenario.onActivity { activity ->
            // Fake fragment result from category picker, invalid
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
                bundleOf(SelectCategoryFragment.CATEGORY_KEY to null)
            )
        }

        // Category text empty
        onView(withId(R.id.tvCategory)).check(matches(withText("")))

        scenario.onActivity { activity ->
            // Fake fragment result from category picker, invalid
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
                bundleOf(SelectCategoryFragment.CATEGORY_KEY to "")
            )
        }

        // Category text empty
        onView(withId(R.id.tvCategory)).check(matches(withText("")))

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
    fun creates_transaction_without_description_correctly() {

        // Setup expected values
        val pay = -1234L
        val payee = "fakePayee"
        val category = "fakeCategory"
        val description = ""

        // Type value
        onView(withId(R.id.etPay)).perform(typeText(pay.toString()))

        // Select payee
        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(SelectPayeeFragment.PAYEE_KEY to payee)
            )
        }

        // Select category
        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
                bundleOf(SelectCategoryFragment.CATEGORY_KEY to category)
            )
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
        assertThat(mockViewModel.pay.value).isEqualTo(pay)
        assertThat(mockViewModel.payeeName.value).isEqualTo(payee)
        assertThat(mockViewModel.categoryName.value).isEqualTo(category)
        assertThat(mockViewModel.description.value).isEqualTo(description)
        assertThat(mockViewModel.date.value).isEqualTo(LocalDate.now())

    }

    @Test
    fun creates_transaction_with_description_correctly() {

        // Setup expected values
        val pay = 1234L
        val payee = "fakePayee"
        val category = "fakeCategory"
        val description = "     fake Desc ription   "

        // Type value
        onView(withId(R.id.etPay)).perform(typeText(pay.toString()))

        // Type description
        onView(withId(R.id.etDescription)).perform(typeText(description))

        // Select payee
        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(SelectPayeeFragment.PAYEE_KEY to payee)
            )
        }

        // Select today as date
        onView(withId(R.id.layoutDate)).perform(click())
        onView(withId(R.id.confirm_button)).perform(click())

        // Create btn not enabled
        onView(withId(R.id.fabCreateApply)).check(matches(not(isEnabled())))

        // Stub data validity check to return true
        `when`(mockViewModel.isDataValid()).thenReturn(true)

        // Select category
        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
                bundleOf(SelectCategoryFragment.CATEGORY_KEY to category)
            )
        }

        // Check create enabled
        onView(withId(R.id.fabCreateApply)).check(matches(isEnabled()))

        // Create transaction
        onView(withId(R.id.fabCreateApply)).perform(click())

        // Verify applyData() on ViewModel called
        verify(mockViewModel).applyData()

        // Assert transaction values correct
        assertThat(mockViewModel.pay.value).isEqualTo(pay)
        assertThat(mockViewModel.payeeName.value).isEqualTo(payee)
        assertThat(mockViewModel.categoryName.value).isEqualTo(category)
        assertThat(mockViewModel.description.value).isEqualTo(description)
        assertThat(mockViewModel.date.value).isEqualTo(LocalDate.now())
    }

}