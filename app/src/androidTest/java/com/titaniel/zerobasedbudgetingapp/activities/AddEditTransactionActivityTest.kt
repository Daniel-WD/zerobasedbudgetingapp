package com.titaniel.zerobasedbudgetingapp.activities

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.activties.AddEditTransactionActivity
import com.titaniel.zerobasedbudgetingapp.datamanager.DataManager
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddEditTransactionActivityTest {

    @Test
    fun creates_add_edit_transaction_activity_correctly() {
        val dataManager = DataManager(
            Mockito.mock(Context::class.java),
            Mockito.mock(Lifecycle::class.java)
        )
        //PowerMockito.whenNew(DataManager::class.java).withAnyArguments().thenReturn(dataManager)

        launchActivity<AddEditTransactionActivity>().use { scenario ->
            onView(withId(R.id.delete)).perform(click())
            scenario.onActivity {
                assertThat(it.isFinishing).isTrue()
            }

        }
    }

}