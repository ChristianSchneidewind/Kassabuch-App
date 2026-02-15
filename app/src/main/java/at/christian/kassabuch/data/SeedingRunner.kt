package at.christian.kassabuch.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class SeedingRunner(private val payoutScheduleDao: PayoutScheduleDao) {
    suspend fun seedIfEmpty() {
        withContext(Dispatchers.IO) {
            val existing = payoutScheduleDao.observeAll().firstOrNull().orEmpty()
            if (existing.isNotEmpty()) {
                return@withContext
            }
            PayoutSeeder.defaultSchedules.forEach { schedule ->
                payoutScheduleDao.insert(schedule)
            }
        }
    }
}
