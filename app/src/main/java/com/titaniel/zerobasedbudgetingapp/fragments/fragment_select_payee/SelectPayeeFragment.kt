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
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.database.repositories.PayeeRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectPayeeViewModel @Inject constructor(
    payeeRepository: PayeeRepository
) : ViewModel() {

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
    private lateinit var mIvAddPayee: ImageView

    /**
     * New payee text
     */
    private lateinit var mEtNewPayee: EditText

    /**
     * Payees list
     */
    private lateinit var mListPayees: RecyclerView

    /**
     * View model
     */
    private val mViewModel: SelectPayeeViewModel by viewModels()

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
        mIvAddPayee = requireView().findViewById(R.id.ivAddPayee)
        mListPayees = requireView().findViewById(R.id.listPayees)
        mEtNewPayee = requireView().findViewById(R.id.etNewPayee)

        // Add payee listener
        mIvAddPayee.setOnClickListener {
            selectPayee(mEtNewPayee.text.toString())
        }
        // Keyboard 'OK' click listener
        mEtNewPayee.setOnEditorActionListener { _, _, _ ->
            selectPayee(mEtNewPayee.text.toString())
            true
        }

        // Payee list init
        // Set layout manager
        mListPayees.layoutManager = LinearLayoutManager(requireContext())

        // Fix size
        mListPayees.setHasFixedSize(true)

        // Set adapter
        mListPayees.adapter = PayeesListAdapter(
            mViewModel.payees,
            { payee -> // Payee click callback
                selectPayee(payee.name)
            },
            requireContext(),
            viewLifecycleOwner
        )

    }

    /**
     * Validate and return payee to parent
     * @param payee Payee
     * @return If payee is valid
     */
    private fun selectPayee(payee: String): Boolean {
        // Payee string not blank?
        if (payee.isNotBlank()) {

            // Return fragment result
            setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(PAYEE_KEY to payee)
            )

            // Close fragment
            dismiss()
            return true
        }
        return false
    }

}