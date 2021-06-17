package com.titaniel.zerobasedbudgetingapp.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.titaniel.zerobasedbudgetingapp.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainActivityTest {

//    /**
//     * Activity scenario
//     */
//    private lateinit var scenario: ActivityScenario<MainActivity>
//
//    @Before
//    fun setup() {
//
//        // Launch scenario
//        scenario = launchActivity()
//
//        // Initialize Intent recording
//        Intents.init()
//    }
//
//    @After
//    fun tearDown() {
//
//        // Release intent recording
//        Intents.release()
//
//        // Close scenario
//        scenario.close()
//    }
//
//    @Test
//    fun starts_with_correct_fragment() {
//
//        // Budget list is present
//        onView(withId(R.id.listBudgeting)).check(matches(isDisplayed()))
//
//    }
//
//    @Test
//    fun opens_page_budget_correctly() {
//
//        // Click on budget item in bottom navigation
//        onView(withId(R.id.page_budget)).perform(click())
//
//        // Budget list is present
//        onView(withId(R.id.listBudgeting)).check(matches(isDisplayed()))
//
//    }
//
//    @Test
//    fun opens_add_edit_transaction_activity_correctly() {
//
//        // Click on add edit transaction item in bottom navigation
//        onView(withId(R.id.page_add_edit_transaction)).perform(click())
//
//        // AddEditTransactionActivity is present
//        intended(hasComponent(AddEditTransactionActivity::class.java.name))
//
//        // Close keyboard (could interfere with other tests)
//        closeSoftKeyboard()
//    }
//
//    @Test
//    fun opens_page_transactions_correctly() {
//
//        // Click on budget item in bottom navigation
//        onView(withId(R.id.page_transactions)).perform(click())
//
//        // Transaction list is present
//        onView(withId(R.id.transactionsList)).check(matches(isDisplayed()))
//    }

}