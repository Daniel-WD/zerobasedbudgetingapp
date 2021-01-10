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

class SelectPayeeFragment : BottomSheetDialogFragment() {

    companion object {
        const val PAYEE_KEY = "payee_key"
    }

    private lateinit var mIvAddPayee: ImageView
    private lateinit var mListPayees: RecyclerView
    private lateinit var mEtPayee: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_payee, container, false)

        // View initialization.
        mIvAddPayee = view.findViewById(R.id.ivAddPayee)
        mListPayees = view.findViewById(R.id.listPayees)
        mEtPayee = view.findViewById(R.id.etPayee)

        // Add payee listener.
        mIvAddPayee.setOnClickListener {
            returnPayee(mEtPayee.text.toString())
        }
        mEtPayee.setOnEditorActionListener { _, _, _ ->
            returnPayee(mEtPayee.text.toString())
            true
        }


        // ListPayees initialization.
        mListPayees.layoutManager = LinearLayoutManager(requireContext())
        mListPayees.setHasFixedSize(true)
        mListPayees.adapter = PayeesListAdapter(
            listOf(
                "Aldi",
                "Rossmann",
                "Lidl",
                "Autohaus",
                "Kaufland",
                "New Yorker",
                "Centrum Galerie",
                "Check24",
                "Amazon Ratenzahlung",
                "Samsung",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee"
            ).sorted(),
            {
                returnPayee(it)
            },
            requireContext()
        )

        return view
    }

    private fun returnPayee(payee: String): Boolean {
        if (payee.isNotBlank()) {
            setFragmentResult(
                AddEditTransactionActivity.PAYEE_REQUEST_KEY,
                bundleOf(PAYEE_KEY to payee)
            )
            dismiss()
            return true
        }
        return false
    }

}