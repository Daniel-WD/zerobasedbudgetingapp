package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionViewModel
import com.titaniel.zerobasedbudgetingapp.utils.provideActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Bottom sheet dialog fragment for payee selection
 */
@AndroidEntryPoint
class SelectPayeeFragment : BottomSheetDialogFragment() {

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
            // Try set new payee, dismiss if successful
            parentViewModel.setNewPayee(etNewPayee.text.toString()).let { if (it) dismiss() }
        }

        // Keyboard 'OK' click listener
        etNewPayee.setOnEditorActionListener { _, _, _ ->
            // Try set new payee, dismiss if successful
            parentViewModel.setNewPayee(etNewPayee.text.toString()).let { if (it) dismiss() }
            true
        }

        // Payee list init
        // Set layout manager
        listPayees.layoutManager = LinearLayoutManager(requireContext())

        // Fix size
        listPayees.setHasFixedSize(true)

        // Set adapter
        listPayees.adapter = PayeesListAdapter(
            parentViewModel.allPayees,
            { payee -> // Payee click callback

                // Set payee
                parentViewModel.payee.value = payee

                dismiss()
            },
            requireContext(),
            viewLifecycleOwner
        )

    }

}