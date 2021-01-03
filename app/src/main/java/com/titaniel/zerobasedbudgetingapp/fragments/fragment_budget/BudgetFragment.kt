package com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.titaniel.zerobasedbudgetingapp.R

/**
 * Fragment to show a list of categories. Each item contains budgeting information, which can be edited.
 */
class BudgetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

}