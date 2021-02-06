package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.datamanager.Category
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager

/**
 * Bottom sheet dialog fragment for category selection
 */
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
     * Data manager
     */
    private lateinit var mDataManager: DataManager

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

        // Init data manager
        mDataManager = DataManager(requireContext(), lifecycle)

        // Category list init
        // Set layout manager
        mListCategories.layoutManager = LinearLayoutManager(requireContext())

        // Fix size
        mListCategories.setHasFixedSize(true)

        // Set adapter
        mListCategories.adapter = CategoriesListAdapter(
            mDataManager.categories,
            { categoryName -> // Category click callback
                returnResult(categoryName)
            },
            requireContext()
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