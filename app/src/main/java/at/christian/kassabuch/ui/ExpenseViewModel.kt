package at.christian.kassabuch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.christian.kassabuch.data.ExpenseEntity
import at.christian.kassabuch.data.ExpenseRepository
import at.christian.kassabuch.data.ExpenseType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val expenseDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val expenseCurrencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY)

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeExpenses().collect { expenses ->
                _uiState.update { state ->
                    state.copy(
                        fixedItems = expenses.filter { it.type == ExpenseType.FIXED }.map { expense ->
                            ExpenseListItem(
                                id = expense.id,
                                category = expense.category,
                                amount = expenseCurrencyFormatter.format(expense.amount),
                                date = expense.date.format(expenseDateFormatter),
                                typeLabel = "Fix",
                                note = expense.note
                            )
                        },
                        variableItems = expenses.filter { it.type == ExpenseType.VARIABLE }.map { expense ->
                            ExpenseListItem(
                                id = expense.id,
                                category = expense.category,
                                amount = expenseCurrencyFormatter.format(expense.amount),
                                date = expense.date.format(expenseDateFormatter),
                                typeLabel = "Variabel",
                                note = expense.note
                            )
                        }
                    )
                }
            }
        }
    }

    fun addExpense(
        category: String,
        amount: Double,
        date: LocalDate,
        type: ExpenseType,
        note: String?
    ) {
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    category = category,
                    amount = amount,
                    date = date,
                    type = type,
                    note = note
                )
            )
        }
    }
}

class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class ExpenseUiState(
    val fixedItems: List<ExpenseListItem> = emptyList(),
    val variableItems: List<ExpenseListItem> = emptyList()
)

data class ExpenseListItem(
    val id: Long,
    val category: String,
    val amount: String,
    val date: String,
    val typeLabel: String,
    val note: String?
)
