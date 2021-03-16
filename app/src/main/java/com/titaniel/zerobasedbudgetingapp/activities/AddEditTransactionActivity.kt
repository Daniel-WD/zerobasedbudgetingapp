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
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
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
import kotlinx.coroutines.flow.first
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
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val payeeRepository: PayeeRepository
) : ViewModel() {

    /**
     * All payees
     */
    val allPayees = payeeRepository.getAllPayees().asLiveData()

    /**
     * All categories
     */
    val allCategories = categoryRepository.getAllCategories().asLiveData()

    /**
     * [pay] of transaction
     */
    val pay = MutableLiveData(0L)

    /**
     * [payee] of transaction
     */
    val payee: MutableLiveData<Payee> = MutableLiveData()

    /**
     * [category] of transaction
     */
    val category: MutableLiveData<Category> = MutableLiveData()

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
     * Creates a new payee with [payeeName] and sets it, if [payeeName] is not blank and does'nt already exist.
     * Returns true if new payee could successfully be set, false otherwise.
     */
    fun setNewPayee(payeeName: String): Boolean {
        // Check payee name not blank and does'nt exist in existing payees
        if (payeeName.isBlank() || allPayees.value!!.find { it.name == payeeName } != null) {
            return false
        }

        // Create and set new payee
        payee.value = Payee(payeeName)

        return true
    }

    /**
     * Sets [payee] to payee with [payeeId]
     */
    fun setPayeeById(payeeId: Long) {

        viewModelScope.launch {
            // Get payee and set it
            payee.value = payeeRepository.getPayeeById(payeeId).first()
        }

    }

    /**
     * Sets [category] to payee with [categoryId]
     */
    fun setCategoryById(categoryId: Long) {

        // Check if it is TO_BE_BUDGETED
        if(categoryId == Category.TO_BE_BUDGETED.id) {
            // Set to be budgeted as category
            category.value = Category.TO_BE_BUDGETED
        } else {
            viewModelScope.launch {
                // Get category and set it
                category.value = categoryRepository.getCategoryById(categoryId).first()
            }
        }

    }

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
            editTransaction.value!!.payeeId = payee.value!!.id
            editTransaction.value!!.categoryId = category.value!!.id
            editTransaction.value!!.date = date.value!!
            editTransaction.value!!.description = description.value!!.trim()

            // Update transaction
            viewModelScope.launch {
                transactionRepository.updateTransactions(editTransaction.value!!)
            }

        } else { // Create new transaction

            viewModelScope.launch {
                // Add payee if new
                val payeeId = payeeRepository.addPayees(payee.value!!)[0]

                // Save new transaction
                transactionRepository.addTransactions(
                    Transaction(
                        pay.value!!,
                        payeeId,
                        category.value!!.id,
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
    fun isDataValid() = payee.value != null && category.value != null && date.value != null

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

            it?.let {
                // Change texts for edit mode
                updateUiToEditMode()

                // Set pay text (ViewModel value gets set when pay text changes)
                etPay.setText(it.pay.toString())

                // Set value text cursor to end
                etPay.setSelection(etPay.text.length)

                // Set description text (ViewModel value gets set when description text changes)
                etDescription.setText(it.description)

                // Set ViewModel values
                viewModel.setPayeeById(it.payeeId)
                viewModel.setCategoryById(it.categoryId)
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
        viewModel.payee.observe(this) {
            // Set payee text
            tvPayee.text = it.name

            updateCreateApplyEnabled()
        }

        // Set category observer
        viewModel.category.observe(this) {
            // Set category text
            tvCategory.text =
                if (it == Category.TO_BE_BUDGETED) getString(R.string.activity_add_edit_transaction_to_be_budgeted) else it.name

            updateCreateApplyEnabled()
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