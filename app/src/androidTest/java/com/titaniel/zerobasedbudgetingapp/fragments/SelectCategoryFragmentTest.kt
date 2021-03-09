package com.titaniel.zerobasedbudgetingapp.fragments

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp._testutils.atPosition
import com.titaniel.zerobasedbudgetingapp._testutils.checkRecyclerViewContentHasCorrectData
import com.titaniel.zerobasedbudgetingapp._testutils.launchFragmentInHiltContainer
import com.titaniel.zerobasedbudgetingapp._testutils.replace
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.CategoriesListAdapter
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryViewModel
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
    private lateinit var mockViewModel: SelectCategoryViewModel

    /**
     * Example categories
     */
    private val exampleCategories = mutableListOf(
        Category("cat1"),
        Category("cat2"),
        Category("cat3"),
        Category("cat4"),
        Category("cat5")
    )

    @Before
    fun setup() {
        // Set ViewModel properties
        `when`(mockViewModel.categories).thenReturn(MutableLiveData(exampleCategories))

        // Launch scenario
        launchFragmentInHiltContainer<SelectCategoryFragment> {
            (this as SelectCategoryFragment).apply {
                replace(SelectCategoryFragment::viewModel, mockViewModel)
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
        exampleCategories.add(Category("newCat"))

        checkCategoryListContent()

    }

    @Test
    fun performs_item_click_correctly() {

        // Set fragment result listener
        testFragment.requireActivity().runOnUiThread {
            testFragment.setFragmentResultListener(AddEditTransactionActivity.CATEGORY_REQUEST_KEY) { s: String, bundle: Bundle ->
                assertThat(bundle[SelectCategoryFragment.CATEGORY_KEY]).isEqualTo("cat5")
            }
        }

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

    }

    private fun checkCategoryListContent() {
        checkRecyclerViewContentHasCorrectData(R.id.listCategories, exampleCategories, { hasDescendant(withText(it.name)) })
    }

}