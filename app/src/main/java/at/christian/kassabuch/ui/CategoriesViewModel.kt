package at.christian.kassabuch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import at.christian.kassabuch.data.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel(private val repository: CategoryRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeIncomeCategories().collect { categories ->
                _uiState.update { state ->
                    state.copy(
                        income = categories.map { CategoryItem(id = it.id, name = it.name) }
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.observeExpenseCategories().collect { categories ->
                _uiState.update { state ->
                    state.copy(
                        expenses = categories.map { CategoryItem(id = it.id, name = it.name) }
                    )
                }
            }
        }
    }

    fun addIncomeCategory(name: String) {
        viewModelScope.launch { repository.addIncomeCategory(name) }
    }

    fun addExpenseCategory(name: String) {
        viewModelScope.launch { repository.addExpenseCategory(name) }
    }

    fun renameIncomeCategory(id: Long, name: String) {
        viewModelScope.launch { repository.renameIncomeCategory(id, name) }
    }

    fun renameExpenseCategory(id: Long, name: String) {
        viewModelScope.launch { repository.renameExpenseCategory(id, name) }
    }
}

class CategoriesViewModelFactory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class CategoriesUiState(
    val income: List<CategoryItem> = emptyList(),
    val expenses: List<CategoryItem> = emptyList()
)

data class CategoryItem(
    val id: Long,
    val name: String
)
