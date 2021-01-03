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
import com.titaniel.zerobasedbudgetingapp.MainActivity
import com.titaniel.zerobasedbudgetingapp.R

class TransactionsFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var transactionsList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        // View init
        toolbar = view!!.findViewById(R.id.toolbar)
        transactionsList = view.findViewById(R.id.transactionsList)

        transactionsList.layoutManager = LinearLayoutManager(context)
        transactionsList.setHasFixedSize(true)
        transactionsList.adapter = TransactionsListAdapter(
            (activity as MainActivity).transactionManager.transactions,
            context!!
        )
        transactionsList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        // Listeners
        toolbar.setNavigationOnClickListener {

        }

        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

}