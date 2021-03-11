package com.titaniel.zerobasedbudgetingapp.fragments.fragment_add_edit_category

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetViewModel
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [UpdateBudgetViewModel] with [savedStateHandle] and [budgetRepository]
 */
@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    /**
     * Category to edit
     */
    val category =
        categoryRepository.getCategoryByName(savedStateHandle[AddEditCategoryFragment.CATEGORY_ID_KEY]!!)
            .asLiveData()

    /**
     * Update category to have [categoryName]
     */
    fun updateCategory(categoryName: String) {

        // Get budget, check non-null
        val bud = category.value
        requireNotNull(bud)

        // Set category name TODO
//        bud.budgeted = budgeted
        viewModelScope.launch {
            // Update budget in repo TODO
//            budgetRepository.updateBudgets(bud)
        }

    }

}

/**
 * [UpdateBudgetFragment] to change budgeted value of a [Budget]
 */
@AndroidEntryPoint
class AddEditCategoryFragment : BottomSheetDialogFragment() {

    companion object {

        /**
         * Category id key
         */
        const val CATEGORY_ID_KEY = "category_name_key"

    }

    /**
     * Category text
     */
    private lateinit var etCategory: EditText

    /**
     * Done button
     */
    private lateinit var ivDone: ImageView

    /**
     * View model
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: AddEditCategoryViewModel by provideViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Create root view
        return inflater.inflate(R.layout.fragment_add_edit_category, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Initialize views
        etCategory = requireView().findViewById(R.id.etCategory)
        ivDone = requireView().findViewById(R.id.ivDone)

        // Category observer
        viewModel.category.observe(viewLifecycleOwner) {
            // Check category non-null
            if (it == null) {
                return@observe
            }

            // Set category name
            etCategory.setText(it.name)

            // Select category name text
            etCategory.selectAll()

            // Focus category name EditText
            etCategory.requestFocus()
        }

        // Setup done listener
        ivDone.setOnClickListener {
            updateCategoryName()
        }

        // Keyboard done action click listener
        etCategory.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                updateCategoryName()
            }
            false
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Get dialog instance
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        // FIXME This is a workaround for wrong height of dialog when keyboard is shown and its deprecated
        // Set soft input mode to SOFT_INPUT_ADJUST_RESIZE
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return dialog
    }

    /**
     * Update category name and dismiss dialog
     */
    private fun updateCategoryName() {

        // Budgeted value
        val newCategoryName = etCategory.text.toString()

        viewModel.updateCategory(newCategoryName)

        // Close fragment
        dismiss()
    }
}