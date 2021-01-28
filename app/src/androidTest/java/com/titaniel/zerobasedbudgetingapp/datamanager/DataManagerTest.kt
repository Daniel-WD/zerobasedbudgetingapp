package com.titaniel.zerobasedbudgetingapp.datamanager

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.titaniel.zerobasedbudgetingapp.activties.MainActivity
import com.titaniel.zerobasedbudgetingapp.test.BuildConfig
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
class DataManagerTests {

    private lateinit var dataManager: DataManager

    @Before
    fun setup() {
        val scenario = launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            dataManager = DataManager(activity, activity.lifecycle)
        }
        scenario.close()

    }

    @Test
    fun loadSaveDataTest() {

        val fakePayees =
            mutableListOf(
                "Aldi",
                "Rossmann",
                "Lidl",
                "Autohaus",
                "Kaufland",
                "New Yorker",
            )

        val fakeCategories = mutableListOf(
            Category(mapOf(1 to 2, 2 to 3), "Süßes"),
            Category(emptyMap(), "Lebensmittel"),
            Category(emptyMap(), "Autos"),
            Category(emptyMap(), "Persönlich"),
            Category(emptyMap(), "Sexspielzeuge")
        )

        val fakeTransactions = mutableListOf(
            Transaction(5, "Aldi", "Lebensmittel", "kdjf", 347583945),
            Transaction(10, "Rossmann", "Süßes", "", 23452345),
            Transaction(-34, "Lidl", "Lebensmittel", "", 7567364),
            Transaction(-235, "Autohaus", "Autos", "", 3464593)
        )

        dataManager.payees.clear()
        dataManager.payees.addAll(fakePayees)

        dataManager.categories.clear()
        dataManager.categories.addAll(fakeCategories)

        dataManager.transactions.clear()
        dataManager.transactions.addAll(fakeTransactions)

        dataManager.save()
        dataManager.load()

        // Assert
        assertEquals(dataManager.payees, fakePayees)
        assertEquals(dataManager.transactions, fakeTransactions)
        assertEquals(dataManager.categories, fakeCategories)
    }
}