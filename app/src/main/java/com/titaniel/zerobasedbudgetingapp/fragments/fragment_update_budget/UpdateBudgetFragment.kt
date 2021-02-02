package com.titaniel.zerobasedbudgetingapp.fragments.fragment_update_budget

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.BudgetFragment


class UpdateBudgetFragment : BottomSheetDialogFragment() {

    companion object {
        /**
         * Update budget key
         */
        const val NEW_BUDGET_KEY = "new_budget_key"

        /**
         * Category name key
         */
        const val CATEGORY_NAME_KEY = "category_name_key"

        /**
         * Category name key
         */
        const val BUDGETED_VALUE_KEY = "budgeted_value_key"
    }

    /**
     * Category text
     */
    private lateinit var mTvCategory: TextView

    /**
     * Budgeted value edit text
     */
    private lateinit var mEtBudgeted: EditText

    /**
     * Done button
     */
    private lateinit var mIvDone: ImageView

    /**
     * Category name
     */
    private lateinit var mCategoryName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Create root view
        val view = inflater.inflate(R.layout.fragment_update_budget, container, false)

        // Load category name and previously budgeted value
        mCategoryName = requireArguments().getString(CATEGORY_NAME_KEY)!!
        val budgetedValue = requireArguments().getLong(BUDGETED_VALUE_KEY)

        // Initialize views
        mTvCategory = view.findViewById(R.id.tvCategory)
        mEtBudgeted = view.findViewById(R.id.etBudgeted)
        mIvDone = view.findViewById(R.id.ivDone)

        // Set category name
        mTvCategory.text = mCategoryName

        // Setup done listener
        mIvDone.setOnClickListener {
            returnBudgetedValue()
        }

        // Keyboard done action click listener
        mEtBudgeted.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                returnBudgetedValue()
            }
            false
        }

        // Set budgeted value
        mEtBudgeted.setText(budgetedValue.toString())

        // Focus budgeted edittext
        mEtBudgeted.requestFocus()

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Get dialog instance
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        // FIXME This is a workaround for wrong height of dialog when keyboard is shown
        // Set soft input mode to SOFT_INPUT_ADJUST_RESIZE
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        return dialog
    }

    /**
     * Returns budgeted value to budget fragment and dismiss dialog
     */
    private fun returnBudgetedValue() {

        // Budgeted value
        val budgeted =
            if (mEtBudgeted.text.isBlank()) 0 else mEtBudgeted.text.toString().toLong()

        // Return fragment result
        setFragmentResult(
            BudgetFragment.BUDGETED_VALUE_REQUEST_KEY,
            bundleOf(NEW_BUDGET_KEY to budgeted, CATEGORY_NAME_KEY to mCategoryName)
        )

        // Close fragment
        dismiss()
    }
}