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
    private lateinit var mToolbar: MaterialToolbar

    /**
     * List of all transactions
     */
    private lateinit var mTransactionsList: RecyclerView

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
        mToolbar = view!!.findViewById(R.id.toolbar)
        mTransactionsList = view.findViewById(R.id.transactionsList)

        // Init data manager
        mDataManager = DataManager(requireContext(), lifecycle)

        // Init transactionList
        // Set layout manager
        mTransactionsList.layoutManager = LinearLayoutManager(context)

        // Fix size
        mTransactionsList.setHasFixedSize(true)

        // Set adapter
        mTransactionsList.adapter = TransactionsListAdapter(
            mDataManager.transactions,
            requireContext()
        )

        // Add horizontal dividers
        mTransactionsList.addItemDecoration(
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
        mTransactionsList.adapter?.notifyDataSetChanged()
    }

}