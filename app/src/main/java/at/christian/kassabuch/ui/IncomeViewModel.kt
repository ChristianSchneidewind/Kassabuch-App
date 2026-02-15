package at.christian.kassabuch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.christian.kassabuch.data.IncomeEntity
import at.christian.kassabuch.data.IncomeRepository
import at.christian.kassabuch.data.IncomeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY)

class IncomeViewModel(private val repository: IncomeRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeIncomes().collect { incomes ->
                _uiState.update { state ->
                    state.copy(
                        items = incomes.map { income ->
                            IncomeListItem(
                                id = income.id,
                                category = income.category,
                                amount = currencyFormatter.format(income.amount),
                                date = income.date.format(dateFormatter),
                                typeLabel = if (income.type == IncomeType.ONE_TIME) {
                                    "Einmalig"
                                } else {
                                    "Wiederkehrend"
                                },
                                note = income.note
                            )
                        }
                    )
                }
            }
        }
    }

    fun addIncome(
        category: String,
        amount: Double,
        date: LocalDate,
        type: IncomeType,
        note: String?
    ) {
        viewModelScope.launch {
            repository.addIncome(
                IncomeEntity(
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

class IncomeViewModelFactory(private val repository: IncomeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IncomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IncomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class IncomeUiState(
    val items: List<IncomeListItem> = emptyList()
)

data class IncomeListItem(
    val id: Long,
    val category: String,
    val amount: String,
    val date: String,
    val typeLabel: String,
    val note: String?
)
