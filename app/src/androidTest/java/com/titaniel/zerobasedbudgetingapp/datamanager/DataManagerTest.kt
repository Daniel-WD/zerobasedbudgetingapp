package com.titaniel.zerobasedbudgetingapp.datamanager

import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.titaniel.zerobasedbudgetingapp.activties.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataManagerInstrumentedTest {

    /**
     * DataManager to test
     */
    private lateinit var mDataManager: DataManager

    @Before
    fun setup() {

        // Create activity scenario
        launchActivity<MainActivity>().use { scenario ->
            // Wait until acitivity is created
            scenario.onActivity { activity ->
                // Initialize data manager
                mDataManager = DataManager(activity, activity.lifecycle)
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
        mDataManager.payees.clear()
        mDataManager.payees.addAll(fakePayees)

        mDataManager.categories.clear()
        mDataManager.categories.addAll(fakeCategories)

        mDataManager.transactions.clear()
        mDataManager.transactions.addAll(fakeTransactions)

        // Save and load
        mDataManager.save()
        mDataManager.load()

        // Check validity
        assertEquals(mDataManager.payees, fakePayees)
        assertEquals(mDataManager.transactions, fakeTransactions)
        assertEquals(mDataManager.categories, fakeCategories)
    }
}