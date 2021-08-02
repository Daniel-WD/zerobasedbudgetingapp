package com.titaniel.zerobasedbudgetingapp.compose.screen_budget

import androidx.compose.animation.*
import androidx.compose.animation.core.FloatSpringSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlin.math.roundToInt


/**
 * [BudgetViewModel] for BudgetScreen.
 */
@HiltViewModel
class BudgetViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
    val budgetRepository: BudgetRepository,
    val settingRepository: SettingRepository,
    groupRepository: GroupRepository
) : ViewModel() {

    /**
     * Budgets before they have been changed through zeroing or setting the budget from last month
     * <budget id, budgeted value>
     */
    private val lastBudgets = mutableMapOf<Long, Long>()

    /**
     * Id of currently edited budget
     */
    private val editedBudgetId: MutableLiveData<Long> = MutableLiveData()

    /**
     * Whether a budget is currently edited
     */
    val inBudgetChangeMode = Transformations.map(editedBudgetId) { it != null }

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
     * All budgets until this month
     */
    private val relevantBudgets = Transformations.switchMap(month) { month ->
        requireNotNull(month)

        budgetRepository.getBudgetsUntilMonth(month).asLiveData()
    }

    /**
     * To be budgeted value
     */
    val toBeBudgeted = Transformations.map(
        DoubleLiveData(
            relevantTransactions,
            relevantBudgets
        )
    ) { (relTrans, relBudgets) ->

        if (relTrans == null || relBudgets == null) {
            return@map 0L
        }

        // Calc 'to be budgeted' value
        relTrans
            // Find all 'to be budgeted' transactions
            .filter { transaction -> transaction.categoryId == Category.TO_BE_BUDGETED.id }
            // Sum transaction pay up
            .fold(0L) { acc, transaction -> acc.plus(transaction.pay) }
            // Subtract...
            .minus(
                // ...the sum of all budget values
                relBudgets.fold(0L, { acc, budget -> acc + budget.budgeted })
            )
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
        QuintupleLiveData(
            budgetsToShow,
            groups,
            relevantTransactions,
            relevantBudgets,
            editedBudgetId
        )
    ) { (budsToShow, groups, relTransactions, relBudgets, editedBudgetId) ->

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
                                ),
                            budgetId = budgetWithCategory.budget.id,
                            state = when (editedBudgetId) {
                                null -> CategoryItemState.NORMAL
                                budgetWithCategory.budget.id -> CategoryItemState.CHANGE_SELECTED
                                else -> CategoryItemState.CHANGE_UNSELECTED
                            }
                        )
                    }
            )
        }
    }

    /**
     * Gets called when a user clicks on the budget with [budgetId]
     */
    fun onItemClick(budgetId: Long) {

        // If theres not already an edited budget
        if (editedBudgetId.value == null) {

            // Set budget to edit
            editedBudgetId.value = budgetId
        }
    }

    /**
     * Gets when a budget with [amount] should be applied to the currently edited budget
     */
    fun onBudgetConfirmationClick(amount: Long) {

        if (editedBudgetId.value == null) {
            throw IllegalStateException()
        }

        viewModelScope.launch {

            // Get edited budget
            val budget = budgetRepository.getBudgetById(editedBudgetId.value!!).first()

            // Update amount
            budget.budgeted = amount

            // Update budget
            budgetRepository.updateBudgets(budget)

            // Deselect budget
            editedBudgetId.value = null
        }

    }

    /**
     * Gets called when the budget editing should stop without saving
     */
    fun onAbortBudgetChange() {
        // Make edited budget being null
        editedBudgetId.value = null
    }

    /**
     * Clear all budgets
     */
    fun onClearAllBudgets() {

        // Get budgets
        val budgets = budgetsWithCategoryOfMonth.value!!.map { it.budget }

        // Set all budgets to 0
        budgets.forEach { budget -> budget.budgeted = 0 }

        // Update budgets in db
        viewModelScope.launch {
            budgetRepository.updateBudgets(*budgets.toTypedArray())
        }

    }

    /**
     * Zero budget with [budgetId]
     */
    fun onZeroBudget(budgetId: Long) {

        viewModelScope.launch {

            // Get budget to zero
            val budget = budgetRepository.getBudgetById(budgetId).first()

            // Save current budgeted value
            lastBudgets[budgetId] = budget.budgeted

            // Set budget to 0
            budget.budgeted = 0

            // Update budget
            budgetRepository.updateBudgets(budget)
        }
    }

    /**
     * Sets budget from last month to budget with [budgetId]
     */
    fun onBudgetFromLastMonth(budgetId: Long, onBudgetSet: (success: Boolean) -> Unit) {

        // Get month
        val mon = month.value
        requireNotNull(mon)

        // Calc last month
        val lastMonth = mon.minusMonths(1)

        viewModelScope.launch {

            // Get available months
            val availMonths = settingRepository.availableMonths.first()

            // No success when last month doesn't exist
            if (!availMonths.contains(lastMonth)) {
                onBudgetSet(false)
                return@launch
            }

            // Get budget to set the budget from last month to
            val budget = budgetRepository.getBudgetById(budgetId).first()

            // Save current budgeted value
            lastBudgets[budgetId] = budget.budgeted

            // Get budget from last month
            val lastMonthBudget = budgetRepository.getBudgetByCategoryIdAndMonth(
                budget.categoryId,
                lastMonth
            ).firstOrNull()

            // Set budget value from last month, zero when it doesn't exist
            budget.budgeted = lastMonthBudget?.budgeted ?: 0

            // Update budget
            budgetRepository.updateBudgets(budget)

            // Notify success
            onBudgetSet(true)
        }
    }

    /**
     * Undo a budget change through zeroing or setting the budget form last month
     */
    fun onUndoBudget(budgetId: Long) {

        // Get saved budget
        val lastBudget = lastBudgets[budgetId]
        requireNotNull(lastBudget)

        viewModelScope.launch {

            // Get budget to zero
            val budget = budgetRepository.getBudgetById(budgetId).first()

            // Set budget to saved
            budget.budgeted = lastBudget

            // Remove saved budget
            lastBudgets.remove(budgetId)

            // Update budget
            budgetRepository.updateBudgets(budget)
        }
    }

}

/**
 * Class to represent visual state of a CategoryItem
 */
enum class CategoryItemState {
    /**
     * Normal state
     */
    NORMAL,

    /**
     * This items budget can be changed
     */
    CHANGE_SELECTED,

    /**
     * The budget of a different item can be changed
     */
    CHANGE_UNSELECTED
}

/**
 * Item swipe state
 */
enum class ItemSwipeState {
    /**
     * Normal item swipe state
     */
    NORMAL,

    /**
     * State when budget of item should be zeroed
     */
    BUDGET_ZERO,

    /**
     * State when budget of item should be same as last month
     */
    BUDGET_LAST_MONTH
}

/**
 * Data class to represent a budget row
 */
data class CategoryItemData(
    val categoryName: String,
    val budgetedAmount: Long,
    val availableAmount: Long,
    val budgetId: Long,
    val state: CategoryItemState
)

/**
 * Data class to represent a group with its budgets
 */
data class GroupData(val groupName: String, val items: List<CategoryItemData>)

/**
 * Budget screen taking only the [viewModel] as input.
 */
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun BudgetScreenWrapper(viewModel: BudgetViewModel = viewModel()) {

    // Month state
    val month by viewModel.month.observeAsState()

    // To be budgeted amount state
    val toBeBudgetedAmount by viewModel.toBeBudgeted.observeAsState()

    // Groups list state
    val groups by viewModel.groupList.observeAsState()

    // inBudgetChangeMode state
    val inBudgetChangeMode by viewModel.inBudgetChangeMode.observeAsState(false)

    BudgetScreen(
        month = month ?: YearMonth.now(),
        toBeBudgetedAmount = toBeBudgetedAmount ?: 0,
        groups = groups ?: emptyList(),
        onItemClick = viewModel::onItemClick,
        onBudgetConfirmationClick = viewModel::onBudgetConfirmationClick,
        onAbortBudgetChange = viewModel::onAbortBudgetChange,
        inBudgetChangeMode = inBudgetChangeMode,
        onClearAllBudgets = viewModel::onClearAllBudgets,
        onZeroBudget = viewModel::onZeroBudget,
        onBudgetFromLastMonth = viewModel::onBudgetFromLastMonth,
        onUndoBudget = viewModel::onUndoBudget
    )

}

/**
 * Budget Screen, decoupled from a view model.
 * For preview purposes.
 */
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun BudgetScreen(
    month: YearMonth,
    toBeBudgetedAmount: Long,
    groups: List<GroupData>,
    onItemClick: (budgetId: Long) -> Unit,
    onBudgetConfirmationClick: (amount: Long) -> Unit,
    onAbortBudgetChange: () -> Unit,
    inBudgetChangeMode: Boolean,
    onClearAllBudgets: () -> Unit,
    onZeroBudget: (budgetId: Long) -> Unit,
    onBudgetFromLastMonth: (budgetId: Long, onBudgetSet: (success: Boolean) -> Unit) -> Unit,
    onUndoBudget: (budgetId: Long) -> Unit
) {
    // Scaffold state
    val scaffoldState = rememberScaffoldState()

    // Month picker state. Can be hidden or shown.
    val monthPickerState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    // Coroutine scope
    val scope = rememberCoroutineScope()

    // Add material theme
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
                scaffoldState = scaffoldState,
                topBar = {
                    Toolbar(
                        selectedMonth = month,
                        toBeBudgetedAmount = toBeBudgetedAmount,
                        scope = scope,
                        monthPickerState = monthPickerState,
                        inBudgetChangeMode = inBudgetChangeMode,
                        onAbortBudgetChange = onAbortBudgetChange,
                        onClearAllBudgets = onClearAllBudgets
                    )
                },
                bottomBar = { if (!inBudgetChangeMode) BottomBar() },
                backgroundColor = BackgroundColor,
                floatingActionButton = { if (!inBudgetChangeMode) Fab() }
            ) {
                GroupList(
                    groups = groups,
                    onItemClick = onItemClick,
                    onBudgetConfirmationClick = onBudgetConfirmationClick,
                    onAbortBudgetChange = onAbortBudgetChange,
                    onZeroBudget = onZeroBudget,
                    onBudgetFromLastMonth = onBudgetFromLastMonth,
                    scaffoldState = scaffoldState,
                    screenScope = scope,
                    onUndoBudget = onUndoBudget
                )
            }
        }
    }
}

/**
 * Floating action button for creating a new transaction
 */
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

/**
 * Toolbar that shows 'to be budgeted' value, app bar and table header
 */
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun Toolbar(
    selectedMonth: YearMonth,
    toBeBudgetedAmount: Long,
    scope: CoroutineScope,
    monthPickerState: ModalBottomSheetState,
    inBudgetChangeMode: Boolean,
    onAbortBudgetChange: () -> Unit,
    onClearAllBudgets: () -> Unit
) {
    // Toolbar background color, animated by budget change mode
    val backgroundColor by animateColorAsState(targetValue = if (!inBudgetChangeMode) SolidToolbarColor else SolidHighlightColor)

    Surface(
        elevation = 4.dp,
        color = backgroundColor
    ) {
        Column {
            Box {
                DefaultAppBar(
                    selectedMonth = selectedMonth,
                    scope = scope,
                    monthPickerState = monthPickerState,
                    onClearAllBudgets = onClearAllBudgets
                )
                androidx.compose.animation.AnimatedVisibility(
                    visible = inBudgetChangeMode,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    BudgetChangeAppBar(onAbortBudgetChange = onAbortBudgetChange)
                }
            }
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

/**
 * App bar that shows selected month, month picker link and context menu
 */
@ExperimentalMaterialApi
@Composable
fun DefaultAppBar(
    selectedMonth: YearMonth,
    scope: CoroutineScope,
    monthPickerState: ModalBottomSheetState,
    onClearAllBudgets: () -> Unit
) {
    TopAppBar(
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
            DefaultAppBarMenu(onClearAllBudgets = onClearAllBudgets)
        },
        contentColor = Color.White,
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}

/**
 * Context menu with 'show' button.
 */
@Composable
fun DefaultAppBarMenu(onClearAllBudgets: () -> Unit) {

    // Expansion state of context menu
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
        DropdownMenuItem(
            modifier = Modifier.testTag("ClearAllBudgets"),
            onClick = onClearAllBudgets
        ) {
            Text(
                stringResource(R.string.clear_all_budgets),
                color = Text87Color
            )
        }
        DropdownMenuItem(onClick = {}) {
            Text(
                stringResource(R.string.manage_categories),
                color = Text87Color
            )
        }
        DropdownMenuItem(onClick = {}) {
            Text(stringResource(R.string.manage_payees), color = Text87Color)
        }
        DropdownMenuItem(onClick = {}) {
            Text(stringResource(R.string.settings), color = Text87Color)
        }
    }
}

/**
 * App bar to notify user, that he is in 'change budget' mode. Option to abort this mode.
 */
@Composable
fun BudgetChangeAppBar(onAbortBudgetChange: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.change_budget)) },
        actions = {
            IconButton(
                modifier = Modifier.testTag("AbortBudgetChange"),
                onClick = { onAbortBudgetChange() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_close_24),
                    contentDescription = null,
                    tint = NormalIconColor
                )
            }
        },
        contentColor = Color.White,
        backgroundColor = SolidHighlightColor,
        elevation = 0.dp
    )
}

/**
 * Header for column descriptions
 */
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

/**
 * Bottom navigation
 */
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

/**
 * List of groups
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun GroupList(
    groups: List<GroupData>,
    onItemClick: (budgetId: Long) -> Unit,
    onBudgetConfirmationClick: (amount: Long) -> Unit,
    onAbortBudgetChange: () -> Unit,
    onZeroBudget: (budgetId: Long) -> Unit,
    onBudgetFromLastMonth: (budgetId: Long, onBudgetSet: (success: Boolean) -> Unit) -> Unit,
    scaffoldState: ScaffoldState,
    screenScope: CoroutineScope,
    onUndoBudget: (budgetId: Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.testTag("GroupList"),
        contentPadding = PaddingValues(bottom = (2.5 * 55).dp)
    ) {
        items(groups) { groupData ->
            Group(
                data = groupData,
                onItemClick = onItemClick,
                onBudgetConfirmationClick = onBudgetConfirmationClick,
                onAbortChange = onAbortBudgetChange,
                onZeroBudget = onZeroBudget,
                onBudgetFromLastMonth = onBudgetFromLastMonth,
                scaffoldState = scaffoldState,
                screenScope = screenScope,
                onUndoBudget = onUndoBudget
            )
        }
    }
}

/**
 * Group that shows group information and its categories.
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun Group(
    data: GroupData,
    onItemClick: (budgetId: Long) -> Unit,
    onBudgetConfirmationClick: (amount: Long) -> Unit,
    onAbortChange: () -> Unit,
    onZeroBudget: (budgetId: Long) -> Unit,
    onBudgetFromLastMonth: (budgetId: Long, onBudgetSet: (success: Boolean) -> Unit) -> Unit,
    scaffoldState: ScaffoldState,
    screenScope: CoroutineScope,
    onUndoBudget: (budgetId: Long) -> Unit
) {

    // Sum budget sum of categories of this group
    val budgetedSum =
        data.items.fold(0L, { acc, categoryItemData -> acc + categoryItemData.budgetedAmount })

    // Sum of available amount of categories of this group
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
                CategoryItem(
                    data = categoryItemData,
                    onItemClick = onItemClick,
                    onBudgetConfirmationClick = onBudgetConfirmationClick,
                    onAbortChange = onAbortChange,
                    onZeroBudget = onZeroBudget,
                    onBudgetFromLastMonth = onBudgetFromLastMonth,
                    scaffoldState = scaffoldState,
                    screenScope = screenScope,
                    onUndoBudget = onUndoBudget
                )
            }
        }
    }

}

/**
 * Category item to represent a category. Budget can be edited.
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun CategoryItem(
    data: CategoryItemData,
    onItemClick: (budgetId: Long) -> Unit,
    onBudgetConfirmationClick: (amount: Long) -> Unit,
    onAbortChange: () -> Unit,
    onZeroBudget: (budgetId: Long) -> Unit,
    onBudgetFromLastMonth: (budgetId: Long, onBudgetSet: (success: Boolean) -> Unit) -> Unit,
    scaffoldState: ScaffoldState,
    screenScope: CoroutineScope,
    onUndoBudget: (budgetId: Long) -> Unit
) {

    // Transition to coordinate animations by item state
    val transition = updateTransition(targetState = data.state, label = null)

    // Divider color, animated by item state
    val dividerColor by transition.animateColor(label = "") { state ->
        when (state) {
            CategoryItemState.CHANGE_SELECTED -> Divider87Color
            else -> Divider12Color
        }
    }

    // Background color, animated by item state
    val backgroundColor by transition.animateColor(label = "") { state ->
        when (state) {
            CategoryItemState.CHANGE_SELECTED -> SolidHighlightColor
            else -> BackgroundColor
        }
    }

    // Text color, animated by item state
    val textColor by transition.animateColor(label = "") { state ->
        when (state) {
            CategoryItemState.CHANGE_UNSELECTED -> Text50Color
            else -> Text87Color
        }
    }

    // Get screen width, corresponds to item width
    val screenWidth = LocalContext.current.resources.displayMetrics.widthPixels.toFloat()

    // Swipe state
    val swipeableState = rememberSwipeableState(ItemSwipeState.NORMAL)

    // Swipe state, the item is closest
    val closestSwipeState =
        if (swipeableState.offset.value > 0) ItemSwipeState.BUDGET_ZERO else ItemSwipeState.BUDGET_LAST_MONTH

    // Swipe anchor points to state mapping
    val anchors = mapOf(
        0f to ItemSwipeState.NORMAL,
        screenWidth to ItemSwipeState.BUDGET_ZERO,
        -screenWidth to ItemSwipeState.BUDGET_LAST_MONTH
    )

    // When user has swiped to BUDGET_ZERO state...
    if (swipeableState.currentValue != ItemSwipeState.NORMAL) {
        LaunchedEffect(true) {

            // Perform action based on side of swipe
            if (closestSwipeState == ItemSwipeState.BUDGET_ZERO) {

                // Zero budget
                onZeroBudget(data.budgetId)

                // Show snackbar
                screenScope.launch {

                    // Dismiss currently shown snackbar if there is one
                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()

                    // Show new snackbar
                    scaffoldState.snackbarHostState.showSnackbar(
                        "Set budgeted value of ${data.categoryName} to 0.",
                        "UNDO"
                    ).let { result ->

                        // If 'UNDO' has been clicked
                        if (result == SnackbarResult.ActionPerformed) {
                            onUndoBudget(data.budgetId)
                        }
                    }
                }

            } else {
                // Set budget from last month
                onBudgetFromLastMonth(data.budgetId) { success ->

                    // Show snackbar
                    screenScope.launch {

                        // Dismiss currently shown snackbar if there is one
                        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()

                        // Was there are last months budget?
                        if (success) {

                            // Show snackbar for successful change
                            scaffoldState.snackbarHostState.showSnackbar(
                                "Set same budget as last month for ${data.categoryName}.",
                                "UNDO"
                            ).let { result ->

                                // If 'UNDO' has been clicked
                                if (result == SnackbarResult.ActionPerformed) {
                                    onUndoBudget(data.budgetId)
                                }
                            }

                        } else {

                            // Show snackbar for no last months budget
                            scaffoldState.snackbarHostState.showSnackbar("There is no budget for ${data.categoryName} in the last month.")
                        }
                    }
                }
            }

            // Animate back to NORMAL swipe state
            swipeableState.animateTo(ItemSwipeState.NORMAL)

        }
    }

    // If item state changes...
    if (data.state != CategoryItemState.NORMAL) {

        // Dismiss currently shown snackbar if there is one
        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Set background for zeroing if closest swipe state is BUDGET_ZERO, background for last month budget otherwise
            .background(if (closestSwipeState == ItemSwipeState.BUDGET_ZERO) SwipeToZeroColor else SwipeLastMonthBudgetColor)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.7f) },
                orientation = Orientation.Horizontal,
                // Swipeable when nothing is edited and return animation is not running
                enabled = data.state == CategoryItemState.NORMAL && !swipeableState.isAnimationRunning
            )
    ) {
        if (closestSwipeState == ItemSwipeState.BUDGET_ZERO) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp),
                painter = painterResource(id = R.drawable.ic_baseline_exposure_zero_24),
                contentDescription = null
            )
        } else {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                painter = painterResource(id = R.drawable.ic_baseline_cached_24),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .background(backgroundColor)
                .clickable {
                    onItemClick(data.budgetId)
                }
        ) {
            Row(
                modifier = Modifier
                    .height(54.dp)
                    .zIndex(5f)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(2f)
                        .padding(end = 16.dp),
                    text = data.categoryName,
                    color = textColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                ) {
                    // View Budget
                    androidx.compose.animation.AnimatedVisibility(
                        visible = data.state != CategoryItemState.CHANGE_SELECTED,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CategoryItemShowBudget(data = data, normalTextColor = textColor)
                    }
                    // Change budget
                    androidx.compose.animation.AnimatedVisibility(
                        visible = data.state == CategoryItemState.CHANGE_SELECTED,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CategoryItemBudgetInput(
                            data = data,
                            onAbortChange = onAbortChange,
                            onBudgetConfirmationClick = onBudgetConfirmationClick
                        )
                    }
                }
            }
            Divider(
                modifier = Modifier.height(1.dp),
                color = dividerColor
            )
        }
    }
}

/**
 * Part of [CategoryItem] to show budget and available amount.
 */
@Composable
fun CategoryItemShowBudget(data: CategoryItemData, normalTextColor: Color) {

    // Text color of available money values, animated by item state
    val availableMoneyTextColor by animateColorAsState(
        targetValue = when (data.state) {
            CategoryItemState.CHANGE_UNSELECTED -> when {
                data.availableAmount > 0 -> TextGreen50Color
                data.availableAmount < 0 -> TextRed50Color
                else -> Text50Color
            }
            else -> when {
                data.availableAmount > 0 -> TextGreenColor
                data.availableAmount < 0 -> TextRedColor
                else -> Text87Color
            }
        }
    )

    Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.weight(1f),
            text = data.budgetedAmount.moneyFormat(),
            color = normalTextColor,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            text = data.availableAmount.moneyFormat(),
            color = availableMoneyTextColor,
            textAlign = TextAlign.End,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Part of category item to change budget
 */
@Composable
fun CategoryItemBudgetInput(
    data: CategoryItemData,
    onAbortChange: () -> Unit,
    onBudgetConfirmationClick: (amount: Long) -> Unit
) {

    // Budget value text
    var editBudgetValue by remember { mutableStateOf(data.budgetedAmount.toString()) }

    // Abort budget change if text field focus gets lost?
    var abortable by remember { mutableStateOf(false) }

    // Requester for focus on text field
    val focusRequester = remember { FocusRequester() }

    Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
        BasicTextField(
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged { state ->
                    // If aborting is allowed and text is not focused
                    if (abortable && !state.isFocused) {
                        onAbortChange()
                    }
                },
            value = TextFieldValue(
                text = editBudgetValue,
                selection = TextRange(editBudgetValue.length) // Cursor always on the end
            ),
            onValueChange = { editBudgetValue = moneyOnValueChange(it, 6) },
            textStyle = MaterialTheme.typography.body1.copy(
                textAlign = TextAlign.End,
                color = Text87Color
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                autoCorrect = false,
                imeAction = ImeAction.Done
            ),
            maxLines = 1,
            cursorBrush = SolidColor(Text87Color),
            visualTransformation = MoneyVisualTransformation,
            keyboardActions = KeyboardActions {
                onBudgetConfirmationClick(editBudgetValue.toLong())
                abortable = false
            }
        )
        DisposableEffect(Unit) {
            focusRequester.requestFocus()
            abortable = true
            onDispose { }
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            IconButton(
                modifier = Modifier.offset(x = 12.dp),
                onClick = {
                    onBudgetConfirmationClick(editBudgetValue.toLong())
                    abortable = false
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_check_24),
                    contentDescription = null,
                    tint = Text87Color
                )
            }
        }
    }

}

/**
 * Preview of budget screen.
 */
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Preview
/*(widthDp = 360, heightDp = 640)*/
@Composable
fun BudgetScreenPreview() {

    CategoryItem(
        data = CategoryItemData("name", 100, 50, 2, CategoryItemState.NORMAL),
        onItemClick = {},
        onBudgetConfirmationClick = {},
        onAbortChange = {},
        onZeroBudget = {},
        onBudgetFromLastMonth = { a, b -> },
        scaffoldState = rememberScaffoldState(),
        screenScope = rememberCoroutineScope(),
        onUndoBudget = {}
    )

//    BudgetScreen(
//        month = YearMonth.now(),
//        toBeBudgetedAmount = 100000,
//        groups = listOf(
//            GroupData(
//                "Persönlich", listOf(
//                    CategoryItemData(
//                        categoryName = "Lebensmittel",
//                        budgetedAmount = 1000000,
//                        availableAmount = 1200,
//                        budgetId = 1,
//                        state = CategoryItemState.NORMAL
//                    ),
//                    CategoryItemData(
//                        categoryName = "Investment",
//                        budgetedAmount = 0,
//                        availableAmount = 0,
//                        budgetId = 2,
//                        state = CategoryItemState.NORMAL
//                    ),
//                    CategoryItemData(
//                        categoryName = "Bücher",
//                        budgetedAmount = 10000,
//                        availableAmount = -1412,
//                        budgetId = 3,
//                        state = CategoryItemState.NORMAL
//                    )
//                )
//            ),
//            GroupData(
//                "Haushalt", listOf(
//                    CategoryItemData(
//                        categoryName = "Lebensmittel",
//                        budgetedAmount = 1000000,
//                        availableAmount = 1200,
//                        budgetId = 4,
//                        state = CategoryItemState.NORMAL
//                    ),
//                    CategoryItemData(
//                        categoryName = "Investment",
//                        budgetedAmount = 0,
//                        availableAmount = 0,
//                        budgetId = 5,
//                        state = CategoryItemState.NORMAL
//                    ),
//                    CategoryItemData(
//                        categoryName = "Bücher",
//                        budgetedAmount = 10000,
//                        availableAmount = -1412,
//                        budgetId = 6,
//                        state = CategoryItemState.NORMAL
//                    )
//                )
//            )
//        ),
//        onItemClick = {},
//        onBudgetConfirmationClick = {},
//        onAbortBudgetChange = {},
//        inBudgetChangeMode = true,
//        onClearAllBudgets = {}
//    )
}