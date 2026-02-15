package at.christian.kassabuch.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class DailyRateRepository(private val dailyRateDao: DailyRateDao) {
    fun observeRates(): Flow<List<DailyRateEntity>> = dailyRateDao.observeAll()

    suspend fun addRate(rateAmount: Double, validFrom: LocalDate) {
        val openRate = dailyRateDao.findOpenRateBefore(validFrom)
        if (openRate != null && openRate.validFrom.isBefore(validFrom)) {
            dailyRateDao.update(openRate.copy(validTo = validFrom.minusDays(1)))
        }
        dailyRateDao.insert(
            DailyRateEntity(
                rateAmount = rateAmount,
                validFrom = validFrom,
                validTo = null
            )
        )
    }
}
