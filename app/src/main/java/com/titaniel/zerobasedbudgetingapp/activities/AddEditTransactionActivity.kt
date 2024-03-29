package com.titaniel.zerobasedbudgetingapp.activities

import android.os.Bundle
import android.widget.EditText
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
import com.blackcat.currencyedittext.CurrencyEditText
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.rm.rmswitch.RMSwitch
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.PayeeRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Payee
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Transaction
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_category.SelectCategoryFragment
import com.titaniel.zerobasedbudgetingapp.fragments.fragment_select_payee.SelectPayeeFragment
import com.titaniel.zerobasedbudgetingapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.absoluteValue


/**
 * [AddEditTransactionViewModel] for [AddEditTransactionActivity] with [savedStateHandle], [transactionRepository] and [payeeRepository]
 */
@HiltViewModel
class AddEditTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    categoryRepository: CategoryRepository,
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
     * Flag, if pay is negative or positive
     */
    var positive = false

    /**
     * [pay] of transaction, should always be positive.
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
     * Contains [editTransactionWithCategoryAndPayee]. Presence indicates that [editTransactionWithCategoryAndPayee] should be edited.
     */
    val editTransactionWithCategoryAndPayee =
        transactionRepository.getTransactionWithCategoryAndPayeeById(
            savedStateHandle[AddEditTransactionActivity.EDIT_TRANSACTION_ID_KEY] ?: -1
        ).asLiveData()

    /**
     * Creates a new payee with [payeeName] and sets it, if [payeeName] is not blank and does'nt already exist.
     * Returns true if [payeeName] meets the requirements, false otherwise.
     */
    fun setNewPayee(payeeName: String): Boolean {
        // Check payee name not blank and does'nt exist in existing payees
        if (payeeName.isBlank() || allPayees.value!!.find { it.name == payeeName } != null) {
            return false
        }

        // Create and set new payee
        payee.value = Payee(payeeName.trim())

        return true
    }

    /**
     * Deletes [editTransactionWithCategoryAndPayee]
     */
    fun deleteEditTransaction() {
        editTransactionWithCategoryAndPayee.value?.let {
            viewModelScope.launch {
                transactionRepository.deleteTransactions(it.transaction)
            }
        }
    }

    /**
     * When [editTransactionWithCategoryAndPayee] is present, it gets updated. Otherwise adds a new transaction to [transactionRepository]
     */
    fun applyData() {
        // Validate data
        require(isDataValid())

        // Get editTransaction
        val eTransWRest = editTransactionWithCategoryAndPayee.value

        // Real pay value
        val realPay = pay.value!!.let { if (positive) it else -it }

        // Check if should edit transaction
        if (eTransWRest != null) { // Edit transaction

            // Apply new values
            eTransWRest.transaction.pay = realPay
            eTransWRest.transaction.payeeId = payee.value!!.id
            eTransWRest.transaction.categoryId = category.value!!.id
            eTransWRest.transaction.date = date.value!!
            eTransWRest.transaction.description = description.value!!.trim()

            // Update transaction
            viewModelScope.launch {
                transactionRepository.updateTransactions(eTransWRest.transaction)
            }

        } else { // Create new transaction

            viewModelScope.launch {
                // Add payee if new
                val payeeId = payeeRepository.addPayees(payee.value!!)[0]

                // Save new transaction
                transactionRepository.addTransactions(
                    Transaction(
                        realPay,
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
     * CurrencyEditText for entering the pay value
     */
    private lateinit var etPay: CurrencyEditText

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
    private lateinit var lPayee: ConstraintLayout

    /**
     * Category layout
     */
    private lateinit var lCategory: ConstraintLayout

    /**
     * Date layout
     */
    private lateinit var lDate: ConstraintLayout

    /**
     * Description layout
     */
    private lateinit var lDescription: ConstraintLayout

    /**
     * Date picker
     */
    private lateinit var datePicker: MaterialDatePicker<Long>

    /**
     * Switch positive/negative pay
     */
    private lateinit var switchPosNeg: RMSwitch

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
        switchPosNeg = findViewById(R.id.switchPosNeg)

        // Transaction observer
        viewModel.editTransactionWithCategoryAndPayee.observe(this, {

            it?.let {
                // Change texts for edit mode
                updateUiToEditMode()

                // Set pos/neg in viewmodel
                viewModel.positive = it.transaction.pay > 0

                // Set pay text (ViewModel value gets set when pay text changes)
                etPay.setText(it.transaction.pay.absoluteValue.toString())

                // Set pos/neg switch
                switchPosNeg.isChecked = viewModel.positive

                // Set value text cursor to end
                etPay.cursorEnd()

                // Set description text (ViewModel value gets set when description text changes)
                etDescription.setText(it.transaction.description)

                // Set ViewModel values
                viewModel.payee.value = it.payee
                viewModel.category.value = it.resolvedCategory
                viewModel.date.value = it.transaction.date

                // Update save btn enabled
                updateCreateApplyEnabled()

            }

            // Date picker setup
            // Create builder for date picker
            val builder = MaterialDatePicker.Builder.datePicker()

            // If edit transaction exists, set its timestamp as selected date
            builder.setSelection(
                it?.transaction?.date?.let { date ->
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

        // Add positive/negative switch change listener
        switchPosNeg.addSwitchObserver { _, positive -> viewModel.positive = positive }

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
            tvPayee.text = it?.name ?: ""

            updateCreateApplyEnabled()
        }

        // Set category observer
        viewModel.category.observe(this) {
            // Set category text
            tvCategory.text = it?.let {
                if (it == Category.TO_BE_BUDGETED) getString(R.string.activity_add_edit_transaction_to_be_budgeted) else it.name
            } ?: ""

            updateCreateApplyEnabled()
        }

        // Set date observer
        viewModel.date.observe(this) {
            it?.let {
                tvDate.text = convertLocalDateToString(it)
            }
        }

        // Value text clicked listener
        etPay.setOnClickListener {
            etPay.cursorEnd()
        }

        // Value text changed listener
        etPay.addTextChangedListener {
            // Set transaction value, from text
            // Set transaction value, from text
            viewModel.pay.value = etPay.rawValue
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