package at.christian.kassabuch.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FixedExpenseRuleRepository(private val ruleDao: FixedExpenseRuleDao) {
    fun observeRules(): Flow<List<FixedExpenseRuleEntity>> = ruleDao.observeAll()

    suspend fun upsertRule(category: String, amount: Double, validFrom: LocalDate) {
        val openRule = ruleDao.findOpenRule(category, validFrom)
        if (openRule != null && openRule.validFrom.isBefore(validFrom)) {
            ruleDao.update(openRule.copy(validTo = validFrom.minusDays(1)))
        }
        ruleDao.insert(
            FixedExpenseRuleEntity(
                category = category,
                amount = amount,
                validFrom = validFrom,
                validTo = null
            )
        )
    }
}
