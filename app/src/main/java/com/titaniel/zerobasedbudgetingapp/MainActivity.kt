package com.titaniel.zerobasedbudgetingapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_add_transaction.AddTransactionFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.BudgetFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions.TransactionsFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            loadFragment(
                when (item.itemId) {
                    R.id.page_budget -> {
                        BudgetFragment()
                    }
                    R.id.page_add_transaction -> {
                        AddTransactionFragment()
                    }
                    R.id.page_transactions -> {
                        TransactionsFragment()
                    }
                    else -> null
                }
            )
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.page_budget


    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        //switching fragment
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            return true
        }
        return false
    }
}