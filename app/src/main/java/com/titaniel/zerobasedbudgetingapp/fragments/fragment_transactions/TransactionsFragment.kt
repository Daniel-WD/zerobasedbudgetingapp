package com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions

import android.content.Intent
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
import com.titaniel.zerobasedbudgetingapp.activities.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * [TransactionsViewModel] with [transactionRepository].
 */
@HiltViewModel
class TransactionsViewModel @Inject constructor(
        transactionRepository: TransactionRepository
) : ViewModel() {

    /**
     * All transactions
     */
    val transactions: LiveData<List<Transaction>> =
            transactionRepository.getAllTransactions().asLiveData()

}

/**
 * Fragment to display list of transactions
 */
@AndroidEntryPoint
class TransactionsFragment : Fragment(R.layout.fragment_transactions) {

    /**
     * Toolbar
     */
    private lateinit var toolbar: MaterialToolbar

    /**
     * List of all transactions
     */
    private lateinit var transactionsList: RecyclerView

    /**
     * ViewModel
     */
    private val viewModel: TransactionsViewModel by viewModels()

    override fun onStart() {
        super.onStart()

        // Init views
        toolbar = requireView().findViewById(R.id.toolbar)
        transactionsList = requireView().findViewById(R.id.transactionsList)

        // Init transactionList
        // Set layout manager
        transactionsList.layoutManager = LinearLayoutManager(context)

        // Fix size
        transactionsList.setHasFixedSize(true)

        // Set adapter
        transactionsList.adapter = TransactionsListAdapter(
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
        transactionsList.addItemDecoration(
                DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                )
        )
    }

}