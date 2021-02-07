package com.titaniel.zerobasedbudgetingapp.datamanager

import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.titaniel.zerobasedbudgetingapp.activties.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for DataManager
 */
@RunWith(AndroidJUnit4::class)
class DataManagerTests {

    /**
     * DataManager to test
     */
    private lateinit var dataManager: DataManager

    @Before
    fun setup() {

        // Create activity scenario
        launchActivity<MainActivity>().use { scenario ->
            // Wait until acitivity is created
            scenario.onActivity { activity ->
                // Initialize data manager
                dataManager = DataManager(activity, activity.lifecycle)
            }
        }

    }

    @Test
    fun loaded_and_saved_data_should_be_identical() {

        // Fake data
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
            Category(mutableMapOf(), mutableMapOf(), "Süßes"),
            Category(mutableMapOf(), mutableMapOf(), "Lebensmittel"),
            Category(mutableMapOf(), mutableMapOf(), "Autos"),
            Category(mutableMapOf(), mutableMapOf(), "Persönlich"),
            Category(mutableMapOf(), mutableMapOf(), "Sexspielzeuge")
        )

        val fakeTransactions = mutableListOf(
            Transaction(5, "Aldi", "Lebensmittel", "kdjf", 347583945),
            Transaction(10, "Rossmann", "Süßes", "", 23452345),
            Transaction(-34, "Lidl", "Lebensmittel", "", 7567364),
            Transaction(-235, "Autohaus", "Autos", "", 3464593)
        )

        // Prepare data manager
        dataManager.payees.clear()
        dataManager.payees.addAll(fakePayees)

        dataManager.categories.clear()
        dataManager.categories.addAll(fakeCategories)

        dataManager.transactions.clear()
        dataManager.transactions.addAll(fakeTransactions)

        // Save and load
        dataManager.save()
        dataManager.load()

        // Check validity
        assertEquals(dataManager.payees, fakePayees)
        assertEquals(dataManager.transactions, fakeTransactions)
        assertEquals(dataManager.categories, fakeCategories)
    }
}