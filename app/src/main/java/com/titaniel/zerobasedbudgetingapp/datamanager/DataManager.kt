package com.titaniel.zerobasedbudgetingapp.datamanager

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * Helper class to load and save data to shared preferences
 * @param mContext Context
 * @param lifecycle Lifecycle of client
 * @param mLoadedCallback Callback, called after data has been laoded
 */
class DataManager(
    private val mContext: Context,
    lifecycle: Lifecycle
) : LifecycleObserver {

    companion object {

        /**
         * Key, shared preferences file
         */
        const val SHARED_PREFERENCES_KEY = "com.titaniel.zerobasedbudgetingapp.data"

        /**
         * Payee data key
         */
        const val PAYEES_KEY = "com.titaniel.zerobasedbudgetingapp.payees"

        /**
         * Transaction data key
         */
        const val TRANSACTIONS_KEY = "com.titaniel.zerobasedbudgetingapp.transactions"

        /**
         * Category data key
         */
        const val CATEGORIES_KEY = "com.titaniel.zerobasedbudgetingapp.categories"

        /**
         * To be budgeted value key
         */
        const val TO_BE_BUDGETED_KEY = "com.titaniel.zerobasedbudgetingapp.to_be_budgeted"

        /**
         * To be month value key
         */
        const val MONTH_KEY = "com.titaniel.zerobasedbudgetingapp.month"

        /**
         * State, where data is not loaded
         */
        const val STATE_NOT_LOADED = 0

        /**
         * State, where data is loaded
         */
        const val STATE_LOADED = 1

    }

    /**
     * Payees
     */
    val payees: MutableList<String> = mutableListOf()

    /**
     * Transactions
     */
    val transactions: MutableList<Transaction> = mutableListOf()

    /**
     * Categories
     */
    val categories: MutableList<Category> = mutableListOf()

    /**
     * To be budgeted value
     */
    var toBeBudgeted: Long = 0

    /**
     * UTC timestamp of first of selected month
     */
    val month: Long = 1612137600000 //February

    var loadedCallback: () -> Unit = {}

    /**
     * Payee list type token
     */
    private val mPayeesTypeToken = object : TypeToken<MutableList<String>>() {}.type

    /**
     * Transaction list type token
     */
    private val mTransactionsTypeToken = object : TypeToken<MutableList<Transaction>>() {}.type

    /**
     * Category list type token
     */
    private val mCategoriesTypeToken = object : TypeToken<MutableList<Category>>() {}.type

    /**
     * State
     */
    var state = STATE_NOT_LOADED
        set(value) {
            if (value == STATE_NOT_LOADED) {
                // Empty data
                payees.clear()
                transactions.clear()
                categories.clear()
                toBeBudgeted = 0
            }
            field = value
        }

    init {
        // Hook to lifecycle events of client
        lifecycle.addObserver(this)

    }

    /**
     * Load data from shared preferences
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun load() {
        // Return if already loaded
        if(state == STATE_LOADED) {
            return
        }

        // Init gson parser
        val gson = Gson()

        // Get shared preferences
        val preferences =
            mContext.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

        // Get saved serielized data
        val serializedPayees = preferences.getString(PAYEES_KEY, "[]")
        val serializedTransactions = preferences.getString(TRANSACTIONS_KEY, "[]")
        val serializedCategories = preferences.getString(CATEGORIES_KEY, "[]")

        // Get saved primitive data
        toBeBudgeted = preferences.getLong(TO_BE_BUDGETED_KEY, 0)
        // FIXME month = preferences.getLong(MONTH_KEY, 0)

        // Empty data containers
        payees.clear()
        transactions.clear()
        categories.clear()

        // Deserialize and fill data
        payees.addAll(gson.fromJson(serializedPayees, mPayeesTypeToken))
        transactions.addAll(gson.fromJson(serializedTransactions, mTransactionsTypeToken))
        categories.addAll(gson.fromJson(serializedCategories, mCategoriesTypeToken))

        // TODO remove, when changeble through user
        /*categories.clear()
        categories.addAll(
            mutableListOf(
                Category(mutableMapOf(), mutableMapOf(), "Süßes"),
                Category(mutableMapOf(), mutableMapOf(), "Lebensmittel"),
                Category(mutableMapOf(), mutableMapOf(), "Autos"),
                Category(mutableMapOf(), mutableMapOf(), "Persönlich"),
                Category(mutableMapOf(), mutableMapOf(), "Sexspielzeuge")
            )
        )*/

        // Set state
        state = STATE_LOADED

        // Data loaded, notify callback
        loadedCallback()

    }

    /**
     * Save data in shared preferences
     */
    @SuppressLint("ApplySharedPref")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun save() {
        // Return if already unloaded
        if(state == STATE_NOT_LOADED) {
            return
        }

        // Init gson parser
        val gson = Gson()

        // Get shared preferences
        val preferences =
            mContext.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

        // Serialize data
        val serializedPayees = gson.toJson(payees)
        val serializedTransactions = gson.toJson(transactions)
        val serializedCategories = gson.toJson(categories)

        // Save serialized data and primitive data
        preferences.edit()
            .putString(PAYEES_KEY, serializedPayees)
            .putString(TRANSACTIONS_KEY, serializedTransactions)
            .putString(CATEGORIES_KEY, serializedCategories)
            .putLong(TO_BE_BUDGETED_KEY, toBeBudgeted)
            .putLong(MONTH_KEY, month)
            .commit()

        // Set state
        state = STATE_NOT_LOADED

    }

    /**
     * Updates transaction sums in category, or to be budgeted value
     * @param transaction Transaction to update its category with
     * @param remove If transaction should be removed
     */
    fun updateCategoryTransactionSums(
        transaction: Transaction,
        remove: Boolean = false
    ) {
        if (state == STATE_NOT_LOADED) {
            throw IllegalStateException()
        }
        // Find month timestamp
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = transaction.utcTimestamp
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // Set month timestamp
        val monthTimestmap = calendar.timeInMillis

        // Value operation
        val value = if (remove) -transaction.value else transaction.value

        // If category is TO_BE_BUDGETED, change to be budgeted value
        if (transaction.category == Category.TO_BE_BUDGETED) {
            toBeBudgeted = toBeBudgeted.plus(value)
            return
        }

        // Find transaction category
        val category =
            categories.find { category -> category.name == transaction.category }

        // Update transaction sums for month. Create key if necessary.
        category!!.transactionSums[monthTimestmap] = category.transactionSums[monthTimestmap]?.plus(
            value
        ) ?: value

    }

}