package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager

/**
 * Bottom sheet dialog fragment for payee selection
 */
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
     * Data manager
     */
    private lateinit var mDataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Create root view
        val view = inflater.inflate(R.layout.fragment_select_payee, container, false)

        // Init data manager
        mDataManager = DataManager.create(requireContext(), lifecycle)

        // Initialize views
        mIvAddPayee = view.findViewById(R.id.ivAddPayee)
        mListPayees = view.findViewById(R.id.listPayees)
        mEtNewPayee = view.findViewById(R.id.etNewPayee)

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
            mDataManager.payees,
            { payee -> // Payee click callback
                selectPayee(payee)
            },
            requireContext()
        )

        return view
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