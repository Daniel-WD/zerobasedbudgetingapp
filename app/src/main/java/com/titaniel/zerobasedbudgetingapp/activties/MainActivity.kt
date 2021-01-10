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
    // TODO --> Service
    lateinit var transactionManager: TransactionManager;

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
    // TODO --> Service
    private fun initData() {
        transactionManager = TransactionManager(
            listOf(
                "Aldi",
                "Rossmann",
                "Lidl",
                "Autohaus",
                "Kaufland",
                "New Yorker",
                "Centrum Galerie",
                "Check24",
                "Amazon Ratenzahlung",
                "Samsung",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee"
            ),
            listOf(
                Transaction(5, "Aldi", "Hallaölskdjf", 347583945, Category(HashMap(), "Lebensmittel")),
                Transaction(10, "Rossmann", "", 23452345, Category(HashMap(), "Süßes")),
                Transaction(-34, "Lidl", "", 7567364, Category(HashMap(), "Lebensmittel")),
                Transaction(-235, "Autohaus", "", 3464593, Category(HashMap(), "Autos"))
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