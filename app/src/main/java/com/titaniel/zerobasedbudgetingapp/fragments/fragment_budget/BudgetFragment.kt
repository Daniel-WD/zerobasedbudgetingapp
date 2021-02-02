package com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_update_budget.UpdateBudgetFragment

/**
 * Fragment to show a list of categories. Each item contains budgeting information, which can be edited.
 */
class BudgetFragment : Fragment() {

    companion object {

        /**
         * Budgeted value request key
         */
        const val BUDGETED_VALUE_REQUEST_KEY = "budgeted_value_request_key"

    }

    /**
     * Toolbar
     */
    private lateinit var mToolbar: MaterialToolbar

    /**
     * "To be budgeted" Text
     */
    private lateinit var mTvToBeBudgeted: TextView

    /**
     * Budgeting list
     */
    private lateinit var mListBudgeting: RecyclerView

    /**
     * Data manager
     */
    private lateinit var mDataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        // Init
        mToolbar = view.findViewById(R.id.toolbar)
        mTvToBeBudgeted = view.findViewById(R.id.tvToBeBudgeted)
        mListBudgeting = view.findViewById(R.id.listBudgeting)

        // Init data manager
        mDataManager = DataManager(requireContext(), lifecycle)

        // Setup toolbar
        mToolbar.menu

        // Init list categories
        // Set layout manager
        mListBudgeting.layoutManager = LinearLayoutManager(requireContext())

        // Add adapter
        mListBudgeting.adapter = BudgetingListAdapter(mDataManager.categories, { categoryName -> // Category click

            // Create update budget fragment
            val updateBudgetFragment = UpdateBudgetFragment()

            // Category name,  budgeted value as arguments
            updateBudgetFragment.arguments =
                bundleOf(UpdateBudgetFragment.CATEGORY_NAME_KEY to categoryName, UpdateBudgetFragment.BUDGETED_VALUE_KEY to 55L)

            // Show update budget fragment
            updateBudgetFragment.show(childFragmentManager, "UpdateBudgetFragment")

        }, requireContext())

        // Add horizontal dividers
        mListBudgeting.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        // Set budgeted value result listener
        childFragmentManager
            .setFragmentResultListener(BUDGETED_VALUE_REQUEST_KEY, this) { _, bundle ->

                // New budgeted value
                val newBudget = bundle.getLong(UpdateBudgetFragment.NEW_BUDGET_KEY)

                // Corresponding category
                val category = mDataManager.categories.find { category ->
                    category.name == bundle.getString(UpdateBudgetFragment.CATEGORY_NAME_KEY)
                }

                // TODO Save budgeted value
                Log.d("TAg", "$newBudget $category")

            }

        return view
    }

}