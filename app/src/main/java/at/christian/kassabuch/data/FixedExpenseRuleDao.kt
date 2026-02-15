package at.christian.kassabuch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface FixedExpenseRuleDao {
    @Query("SELECT * FROM fixed_expense_rule ORDER BY validFrom DESC")
    fun observeAll(): Flow<List<FixedExpenseRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: FixedExpenseRuleEntity)

    @Update
    suspend fun update(rule: FixedExpenseRuleEntity)

    @Query("SELECT * FROM fixed_expense_rule WHERE category = :category AND validTo IS NULL AND validFrom <= :validFrom ORDER BY validFrom DESC LIMIT 1")
    suspend fun findOpenRule(category: String, validFrom: LocalDate): FixedExpenseRuleEntity?
}
