package com.titaniel.zerobasedbudgetingapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.compose.screen_budget.BudgetFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions.TransactionsFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * [MainActivity] where the application starts.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bottom navigation listener
        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_budget -> {
                    // Load budget fragment
                    loadFragment(BudgetFragment())
                }
                R.id.page_add_edit_transaction -> {
                    // Start add/edit transaction activity
                    startActivity(Intent(this, AddEditTransactionActivity::class.java))
                    false
                }
                R.id.page_transactions -> {
                    // Load transactions fragment
                    loadFragment(TransactionsFragment())
                }
                else -> false
            }
        }

        // Set start bottom navigation page
        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.page_budget

    }

    /**
     * Loads [fragment] into fragment container. Returns if fragment is not null.
     */
    private fun loadFragment(fragment: Fragment?): Boolean = if (fragment != null) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        true
    } else false


}