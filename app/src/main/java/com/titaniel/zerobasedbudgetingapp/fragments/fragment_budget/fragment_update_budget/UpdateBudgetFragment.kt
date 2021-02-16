package com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateBudgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    /**
     * Category name
     */
    val budget =
        budgetRepository.getBudgetById(savedStateHandle[UpdateBudgetFragment.BUDGET_ID_KEY]!!)
            .asLiveData()

    fun updateBudget(budgeted: Long) {

        val bud = budget.value!!

        bud.budgeted = budgeted
        viewModelScope.launch {
            budgetRepository.updateBudget(bud)
        }

    }

}

@AndroidEntryPoint
class UpdateBudgetFragment : BottomSheetDialogFragment() {

    companion object {

        /**
         * Budget id key
         */
        const val BUDGET_ID_KEY = "budget_id_key"

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
     * View model
     */
    private val mViewModel: UpdateBudgetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Create root view
        val view = inflater.inflate(R.layout.fragment_update_budget, container, false)

        // Initialize views
        mTvCategory = view.findViewById(R.id.tvCategory)
        mEtBudgeted = view.findViewById(R.id.etBudgeted)
        mIvDone = view.findViewById(R.id.ivDone)

        // Budget observer
        mViewModel.budget.observe(viewLifecycleOwner) {

            // Set category name
            mTvCategory.text = it.categoryName

            // Set budgeted value
            mEtBudgeted.setText(it.budgeted.toString())

            // Select budget text
            mEtBudgeted.selectAll()

            // Focus budgeted edittext
            mEtBudgeted.requestFocus()
        }

        // Setup done listener
        mIvDone.setOnClickListener {
            updateBudget()
        }

        // Keyboard done action click listener
        mEtBudgeted.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                updateBudget()
            }
            false
        }

        return view
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
     * Updates budget and dismiss dialog
     */
    private fun updateBudget() {
        // TODO ENTWERDER SO ODER WIE IN ADD EDIT TRANSACTION ACTIVITY

        // Budgeted value
        val budgeted =
            if (mEtBudgeted.text.isBlank()) 0 else mEtBudgeted.text.toString().toLong()

        mViewModel.updateBudget(budgeted)

        // Close fragment
        dismiss()
    }
}