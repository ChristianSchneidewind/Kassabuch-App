package at.christian.kassabuch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.christian.kassabuch.data.DailyRateRepository
import at.christian.kassabuch.data.ExpenseRepository
import at.christian.kassabuch.data.IncomeRepository
import at.christian.kassabuch.data.MonthlyBudgetRepository
import at.christian.kassabuch.data.PayoutScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

private val dashboardCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
private val dashboardMonthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMANY)

class DashboardViewModel(
    private val rateRepository: DailyRateRepository,
    private val payoutRepository: PayoutScheduleRepository,
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: MonthlyBudgetRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DashboardUiState(
            monthTitle = YearMonth.now().format(dashboardMonthFormatter),
            payoutAmount = "0,00 €",
            payoutDate = "—",
            monthlyBalance = "0,00 €",
            incomeSum = "0,00 €",
            expenseSum = "0,00 €",
            recentExpenses = emptyList(),
            isBudgetExceeded = false
        )
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            rateRepository.observeRates().collect { rates ->
                val previousMonth = YearMonth.now().minusMonths(1)
                val monthStart = previousMonth.atDay(1)
                val monthEnd = previousMonth.atEndOfMonth()
                val total = rates.sumOf { rate ->
                    val start = maxOf(rate.validFrom, monthStart)
                    val end = minOf(rate.validTo ?: LocalDate.MAX, monthEnd)
                    if (start.isAfter(end)) {
                        0.0
                    } else {
                        val days = ChronoUnit.DAYS.between(start, end) + 1
                        rate.rateAmount * days
                    }
                }

                _uiState.update { state ->
                    state.copy(
                        monthTitle = YearMonth.now().format(dashboardMonthFormatter),
                        payoutAmount = dashboardCurrencyFormatter.format(total)
                    )
                }
            }
        }

        viewModelScope.launch {
            payoutRepository.observeSchedules().collect { schedules ->
                val previousMonth = YearMonth.now().minusMonths(1)
                val payout = schedules.firstOrNull { it.month == previousMonth }
                val formatted = payout?.payoutDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    ?: "—"
                _uiState.update { state ->
                    state.copy(payoutDate = formatted)
                }
            }
        }

        viewModelScope.launch {
            combine(
                incomeRepository.observeIncomes(),
                expenseRepository.observeExpenses(),
                budgetRepository.observeBudgets()
            ) { incomes, expenses, budgets ->
                val currentMonth = YearMonth.now()
                val incomeSum = incomes
                    .filter { YearMonth.from(it.date) == currentMonth }
                    .sumOf { it.amount }
                val expenseSum = expenses
                    .filter { YearMonth.from(it.date) == currentMonth }
                    .sumOf { it.amount }
                val balance = incomeSum - expenseSum
                val budgetAmount = budgets.firstOrNull { it.month == currentMonth }?.amount
                val budgetExceeded = budgetAmount != null && expenseSum > budgetAmount
                val recentExpenses = expenses
                    .sortedByDescending { it.date }
                    .take(3)
                    .map { expense ->
                        DashboardExpenseItem(
                            title = expense.category,
                            date = expense.date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                            amount = dashboardCurrencyFormatter.format(expense.amount)
                        )
                    }

                DashboardMetrics(
                    incomeSum = incomeSum,
                    expenseSum = expenseSum,
                    balance = balance,
                    recentExpenses = recentExpenses,
                    budgetExceeded = budgetExceeded
                )
            }.collect { metrics ->
                _uiState.update { state ->
                    state.copy(
                        incomeSum = dashboardCurrencyFormatter.format(metrics.incomeSum),
                        expenseSum = dashboardCurrencyFormatter.format(metrics.expenseSum),
                        monthlyBalance = formatSignedCurrency(metrics.balance),
                        recentExpenses = metrics.recentExpenses,
                        isBudgetExceeded = metrics.budgetExceeded
                    )
                }
            }
        }
    }
}

private data class DashboardMetrics(
    val incomeSum: Double,
    val expenseSum: Double,
    val balance: Double,
    val recentExpenses: List<DashboardExpenseItem>,
    val budgetExceeded: Boolean
)

private fun formatSignedCurrency(amount: Double): String {
    return when {
        amount > 0.0 -> "+ ${dashboardCurrencyFormatter.format(amount)}"
        amount < 0.0 -> "- ${dashboardCurrencyFormatter.format(abs(amount))}"
        else -> dashboardCurrencyFormatter.format(0)
    }
}

class DashboardViewModelFactory(
    private val rateRepository: DailyRateRepository,
    private val payoutRepository: PayoutScheduleRepository,
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: MonthlyBudgetRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(
                rateRepository,
                payoutRepository,
                incomeRepository,
                expenseRepository,
                budgetRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
