package com.titaniel.zerobasedbudgetingapp.activities

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEditTransactionActivityTest {

    private lateinit var scenario: ActivityScenario<AddEditTransactionActivity>

    @Before
    fun setup() {
        scenario = launchActivity()
    }

    @Test
    fun updates_ui_to_edit_mode_correctly() {

        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.activity_add_edit_transaction_create_transaction))))
        onView(withId(R.id.fabCreateApply)).check(matches(withText(R.string.activity_add_edit_transaction_create)))

    }

    @After
    fun tearDown() {
        scenario.close()
    }

}