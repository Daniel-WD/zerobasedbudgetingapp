package com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.database.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.repositories.TransactionRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    transactionRepository: TransactionRepository
) : ViewModel() {

    val transactions: LiveData<List<Transaction>> =
        transactionRepository.getAllTransactions().asLiveData()

}

/**
 * Fragment to display list of transactions
 */
@AndroidEntryPoint
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
     * Viewmodel
     */
    private val viewModel: TransactionsViewModel by viewModels()

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

        // Init transactionList
        // Set layout manager
        mTransactionsList.layoutManager = LinearLayoutManager(context)

        // Fix size
        mTransactionsList.setHasFixedSize(true)

        // Set adapter
        mTransactionsList.adapter = TransactionsListAdapter(
            viewModel.transactions,
            { transaction ->
                // Start add/edit transaction activity and transmit transaction uuid
                startActivity(
                    Intent(requireContext(), AddEditTransactionActivity::class.java).putExtra(
                        AddEditTransactionActivity.EDIT_TRANSACTION_ID_KEY,
                        transaction.id
                    ),
                )
            },
            requireContext(),
            viewLifecycleOwner
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