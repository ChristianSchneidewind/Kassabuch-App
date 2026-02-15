package at.christian.kassabuch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.christian.kassabuch.data.ExpenseRepository
import at.christian.kassabuch.data.MonthlyBudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.YearMonth
import java.util.Locale

private val budgetFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)

class BudgetViewModel(
    private val budgetRepository: MonthlyBudgetRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            budgetRepository.observeBudgets().collect { budgets ->
                _uiState.update { state ->
                    val selectedMonth = state.selectedMonth
                    val selectedBudget = budgets.firstOrNull { it.month == selectedMonth }
                    state.copy(
                        budgetAmount = selectedBudget?.amount?.let { budgetFormatter.format(it) }
                            ?: "",
                        rawBudgetAmount = selectedBudget?.amount
                    )
                }
            }
        }

        viewModelScope.launch {
            expenseRepository.observeExpenses().collect { expenses ->
                _uiState.update { state ->
                    val total = expenses
                        .filter { YearMonth.from(it.date) == state.selectedMonth }
                        .sumOf { it.amount }
                    state.copy(currentSpend = total)
                }
            }
        }
    }

    fun setMonth(month: YearMonth) {
        _uiState.update { it.copy(selectedMonth = month) }
    }

    fun setBudget(amount: Double) {
        viewModelScope.launch {
            budgetRepository.setBudget(_uiState.value.selectedMonth, amount)
        }
    }
}

class BudgetViewModelFactory(
    private val budgetRepository: MonthlyBudgetRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(budgetRepository, expenseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class BudgetUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val budgetAmount: String = "",
    val rawBudgetAmount: Double? = null,
    val currentSpend: Double = 0.0
) {
    val isOverBudget: Boolean
        get() = rawBudgetAmount != null && currentSpend > rawBudgetAmount
}
