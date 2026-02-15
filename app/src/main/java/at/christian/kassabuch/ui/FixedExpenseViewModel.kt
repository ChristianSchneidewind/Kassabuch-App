package at.christian.kassabuch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.christian.kassabuch.data.FixedExpenseRuleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

private val fixedAmountFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
private val fixedDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

class FixedExpenseViewModel(private val repository: FixedExpenseRuleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FixedExpenseUiState())
    val uiState: StateFlow<FixedExpenseUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeRules().collect { rules ->
                _uiState.update { state ->
                    state.copy(
                        items = rules.map { rule ->
                            FixedExpenseItem(
                                id = rule.id,
                                category = rule.category,
                                amount = fixedAmountFormatter.format(rule.amount),
                                validFrom = rule.validFrom.format(fixedDateFormatter),
                                validTo = rule.validTo?.format(fixedDateFormatter)
                            )
                        }
                    )
                }
            }
        }
    }

    fun updateRule(category: String, amount: Double, validFrom: java.time.LocalDate) {
        viewModelScope.launch {
            repository.upsertRule(category, amount, validFrom)
        }
    }
}

class FixedExpenseViewModelFactory(private val repository: FixedExpenseRuleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FixedExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FixedExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class FixedExpenseUiState(
    val items: List<FixedExpenseItem> = emptyList()
)

data class FixedExpenseItem(
    val id: Long,
    val category: String,
    val amount: String,
    val validFrom: String,
    val validTo: String?
)
