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
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activities.ManageCategoriesViewModel
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * [UpdateBudgetFragment] to change budgeted value of a [Budget]
 */
@AndroidEntryPoint
class AddEditCategoryFragment : BottomSheetDialogFragment() {

    companion object {

        /**
         * Category id key
         */
        const val CATEGORY_ID_KEY = "category_id_key"

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
     * Parent ViewModel
     */
    private val parentViewModel: ManageCategoriesViewModel by activityViewModels()

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

        // Get category
        val category =
            requireArguments()[CATEGORY_ID_KEY].let { id -> parentViewModel.newCategories.value?.find { it.id == id } }

        // Setup etCategory
        // Set category name
        etCategory.setText(category?.name ?: getString(R.string.activity_manage_categories_new_category))

        // Select category name text
        etCategory.selectAll()

        // Focus category name EditText
        etCategory.requestFocus()

        // Setup done listener
        ivDone.setOnClickListener {
            // Try change category
            parentViewModel.addEditCategory(
                category?.id,
                etCategory.text.toString()
            ).let {
                if (it) {
                    dismiss()
                }
            }
        }

        // Keyboard done action click listener
        etCategory.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                // Try change category
                parentViewModel.addEditCategory(
                    category?.id,
                    etCategory.text.toString()
                ).let {
                    if (it) {
                        dismiss()
                    }
                }
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

}