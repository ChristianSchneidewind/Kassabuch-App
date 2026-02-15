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
import androidx.lifecycle.viewmodel.compose.viewModel
import at.christian.kassabuch.data.AppDatabase
import at.christian.kassabuch.data.DailyRateRepository
import at.christian.kassabuch.data.ExpenseRepository
import at.christian.kassabuch.data.IncomeRepository
import at.christian.kassabuch.ui.DailyRateViewModel
import at.christian.kassabuch.ui.DailyRateViewModelFactory
import at.christian.kassabuch.ui.DashboardScreen
import at.christian.kassabuch.ui.DashboardViewModel
import at.christian.kassabuch.ui.DashboardViewModelFactory
import at.christian.kassabuch.ui.ExpenseScreen
import at.christian.kassabuch.ui.ExpenseViewModel
import at.christian.kassabuch.ui.ExpenseViewModelFactory
import at.christian.kassabuch.ui.IncomeScreen
import at.christian.kassabuch.ui.IncomeViewModel
import at.christian.kassabuch.ui.IncomeViewModelFactory

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
        factory = DashboardViewModelFactory(dailyRateRepository)
    )

    val incomeState by incomeViewModel.uiState.collectAsState()
    val expenseState by expenseViewModel.uiState.collectAsState()
    val dailyRateState by dailyRateViewModel.uiState.collectAsState()
    val dashboardState by dashboardViewModel.uiState.collectAsState()

    var currentScreen by rememberSaveable { mutableStateOf(Screen.Dashboard) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                Screen.Dashboard -> {
                    DashboardScreen(
                        uiState = dashboardState,
                        onAddIncome = { currentScreen = Screen.Income },
                        onAddExpense = { currentScreen = Screen.Expense }
                    )
                }
                Screen.Income -> {
                    IncomeScreen(
                        uiState = incomeState,
                        rateUiState = dailyRateState,
                        categories = listOf("Lohn", "Sozialleistungen"),
                        onAddIncome = incomeViewModel::addIncome,
                        onEditDailyRate = dailyRateViewModel::addRate,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }
                Screen.Expense -> {
                    ExpenseScreen(
                        uiState = expenseState,
                        categories = listOf(
                            "Lebensmittel",
                            "Wohnen/Miete",
                            "Alimente",
                            "Heizen/Strom",
                            "Internet/Telefon",
                            "Mobilität/Transport",
                            "Gesundheit/Medikamente",
                            "Kleidung",
                            "Freizeit",
                            "Versicherungen",
                            "Google Play",
                            "Sonstiges"
                        ),
                        onAddExpense = expenseViewModel::addExpense,
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
    Expense
}
