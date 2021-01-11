package com.titaniel.zerobasedbudgetingapp.activties

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.utils.Utils

class AddEditTransactionActivity : AppCompatActivity() {

    companion object {
        const val PAYEE_REQUEST_KEY = "payee_request_key"
        const val CATEGORY_REQUEST_KEY = "category_request_key"
    }

    private lateinit var mToolbar: MaterialToolbar
    private lateinit var mEtValue: EditText
    private lateinit var mTvPayee: TextView
    private lateinit var mTvCategory: TextView
    private lateinit var mTvDate: TextView
    private lateinit var mEtDescription: EditText
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
        mEtDescription = findViewById(R.id.etDescription)
        mFabCreate = findViewById(R.id.fabCreate)
        mLlPayee = findViewById(R.id.layoutPayee);
        mLlCategory = findViewById(R.id.layoutCategory);
        mLlDate = findViewById(R.id.layoutDate);
        mLlDescription = findViewById(R.id.layoutDescription);

        // Toolbar listener
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

        // Setup date picker
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener {
            mTvDate.text = Utils.convertUtcToString(it)
        }

        // Set listeners for setting transaction values
        mLlPayee.setOnClickListener {
            mEtDescription.clearFocus()
            val selectPayeeFragment = SelectPayeeFragment()
            selectPayeeFragment.show(supportFragmentManager, "SelectPayeeFragment")
        }
        mLlCategory.setOnClickListener {
            mEtDescription.clearFocus()
            val selectCategoryFragment = SelectCategoryFragment()
            selectCategoryFragment.show(supportFragmentManager, "SelectCategoryFragment")
        }
        mLlDate.setOnClickListener {
            mEtDescription.clearFocus()
            datePicker.show(supportFragmentManager, "DatePicker")
        }

        // Set listeners for fragment results
        supportFragmentManager
            .setFragmentResultListener(PAYEE_REQUEST_KEY, this) { _, bundle ->
                val payee = bundle.getString(SelectPayeeFragment.PAYEE_KEY)
                mTvPayee.text = payee
            }
        supportFragmentManager
            .setFragmentResultListener(CATEGORY_REQUEST_KEY, this) { _, bundle ->
                val category = bundle.getString(SelectCategoryFragment.CATEGORY_KEY)
                mTvCategory.text = category
            }



    }

}