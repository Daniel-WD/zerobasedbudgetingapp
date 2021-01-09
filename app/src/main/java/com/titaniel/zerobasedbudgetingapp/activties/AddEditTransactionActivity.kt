package com.titaniel.zerobasedbudgetingapp.activties

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.transaction.TransactionManager

class AddEditTransactionActivity : AppCompatActivity() {

    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mEtValue: EditText
    private lateinit var mTvPayee: TextView
    private lateinit var mTvCategory: TextView
    private lateinit var mTvDate: TextView
    private lateinit var mTvDescription: TextView
    private lateinit var mFabCreate: ExtendedFloatingActionButton
    private lateinit var mLlPayee: LinearLayout
    private lateinit var mLlCategory: LinearLayout
    private lateinit var mLlDate: LinearLayout
    private lateinit var mLlDescription: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_transaction)

        // View initialization.
        mToolbar = findViewById(R.id.toolbar)
        mEtValue = findViewById(R.id.etValue)
        mTvPayee = findViewById(R.id.tvPayee)
        mTvCategory = findViewById(R.id.tvCategory)
        mTvDate = findViewById(R.id.tvDate)
        mTvDescription = findViewById(R.id.tvDescription)
        mFabCreate = findViewById(R.id.fabCreate)
        mLlPayee = findViewById(R.id.layoutPayee);
        mLlCategory = findViewById(R.id.layoutCategory);
        mLlDate = findViewById(R.id.layoutDate);
        mLlDescription = findViewById(R.id.layoutDescription);


        // Toolbar listener.
        mToolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.delete -> {
                    finish()
                    // Handle delete icon press.
                    true
                }
                else -> false
            }
        }

        // Layout payee listener.
        mLlPayee.setOnClickListener {
            val selectPayeeFragment = SelectPayeeFragment()
            selectPayeeFragment.show(supportFragmentManager, "SelectPayeeFragment")
        }
    }

}