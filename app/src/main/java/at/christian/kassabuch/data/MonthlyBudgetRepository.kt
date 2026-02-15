package at.christian.kassabuch.data

import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

class MonthlyBudgetRepository(private val budgetDao: MonthlyBudgetDao) {
    fun observeBudgets(): Flow<List<MonthlyBudgetEntity>> = budgetDao.observeAll()

    suspend fun setBudget(month: YearMonth, amount: Double) {
        val existing = budgetDao.findByMonth(month)
        val entity = MonthlyBudgetEntity(
            id = existing?.id ?: 0,
            month = month,
            amount = amount
        )
        budgetDao.insert(entity)
    }
}
