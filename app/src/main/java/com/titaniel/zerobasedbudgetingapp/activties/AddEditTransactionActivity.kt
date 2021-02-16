package com.titaniel.zerobasedbudgetingapp.activties

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.forceHideSoftKeyboard
import com.titaniel.zerobasedbudgetingapp.forceShowSoftKeyboard
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.database.repositories.PayeeRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val payeeRepository: PayeeRepository
) : ViewModel() {

    //TODO lazy?
    val pay = MutableLiveData(0L)
    val payee = MutableLiveData("")
    val category = MutableLiveData("")
    val description = MutableLiveData("")
    val date: MutableLiveData<LocalDate> = MutableLiveData()

    val editTransaction = transactionRepository.getTransactionById(
        savedStateHandle[AddEditTransactionActivity.EDIT_TRANSACTION_ID_KEY] ?: -1
    ).asLiveData()

    fun deleteEditTransaction() {
        editTransaction.value?.let {
            viewModelScope.launch {
                transactionRepository.deleteTransaction(it)
            }
        }
    }

    fun applyData() {
        // Check if should edit transaction
        if (editTransaction.value != null) { // Edit transaction

            // Apply new values
            editTransaction.value!!.pay = pay.value!!
            editTransaction.value!!.payeeName = payee.value!!
            editTransaction.value!!.categoryName = category.value!!
            editTransaction.value!!.date = date.value!!
            editTransaction.value!!.description = description.value!!

            // Update transaction
            viewModelScope.launch {
                transactionRepository.updateTransaction(editTransaction.value!!)
            }

        } else { // Create new transaction

            viewModelScope.launch {
                // Add payee if new
                payeeRepository.addPayee(Payee(payee.value!!))

                // Save new transaction
                transactionRepository.addTransaction(
                    Transaction(
                        pay.value!!,
                        payee.value!!,
                        category.value!!.trim(),
                        description.value!!,
                        date.value!!
                    )
                )
            }
        }
    }

}

/**
 * Activity to create or edit a transaction
 */
@AndroidEntryPoint
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
        const val EDIT_TRANSACTION_ID_KEY = "edit_transaction_key"

    }

    /**
     * View model
     */
    private val viewModel: AddEditTransactionViewModel by viewModels()

    /**
     * Toolbar
     */
    private lateinit var mToolbar: MaterialToolbar

    /**
     * Money value edittext
     */
    private lateinit var mEtPay: EditText

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
     * Date picker
     */
    private lateinit var mDatePicker: MaterialDatePicker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_transaction)

        // View initialization
        mToolbar = findViewById(R.id.toolbar)
        mEtPay = findViewById(R.id.etPay)
        mTvPayee = findViewById(R.id.tvPayee)
        mTvCategory = findViewById(R.id.tvCategory)
        mTvDate = findViewById(R.id.tvDate)
        mEtDescription = findViewById(R.id.etDescription)
        mFabCreateApply = findViewById(R.id.fabCreateApply)
        mLlPayee = findViewById(R.id.layoutPayee)
        mLlCategory = findViewById(R.id.layoutCategory)
        mLlDate = findViewById(R.id.layoutDate)
        mLlDescription = findViewById(R.id.layoutDescription)

        // Transaction observer
        viewModel.editTransaction.observe(this, {
            // Show transaction
            Log.d("sdfasdf", "onCreate: $it")

            it?.let {
                // Change texts for edit mode
                updateUiToEditMode()

                mEtPay.setText(it.pay.toString())

                // Set value text cursor to end
                mEtPay.setSelection(mEtPay.text.length)


                mEtDescription.setText(it.description)

                viewModel.payee.value = it.payeeName
                viewModel.category.value = it.categoryName
                viewModel.date.value = it.date

                // Set value text cursor to end
                mEtPay.setSelection(mEtPay.text.length)

                // Update save btn enabled
                checkCreateApplyEnabled()

            }

            // Date picker setup
            // Create builder for date picker
            val builder = MaterialDatePicker.Builder.datePicker()

            // If edit transaction exists, set its timestamp as selected date
            builder.setSelection(
                it?.date?.let { date ->
                    date.atStartOfDay(ZoneId.of("GMT"))!!.toInstant()!!.toEpochMilli() +1
                } ?: MaterialDatePicker.todayInUtcMilliseconds())

            // Builder date picker
            mDatePicker = builder.build()

            // Date confirmation listener
            mDatePicker.addOnPositiveButtonClickListener { timestamp ->

                // Set transaction timestamp
                viewModel.date.value =
                    Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("GMT")).toLocalDate()

                // Check save enabled
                checkCreateApplyEnabled()

            }
        })

        // Toolbar menu listener
        mToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete -> {

                    // Delete edit transaction if existing
                    viewModel.deleteEditTransaction()

                    // Hide keyboard and close activity
                    forceHideSoftKeyboard()
                    finish()
                    true
                }
                else -> false
            }
        }

        // Set payee observer
        viewModel.payee.observe(this) {
            mTvPayee.text = it
        }

        // Set model observer
        viewModel.category.observe(this) {
            mTvCategory.text =
                if (it == Category.TO_BE_BUDGETED) getString(R.string.activity_add_edit_transaction_to_be_budgeted) else it
        }

        // Set date observer
        viewModel.date.observe(this) {
            it?.let {
                mTvDate.text = Utils.convertLocalDateToString(it)
            }
        }

        // Value text clicked listener
        mEtPay.setOnClickListener {
            // Cursor position to end
            mEtPay.setSelection(mEtPay.text.length)
        }

        // Value text changed listener
        mEtPay.addTextChangedListener { value ->
            // Set transaction value, 0 when blank
            viewModel.pay.value =
                if (value.toString().isBlank() || value.toString() == "-") 0 else value.toString()
                    .toLong()
        }

        // Description text changed listener
        mEtDescription.addTextChangedListener { description ->
            // Set description text
            viewModel.description.value = description.toString()
        }

        // Create/Apply-button listener
        mFabCreateApply.setOnClickListener {
            // Hide keyboard
            forceHideSoftKeyboard()

            // Apply data
            viewModel.applyData()

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
            mDatePicker.show(supportFragmentManager, "DatePicker")
        }

        // Fragment result listeners
        // Set payee on payee fragment result
        supportFragmentManager
            .setFragmentResultListener(PAYEE_REQUEST_KEY, this) { _, bundle ->
                val payee = bundle.getString(SelectPayeeFragment.PAYEE_KEY)
                payee?.let { viewModel.payee.value = it }
                checkCreateApplyEnabled()
            }

        // Set category on category fragment result
        supportFragmentManager
            .setFragmentResultListener(CATEGORY_REQUEST_KEY, this) { _, bundle ->
                val category = bundle.getString(SelectCategoryFragment.CATEGORY_KEY)
                category?.let { viewModel.category.value = it }
                checkCreateApplyEnabled()
            }

        // Focus value text
        mEtPay.requestFocus()

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
     * Update if create/apply btn should be enabled
     */
    private fun checkCreateApplyEnabled() {
        mFabCreateApply.isEnabled =
            viewModel.payee.value?.isNotBlank() == true && viewModel.category.value?.isNotBlank() == true && viewModel.date.value != null
    }

}