package at.christian.kassabuch.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    fun observeExpenses(): Flow<List<ExpenseEntity>> = expenseDao.observeAll()

    suspend fun addExpense(expense: ExpenseEntity) {
        expenseDao.insert(expense)
    }
}
