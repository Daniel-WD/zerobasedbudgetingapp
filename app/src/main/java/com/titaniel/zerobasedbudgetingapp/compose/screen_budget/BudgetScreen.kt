package com.titaniel.zerobasedbudgetingapp.compose.screen_budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.compose.assets.*
import com.titaniel.zerobasedbudgetingapp.compose.dialog_month_picker.MonthPickerDialogWrapper
import com.titaniel.zerobasedbudgetingapp.database.repositories.*
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Budget
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject


/**
 * [BudgetViewModel] for BudgetScreen.
 */
@HiltViewModel
class BudgetViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    budgetRepository: BudgetRepository,
    settingRepository: SettingRepository,
    groupRepository: GroupRepository
) : ViewModel() {

    /**
     * Month
     */
    val month = settingRepository.getMonth().asLiveData()

    /**
     * All transactions until this month, but not later than LocalDate.now()
     */
    private val relevantTransactions =
        Transformations.switchMap(month) { month ->
            requireNotNull(month)

            transactionRepository.getTransactionsUntilDate(
                // Get transactions until this month, but not later than today
                if (LocalDate.now()
                        .isBefore(month.asLocalDate())
                ) LocalDate.now() else month.asLocalDate()
            ).asLiveData()
        }

    /**
     * To be budgeted value
     */
    val toBeBudgeted = Transformations.map(relevantTransactions) { relTrans ->
        // Calc 'to be budgeted' value
        relTrans
            // Find all 'to be budgeted' transactions
            .filter { transaction -> transaction.categoryId == Category.TO_BE_BUDGETED.id }
            // Sum transaction pay up
            .fold(0L) { acc, transaction -> acc.plus(transaction.pay) }
    }

    /**
     * All budgets until this month
     */
    private val relevantBudgets = Transformations.switchMap(month) { month ->
        requireNotNull(month)

        budgetRepository.getBudgetsUntilMonth(month).asLiveData()
    }

    /**
     * All categories
     */
    private val categories = categoryRepository.getAllCategories().asLiveData()

    /**
     * Groups
     */
    private val groups =
        groupRepository.getAllGroups().map { groups -> groups.sortedBy { group -> group.position } }
            .asLiveData()

    /**
     * BudgetsWithCategory of current month
     */
    private val budgetsWithCategoryOfMonth = Transformations.switchMap(month) { month ->
        requireNotNull(month)

        budgetRepository.getBudgetsWithCategoryByMonth(month).asLiveData()
    }

    /**
     * Contains all budget of this month, when every category has a budget for this month. Empty otherwise.
     */
    private val budgetsToShow = Transformations.map(
        TripleLiveData(
            budgetsWithCategoryOfMonth,
            categories,
            month
        )
    ) { (budsWithCatOfMonth, cats, month) ->

        if (budsWithCatOfMonth == null || cats == null || month == null) {
            return@map budsWithCatOfMonth
        }

        // Calc budgets that need to be added
        val missingBudgets =

            // Filter categories that are not represented as a budget in this month
            cats.filter { cat ->
                budsWithCatOfMonth
                    // Find a budget for cat in this month
                    .find { budgetWithCategory -> budgetWithCategory.category == cat } == null
            }
                // Create budgets for categories that have no budget representation
                .map { cat -> Budget(cat.id, month, 0) }

        // Add missing budget to repository if there are any (this will transformation will be called again in that case)
        if (missingBudgets.isNotEmpty()) {
            viewModelScope.launch {
                budgetRepository.addBudgets(*missingBudgets.toTypedArray())
            }
        }

        budsWithCatOfMonth

    }

    /**
     * List of GroupData to represent in the ui
     */
    val groupList = Transformations.map(
        QuadrupleLiveData(
            budgetsToShow,
            groups,
            relevantTransactions,
            relevantBudgets
        )
    ) { (budsToShow, groups, relTransactions, relBudgets) ->

        if (budsToShow == null || groups == null || relTransactions == null || relBudgets == null) {
            return@map emptyList()
        }

        // Create GroupDate for each group
        groups.map { group ->
            GroupData(
                groupName = group.name,
                items = budsToShow

                    // Find BudgetsWithCategory of this group
                    .filter { it.category.groupId == group.id }

                    // Sort by positionInGroup
                    .sortedBy { it.category.positionInGroup }

                    // Create CategoryItemData for each BudgetsWithCategory
                    .map { budgetWithCategory ->
                        CategoryItemData(
                            categoryName = budgetWithCategory.category.name,
                            budgetedAmount = budgetWithCategory.budget.budgeted,
                            availableAmount = relTransactions
                                .filter { transaction -> transaction.categoryId == budgetWithCategory.category.id }
                                .fold(0L) { acc, transaction -> acc + transaction.pay }
                                .plus(
                                    relBudgets
                                        .filter { budget -> budget.categoryId == budgetWithCategory.category.id }
                                        .fold(0L) { acc, budget -> acc + budget.budgeted }
                                )

                        )
                    }
            )
        }
    }

}

/**
 * Data class to represent a budget row
 */
data class CategoryItemData(
    val categoryName: String,
    val budgetedAmount: Long,
    val availableAmount: Long
)

/**
 * Data class to represent a group with its budgets
 */
data class GroupData(val groupName: String, val items: List<CategoryItemData>)

@ExperimentalMaterialApi
@Composable
fun BudgetScreenWrapper(viewModel: BudgetViewModel = viewModel()) {

    val month by viewModel.month.observeAsState()
    val toBeBudgetedAmount by viewModel.toBeBudgeted.observeAsState()
    val groups by viewModel.groupList.observeAsState()

    BudgetScreen(
        month = month ?: YearMonth.now(),
        toBeBudgetedAmount = toBeBudgetedAmount ?: 0,
        groups = groups ?: emptyList()
    )

}

@ExperimentalMaterialApi
@Composable
fun BudgetScreen(month: YearMonth, toBeBudgetedAmount: Long, groups: List<GroupData>) {
    val monthPickerState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    MaterialTheme {
        ModalBottomSheetLayout(sheetState = monthPickerState, sheetContent = {
            Surface(
                modifier = Modifier.testTag("MonthPickerDialog"),
                color = BottomSheetBackgroundColor
            ) {
                MonthPickerDialogWrapper { scope.launch { monthPickerState.hide() } }
            }
//            Surface(
//                modifier = Modifier
//                    .height(100.dp)
//                    .testTag("MonthPickerDialog"),
//                color = BottomSheetBackgroundColor
//            ) {}
        }) {
            Scaffold(
                topBar = {
                    Toolbar(
                        selectedMonth = month,
                        toBeBudgetedAmount = toBeBudgetedAmount,
                        scope = scope,
                        monthPickerState = monthPickerState
                    )
                },
                bottomBar = { BottomBar() },
                backgroundColor = BackgroundColor,
                floatingActionButton = { Fab() }
            ) {
                GroupList(groups = groups)
            }
        }
    }
}

@Composable
fun Fab() {
    FloatingActionButton(
        onClick = { },
        backgroundColor = PrimaryColor,
        contentColor = Color.Black
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_outline_post_add_24),
            contentDescription = null
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun Toolbar(
    selectedMonth: YearMonth,
    toBeBudgetedAmount: Long,
    scope: CoroutineScope,
    monthPickerState: ModalBottomSheetState
) {
    Surface(elevation = 4.dp, color = SolidToolbarColor) {
        Column {
            TopAppBar(
                modifier = Modifier,
                title = { Text(text = "${selectedMonth.monthName()} ${selectedMonth.year}") },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.testTag("MonthButton"),
                        onClick = { scope.launch { monthPickerState.show() } }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_menu_24),
                            contentDescription = null,
                            tint = NormalIconColor
                        )
                    }
                },
                actions = {
                    val menuExpanded = remember {
                        mutableStateOf(false)
                    }

                    IconButton(
                        modifier = Modifier.testTag("MenuButton"),
                        onClick = { menuExpanded.value = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_more_vert_24),
                            contentDescription = null,
                            tint = NormalIconColor
                        )
                    }
                    DropdownMenu(
                        modifier = Modifier
                            .background(color = Color.Black)
                            .testTag("DropdownMenu"),
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false }
                    ) {
                        DropdownMenuItem(onClick = {}) {
                            Text(stringResource(R.string.clear_all_budgets), color = Text87Color)
                        }
                        DropdownMenuItem(onClick = {}) {
                            Text(stringResource(R.string.manage_categories), color = Text87Color)
                        }
                        DropdownMenuItem(onClick = {}) {
                            Text(stringResource(R.string.manage_payees), color = Text87Color)
                        }
                        DropdownMenuItem(onClick = {}) {
                            Text(stringResource(R.string.settings), color = Text87Color)
                        }
                    }
                },
                contentColor = Color.White,
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .offset(y = (-4).dp)
            ) {
                Text(
                    text = toBeBudgetedAmount.moneyFormat(),
                    color = when {
                        toBeBudgetedAmount > 0 -> TextGreenColor
                        toBeBudgetedAmount < 0 -> TextRedColor
                        else -> Text87Color
                    },
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = " " + stringResource(id = R.string.available),
                    color = Text87Color,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Header()
        }
    }
}

@Composable
fun Header() {
    Row(Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)) {
        Text(
            modifier = Modifier.weight(2f),
            text = stringResource(id = R.string.fragment_budget_category),
            color = Text50Color
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.fragment_budget_budgeted),
            textAlign = TextAlign.End,
            color = Text50Color
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.fragment_budget_available),
            textAlign = TextAlign.End,
            color = Text50Color
        )
    }
}

@Composable
fun BottomBar() {
    BottomNavigation(backgroundColor = SolidBottomNavigationColor) {
        BottomNavigationItem(
            selected = true,
            onClick = { },
            icon = {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_outline_payments_24
                    ), contentDescription = null
                )
            },
            label = {
                Text(text = stringResource(R.string.budgets))
            },
            selectedContentColor = PrimaryColor,
            unselectedContentColor = Text60Color
        )
        BottomNavigationItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_baseline_list_24
                    ), contentDescription = null
                )
            },
            label = {
                Text(text = stringResource(R.string.transactions))
            },
            selectedContentColor = PrimaryColor,
            unselectedContentColor = Text60Color
        )
    }
}

@Composable
fun GroupList(groups: List<GroupData>) {
    LazyColumn(
        modifier = Modifier.testTag("GroupList"),
        contentPadding = PaddingValues(bottom = (2.5 * 55).dp)
    ) {
        items(groups) { groupData ->
            Group(data = groupData)
        }
    }
}

@Composable
fun Group(data: GroupData) {

    val budgetedSum =
        data.items.fold(0L, { acc, categoryItemData -> acc + categoryItemData.budgetedAmount })
    val availableSum =
        data.items.fold(0L, { acc, categoryItemData -> acc + categoryItemData.availableAmount })

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(59.dp)
                .padding(top = 30.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                modifier = Modifier
                    .weight(2f)
                    .padding(end = 16.dp),
                text = data.groupName,
                color = Text50Color,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Text(
                modifier = Modifier.weight(1f),
                text = budgetedSum.moneyFormat(),
                color = Text50Color,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                text = availableSum.moneyFormat(),
                color = Text50Color,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Divider(Modifier.height(1.dp), color = Divider12Color)
        Column {
            data.items.forEach { categoryItemData ->
                CategoryItem(data = categoryItemData)
            }
        }
    }

}

@Composable
fun CategoryItem(data: CategoryItemData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .height(54.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(2f)
                    .padding(end = 16.dp),
                text = data.categoryName,
                color = Text87Color,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Text(
                modifier = Modifier.weight(1f),
                text = data.budgetedAmount.moneyFormat(),
                color = Text87Color,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                text = data.availableAmount.moneyFormat(),
                color = when {
                    data.availableAmount > 0 -> TextGreenColor
                    data.availableAmount < 0 -> TextRedColor
                    else -> Text87Color
                },
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Divider(Modifier.height(1.dp), color = Divider12Color)
    }
}

@ExperimentalMaterialApi
@Preview
/*(widthDp = 360, heightDp = 640)*/
@Composable
fun BudgetScreenPreview() {

    BudgetScreen(
        month = YearMonth.now(),
        toBeBudgetedAmount = 100000,
        groups = listOf(
            GroupData(
                "Persönlich", listOf(
                    CategoryItemData(
                        categoryName = "Lebensmittel",
                        budgetedAmount = 1000000,
                        availableAmount = 1200
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0
                    ),
                    CategoryItemData(
                        categoryName = "Bücher",
                        budgetedAmount = 10000,
                        availableAmount = -1412
                    )
                )
            ),
            GroupData(
                "Haushalt", listOf(
                    CategoryItemData(
                        categoryName = "Lebensmittel",
                        budgetedAmount = 1000000,
                        availableAmount = 1200
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0
                    ),
                    CategoryItemData(
                        categoryName = "Bücher",
                        budgetedAmount = 10000,
                        availableAmount = -1412
                    )
                )
            ),
            GroupData(
                "Haushalt", listOf(
                    CategoryItemData(
                        categoryName = "Lebensmittel",
                        budgetedAmount = 1000000,
                        availableAmount = 1200
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0
                    ),
                    CategoryItemData(
                        categoryName = "Bücher",
                        budgetedAmount = 10000,
                        availableAmount = -1412
                    )
                )
            ),
            GroupData(
                "Haushalt", listOf(
                    CategoryItemData(
                        categoryName = "Lebensmittel",
                        budgetedAmount = 1000000,
                        availableAmount = 1200
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0
                    ),
                    CategoryItemData(
                        categoryName = "Bücher",
                        budgetedAmount = 10000,
                        availableAmount = -1412
                    )
                )
            ),
            GroupData(
                "Haushalt", listOf(
                    CategoryItemData(
                        categoryName = "Lebensmittel",
                        budgetedAmount = 1000000,
                        availableAmount = 1200
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0
                    ),
                    CategoryItemData(
                        categoryName = "Bücher",
                        budgetedAmount = 10000,
                        availableAmount = -1412
                    )
                )
            ),
            GroupData(
                "Haushalt", listOf(
                    CategoryItemData(
                        categoryName = "Lebensmittel",
                        budgetedAmount = 1000000,
                        availableAmount = 1200
                    ),
                    CategoryItemData(
                        categoryName = "Investment",
                        budgetedAmount = 0,
                        availableAmount = 0
                    ),
                    CategoryItemData(
                        categoryName = "Bücher",
                        budgetedAmount = 10000,
                        availableAmount = -1412
                    )
                )
            )
        )
    )
}