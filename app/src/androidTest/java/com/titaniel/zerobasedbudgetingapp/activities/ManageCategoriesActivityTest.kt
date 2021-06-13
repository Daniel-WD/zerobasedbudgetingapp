package com.titaniel.zerobasedbudgetingapp.activities

import android.app.Activity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.actionOnItemView
import com.titaniel.zerobasedbudgetingapp._testutils.checkRecyclerViewContentHasCorrectData
import com.titaniel.zerobasedbudgetingapp._testutils.replace
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ManageCategoriesActivityTest {

    /**
     * Activity scenario
     */
    private lateinit var scenario: ActivityScenario<ManageCategoriesActivity>

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockViewModel: ManageCategoriesViewModel

    /**
     * Example category list
     */
    private val categories = mutableListOf(
        Category("cat1", 0, 1, 1),
        Category("cat2", 0, 2, 2),
        Category("cat3", 0, 3, 3),
        Category("cat4", 0, 4, 4),
        Category("cat5", 0, 5, 5)
    )

    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockViewModel.newCategories).thenReturn(MutableLiveData(categories))
        `when`(mockViewModel.categoriesChanged()).thenReturn(false)

        // Add lifecycle callback
        ActivityLifecycleMonitorRegistry.getInstance()
            .addLifecycleCallback { activity: Activity, stage: Stage ->
                if (stage == Stage.PRE_ON_CREATE && activity is ManageCategoriesActivity) {
                    // Set mockViewModel before onCreate()
                    activity.apply {
                        replace(ManageCategoriesActivity::viewModel, mockViewModel)
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

        checkCategoryListContent()

    }

    @Test
    fun handles_confirm_button_click_correctly() {

        // Click button
        onView(withId(R.id.fabConfirm)).perform(click())

        // Saves
        verify(mockViewModel).saveNewCategories()

        // Check activity finishing
        assertThat(scenario.state == Lifecycle.State.DESTROYED).isTrue()

    }

    @Test
    fun handles_add_category_click_correctly() {

        // Click btn
        onView(withId(R.id.addCategory)).perform(click())

        // AddEditCategoryFragment is present
        onView(withId(R.id.etCategory)).check(matches(isDisplayed()))

    }

    @Test
    fun handles_close_click_correctly_when_no_changes() {

        // Click close
        onView(withClassName(`is`(AppCompatImageButton::class.qualifiedName))).perform(click())

        // Check activity finishing
        assertThat(scenario.state == Lifecycle.State.DESTROYED).isTrue()

    }

    @Test
    fun handles_close_click_correctly_when_changes_and_cancel() {

        // Define that changes present
        `when`(mockViewModel.categoriesChanged()).thenReturn(true)

        // Click close
        onView(withClassName(`is`(AppCompatImageButton::class.qualifiedName))).perform(click())

        // Check alert dialog present
        onView(withText(R.string.activity_manage_categories_discard_dialog_content)).check(
            matches(
                isDisplayed()
            )
        )

        // Click cancel
        onView(withText("CANCEL")).perform(click())

        // Check activity finishing
        assertThat(scenario.state == Lifecycle.State.DESTROYED).isFalse()
    }

    @Test
    fun handles_close_click_correctly_when_changes_and_yes() {

        // Define that changes present
        `when`(mockViewModel.categoriesChanged()).thenReturn(true)

        // Click close
        onView(withClassName(`is`(AppCompatImageButton::class.qualifiedName))).perform(click())

        // Check alert dialog present
        onView(withText(R.string.activity_manage_categories_discard_dialog_content)).check(
            matches(
                isDisplayed()
            )
        )

        // Click yes
        onView(withText("YES")).perform(click())

        // Check activity finishing
        assertThat(scenario.state == Lifecycle.State.DESTROYED).isTrue()
    }

    @Test
    fun handles_item_edit_correctly() {

        // Click edit btn in second item
        onView(withId(R.id.listManageCategories)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                actionOnItemView(withId(R.id.ivEdit), click())
            )
        )

        // AddEditCategoryFragment is present
        onView(withId(R.id.etCategory)).check(matches(isDisplayed()))

    }

    @Test
    fun handles_item_delete_and_cancel_correctly() {

        // Expected cats
        val cats = categories.map { it.copy() }

        // Click edit btn in second item
        onView(withId(R.id.listManageCategories)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                actionOnItemView(withId(R.id.ivDelete), click())
            )
        )

        // Check alert dialog present
        onView(withText(R.string.activity_manage_categories_delete_dialog_title)).check(
            matches(
                isDisplayed()
            )
        )

        // Click cancel
        onView(withText("CANCEL")).perform(click())

        // Check categories like expected
        assertThat(categories).isEqualTo(cats)

    }

    @Test
    fun handles_item_delete_and_yes_correctly() {

        // Expected cats
        val cats = categories.map { it.copy() }.toMutableList().apply {
            removeAt(1)
        }

        // Click edit btn in second item
        onView(withId(R.id.listManageCategories)).perform(
            actionOnItemAtPosition<RecyclerView.ViewHolder>(
                1,
                actionOnItemView(withId(R.id.ivDelete), click())
            )
        )

        // Check alert dialog present
        onView(withText(R.string.activity_manage_categories_delete_dialog_title)).check(
            matches(
                isDisplayed()
            )
        )

        // Click cancel
        onView(withText("YES")).perform(click())

        // Check categories like expected
        assertThat(categories).isEqualTo(cats)

    }

    @Test
    fun reacts_on_category_changes_correctly() {

        // Change categories
        categories[0] = categories[2].also { categories[2] = categories[0] }
        categories[3].name = "new_name"

        checkCategoryListContent()
    }

    private fun checkCategoryListContent() {
        checkRecyclerViewContentHasCorrectData(R.id.listManageCategories, categories,
            { hasDescendant(withText(it.name)) })
    }

}