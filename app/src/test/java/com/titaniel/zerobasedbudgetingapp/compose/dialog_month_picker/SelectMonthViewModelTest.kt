package com.titaniel.zerobasedbudgetingapp.compose.dialog_month_picker

import com.jraska.livedata.test
import com.titaniel.zerobasedbudgetingapp._testutils.CoroutinesAndLiveDataTest
import com.titaniel.zerobasedbudgetingapp.database.repositories.SettingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.time.YearMonth

@RunWith(MockitoJUnitRunner::class)
class SelectMonthViewModelTest : CoroutinesAndLiveDataTest() {

    /**
     * SettingRepository mock
     */
    @Mock
    private lateinit var settingRepositoryMock: SettingRepository

    /**
     * UpdateBudgetViewModel to test
     */
    private lateinit var viewModel: MonthPickerViewModel

    /**
     * Test availableMonths
     */
    private val availableMonths = listOf(
        YearMonth.of(2020, 4),
        YearMonth.of(2020, 5),
        YearMonth.of(2020, 6)
    )

    @ExperimentalCoroutinesApi
    @Before
    override fun setup() {
        super.setup()

        // Stub availableMonths
        `when`(settingRepositoryMock.availableMonths).thenReturn(flow { emit(availableMonths) })


        // Create ViewModel instance
        viewModel = MonthPickerViewModel(settingRepositoryMock)

        // Make sure viewModel.selectableMonths has a value
        viewModel.selectableMonths.test().awaitValue()

    }

    @Test
    fun performs_on_month_click_correctly(): Unit = runBlocking {

        // Set month
        viewModel.onMonthClick(availableMonths[2])

        // Check correct month has been set
        verify(settingRepositoryMock).setMonth(availableMonths[2])

    }

}