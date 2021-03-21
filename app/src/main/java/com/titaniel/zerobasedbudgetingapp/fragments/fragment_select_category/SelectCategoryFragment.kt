package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionViewModel
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.utils.provideActivityViewModel
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

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
    private lateinit var listCategories: RecyclerView

    /**
     * To be budgeted text
     */
    private lateinit var tvToBeBudgeted: TextView

    /**
     * Parent ViewModel
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val parentViewModel: AddEditTransactionViewModel by provideActivityViewModel()

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
        listCategories = requireView().findViewById(R.id.listCategories)
        tvToBeBudgeted = requireView().findViewById(R.id.tvToBeBudgeted)

        // Category list init
        // Set layout manager
        listCategories.layoutManager = LinearLayoutManager(requireContext())

        // Fix size
        listCategories.setHasFixedSize(true)

        // Set adapter
        listCategories.adapter = CategoriesListAdapter(
            parentViewModel.allCategories,
            { category -> // Category click callback

                // Set category
                parentViewModel.category.value = category

                dismiss()

            },
            requireContext(),
            viewLifecycleOwner
        )

        // To be budgeted text click listener
        tvToBeBudgeted.setOnClickListener {

            // Set category to "To be budgeted"
            parentViewModel.category.value = Category.TO_BE_BUDGETED

            dismiss()
        }
    }

}