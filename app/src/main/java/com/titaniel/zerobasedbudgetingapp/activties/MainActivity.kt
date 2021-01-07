package com.titaniel.zerobasedbudgetingapp.activties

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.budget.Category
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.BudgetFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_transactions.TransactionsFragment
import com.titaniel.zerobasedbudgetingapp.transaction.Transaction
import com.titaniel.zerobasedbudgetingapp.transaction.TransactionManager
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    /**
     * Holds data for transactions.
     */
    lateinit var mTransactionManager: TransactionManager;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()

        findViewById<BottomNavigationView>(R.id.bottom_navigation).setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_budget -> {
                    loadFragment(BudgetFragment())
                }
                R.id.page_add_edit_transaction -> {
                    startActivity(Intent(this, AddEditTransactionActivity::class.java))
                    false
                }
                R.id.page_transactions -> {
                    loadFragment(TransactionsFragment())
                }
                else -> false
            }
        }

        findViewById<BottomNavigationView>(R.id.bottom_navigation).selectedItemId = R.id.page_budget

    }

    /**
     * Load saved transaction and budgeting data.
     */
    private fun initData() {
        mTransactionManager = TransactionManager(
            emptyList(), listOf(
                Transaction(5, "Aldi", "Hallaölskdjf", Date(), Category(HashMap(), "Lebensmittel")),
                Transaction(10, "Rossmann", "", Date(), Category(HashMap(), "Süßes")),
                Transaction(-34, "Lidl", "", Date(), Category(HashMap(), "Lebensmittel")),
                Transaction(-235, "Autohaus", "", Date(), Category(HashMap(), "Autos"))
            )
        )
    }

    /**
     * Loads a fragment into fragment container.
     * @param fragment Fragment to load.
     * @return If operation could successfully be started.
     */
    private fun loadFragment(fragment: Fragment?): Boolean = if (fragment != null) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        true
    } else {
        false
    }

}