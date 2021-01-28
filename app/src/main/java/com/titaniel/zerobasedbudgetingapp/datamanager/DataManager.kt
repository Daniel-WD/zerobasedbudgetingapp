package com.titaniel.zerobasedbudgetingapp.datamanager

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DataManager(private val context: Context, lifecycle: Lifecycle) :
    LifecycleObserver {

    companion object {

        const val SHARED_PREFERENCES_KEY = "com.titaniel.zerobasedbudgetingapp.data"

        const val PAYEES_KEY = "com.titaniel.zerobasedbudgetingapp.payees"
        const val TRANSACTIONS_KEY = "com.titaniel.zerobasedbudgetingapp.transactions"
        const val CATEGORIES_KEY = "com.titaniel.zerobasedbudgetingapp.categories"

    }

    val payees: MutableList<String> = mutableListOf()
    val transactions: MutableList<Transaction> = mutableListOf()
    val categories: MutableList<Category> = mutableListOf()

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun load() {
        val gson = Gson()
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

        val serializedPayees = preferences.getString(PAYEES_KEY, "[]")
        val serializedTransactions = preferences.getString(TRANSACTIONS_KEY, "[]")
        val serializedCategories = preferences.getString(CATEGORIES_KEY, "[]")

        val payeesTypeToken = object : TypeToken<MutableList<String>>() {}.type
        val transactionsTypeToken = object : TypeToken<MutableList<Transaction>>() {}.type
        val categoriesTypeToken = object : TypeToken<MutableList<Category>>() {}.type

        payees.clear()
        transactions.clear()
        categories.clear()

        payees.addAll(gson.fromJson(serializedPayees, payeesTypeToken))
        transactions.addAll(gson.fromJson(serializedTransactions, transactionsTypeToken))
        categories.addAll(gson.fromJson(serializedCategories, categoriesTypeToken))

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

    @SuppressLint("ApplySharedPref")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun save() {
        val gson = Gson()
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

        val serializedPayees = gson.toJson(payees)
        val serializedTransactions = gson.toJson(transactions)
        val serializedCategories = gson.toJson(categories)

        preferences.edit()
            .putString(PAYEES_KEY, serializedPayees)
            .putString(TRANSACTIONS_KEY, serializedTransactions)
            .putString(CATEGORIES_KEY, serializedCategories)
            .commit()

        payees.clear()
        transactions.clear()
        categories.clear()

    }

}