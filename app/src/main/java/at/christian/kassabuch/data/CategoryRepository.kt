package at.christian.kassabuch.data

import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val incomeDao: IncomeCategoryDao,
    private val expenseDao: ExpenseCategoryDao
) {
    fun observeIncomeCategories(): Flow<List<IncomeCategoryEntity>> = incomeDao.observeAll()
    fun observeExpenseCategories(): Flow<List<ExpenseCategoryEntity>> = expenseDao.observeAll()

    suspend fun addIncomeCategory(name: String) {
        incomeDao.insert(IncomeCategoryEntity(name = name))
    }

    suspend fun addExpenseCategory(name: String) {
        expenseDao.insert(ExpenseCategoryEntity(name = name))
    }

    suspend fun renameIncomeCategory(id: Long, name: String) {
        incomeDao.update(IncomeCategoryEntity(id = id, name = name))
    }

    suspend fun renameExpenseCategory(id: Long, name: String) {
        expenseDao.update(ExpenseCategoryEntity(id = id, name = name))
    }
}
