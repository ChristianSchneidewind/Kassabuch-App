package at.christian.kassabuch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.christian.kassabuch.data.PayoutScheduleEntity
import at.christian.kassabuch.data.PayoutScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val payoutDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val payoutMonthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.GERMANY)

class PayoutScheduleViewModel(private val repository: PayoutScheduleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PayoutScheduleUiState())
    val uiState: StateFlow<PayoutScheduleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeSchedules().collect { schedules ->
                _uiState.update {
                    it.copy(
                        items = schedules.map { schedule ->
                            PayoutScheduleItem(
                                id = schedule.id,
                                month = schedule.month.format(payoutMonthFormatter),
                                rawMonth = schedule.month,
                                payoutDate = schedule.payoutDate.format(payoutDateFormatter)
                            )
                        }
                    )
                }
            }
        }
    }

    fun updateSchedule(id: Long, month: YearMonth, payoutDate: LocalDate) {
        viewModelScope.launch {
            repository.upsertSchedule(
                PayoutScheduleEntity(
                    id = id,
                    month = month,
                    payoutDate = payoutDate
                )
            )
        }
    }
}

class PayoutScheduleViewModelFactory(private val repository: PayoutScheduleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PayoutScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PayoutScheduleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class PayoutScheduleUiState(
    val items: List<PayoutScheduleItem> = emptyList()
)

data class PayoutScheduleItem(
    val id: Long,
    val month: String,
    val rawMonth: YearMonth,
    val payoutDate: String
)
