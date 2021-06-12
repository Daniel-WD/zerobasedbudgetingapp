package com.titaniel.zerobasedbudgetingapp.fragments.fragment_set_month

import androidx.fragment.app.Fragment
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_month.SelectMonthFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Dropdown to put into a toolbar to set the month
 */
@AndroidEntryPoint
class SetMonthFragment : Fragment(R.layout.fragment_set_month) {

    override fun onStart() {
        super.onStart()

        requireView().setOnClickListener {

            val selectMonthFragment = SelectMonthFragment()
            selectMonthFragment.show(childFragmentManager, "SelectMonthFragment")
        }

    }

}