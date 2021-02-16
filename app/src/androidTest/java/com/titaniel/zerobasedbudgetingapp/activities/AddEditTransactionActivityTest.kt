package com.titaniel.zerobasedbudgetingapp.activities

import androidx.core.os.bundleOf
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager
import com.titaniel.zerobasedbudgetingapp.datamanager.Transaction
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.utils.Utils
import org.hamcrest.Matchers.isEmptyString
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.Mockito
import java.util.*


@RunWith(AndroidJUnit4::class)
class AddEditTransactionActivityTest {

    /**
     * Activity scenario
     */
    private lateinit var scenario: ActivityScenario<AddEditTransactionActivity>

    @Before
    fun setup() {

        // Launch scenario
        scenario = launchActivity()

        // Setup data manager
        scenario.onActivity { activity ->
            activity.mDataManager.detach()
            activity.mDataManager.state = DataManager.STATE_NOT_LOADED
            activity.mDataManager.state = DataManager.STATE_LOADED // TODO is this needed?
        }
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

    }

    @Test
    fun handles_delete_click_correctly_on_new_transaction_mode() {

        // Click delete
        onView(withId(R.id.delete)).perform(click())

        scenario.onActivity { activity ->
            // Finishes
            assertThat(activity.isFinishing).isTrue()

            // Data is empty
            assertThat(activity.mDataManager.payees.size).isEqualTo(0)
            assertThat(activity.mDataManager.transactions.size).isEqualTo(0)
            assertThat(activity.mDataManager.categories.size).isEqualTo(0)
            assertThat(activity.mDataManager.toBeBudgeted).isEqualTo(0)
        }

    }

    @Test
    fun displays_correct_date_after_it_has_been_set_with_date_picker() {

        // Click date layout
        onView(withId(R.id.layoutDate)).perform(click())

        // Click ok on date picker
        onView(withId(R.id.confirm_button)).perform(click())

        // Check if correct date is selected, and is correctly formatted
        val calender = Calendar.getInstance()
        val dateString = Utils.convertLocalDateToString(calender.timeInMillis)
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

        // Set timzone
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

        // Setup expected values
        val value = -1234L
        val payee = "fakePayee"
        val category = "fakeCategory"
        val description = ""

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dateTimestamp = calendar.timeInMillis

        val expectedTransaction = Transaction(value, payee, category, description, dateTimestamp)

        // Setup spy data manager
        scenario.onActivity { activity ->
            val spyDataManager = Mockito.spy(activity.mDataManager)
            activity.mDataManager = spyDataManager

            // Do nothing here
            Mockito.doNothing().`when`(spyDataManager)
                .updateCategoryTransactionSums(any(), anyBoolean())
        }

        // Type value
        onView(withId(R.id.etPay)).perform(typeText(value.toString()))

        // Create btn not enabled
        onView(withId(R.id.fabCreateApply)).check(matches(not(isEnabled())))

        // Select payee
        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(SelectPayeeFragment.PAYEE_KEY to payee)
            )
        }

        // Create btn not enabled
        onView(withId(R.id.fabCreateApply)).check(matches(not(isEnabled())))

        // Select category
        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
                bundleOf(SelectCategoryFragment.CATEGORY_KEY to category)
            )
        }

        // Create btn not enabled
        onView(withId(R.id.fabCreateApply)).check(matches(not(isEnabled())))

        // Select today as date
        onView(withId(R.id.layoutDate)).perform(click())
        onView(withId(R.id.confirm_button)).perform(click())

        // Check create enabled
        onView(withId(R.id.fabCreateApply)).check(matches(isEnabled()))

        // Create transaction
        onView(withId(R.id.fabCreateApply)).perform(click())

        scenario.onActivity { activity ->
            // Correct transaction created
            assertThat(activity.mDataManager.transactions).contains(expectedTransaction)

            // updateCategoryTransactionSums() called
            Mockito.verify(activity.mDataManager).updateCategoryTransactionSums(any(), anyBoolean())
        }
    }

    @Test
    fun creates_transaction_with_description_correctly() {

        // Set timzone
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"))

        // Setup expected values
        val value = 1234L
        val payee = "fakePayee"
        val category = "fakeCategory"
        val description = "     \nfake Desc ription   "

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dateTimestamp = calendar.timeInMillis

        val expectedTransaction =
            Transaction(value, payee, category, description.trim(), dateTimestamp)

        // Setup spy data manager
        scenario.onActivity { activity ->
            val spyDataManager = Mockito.spy(activity.mDataManager)
            activity.mDataManager = spyDataManager

            // Do nothing here
            Mockito.doNothing().`when`(spyDataManager)
                .updateCategoryTransactionSums(any(), anyBoolean())
        }

        // Type value
        onView(withId(R.id.etPay)).perform(typeText(value.toString()))

        // Create btn not enabled
        onView(withId(R.id.fabCreateApply)).check(matches(not(isEnabled())))

        // Type description
        onView(withId(R.id.etDescription)).perform(typeText(description))

        // Create btn not enabled
        onView(withId(R.id.fabCreateApply)).check(matches(not(isEnabled())))

        // Select payee
        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(SelectPayeeFragment.PAYEE_KEY to payee)
            )
        }

        // Create btn not enabled
        onView(withId(R.id.fabCreateApply)).check(matches(not(isEnabled())))

        // Select today as date
        onView(withId(R.id.layoutDate)).perform(click())
        onView(withId(R.id.confirm_button)).perform(click())

        // Create btn not enabled
        onView(withId(R.id.fabCreateApply)).check(matches(not(isEnabled())))

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

        scenario.onActivity { activity ->
            // Correct transaction created
            assertThat(activity.mDataManager.transactions).contains(expectedTransaction)

            // updateCategoryTransactionSums() called
            Mockito.verify(activity.mDataManager).updateCategoryTransactionSums(any(), anyBoolean())
        }
    }

    /**
     * Mockit.any not null workaround
     */
    private fun <T> any(): T {
        return Mockito.any<T>()
    }

}