package at.christian.kassabuch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.christian.kassabuch.data.DailyRateRepository
import at.christian.kassabuch.data.PayoutScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val dashboardCurrencyFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
private val dashboardMonthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMANY)

class DashboardViewModel(
    private val rateRepository: DailyRateRepository,
    private val payoutRepository: PayoutScheduleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        DashboardUiState(
            monthTitle = YearMonth.now().format(dashboardMonthFormatter),
            payoutAmount = "0,00 €",
            payoutDate = "—",
            monthlyBalance = "0,00 €",
            incomeSum = "0,00 €",
            expenseSum = "0,00 €",
            recentExpenses = emptyList()
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
    }
}

class DashboardViewModelFactory(
    private val rateRepository: DailyRateRepository,
    private val payoutRepository: PayoutScheduleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(rateRepository, payoutRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
