package at.christian.kassabuch.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class SeedingRunner(
    private val payoutScheduleDao: PayoutScheduleDao,
    private val incomeCategoryDao: IncomeCategoryDao,
    private val expenseCategoryDao: ExpenseCategoryDao
) {
    suspend fun seedIfEmpty() {
        withContext(Dispatchers.IO) {
            val existing = payoutScheduleDao.observeAll().firstOrNull().orEmpty()
            if (existing.isEmpty()) {
                PayoutSeeder.defaultSchedules.forEach { schedule ->
                    payoutScheduleDao.insert(schedule)
                }
            }

            val incomeExisting = incomeCategoryDao.observeAll().firstOrNull().orEmpty()
            if (incomeExisting.isEmpty()) {
                CategorySeeder.incomeDefaults.forEach { name ->
                    incomeCategoryDao.insert(IncomeCategoryEntity(name = name))
                }
            }

            val expenseExisting = expenseCategoryDao.observeAll().firstOrNull().orEmpty()
            if (expenseExisting.isEmpty()) {
                CategorySeeder.expenseDefaults.forEach { name ->
                    expenseCategoryDao.insert(ExpenseCategoryEntity(name = name))
                }
            }
        }
    }
}
