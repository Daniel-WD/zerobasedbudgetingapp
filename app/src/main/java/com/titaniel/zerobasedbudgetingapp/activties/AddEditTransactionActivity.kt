package com.titaniel.zerobasedbudgetingapp.activties

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.datamanager.Category
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager
import com.titaniel.zerobasedbudgetingapp.datamanager.Transaction
import com.titaniel.zerobasedbudgetingapp.forceHideSoftKeyboard
import com.titaniel.zerobasedbudgetingapp.forceShowSoftKeyboard
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
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

        /**
         * Edit transaction key
         */
        const val EDIT_TRANSACTION_UUID_KEY = "edit_transaction_key"
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

    /**
     * Date picker
     */
    private lateinit var datePicker: MaterialDatePicker<Long>

    /**
     * Transaction
     */
    private lateinit var mTransaction: Transaction

    /**
     * Old transaction, transaction that should be edited
     */
    private lateinit var mOldTransaction: Transaction

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

                    // If transaction exists
                    if (mDataManager.transactions.contains(mTransaction)) {
                        // Delete transaction
                        mDataManager.transactions.remove(mTransaction)

                        // Remove transaction value from category
                        mDataManager.updateCategoryTransactionSums(mTransaction, true)
                    }

                    // Hide keyboard and close activity
                    forceHideSoftKeyboard()
                    finish()
                    true
                }
                else -> false
            }
        }

        // Init data manager
        mDataManager = DataManager(this, lifecycle) {
            // Find transaction to edit, if existing
            val transactionUuid = intent.extras?.get(EDIT_TRANSACTION_UUID_KEY)
            val editTransaction =
                mDataManager.transactions.find { transaction -> transaction.uuid == transactionUuid }

            if (editTransaction != null) { // Edit existing transaction
                // Set editTransaction as transaction data
                mTransaction = editTransaction

                // Save editTransaction data as old transaction
                mOldTransaction = editTransaction.copy()

                // Change texts for edit mode
                updateUiToEditMode()

                // Update ui
                updateUi()

                // Set value text cursor to end
                mEtValue.setSelection(mEtValue.text.length)

                // Update save btn enabled
                checkCreateApplyEnabled()
            } else { // Create new transaction
                mTransaction = Transaction(0, "", "", "", -1)
            }

            // Date picker setup
            // Create builder for date picker
            val builder = MaterialDatePicker.Builder.datePicker()

            // Set default date to transaction date(when editing transaction), today otherwise
            builder.setSelection(
                if (mTransaction.utcTimestamp != -1L) mTransaction.utcTimestamp else MaterialDatePicker.todayInUtcMilliseconds()
            )

            // Builder date picker
            datePicker = builder.build()

            // Date confirmation listener
            datePicker.addOnPositiveButtonClickListener { timestamp ->

                // Set transaction timestamp
                mTransaction.utcTimestamp = timestamp

                // Update ui
                updateUi()

                // Check save enabled
                checkCreateApplyEnabled()

            }
        }

        // Value text clicked listener
        mEtValue.setOnClickListener {
            // Cursor position to end
            mEtValue.setSelection(mEtValue.text.length)
        }

        // Value text changed listener
        mEtValue.addTextChangedListener { value ->
            // Set transaction value, 0 when blank
            mTransaction.value =
                if (value.toString().isBlank() || value.toString() == "-") 0 else value.toString()
                    .toLong()
        }

        // Description text changed listener
        mEtDescription.addTextChangedListener { description ->
            // Set description text
            mTransaction.description = description.toString().trim()
        }

        // Create/Apply-button listener
        mFabCreateApply.setOnClickListener {
            // Hide keyboard
            forceHideSoftKeyboard()

            // Payee
            val payee = mTvPayee.text.toString()

            // Add payee to data manager, if new
            if (!mDataManager.payees.contains(payee)) {
                mDataManager.payees.add(payee)
            }

            // Check if transaction already exists
            if (mDataManager.transactions.find { transaction -> transaction.uuid == mTransaction.uuid } == null) {
                // New transaction, save, update category
                mDataManager.transactions.add(mTransaction)
                mDataManager.updateCategoryTransactionSums(mTransaction)
            } else {
                // Edited transaction, remove old transaction value from category, add new one
                mDataManager.updateCategoryTransactionSums(mOldTransaction, true)
                mDataManager.updateCategoryTransactionSums(mTransaction)
            }

            // Close activity
            finish()
        }

        // Listeners for layout clicks
        // Open payee fragment on payee layout click
        mLlPayee.setOnClickListener {
            forceHideSoftKeyboard()
            mEtDescription.clearFocus()
            val selectPayeeFragment = SelectPayeeFragment()
            selectPayeeFragment.show(supportFragmentManager, "SelectPayeeFragment")
        }

        // Open category on category layout click
        mLlCategory.setOnClickListener {
            forceHideSoftKeyboard()
            mEtDescription.clearFocus()
            val selectCategoryFragment = SelectCategoryFragment()
            selectCategoryFragment.show(supportFragmentManager, "SelectCategoryFragment")
        }

        // Open date picker on date layout click
        mLlDate.setOnClickListener {
            forceHideSoftKeyboard()
            mEtDescription.clearFocus()
            datePicker.show(supportFragmentManager, "DatePicker")
        }

        // Fragment result listeners
        // Set payee on payee fragment result
        supportFragmentManager
            .setFragmentResultListener(PAYEE_REQUEST_KEY, this) { _, bundle ->
                val payee = bundle.getString(SelectPayeeFragment.PAYEE_KEY)
                mTransaction.payee = payee ?: mTransaction.payee
                updateUi()
                checkCreateApplyEnabled()
            }

        // Set category on category fragment result
        supportFragmentManager
            .setFragmentResultListener(CATEGORY_REQUEST_KEY, this) { _, bundle ->
                val category = bundle.getString(SelectCategoryFragment.CATEGORY_KEY)
                mTransaction.category = category ?: mTransaction.category
                updateUi()
                checkCreateApplyEnabled()
            }

        // Focus value text
        mEtValue.requestFocus()

        // Show keyboard
        forceShowSoftKeyboard()
    }

    /**
     * Change texts to fit editing context
     */
    private fun updateUiToEditMode() {
        mToolbar.title = getString(R.string.activity_add_edit_transaction_edit_transaction)
        mFabCreateApply.text = getString(R.string.activity_add_edit_transaction_apply)
    }

    /**
     * Update ui from transaction
     */
    private fun updateUi() {
        mEtValue.setText(mTransaction.value.toString())
        mTvPayee.text = mTransaction.payee
        mTvCategory.text =
            if (mTransaction.category == Category.TO_BE_BUDGETED) getString(R.string.activity_add_edit_transaction_to_be_budgeted) else mTransaction.category
        mTvDate.text =
            if (mTransaction.utcTimestamp != -1L) Utils.convertUtcToString(mTransaction.utcTimestamp) else ""
        mEtDescription.setText(mTransaction.description)
    }

    /**
     * Update if create/apply btn should be enabled
     */
    private fun checkCreateApplyEnabled() {
        mFabCreateApply.isEnabled =
            mTransaction.payee.isNotBlank() && mTransaction.category.isNotBlank() && mTvDate.text.isNotBlank()
    }

}