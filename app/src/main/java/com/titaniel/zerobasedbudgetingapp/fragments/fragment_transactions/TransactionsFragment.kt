package com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions

import android.content.Intent
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
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
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
        mDataManager = DataManager.create(requireContext(), lifecycle)

        // Set loaded callback
        mDataManager.loadedCallback = {

            // Reload transactions list
            mTransactionsList.adapter?.notifyDataSetChanged()
        }

        // Init transactionList
        // Set layout manager
        mTransactionsList.layoutManager = LinearLayoutManager(context)

        // Fix size
        mTransactionsList.setHasFixedSize(true)

        // Set adapter
        mTransactionsList.adapter = TransactionsListAdapter(
            mDataManager.transactions,
            { transaction ->
                // Start add/edit transaction activity and transmit transaction uuid
                startActivity(
                    Intent(requireContext(), AddEditTransactionActivity::class.java).putExtra(
                        AddEditTransactionActivity.EDIT_TRANSACTION_UUID_KEY,
                        transaction.uuid
                    ),
                )
            },
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

}