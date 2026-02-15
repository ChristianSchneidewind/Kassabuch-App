package at.christian.kassabuch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.christian.kassabuch.data.DailyRateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dailyRateFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY)
private val dailyRateDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

class DailyRateViewModel(private val repository: DailyRateRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(DailyRateUiState())
    val uiState: StateFlow<DailyRateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeRates().collect { rates ->
                val current = rates.maxByOrNull { it.validFrom }
                _uiState.update {
                    it.copy(
                        currentRate = current?.let { rate -> dailyRateFormatter.format(rate.rateAmount) }
                            ?: "—",
                        currentValidFrom = current?.validFrom?.format(dailyRateDateFormatter)
                    )
                }
            }
        }
    }

    fun addRate(rateAmount: Double, validFrom: java.time.LocalDate) {
        viewModelScope.launch {
            repository.addRate(rateAmount, validFrom)
        }
    }
}

class DailyRateViewModelFactory(private val repository: DailyRateRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyRateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyRateViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class DailyRateUiState(
    val currentRate: String = "—",
    val currentValidFrom: String? = null
)
