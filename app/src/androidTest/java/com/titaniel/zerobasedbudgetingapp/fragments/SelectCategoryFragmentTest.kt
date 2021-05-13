package com.titaniel.zerobasedbudgetingapp.fragments

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.checkRecyclerViewContentHasCorrectData
import com.titaniel.zerobasedbudgetingapp._testutils.launchFragmentInHiltContainer
import com.titaniel.zerobasedbudgetingapp._testutils.replace
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionViewModel
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.CategoriesListAdapter
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SelectCategoryFragmentTest {

    /**
     * Fragment scenario
     */
    private lateinit var testFragment: SelectCategoryFragment

    /**
     * Mock ViewModel
     */
    @Mock
    private lateinit var mockParentViewModel: AddEditTransactionViewModel

    /**
     * Example categories
     */
    private val exampleCategories = mutableListOf(
        Category("cat1", 0),
        Category("cat2", 1),
        Category("cat3", 2),
        Category("cat4", 3),
        Category("cat5", 4)
    )

    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockParentViewModel.allCategories).thenReturn(MutableLiveData(exampleCategories))
        `when`(mockParentViewModel.category).thenReturn(MutableLiveData())

        // Launch scenario
        launchFragmentInHiltContainer<SelectCategoryFragment> {
            (this as SelectCategoryFragment).apply {
                replace(SelectCategoryFragment::parentViewModel, mockParentViewModel)
                testFragment = this
            }
        }
    }

    @Test
    fun starts_correctly() {

        checkCategoryListContent()

    }

    @Test
    fun handles_data_change_correctly() {

        // Change data
        exampleCategories.add(Category("newCat", 0))

        // Sort list
        exampleCategories.sortBy { it.index }

        checkCategoryListContent()

    }

    @Test
    fun performs_item_click_correctly() {

        // Click item
        onView(withId(R.id.listCategories))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<CategoriesListAdapter.CategoryItem>(
                    4,
                    click()
                )
            )

        // Check if fragment finishes
        assertThat(testFragment.isAdded).isFalse()

        // Check category value in view model
        assertThat(mockParentViewModel.category.value).isEqualTo(exampleCategories[4])

    }

    private fun checkCategoryListContent() {
        checkRecyclerViewContentHasCorrectData(
            R.id.listCategories,
            exampleCategories,
            { hasDescendant(withText(it.name)) })
    }

}