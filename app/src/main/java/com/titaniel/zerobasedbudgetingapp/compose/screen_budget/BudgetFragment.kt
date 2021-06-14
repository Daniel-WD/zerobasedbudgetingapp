package com.titaniel.zerobasedbudgetingapp.compose.screen_budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.titaniel.zerobasedbudgetingapp.R
import com.titaniel.zerobasedbudgetingapp.compose.assets.*
import com.titaniel.zerobasedbudgetingapp.database.repositories.BudgetRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.CategoryRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.SettingRepository
import com.titaniel.zerobasedbudgetingapp.database.repositories.TransactionRepository
import com.titaniel.zerobasedbudgetingapp.database.room.entities.Category
import com.titaniel.zerobasedbudgetingapp.database.room.relations.BudgetWithCategory
import com.titaniel.zerobasedbudgetingapp.utils.createSimpleMediatorLiveData
import com.titaniel.zerobasedbudgetingapp.utils.moneyFormat
import com.titaniel.zerobasedbudgetingapp.utils.monthName
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject

/**
 * [BudgetViewModel] for [BudgetFragment].
 */
@HiltViewModel
class BudgetViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    budgetRepository: BudgetRepository,
    settingRepository: SettingRepository
) : ViewModel() {

    /**
     * Month
     */
    private val month = settingRepository.getMonth().asLiveData()

    /**
     * Budgets by categories
     */
    private val budgetsOfCategories = categoryRepository.getBudgetsOfCategories().asLiveData()

    /**
     * Transactions by categories
     */
    private val transactionsOfCategories =
        categoryRepository.getTransactionsOfCategories().asLiveData()

    /**
     * All budgets
     */
    private val allBudgets = budgetRepository.getAllBudgets().asLiveData()

    /**
     * All transactions
     */
    private val transactions = transactionRepository.getAllTransactions().asLiveData()

    /**
     * All budgetsWithCategory
     */
    private val allBudgetsWithCategory = budgetRepository.getAllBudgetsWithCategory().asLiveData()

    /**
     * To be budgeted
     */
    val toBeBudgeted: MutableLiveData<Long> = MutableLiveData()

    /**
     * All budgetsWithCategory of selected month
     */
    val budgetsWithCategoryOfMonth: MutableLiveData<List<BudgetWithCategory>> = MutableLiveData()

    /**
     * Available money per budget
     */
    val availableMoney: MutableLiveData<Map<BudgetWithCategory, Long>> = MutableLiveData(emptyMap())

    /**
     * MediatorLiveData for [budgetsWithCategoryOfMonth], [transactionsOfCategories], [budgetsOfCategories], [month]
     */
    private val updateAvailableMoneyMediator = createSimpleMediatorLiveData(
        budgetsWithCategoryOfMonth,
        transactionsOfCategories,
        budgetsOfCategories,
        month
    )

    /**
     * MediatorLiveData for [transactions], [allBudgets]
     */
    private val updateToBeBudgetedMediator = createSimpleMediatorLiveData(transactions, allBudgets)

    /**
     * MediatorLiveData for [month], [allBudgetsWithCategory]
     */
    private val budgetsWithCategoryUpdateMediator =
        createSimpleMediatorLiveData(month, allBudgetsWithCategory)

    /**
     * Observer to update [budgetsWithCategoryOfMonth]
     */
    private val budgetsWithCategoryUpdateObserver: Observer<Unit> = Observer {
        val mon = month.value
        val budsWithCat = allBudgetsWithCategory.value

        // Check non null
        if (mon != null && budsWithCat != null) {
            // Filter all budgetsWithCategory of currently selected month
            budgetsWithCategoryOfMonth.value =
                budsWithCat.filter { it.budget.month == mon }
                    .sortedBy { it.category.positionInGroup }
        }

    }

    /**
     * Observer to update [availableMoney]
     */
    private val updateAvailableMoneyObserver: Observer<Unit> = Observer {
        val budsWithCatMon = budgetsWithCategoryOfMonth.value
        val transOfCats = transactionsOfCategories.value
        val budsOfCats = budgetsOfCategories.value
        val mon = month.value

        if (budsWithCatMon != null && transOfCats != null && budsOfCats != null && mon != null && budsOfCats.size == budsWithCatMon.size) {
            // Update available money per budget
            availableMoney.value = budsWithCatMon.map { budgetWithCategory ->
                budgetWithCategory to
                        // Sum of all transactions of the category of this budget until selected month (inclusive)
                        (transOfCats.find { transactionsOfCategory -> transactionsOfCategory.category.id == budgetWithCategory.category.id }?.transactions
                            ?.filter { transaction -> transaction.date.year < mon.year || (transaction.date.year == mon.year && transaction.date.month <= mon.month) }
                            ?.fold(0L, { acc, transaction -> acc + transaction.pay }) ?: 0) +

                        // Added with sum of all budgets with same category before this budget (inclusive)
                        budsOfCats.find { budgetsOfCategory -> budgetsOfCategory.category.id == budgetWithCategory.category.id }!!.budgets
                            .filter { bud -> bud.month <= mon }
                            .fold(0L, { acc, bud -> acc + bud.budgeted })
            }.toMap()
        }
    }

    /**
     * Observer to update [toBeBudgeted]
     */
    private val updateToBeBudgetedObserver: Observer<Unit> = Observer {
        val trans = transactions.value
        val buds = allBudgets.value

        // Check non null
        if (trans != null && buds != null) {
            // Filter all transaction for to be budgeted, sum pays up. Subtract all budget values.
            toBeBudgeted.value = trans.filter { it.categoryId == Category.TO_BE_BUDGETED.id }
                .fold(0L, { acc, transaction -> acc + transaction.pay }) -
                    buds.fold(0L, { acc, budget -> acc + budget.budgeted })
        }
    }

    init {
        // Register all observers
        updateAvailableMoneyMediator.observeForever(updateAvailableMoneyObserver)
        updateToBeBudgetedMediator.observeForever(updateToBeBudgetedObserver)
        budgetsWithCategoryUpdateMediator.observeForever(budgetsWithCategoryUpdateObserver)
    }

    override fun onCleared() {
        super.onCleared()

        // Remove all observers
        updateAvailableMoneyMediator.removeObserver(updateAvailableMoneyObserver)
        updateToBeBudgetedMediator.removeObserver(updateToBeBudgetedObserver)
        budgetsWithCategoryUpdateMediator.removeObserver(budgetsWithCategoryUpdateObserver)
    }

}

/**
 * [BudgetFragment] to show a list of categories. Each item contains budgeting information, which can be edited.
 */
@AndroidEntryPoint
class BudgetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            MaterialTheme {
                BudgetScreenWrapper()
            }
        }
    }

}

data class CategoryItemData(
    val categoryName: String,
    val budgetedAmount: Long,
    val availableAmount: Long
)

data class GroupData(val groupName: String, val items: List<CategoryItemData>)

data class ScreenData(
    val selectedMonth: YearMonth,
    val toBeBudgetedAmount: Long,
    val groups: List<GroupData>
)

@Composable
fun BudgetScreenWrapper(viewModel: BudgetViewModel = viewModel()) {
//    BudgetScreen()
}

@Composable
fun BudgetScreen(screenData: ScreenData) {
    MaterialTheme {
        Scaffold(
            topBar = {
                Toolbar(
                    selectedMonth = screenData.selectedMonth,
                    toBeBudgetedAmount = screenData.toBeBudgetedAmount
                )
            },
            bottomBar = { BottomBar() },
            backgroundColor = BackgroundColor,
            floatingActionButton = { Fab() }
        ) {
            GroupList(groups = screenData.groups)
        }
    }
}

@Composable
fun Fab() {
    FloatingActionButton(onClick = { }, backgroundColor = PrimaryColor, contentColor = Color.Black) {
        Icon(
            painter = painterResource(id = R.drawable.ic_outline_post_add_24),
            contentDescription = null
        )
    }
}

@Composable
fun Toolbar(selectedMonth: YearMonth, toBeBudgetedAmount: Long) {
    Surface(elevation = 4.dp, color = SolidToolbarColor) {
        Column {
            TopAppBar(
                title = { Text(text = "${selectedMonth.monthName()} ${selectedMonth.year}") },
                navigationIcon = {
                    IconButton(onClick = {}) {
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

                    IconButton(onClick = { menuExpanded.value = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_more_vert_24),
                            contentDescription = null,
                            tint = NormalIconColor
                        )
                    }
                    DropdownMenu(
                        modifier = Modifier.background(color = Color.Black),
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false }
                    ) {
                        DropdownMenuItem(onClick = {}) {
                            Text("Clear All Budgets", color = Text87Color)
                        }
                        DropdownMenuItem(onClick = {}) {
                            Text("Manage Categories", color = Text87Color)
                        }
                        DropdownMenuItem(onClick = {}) {
                            Text("Manage Payees", color = Text87Color)
                        }
                        DropdownMenuItem(onClick = {}) {
                            Text("Settings", color = Text87Color)
                        }
                    }
                },
                contentColor = Color.White,
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .offset(y = (-4).dp),
                text = "${toBeBudgetedAmount.moneyFormat()} Available",
                color = Color(0xddffffff),
                fontSize = 34.sp,
                fontWeight = FontWeight.Medium
            ) // TODO -> strings.xml
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
                Text(text = "Budgets")
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
                Text(text = "Transactions")
            },
            selectedContentColor = PrimaryColor,
            unselectedContentColor = Text60Color
        )
    }
}

@Composable
fun GroupList(groups: List<GroupData>) {
    LazyColumn {
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

@Preview
/*(widthDp = 360, heightDp = 640)*/
@Composable
fun BudgetScreenPreview() {

    BudgetScreen(
        screenData = ScreenData(
            selectedMonth = YearMonth.now(),
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
    )
}