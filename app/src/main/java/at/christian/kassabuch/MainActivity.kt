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
import at.christian.kassabuch.data.ExpenseRepository
import at.christian.kassabuch.data.IncomeRepository
import at.christian.kassabuch.ui.DashboardExpenseItem
import at.christian.kassabuch.ui.DashboardScreen
import at.christian.kassabuch.ui.DashboardUiState
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

    val incomeViewModel: IncomeViewModel = viewModel(
        key = "income",
        factory = IncomeViewModelFactory(incomeRepository)
    )
    val expenseViewModel: ExpenseViewModel = viewModel(
        key = "expense",
        factory = ExpenseViewModelFactory(expenseRepository)
    )

    val incomeState by incomeViewModel.uiState.collectAsState()
    val expenseState by expenseViewModel.uiState.collectAsState()

    var currentScreen by rememberSaveable { mutableStateOf(Screen.Dashboard) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                Screen.Dashboard -> {
                    DashboardScreen(
                        uiState = DashboardUiState(
                            monthTitle = "März 2026",
                            payoutAmount = "1.234,56 €",
                            payoutDate = "03.04.2026",
                            monthlyBalance = "+ 320,00 €",
                            incomeSum = "1.500,00 €",
                            expenseSum = "1.180,00 €",
                            recentExpenses = listOf(
                                DashboardExpenseItem(
                                    title = "Lebensmittel",
                                    date = "12.03.2026",
                                    amount = "45,90 €"
                                ),
                                DashboardExpenseItem(
                                    title = "Internet/Telefon",
                                    date = "10.03.2026",
                                    amount = "29,90 €"
                                ),
                                DashboardExpenseItem(
                                    title = "Mobilität",
                                    date = "08.03.2026",
                                    amount = "18,00 €"
                                )
                            )
                        ),
                        onAddIncome = { currentScreen = Screen.Income },
                        onAddExpense = { currentScreen = Screen.Expense }
                    )
                }
                Screen.Income -> {
                    IncomeScreen(
                        uiState = incomeState,
                        categories = listOf("Lohn", "Sozialleistungen"),
                        onAddIncome = incomeViewModel::addIncome,
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
