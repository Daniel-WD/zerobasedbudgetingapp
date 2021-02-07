package com.titaniel.zerobasedbudgetingapp.activities

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.activties.MainActivity
import com.titaniel.zerobasedbudgetingapp.utils.Utils
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class AddEditTransactionActivityTest {

    @Test
    fun creates_add_edit_transaction_activity_correctly() {
        launchActivity<AddEditTransactionActivity>().use { scenario ->
            onView(withId(R.id.delete)).perform(click())
            assertThat(scenario.state).isEqualTo(Lifecycle.State.DESTROYED)

        }
    }

}