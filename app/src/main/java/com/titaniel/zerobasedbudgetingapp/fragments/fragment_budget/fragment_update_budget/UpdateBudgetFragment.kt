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
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [UpdateBudgetViewModel] with [savedStateHandle] and [budgetRepository]
 */
@HiltViewModel
class UpdateBudgetViewModel @Inject constructor(
        savedStateHandle: SavedStateHandle,
        private val budgetRepository: BudgetRepository
) : ViewModel() {

    /**
     * Budget to edit
     */
    val budget = budgetRepository.getBudgetById(savedStateHandle[UpdateBudgetFragment.BUDGET_ID_KEY]!!)
            .asLiveData()

    /**
     * Update budget with [budgeted] value
     */
    fun updateBudget(budgeted: Long) {

        // Get budget, check non-null
        val bud = budget.value!!

        // Set budget
        bud.budgeted = budgeted
        viewModelScope.launch {
            // Update budget in repo
            budgetRepository.updateBudgets(bud)
        }

    }

}

/**
 * [UpdateBudgetFragment] to change budgeted value of a [Budget]
 */
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
    private lateinit var tvCategory: TextView

    /**
     * Budgeted value edit text
     */
    private lateinit var etBudgeted: EditText

    /**
     * Done button
     */
    private lateinit var ivDone: ImageView

    /**
     * View model
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: UpdateBudgetViewModel by provideViewModel()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Create root view
        return inflater.inflate(R.layout.fragment_update_budget, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Initialize views
        tvCategory = requireView().findViewById(R.id.tvCategory)
        etBudgeted = requireView().findViewById(R.id.etBudgeted)
        ivDone = requireView().findViewById(R.id.ivDone)

        // Budget observer
        viewModel.budget.observe(viewLifecycleOwner) {
            // Check budget non-null
            if(it == null) {
                return@observe
            }

            // Set category name
            tvCategory.text = it.categoryName

            // Set budgeted value
            etBudgeted.setText(it.budgeted.toString())

            // Select budget text
            etBudgeted.selectAll()

            // Focus budgeted EditText
            etBudgeted.requestFocus()
        }

        // Setup done listener
        ivDone.setOnClickListener {
            updateBudget()
        }

        // Keyboard done action click listener
        etBudgeted.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                updateBudget()
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
     * Update budget and dismiss dialog
     */
    private fun updateBudget() {

        // Budgeted value
        val budgeted =
                if (etBudgeted.text.isBlank()) 0 else etBudgeted.text.toString().toLong()

        viewModel.updateBudget(budgeted)

        // Close fragment
        dismiss()
    }
}