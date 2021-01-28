package com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager

/**
 * Fragment to display list of transactions
 */
class TransactionsFragment : Fragment() {

    /**
     * Toolbar
     */
    private lateinit var toolbar: MaterialToolbar

    /**
     * List of all transactions
     */
    private lateinit var transactionsList: RecyclerView

    /**
     * Data manager
     */
    private lateinit var mDataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate view
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        // Init views
        toolbar = view!!.findViewById(R.id.toolbar)
        transactionsList = view.findViewById(R.id.transactionsList)

        // Init data manager
        mDataManager = DataManager(requireContext(), lifecycle)

        // Init transactionList
        // Set layout manager
        transactionsList.layoutManager = LinearLayoutManager(context)

        // Fix size
        transactionsList.setHasFixedSize(true)

        // Set adapter
        transactionsList.adapter = TransactionsListAdapter(
            mDataManager.transactions,
            requireContext()
        )

        // Add horizontal dividers
        transactionsList.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        return view
    }

    override fun onResume() {
        super.onResume()

        // Reload transactions list
        transactionsList.adapter?.notifyDataSetChanged()
    }

}