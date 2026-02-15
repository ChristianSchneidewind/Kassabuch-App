package at.christian.kassabuch.data

import kotlinx.coroutines.flow.Flow

class IncomeRepository(private val incomeDao: IncomeDao) {
    fun observeIncomes(): Flow<List<IncomeEntity>> = incomeDao.observeAll()

    suspend fun addIncome(income: IncomeEntity) {
        incomeDao.insert(income)
    }
}
