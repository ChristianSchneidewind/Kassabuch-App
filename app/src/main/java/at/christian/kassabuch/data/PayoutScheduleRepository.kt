package at.christian.kassabuch.data

import kotlinx.coroutines.flow.Flow

class PayoutScheduleRepository(private val payoutScheduleDao: PayoutScheduleDao) {
    fun observeSchedules(): Flow<List<PayoutScheduleEntity>> = payoutScheduleDao.observeAll()

    suspend fun updateSchedule(schedule: PayoutScheduleEntity) {
        payoutScheduleDao.update(schedule)
    }

    suspend fun upsertSchedule(schedule: PayoutScheduleEntity) {
        payoutScheduleDao.insert(schedule)
    }
}
