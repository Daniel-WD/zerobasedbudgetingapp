package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.database.repositories.PayeeRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * [SelectPayeeViewModel] with [payeeRepository].
 */
@HiltViewModel
class SelectPayeeViewModel @Inject constructor(
    payeeRepository: PayeeRepository
) : ViewModel() {

    /**
     * All payees
     */
    val payees = payeeRepository.getAllPayees().asLiveData()

}

/**
 * Bottom sheet dialog fragment for payee selection
 */
@AndroidEntryPoint
class SelectPayeeFragment : BottomSheetDialogFragment() {

    companion object {
        /**
         * Payee key
         */
        const val PAYEE_KEY = "payee_key"
    }

    /**
     * Add payee image
     */
    private lateinit var ivAddPayee: ImageView

    /**
     * New payee text
     */
    private lateinit var etNewPayee: EditText

    /**
     * Payees list
     */
    private lateinit var listPayees: RecyclerView

    /**
     * View model
     */
    private val viewModel: SelectPayeeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Create root view
        return inflater.inflate(R.layout.fragment_select_payee, container, false)
    }

    override fun onStart() {
        super.onStart()

        // Initialize views
        ivAddPayee = requireView().findViewById(R.id.ivAddPayee)
        listPayees = requireView().findViewById(R.id.listPayees)
        etNewPayee = requireView().findViewById(R.id.etNewPayee)

        // Add payee listener
        ivAddPayee.setOnClickListener {
            returnPayee(etNewPayee.text.toString())
        }
        // Keyboard 'OK' click listener
        etNewPayee.setOnEditorActionListener { _, _, _ ->
            returnPayee(etNewPayee.text.toString())
            true
        }

        // Payee list init
        // Set layout manager
        listPayees.layoutManager = LinearLayoutManager(requireContext())

        // Fix size
        listPayees.setHasFixedSize(true)

        // Set adapter
        listPayees.adapter = PayeesListAdapter(
            viewModel.payees,
            { payee -> // Payee click callback
                returnPayee(payee.name)
            },
            requireContext(),
            viewLifecycleOwner
        )

    }

    /**
     * Returns [payeeName] to [AddEditTransactionActivity] and dismisses dialog, if [payeeName] is not blank.
     */
    private fun returnPayee(payeeName: String) {
        // Payee string not blank?
        if (payeeName.isNotBlank()) {

            // Return fragment result
            setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(PAYEE_KEY to payeeName)
            )

            // Close fragment
            dismiss()
        }
    }

}