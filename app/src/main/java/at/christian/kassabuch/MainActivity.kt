package at.christian.kassabuch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import at.christian.kassabuch.data.AppDatabase
import at.christian.kassabuch.data.CategoryRepository
import at.christian.kassabuch.data.DailyRateRepository
import at.christian.kassabuch.data.ExpenseRepository
import at.christian.kassabuch.data.FixedExpenseRuleRepository
import at.christian.kassabuch.data.IncomeRepository
import at.christian.kassabuch.data.MonthlyBudgetRepository
import at.christian.kassabuch.data.PayoutScheduleRepository
import at.christian.kassabuch.data.SeedingRunner
import at.christian.kassabuch.ui.BudgetScreen
import at.christian.kassabuch.ui.BudgetViewModel
import at.christian.kassabuch.ui.BudgetViewModelFactory
import at.christian.kassabuch.ui.CategoriesScreen
import at.christian.kassabuch.ui.CategoriesViewModel
import at.christian.kassabuch.ui.CategoriesViewModelFactory
import at.christian.kassabuch.ui.DailyRateViewModel
import at.christian.kassabuch.ui.DailyRateViewModelFactory
import at.christian.kassabuch.ui.DashboardScreen
import at.christian.kassabuch.ui.DashboardViewModel
import at.christian.kassabuch.ui.DashboardViewModelFactory
import at.christian.kassabuch.ui.ExpenseScreen
import at.christian.kassabuch.ui.ExpenseViewModel
import at.christian.kassabuch.ui.ExpenseViewModelFactory
import at.christian.kassabuch.ui.FixedExpenseScreen
import at.christian.kassabuch.ui.FixedExpenseViewModel
import at.christian.kassabuch.ui.FixedExpenseViewModelFactory
import at.christian.kassabuch.ui.IncomeScreen
import at.christian.kassabuch.ui.IncomeViewModel
import at.christian.kassabuch.ui.IncomeViewModelFactory
import at.christian.kassabuch.ui.PayoutScheduleScreen
import at.christian.kassabuch.ui.PayoutScheduleViewModel
import at.christian.kassabuch.ui.PayoutScheduleViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KassabuchApp()
        }
    }
}

@Composable
fun KassabuchApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val incomeRepository = remember { IncomeRepository(database.incomeDao()) }
    val expenseRepository = remember { ExpenseRepository(database.expenseDao()) }
    val dailyRateRepository = remember { DailyRateRepository(database.dailyRateDao()) }
    val payoutScheduleRepository = remember { PayoutScheduleRepository(database.payoutScheduleDao()) }
    val fixedExpenseRepository = remember { FixedExpenseRuleRepository(database.fixedExpenseRuleDao()) }
    val categoryRepository = remember {
        CategoryRepository(database.incomeCategoryDao(), database.expenseCategoryDao())
    }
    val budgetRepository = remember { MonthlyBudgetRepository(database.monthlyBudgetDao()) }

    val incomeViewModel: IncomeViewModel = viewModel(
        key = "income",
        factory = IncomeViewModelFactory(incomeRepository)
    )
    val expenseViewModel: ExpenseViewModel = viewModel(
        key = "expense",
        factory = ExpenseViewModelFactory(expenseRepository)
    )
    val dailyRateViewModel: DailyRateViewModel = viewModel(
        key = "dailyRate",
        factory = DailyRateViewModelFactory(dailyRateRepository)
    )
    val dashboardViewModel: DashboardViewModel = viewModel(
        key = "dashboard",
        factory = DashboardViewModelFactory(dailyRateRepository, payoutScheduleRepository)
    )
    val payoutScheduleViewModel: PayoutScheduleViewModel = viewModel(
        key = "payout",
        factory = PayoutScheduleViewModelFactory(payoutScheduleRepository)
    )
    val fixedExpenseViewModel: FixedExpenseViewModel = viewModel(
        key = "fixed",
        factory = FixedExpenseViewModelFactory(fixedExpenseRepository)
    )
    val categoriesViewModel: CategoriesViewModel = viewModel(
        key = "categories",
        factory = CategoriesViewModelFactory(categoryRepository)
    )
    val budgetViewModel: BudgetViewModel = viewModel(
        key = "budget",
        factory = BudgetViewModelFactory(budgetRepository, expenseRepository)
    )

    val incomeState by incomeViewModel.uiState.collectAsState()
    val expenseState by expenseViewModel.uiState.collectAsState()
    val dashboardState by dashboardViewModel.uiState.collectAsState()
    val payoutState by payoutScheduleViewModel.uiState.collectAsState()
    val fixedExpenseState by fixedExpenseViewModel.uiState.collectAsState()
    val categoriesState by categoriesViewModel.uiState.collectAsState()
    val budgetState by budgetViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        SeedingRunner(
            database.payoutScheduleDao(),
            database.incomeCategoryDao(),
            database.expenseCategoryDao()
        ).seedIfEmpty()
    }

    var currentScreen by rememberSaveable { mutableStateOf(Screen.Dashboard) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                Screen.Dashboard -> {
                    DashboardScreen(
                        uiState = dashboardState,
                        onAddIncome = { currentScreen = Screen.Income },
                        onAddExpense = { currentScreen = Screen.Expense },
                        onShowPayouts = { currentScreen = Screen.Payouts },
                        onShowFixedExpenses = { currentScreen = Screen.FixedExpenses },
                        onShowCategories = { currentScreen = Screen.Categories },
                        onShowBudget = { currentScreen = Screen.Budget }
                    )
                }
                Screen.Income -> {
                    IncomeScreen(
                        uiState = incomeState,
                        categories = categoriesState.income.map { it.name },
                        onAddIncome = incomeViewModel::addIncome,
                        onEditDailyRate = dailyRateViewModel::addRate,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }
                Screen.Expense -> {
                    ExpenseScreen(
                        uiState = expenseState,
                        categories = categoriesState.expenses.map { it.name },
                        onAddExpense = expenseViewModel::addExpense,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }
                Screen.Payouts -> {
                    PayoutScheduleScreen(
                        uiState = payoutState,
                        onUpdateSchedule = payoutScheduleViewModel::updateSchedule,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }
                Screen.FixedExpenses -> {
                    FixedExpenseScreen(
                        uiState = fixedExpenseState,
                        onUpdateRule = fixedExpenseViewModel::updateRule,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }
                Screen.Categories -> {
                    CategoriesScreen(
                        uiState = categoriesState,
                        onAddIncome = categoriesViewModel::addIncomeCategory,
                        onAddExpense = categoriesViewModel::addExpenseCategory,
                        onRenameIncome = categoriesViewModel::renameIncomeCategory,
                        onRenameExpense = categoriesViewModel::renameExpenseCategory,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }
                Screen.Budget -> {
                    BudgetScreen(
                        uiState = budgetState,
                        onSetBudget = budgetViewModel::setBudget,
                        onMonthChange = budgetViewModel::setMonth,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }
            }
        }
    }
}

private enum class Screen {
    Dashboard,
    Income,
    Expense,
    Payouts,
    FixedExpenses,
    Categories,
    Budget
}
