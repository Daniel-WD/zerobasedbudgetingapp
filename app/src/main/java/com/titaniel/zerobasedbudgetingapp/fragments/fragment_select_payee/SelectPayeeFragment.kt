package com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.MainActivity

class SelectPayeeFragment : BottomSheetDialogFragment() {

    private lateinit var mIvAddPayee: ImageView
    private lateinit var mListPayees: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_select_payee, container, false)

        // View initialization.
        mIvAddPayee = view.findViewById(R.id.ivAddPayee)
        mListPayees = view.findViewById(R.id.listPayees)

        // ListPayees initialization.
        mListPayees.layoutManager = LinearLayoutManager(context!!)
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
            context!!
        )

        return view
    }

}