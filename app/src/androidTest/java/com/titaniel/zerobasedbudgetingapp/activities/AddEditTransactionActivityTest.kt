package com.titaniel.zerobasedbudgetingapp.activities

import androidx.core.os.bundleOf
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.utils.Utils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


@RunWith(AndroidJUnit4::class)
class AddEditTransactionActivityTest {

    private lateinit var scenario: ActivityScenario<AddEditTransactionActivity>

    @Before
    fun setup() {
        scenario = launchActivity()
        scenario.onActivity { activity ->
            activity.mDataManager.detach()
            activity.mDataManager.state = DataManager.STATE_NOT_LOADED
        }
    }

    @After
    fun setdown() {
        scenario.close()
    }

    @Test
    fun handles_delete_click_correctly_on_new_transaction_mode() {

        onView(withId(R.id.delete)).perform(click())

        scenario.onActivity { activity ->
            assertThat(activity.isFinishing).isTrue()
            assertThat(activity.mDataManager.payees.size).isEqualTo(0)
            assertThat(activity.mDataManager.transactions.size).isEqualTo(0)
            assertThat(activity.mDataManager.categories.size).isEqualTo(0)
            assertThat(activity.mDataManager.toBeBudgeted).isEqualTo(0)
        }

    }

    @Test
    fun displays_correct_date_after_it_has_been_set_with_date_picker() {

        onView(withId(R.id.layoutDate)).perform(click())
        onView(withId(R.id.confirm_button)).perform(click())

        val calender = Calendar.getInstance()
        val dateString = Utils.convertUtcToString(calender.timeInMillis)
        onView(withId(R.id.tvDate)).check(matches(withText(dateString)))

    }

    @Test
    fun displays_correct_payee_on_valid_fragment_result() {

        val expectedPayee = "aPayee"

        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(SelectPayeeFragment.PAYEE_KEY to expectedPayee)
            )
        }

        onView(withId(R.id.tvPayee)).check(matches(withText(expectedPayee)))

    }

    @Test
    fun displays_correct_payee_on_invalid_fragment_result() {

        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(SelectPayeeFragment.PAYEE_KEY to null)
            )
        }

        onView(withId(R.id.tvPayee)).check(matches(withText("")))

        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(SelectPayeeFragment.PAYEE_KEY to "")
            )
        }

        onView(withId(R.id.tvPayee)).check(matches(withText("")))

    }

    @Test
    fun displays_correct_category_on_valid_fragment_result() {

        val expectedCategory = "aCategory"

        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
                bundleOf(SelectCategoryFragment.CATEGORY_KEY to expectedCategory)
            )
        }

        onView(withId(R.id.tvCategory)).check(matches(withText(expectedCategory)))

    }

    @Test
    fun displays_correct_category_on_invalid_fragment_result() {

        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
                bundleOf(SelectCategoryFragment.CATEGORY_KEY to null)
            )
        }

        onView(withId(R.id.tvCategory)).check(matches(withText("")))

        scenario.onActivity { activity ->
            activity.supportFragmentManager.setFragmentResult(
                AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
                bundleOf(SelectCategoryFragment.CATEGORY_KEY to "")
            )
        }

        onView(withId(R.id.tvCategory)).check(matches(withText("")))

    }


}