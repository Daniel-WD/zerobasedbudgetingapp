package com.titaniel.zerobasedbudgetingapp.fragments

import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.common.truth.Truth
import com.google.common.truth.Truth.*
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.launchFragmentInHiltContainer
import com.titaniel.zerobasedbudgetingapp._testutils.replace
import com.titaniel.zerobasedbudgetingapp.activities.ManageCategoriesViewModel
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_add_edit_category.AddEditCategoryFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddEditCategoryFragmentTest {

    /**
     * Fragment scenario
     */
    private lateinit var testFragment: AddEditCategoryFragment

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockParentViewModel: ManageCategoriesViewModel

    /**
     * Example categories
     */
    private val categories = mutableListOf(
        Category("cat1", 1, 1),
        Category("cat2", 2, 2),
        Category("cat3", 3, 3),
        Category("cat4", 4, 4),
        Category("cat5", 5, 5)
    )

    @Before
    fun setup() {

        // Set ViewModel properties
        `when`(mockParentViewModel.newCategories).thenReturn(MutableLiveData(categories))

        // Launch scenario
        launchFragmentInHiltContainer<AddEditCategoryFragment>(bundleOf(AddEditCategoryFragment.CATEGORY_ID_KEY to -1L)) {
            (this as AddEditCategoryFragment).apply {
                replace(AddEditCategoryFragment::parentViewModel, mockParentViewModel)
                testFragment = this
            }
        }

    }

    @Test
    fun starts_correctly_when_new_category() {

        // Category text correct
        onView(withId(R.id.etCategory)).check(matches(withText(R.string.activity_manage_categories_new_category)))

        // Category text focused
        onView(withId(R.id.etCategory)).check(matches(hasFocus()))

    }

    @Test
    fun starts_correctly_when_edit_category() {

        // Launch scenario
        launchFragmentInHiltContainer<AddEditCategoryFragment>(bundleOf(AddEditCategoryFragment.CATEGORY_ID_KEY to 3L)) {
            (this as AddEditCategoryFragment).apply {
                replace(AddEditCategoryFragment::parentViewModel, mockParentViewModel)
                testFragment = this
            }
        }

        // Category text correct
        onView(withId(R.id.etCategory)).check(matches(withText(categories[2].name)))

        // Category text focused
        onView(withId(R.id.etCategory)).check(matches(hasFocus()))

    }

    @Test
    fun handles_confirm_click_correctly_when_new_category_and_valid_name() {


        val newName = "text"

        // Define newName ist valid
        onView(withId(R.id.etCategory)).perform(clearText())
        `when`(mockParentViewModel.addEditCategory(null, newName)).thenReturn(true)

        // Write something
        onView(withId(R.id.etCategory)).perform(typeText(newName))

        // Confirm
        onView(withId(R.id.ivDone)).perform(click())

        // Check addEditCategoryCalled
        verify(mockParentViewModel).addEditCategory(null, newName)

        assertThat(testFragment.isVisible).isFalse()

    }

    @Test
    fun handles_confirm_click_correctly_when_new_category_and_invalid_name() {

        val newName = "text"

        // Define newName ist valid
        `when`(mockParentViewModel.addEditCategory(null, newName)).thenReturn(false)

        // Write something
        onView(withId(R.id.etCategory)).perform(clearText())
        onView(withId(R.id.etCategory)).perform(typeText(newName))

        // Confirm
        onView(withId(R.id.ivDone)).perform(click())

        // Check addEditCategoryCalled
        verify(mockParentViewModel).addEditCategory(null, newName)

        assertThat(testFragment.isVisible).isTrue()

    }

    @Test
    fun handles_confirm_click_correctly_when_edit_category_and_valid_name() {

        val id = 3L

        // Launch scenario
        launchFragmentInHiltContainer<AddEditCategoryFragment>(bundleOf(AddEditCategoryFragment.CATEGORY_ID_KEY to id)) {
            (this as AddEditCategoryFragment).apply {
                replace(AddEditCategoryFragment::parentViewModel, mockParentViewModel)
                testFragment = this
            }
        }

        val newName = "text"

        // Define newName ist valid
        onView(withId(R.id.etCategory)).perform(clearText())
        `when`(mockParentViewModel.addEditCategory(id, newName)).thenReturn(true)

        // Write something
        onView(withId(R.id.etCategory)).perform(typeText(newName))

        // Confirm
        onView(withId(R.id.ivDone)).perform(click())

        // Check addEditCategoryCalled
        verify(mockParentViewModel).addEditCategory(id, newName)

        assertThat(testFragment.isVisible).isFalse()

    }

    @Test
    fun handles_confirm_click_correctly_when_edit_category_and_invalid_name() {

        val id = 3L

        // Launch scenario
        launchFragmentInHiltContainer<AddEditCategoryFragment>(bundleOf(AddEditCategoryFragment.CATEGORY_ID_KEY to id)) {
            (this as AddEditCategoryFragment).apply {
                replace(AddEditCategoryFragment::parentViewModel, mockParentViewModel)
                testFragment = this
            }
        }

        val newName = "text"

        // Define newName ist valid
        `when`(mockParentViewModel.addEditCategory(id, newName)).thenReturn(false)

        // Write something
        onView(withId(R.id.etCategory)).perform(clearText())
        onView(withId(R.id.etCategory)).perform(typeText(newName))

        // Confirm
        onView(withId(R.id.ivDone)).perform(click())

        // Check addEditCategoryCalled
        verify(mockParentViewModel).addEditCategory(id, newName)

        assertThat(testFragment.isVisible).isTrue()

    }

}