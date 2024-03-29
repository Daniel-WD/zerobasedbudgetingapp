package com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions

//import com.titaniel.zerobasedbudgetingapp.compose.dialog_select_month.SelectMonthDialogFragment
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.relations.TransactionWithCategoryAndPayee
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
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
    val transactionsWithCategoryAndPayee: LiveData<List<TransactionWithCategoryAndPayee>> =
        transactionRepository.getAllTransactionsWithCategoryAndPayee().asLiveData()

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
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: TransactionsViewModel by provideViewModel()

    override fun onStart() {
        super.onStart()

//        // Init views
//        toolbar = requireView().findViewById(R.id.toolbar)
//        transactionsList = requireView().findViewById(R.id.transactionsList)
//
//        // Toolbar click listener
//        toolbar.setOnClickListener {
//            SelectMonthDialogFragment()
//                .show(childFragmentManager, "SelectMonthFragment")
//        }
//
//        // Init transactionList
//        // Set layout manager
//        transactionsList.layoutManager = LinearLayoutManager(context)
//
//        // Fix size
//        transactionsList.setHasFixedSize(true)
//
//        // Set adapter
//        transactionsList.adapter = TransactionsListAdapter(
//            viewModel.transactionsWithCategoryAndPayee,
//            { transactionWithCategoryAndPayee ->
//                // Start add/edit transaction activity and transmit transaction uuid
//                startActivity(
//                    Intent(requireContext(), AddEditTransactionActivity::class.java).putExtra(
//                        AddEditTransactionActivity.EDIT_TRANSACTION_ID_KEY,
//                        transactionWithCategoryAndPayee.transaction.id
//                    ),
//                )
//            },
//            requireContext(),
//            viewLifecycleOwner
//        )
//
//        // Add horizontal dividers
//        transactionsList.addItemDecoration(
//            DividerItemDecoration(
//                context,
//                DividerItemDecoration.VERTICAL
//            )
//        )
    }

}