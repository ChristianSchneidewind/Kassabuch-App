package at.christian.kassabuch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

@Dao
interface MonthlyBudgetDao {
    @Query("SELECT * FROM monthly_budget ORDER BY month DESC")
    fun observeAll(): Flow<List<MonthlyBudgetEntity>>

    @Query("SELECT * FROM monthly_budget WHERE month = :month LIMIT 1")
    suspend fun findByMonth(month: YearMonth): MonthlyBudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: MonthlyBudgetEntity)
}
