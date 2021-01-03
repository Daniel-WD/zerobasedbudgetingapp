package com.titaniel.zerobasedbudgetingapp.fragments.fragment_add_transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.titaniel.zerobasedbudgetingapp.R

/**
 * Fragment to add/edit a transaction
 */
class AddEditTransactionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_edit_transaction, container, false)
    }

}