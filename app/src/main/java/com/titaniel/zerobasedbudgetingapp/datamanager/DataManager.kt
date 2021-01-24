package com.titaniel.zerobasedbudgetingapp.datamanager

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class DataManager(private val context: Context, private val lifecycle: Lifecycle): LifecycleObserver {

    companion object {

        const val SHARED_PREFERENCES_KEY = "com.titaniel.zerobasedbudgetingapp.data"

    }

    val payees: MutableList<String> = mutableListOf()
    val transactions: MutableList<Transaction> = mutableListOf()
    val categories: MutableList<Category> = mutableListOf()

    init {
        lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun load() {
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

        payees.addAll(
            listOf(
                "Aldi",
                "Rossmann",
                "Lidl",
                "Autohaus",
                "Kaufland",
                "New Yorker",
                "Centrum Galerie",
                "Check24",
                "Amazon Ratenzahlung",
                "Samsung",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee",
                "Payee"
            )
        )

        categories.addAll(
            listOf(
                Category(emptyMap(), "Süßes"),
                Category(emptyMap(), "Lebensmittel"),
                Category(emptyMap(), "Autos"),
                Category(emptyMap(), "Persönlich"),
                Category(emptyMap(), "Sexspielzeug")
            )
        )

        transactions.addAll(
            listOf(
                Transaction(
                    5,
                    "Aldi",
                    "Lebensmittel",
                    "kdjf",
                    347583945
                ),
                Transaction(10, "Rossmann",  "Süßes", "", 23452345),
                Transaction(-34, "Lidl", "Lebensmittel", "", 7567364),
                Transaction(-235, "Autohaus", "Autos", "", 3464593)
            )
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun save() {
        val preferences = context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)

    }

}