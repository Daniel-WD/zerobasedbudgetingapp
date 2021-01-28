package com.titaniel.zerobasedbudgetingapp.activties

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager
import com.titaniel.zerobasedbudgetingapp.datamanager.Transaction
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.utils.Utils

/**
 * Activity to create or edit a transaction
 */
class AddEditTransactionActivity : AppCompatActivity() {

    companion object {
        /**
         * Payee request key
         */
        const val PAYEE_REQUEST_KEY = "payee_request_key"

        /**
         * Category request key
         */
        const val CATEGORY_REQUEST_KEY = "category_request_key"
    }

    /**
     * Toolbar
     */
    private lateinit var mToolbar: MaterialToolbar

    /**
     * Money value edittext
     */
    private lateinit var mEtValue: EditText

    /**
     * Payee textview
     */
    private lateinit var mTvPayee: TextView

    /**
     * Category textview
     */
    private lateinit var mTvCategory: TextView

    /**
     * Date textview
     */
    private lateinit var mTvDate: TextView

    /**
     * Description edittext
     */
    private lateinit var mEtDescription: EditText

    /**
     * Create/apply transaction button
     */
    private lateinit var mFabCreateApply: ExtendedFloatingActionButton

    /**
     * Payee layout
     */
    private lateinit var mLlPayee: LinearLayout

    /**
     * Category layout
     */
    private lateinit var mLlCategory: LinearLayout

    /**
     * Date layout
     */
    private lateinit var mLlDate: LinearLayout

    /**
     * Description layout
     */
    private lateinit var mLlDescription: ConstraintLayout

    /**
     * Data manager
     */
    private lateinit var mDataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_transaction)

        // View initialization
        mToolbar = findViewById(R.id.toolbar)
        mEtValue = findViewById(R.id.etValue)
        mTvPayee = findViewById(R.id.tvPayee)
        mTvCategory = findViewById(R.id.tvCategory)
        mTvDate = findViewById(R.id.tvDate)
        mEtDescription = findViewById(R.id.etDescription)
        mFabCreateApply = findViewById(R.id.fabCreateApply)
        mLlPayee = findViewById(R.id.layoutPayee)
        mLlCategory = findViewById(R.id.layoutCategory)
        mLlDate = findViewById(R.id.layoutDate)
        mLlDescription = findViewById(R.id.layoutDescription)

        // Toolbar menu listener
        mToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {
                    // Hide keyboard and close activity
                    hideSoftKeyboard()
                    finish()
                    true
                }
                else -> false
            }
        }

        // Init data manager
        mDataManager = DataManager(this, lifecycle)

        // Date picker setup
        // Create builder for date picker
        val builder = MaterialDatePicker.Builder.datePicker()

        // Set default date to today
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds())

        val datePicker = builder.build()

        // Date confirmation listener
        datePicker.addOnPositiveButtonClickListener { timestamp ->
            // Set date text
            mTvDate.text = Utils.convertUtcToString(timestamp)
            checkCreateEnabled()
        }

        // Value text clicked listener
        mEtValue.setOnClickListener {
            // Cursor position to end
            mEtValue.setSelection(mEtValue.text.length)
        }

        // Create-button listener
        mFabCreateApply.setOnClickListener {
            hideSoftKeyboard()

            val moneyValue = mEtValue.text.toString()
            val payee = mTvPayee.text.toString()

            // Add payee to data manager, if new
            if (!mDataManager.payees.contains(payee)) {
                mDataManager.payees.add(payee)
            }

            // Create transaction
            val transaction = Transaction(
                if (moneyValue.isBlank()) 0 else moneyValue.toLong(),
                mTvPayee.text.toString(),
                mTvCategory.text.toString(),
                mEtDescription.text.toString().trim(),
                datePicker.selection!!
            )

            // Save transaction
            mDataManager.transactions.add(transaction)

            // Close activity
            finish()
        }

        // Listeners for layout clicks
        // Open payee fragment on payee layout click
        mLlPayee.setOnClickListener {
            hideSoftKeyboard()
            mEtDescription.clearFocus()
            val selectPayeeFragment = SelectPayeeFragment()
            selectPayeeFragment.show(supportFragmentManager, "SelectPayeeFragment")
        }

        // Open category on category layout click
        mLlCategory.setOnClickListener {
            hideSoftKeyboard()
            mEtDescription.clearFocus()
            val selectCategoryFragment = SelectCategoryFragment()
            selectCategoryFragment.show(supportFragmentManager, "SelectCategoryFragment")
        }

        // Open date picker on date layout click
        mLlDate.setOnClickListener {
            hideSoftKeyboard()
            mEtDescription.clearFocus()
            datePicker.show(supportFragmentManager, "DatePicker")
        }

        // Fragment result listeners
        // Set payee text on payee fragment result
        supportFragmentManager
            .setFragmentResultListener(PAYEE_REQUEST_KEY, this) { _, bundle ->
                val payee = bundle.getString(SelectPayeeFragment.PAYEE_KEY)
                mTvPayee.text = payee
                checkCreateEnabled()
            }

        // Set category text on category fragment result
        supportFragmentManager
            .setFragmentResultListener(CATEGORY_REQUEST_KEY, this) { _, bundle ->
                val category = bundle.getString(SelectCategoryFragment.CATEGORY_KEY)
                mTvCategory.text = category
                checkCreateEnabled()
            }

        // Focus value text
        mEtValue.requestFocus()

        // Show keyboard
        showSoftKeyboard()
    }

    /**
     * Update if create/apply btn should be enabled
     */
    private fun checkCreateEnabled() {
        mFabCreateApply.isEnabled =
            mTvPayee.text.isNotBlank() && mTvCategory.text.isNotBlank() && mTvDate.text.isNotBlank()
    }

    /**
     * Show keyboard
     */
    private fun showSoftKeyboard() {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    /**
     * Hide keyboard
     */
    private fun hideSoftKeyboard() {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            findViewById<View>(android.R.id.content).windowToken,
            0
        )
    }

}