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
import com.titaniel.zerobasedbudgetingapp.database.entities.Category
import com.titaniel.zerobasedbudgetingapp.repositories.CategoryRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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
 * Bottom sheet dialog fragment for category selection
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
        val view = inflater.inflate(R.layout.fragment_select_category, container, false)

        // Initialize views
        mListCategories = view.findViewById(R.id.listCategories)
        mTvToBeBudgeted = view.findViewById(R.id.tvToBeBudgeted)

        // Category list init
        // Set layout manager
        mListCategories.layoutManager = LinearLayoutManager(requireContext())

        // Fix size
        mListCategories.setHasFixedSize(true)

        // Set adapter
        mListCategories.adapter = CategoriesListAdapter(
            mViewModel.categories,
            { category -> // Category click callback
                returnResult(category.name)
            },
            requireContext(),
            viewLifecycleOwner
        )

        // To be budgeted text click listener
        mTvToBeBudgeted.setOnClickListener {
            returnResult(Category.TO_BE_BUDGETED)
        }

        return view
    }

    private fun returnResult(result: String) {
        // Return fragment result
        setFragmentResult(
            AddEditTransactionActivity.CATEGORY_REQUEST_KEY,
            bundleOf(CATEGORY_KEY to result)
        )

        // Close fragment
        dismiss()
    }

}