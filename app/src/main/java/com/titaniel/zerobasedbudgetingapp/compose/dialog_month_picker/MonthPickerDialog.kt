package com.titaniel.zerobasedbudgetingapp.compose.dialog_month_picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.titaniel.zerobasedbudgetingapp.compose.assets.*
import com.titaniel.zerobasedbudgetingapp.database.repositories.SettingRepository
import com.titaniel.zerobasedbudgetingapp.utils.monthName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

/**
 * [MonthPickerViewModel] for MonthPickerDialog.
 */
@HiltViewModel
class MonthPickerViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : ViewModel() {

    /**
     * Selected month
     */
    val selectedMonth = settingRepository.getMonth().asLiveData()

    /**
     * Selectable months
     */
    val selectableMonths: LiveData<List<YearMonth>> = settingRepository.availableMonths.asLiveData()

    /**
     * Sets [month] as globally selected month
     */
    fun onMonthClick(month: YearMonth) {

        // Set month
        viewModelScope.launch {
            settingRepository.setMonth(month)
        }
    }

}

@Composable
fun MonthPickerDialogWrapper(viewModel: MonthPickerViewModel = viewModel(), onDismiss: () -> Unit) {

    val month by viewModel.selectedMonth.observeAsState()
    val months by viewModel.selectableMonths.observeAsState(emptyList())

    MaterialTheme {
        MonthPickerDialog(
            months = months,
            selectedMonth = month ?: YearMonth.of(1999, 1)
        ) { month ->
            viewModel.onMonthClick(month)
            onDismiss()
        }
    }
}

@Composable
fun MonthPickerDialog(
    months: List<YearMonth>,
    selectedMonth: YearMonth,
    onItemClick: (YearMonth) -> Unit
) {

    Column {
        Header()
        Divider(thickness = 1.dp, color = Divider12Color)
        List(months, selectedMonth, onItemClick)
    }
}

@Composable
fun Header() {
    val typography = MaterialTheme.typography
    Column(
        modifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 11.dp,
            bottom = 13.dp
        )
    ) {
        Text(text = "Month", style = typography.h6, color = Text87Color)
        Text(
            text = "Select a month",
            modifier = Modifier.padding(top = 1.dp),
            style = typography.body2,
            color = Text60Color
        )
    }
}

@Composable
fun List(months: List<YearMonth>, selectedMonth: YearMonth, onItemClick: (YearMonth) -> Unit) {
    LazyColumn(Modifier.testTag("List")) {
        items(months) { month ->
            ListItem(month = month, month == selectedMonth, onItemClick)
        }
    }
}

@Composable
fun ListItem(month: YearMonth, selected: Boolean, onItemClick: (YearMonth) -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onItemClick(month) }
            .fillMaxWidth()
            .height(48.dp)
            .background(if (selected) PrimaryColor.copy(0.08f) else Color.Transparent)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = month.monthName(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) PrimaryColor else Text60Color
        )
        Text(
            text = " " + month.year.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) PrimaryColor else Text40Color
        )
    }
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun SelectMonthDialogPreview() {
    val months = listOf(
        YearMonth.of(2020, 10),
        YearMonth.of(2020, 11),
        YearMonth.of(2020, 12),
        YearMonth.of(2021, 1),
        YearMonth.of(2021, 2),
        YearMonth.of(2021, 3),
        YearMonth.of(2021, 4)
    )

    MonthPickerDialog(months, YearMonth.of(2021, 1)) {}
}