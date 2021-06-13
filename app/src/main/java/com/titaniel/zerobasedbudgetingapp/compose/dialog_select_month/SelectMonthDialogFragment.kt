package com.titaniel.zerobasedbudgetingapp.compose.dialog_select_month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.titaniel.zerobasedbudgetingapp.compose.assets.*
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.SettingRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.util.*
import javax.inject.Inject

/**
 * [SelectMonthViewModel] for [SelectMonthDialogFragment].
 */
@HiltViewModel
class SelectMonthViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
    private val categoryRepository: CategoryRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    /**
     * Selectable months
     */
    private val _selectableMonths: MutableLiveData<List<YearMonth>> = MutableLiveData()
    val selectableMonths: LiveData<List<YearMonth>> = _selectableMonths

    init {

        viewModelScope.launch {

            // Calculate selectableMonths
            settingRepository.availableMonths.first().let { months ->

                // Add missing budgets for each month
                months.forEach(::addMissingBudgets)

                // Set selectable months
                _selectableMonths.value = months
            }
        }

    }

    /**
     * Adds budgets for those categories that have no budget in [month].
     */
    private fun addMissingBudgets(month: YearMonth) {

        viewModelScope.launch {

            val categories = categoryRepository.getAllCategories().first()
            val budgetsOfMonth = budgetRepository.getBudgetsByMonth(month).first()

            // Calc for which categories there are no budgets for month
            val missingBudgets =
                categories.filter { category -> budgetsOfMonth.find { budget -> budget.categoryId == category.id } == null }
                    .map { category -> Budget(category.id, month, 0) }.toTypedArray()

            // Add missing budgets
            budgetRepository.addBudgets(*missingBudgets)

        }

    }

    /**
     * Set new month that has [index] in [_selectableMonths].
     */
    fun onMonthClick(month: YearMonth) {

        // Set month
        viewModelScope.launch {
            settingRepository.setMonth(month)
        }
    }

}

/**
 * Bottom sheet dialog fragment for payee selection
 */
@AndroidEntryPoint
class SelectMonthDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            SelectMonthDialogScreen(onDismiss = ::dismiss)
        }
    }

}

@Composable
fun SelectMonthDialogScreen(viewModel: SelectMonthViewModel = viewModel(), onDismiss: () -> Unit) {
    val months by viewModel.selectableMonths.observeAsState(emptyList())
    MaterialTheme {

        SelectMonthDialog(months = months) { month ->
            viewModel.onMonthClick(month)
            onDismiss()
        }
    }
}

@Composable
fun SelectMonthDialog(months: List<YearMonth>, onItemClick: (YearMonth) -> Unit) {

    Column {
        Header()
        Divider(thickness = 1.dp, color = Divider12Color)
        List(months, onItemClick)
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
fun List(months: List<YearMonth>, onItemClick: (YearMonth) -> Unit) {
    LazyColumn(Modifier.testTag("List")) {
        items(months) { month ->
            ListItem(month = month, onItemClick)
        }
    }
}

@Composable
fun ListItem(month: YearMonth, onItemClick: (YearMonth) -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onItemClick(month) }
            .fillMaxWidth()
            .height(48.dp)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            month.month.name.lowercase().replaceFirstChar { it.uppercaseChar() },
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Text60Color
        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
        )
        Text(
            month.year.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Text40Color
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
        YearMonth.of(2021, 4),
    )

    SelectMonthDialog(months) {}
}