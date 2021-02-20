package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * [SelectCategoryViewModel] with [categoryRepository].
 */
@HiltViewModel
class SelectCategoryViewModel @Inject constructor(
    categoryRepository: CategoryRepository
) : ViewModel() {

    /**
     * All categories
     */
    val categories = categoryRepository.getAllCategories().asLiveData()

}

/**
 * [SelectCategoryFragment] for category selection
 */
@AndroidEntryPoint
class SelectCategoryFragment : BottomSheetDialogFragment() {

    companion object {
        /**
         * Category key
         */
        const val CATEGORY_KEY = "category_key"
    }

    /**
     * Categories list
     */
    private lateinit var mListCategories: RecyclerView

    /**
     * To be budgeted text
     */
    private lateinit var mTvToBeBudgeted: TextView

    /**
     * View model
     */
    private val mViewModel: SelectCategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Create root view
        return inflater.inflate(R.layout.fragment_select_category, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Initialize views
        mListCategories = requireView().findViewById(R.id.listCategories)
        mTvToBeBudgeted = requireView().findViewById(R.id.tvToBeBudgeted)

        // Category list init
        // Set layout manager
        mListCategories.layoutManager = LinearLayoutManager(requireContext())

        // Fix size
        mListCategories.setHasFixedSize(true)

        // Set adapter
        mListCategories.adapter = CategoriesListAdapter(
            mViewModel.categories,
            { category -> // Category click callback
                returnCategory(category.name)
            },
            requireContext(),
            viewLifecycleOwner
        )

        // To be budgeted text click listener
        mTvToBeBudgeted.setOnClickListener {
            returnCategory(Category.TO_BE_BUDGETED)
        }
    }

    /**
     * Return [categoryName] to [AddEditTransactionActivity].
     */
    private fun returnCategory(categoryName: String) {
        // Return fragment result
        setFragmentResult(
            AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
            bundleOf(CATEGORY_KEY to categoryName)
        )

        // Close fragment
        dismiss()
    }

}