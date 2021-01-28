package com.titaniel.zerobasedbudgetingapp.datamanager

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Helper class to load and save data to shared preferences
 * @param context Context
 * @param lifecycle Lifecycle of client
 */
class DataManager(private val context: Context, lifecycle: Lifecycle) : LifecycleObserver {

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
     * Payee list type token
     */
    private val payeesTypeToken = object : TypeToken<MutableList<String>>() {}.type

    /**
     * Transaction list type token
     */
    private val transactionsTypeToken = object : TypeToken<MutableList<Transaction>>() {}.type

    /**
     * Category list type token
     */
    private val categoriesTypeToken = object : TypeToken<MutableList<Category>>() {}.type

    init {
        // Hook to lifecycle events of client
        lifecycle.addObserver(this)
    }

    /**
     * Load data from shared preferences
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun load() {
        // Init gson parser
        val gson = Gson()

        // Get shared preferences
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

        // Get saved serielized data
        val serializedPayees = preferences.getString(PAYEES_KEY, "[]")
        val serializedTransactions = preferences.getString(TRANSACTIONS_KEY, "[]")
        val serializedCategories = preferences.getString(CATEGORIES_KEY, "[]")

        // Empty data containers
        payees.clear()
        transactions.clear()
        categories.clear()

        // Deserialize and fill data
        payees.addAll(gson.fromJson(serializedPayees, payeesTypeToken))
        transactions.addAll(gson.fromJson(serializedTransactions, transactionsTypeToken))
        categories.addAll(gson.fromJson(serializedCategories, categoriesTypeToken))

        // TODO remove, when changeble through user
        categories.clear()
        categories.addAll(
            mutableListOf(
                Category(mapOf(1 to 2, 2 to 3), "Süßes"),
                Category(emptyMap(), "Lebensmittel"),
                Category(emptyMap(), "Autos"),
                Category(emptyMap(), "Persönlich"),
                Category(emptyMap(), "Sexspielzeuge")
            )
        )

    }

    /**
     * Save data in shared preferences
     */
    @SuppressLint("ApplySharedPref")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun save() {
        // Init gson parser
        val gson = Gson()

        // Get shared preferences
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

        // Serialize data
        val serializedPayees = gson.toJson(payees)
        val serializedTransactions = gson.toJson(transactions)
        val serializedCategories = gson.toJson(categories)

        // Save serialized data
        preferences.edit()
            .putString(PAYEES_KEY, serializedPayees)
            .putString(TRANSACTIONS_KEY, serializedTransactions)
            .putString(CATEGORIES_KEY, serializedCategories)
            .commit()

        // Empty data containers
        payees.clear()
        transactions.clear()
        categories.clear()

    }

}