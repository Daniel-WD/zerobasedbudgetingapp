package com.titaniel.zerobasedbudgetingapp.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.VisibleForTesting
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
import com.titaniel.zerobasedbudgetingapp.database.repositories.PayeeRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.utils.Utils
import com.titaniel.zerobasedbudgetingapp.utils.forceHideSoftKeyboard
import com.titaniel.zerobasedbudgetingapp.utils.forceShowSoftKeyboard
import com.titaniel.zerobasedbudgetingapp.utils.provideViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject


/**
 * [AddEditTransactionViewModel] for [AddEditTransactionActivity] with [savedStateHandle], [transactionRepository] and [payeeRepository]
 */
@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val transactionRepository: TransactionRepository,
    private val payeeRepository: PayeeRepository
) : ViewModel() {

    /**
     * [pay] of transaction
     */
    val pay = MutableLiveData(0L)

    /**
     * [payeeName] of transaction
     */
    val payeeName = MutableLiveData<String>()

    /**
     * [categoryName] of transaction
     */
    val categoryName = MutableLiveData("")

    /**
     * [description] of transaction
     */
    val description = MutableLiveData("")

    /**
     * [date] of transaction
     */
    val date: MutableLiveData<LocalDate> = MutableLiveData()

    /**
     * Contains [editTransaction]. Presence indicates that [editTransaction] should be edited
     */
    val editTransaction = transactionRepository.getTransactionById(
        savedStateHandle[AddEditTransactionActivity.EDIT_TRANSACTION_ID_KEY] ?: -1
    ).asLiveData()

    /**
     * Deletes [editTransaction]
     */
    fun deleteEditTransaction() {
        editTransaction.value?.let {
            viewModelScope.launch {
                transactionRepository.deleteTransactions(it)
            }
        }
    }

    /**
     * When [editTransaction] is present, it gets updated. Otherwise adds a new transaction to [transactionRepository]
     */
    fun applyData() {
        // Validate data
        require(isDataValid())

        // Check if should edit transaction
        if (editTransaction.value != null) { // Edit transaction

            // Apply new values
            editTransaction.value!!.pay = pay.value!!
            editTransaction.value!!.payeeName = payeeName.value!!
            editTransaction.value!!.categoryName = categoryName.value!!
            editTransaction.value!!.date = date.value!!
            editTransaction.value!!.description = description.value!!.trim()

            // Update transaction
            viewModelScope.launch {
                transactionRepository.updateTransactions(editTransaction.value!!)
            }

        } else { // Create new transaction

            viewModelScope.launch {
                // Add payee if new
                payeeRepository.addPayees(Payee(payeeName.value!!))

                // Save new transaction
                transactionRepository.addTransactions(
                    Transaction(
                        pay.value!!,
                        payeeName.value!!,
                        categoryName.value!!,
                        description.value!!.trim(),
                        date.value!!
                    )
                )
            }
        }
    }

    /**
     * Checks if essential data for a transaction is present
     */
    fun isDataValid() = payeeName.value!!.isNotBlank() && categoryName.value!!.isNotBlank() && date.value != null

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
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val viewModel: AddEditTransactionViewModel by provideViewModel()

    /**
     * Toolbar
     */
    private lateinit var toolbar: MaterialToolbar

    /**
     * Money value EditText
     */
    private lateinit var etPay: EditText

    /**
     * Payee TextView
     */
    private lateinit var tvPayee: TextView

    /**
     * Category TextView
     */
    private lateinit var tvCategory: TextView

    /**
     * Date TextView
     */
    private lateinit var tvDate: TextView

    /**
     * Description EditText
     */
    private lateinit var etDescription: EditText

    /**
     * Create/apply transaction button
     */
    private lateinit var fabCreateApply: ExtendedFloatingActionButton

    /**
     * Payee layout
     */
    private lateinit var lPayee: LinearLayout

    /**
     * Category layout
     */
    private lateinit var lCategory: LinearLayout

    /**
     * Date layout
     */
    private lateinit var lDate: LinearLayout

    /**
     * Description layout
     */
    private lateinit var lDescription: ConstraintLayout

    /**
     * Date picker
     */
    private lateinit var datePicker: MaterialDatePicker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_transaction)

        // View initialization
        toolbar = findViewById(R.id.toolbar)
        etPay = findViewById(R.id.etPay)
        tvPayee = findViewById(R.id.tvPayee)
        tvCategory = findViewById(R.id.tvCategory)
        tvDate = findViewById(R.id.tvDate)
        etDescription = findViewById(R.id.etDescription)
        fabCreateApply = findViewById(R.id.fabCreateApply)
        lPayee = findViewById(R.id.layoutPayee)
        lCategory = findViewById(R.id.layoutCategory)
        lDate = findViewById(R.id.layoutDate)
        lDescription = findViewById(R.id.layoutDescription)

        // Transaction observer
        viewModel.editTransaction.observe(this, {
            // Show transaction

            it?.let {
                // Change texts for edit mode
                updateUiToEditMode()

                // Set pay test
                etPay.setText(it.pay.toString())

                // Set value text cursor to end
                etPay.setSelection(etPay.text.length)

                // Set description text
                etDescription.setText(it.description)

                // Set ViewModel values
                viewModel.payeeName.value = it.payeeName
                viewModel.categoryName.value = it.categoryName
                viewModel.date.value = it.date

                // Update save btn enabled
                updateCreateApplyEnabled()

            }

            // Date picker setup
            // Create builder for date picker
            val builder = MaterialDatePicker.Builder.datePicker()

            // If edit transaction exists, set its timestamp as selected date
            builder.setSelection(
                it?.date?.let { date ->
                    date.atStartOfDay(ZoneId.of("GMT"))!!.toInstant()!!.toEpochMilli() + 1
                } ?: MaterialDatePicker.todayInUtcMilliseconds())

            // Builder date picker
            datePicker = builder.build()

            // Date confirmation listener
            datePicker.addOnPositiveButtonClickListener { timestamp ->

                // Set transaction timestamp
                viewModel.date.value =
                    Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("GMT")).toLocalDate()

                // Check save enabled
                updateCreateApplyEnabled()

            }
        })

        // Toolbar menu listener
        toolbar.setOnMenuItemClickListener { menuItem ->
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
        viewModel.payeeName.observe(this) {
            tvPayee.text = it
        }

        // Set model observer
        viewModel.categoryName.observe(this) {
            tvCategory.text =
                if (it == Category.TO_BE_BUDGETED) getString(R.string.activity_add_edit_transaction_to_be_budgeted) else it
        }

        // Set date observer
        viewModel.date.observe(this) {
            it?.let {
                tvDate.text = Utils.convertLocalDateToString(it)
            }
        }

        // Value text clicked listener
        etPay.setOnClickListener {
            // Cursor position to end
            etPay.setSelection(etPay.text.length)
        }

        // Value text changed listener
        etPay.addTextChangedListener { value ->
            // Set transaction value, 0 when blank
            viewModel.pay.value =
                if (value.toString().isBlank() || value.toString() == "-") 0 else value.toString()
                    .toLong()
        }

        // Description text changed listener
        etDescription.addTextChangedListener { description ->
            // Set description text
            viewModel.description.value = description.toString()
        }

        // Create/Apply-button listener
        fabCreateApply.setOnClickListener {
            // Hide keyboard
            forceHideSoftKeyboard()

            // Apply data
            viewModel.applyData()

            // Close activity
            finish()
        }

        // Listeners for layout clicks
        // Open payee fragment on payee layout click
        lPayee.setOnClickListener {
            forceHideSoftKeyboard()
            etDescription.clearFocus()
            val selectPayeeFragment = SelectPayeeFragment()
            selectPayeeFragment.show(supportFragmentManager, "SelectPayeeFragment")
        }

        // Open category on category layout click
        lCategory.setOnClickListener {
            forceHideSoftKeyboard()
            etDescription.clearFocus()
            val selectCategoryFragment = SelectCategoryFragment()
            selectCategoryFragment.show(supportFragmentManager, "SelectCategoryFragment")
        }

        // Open date picker on date layout click
        lDate.setOnClickListener {
            forceHideSoftKeyboard()
            etDescription.clearFocus()
            datePicker.show(supportFragmentManager, "DatePicker")
        }

        // Fragment result listeners
        // Set payee on payee fragment result
        supportFragmentManager
            .setFragmentResultListener(PAYEE_REQUEST_KEY, this) { _, bundle ->
                val payee = bundle.getString(SelectPayeeFragment.PAYEE_KEY)
                payee?.let { viewModel.payeeName.value = it }
                updateCreateApplyEnabled()
            }

        // Set category on category fragment result
        supportFragmentManager
            .setFragmentResultListener(CATEGORY_REQUEST_KEY, this) { _, bundle ->
                val category = bundle.getString(SelectCategoryFragment.CATEGORY_KEY)
                category?.let { viewModel.categoryName.value = it }
                updateCreateApplyEnabled()
            }

        // Focus value text
        etPay.requestFocus()

        // Show keyboard
        forceShowSoftKeyboard()
    }

    /**
     * Change texts to fit editing context
     */
    private fun updateUiToEditMode() {
        toolbar.title = getString(R.string.activity_add_edit_transaction_edit_transaction)
        fabCreateApply.text = getString(R.string.activity_add_edit_transaction_apply)
    }

    /**
     * Update if create/apply btn should be enabled
     */
    private fun updateCreateApplyEnabled() {
        fabCreateApply.isEnabled = viewModel.isDataValid()
    }

}