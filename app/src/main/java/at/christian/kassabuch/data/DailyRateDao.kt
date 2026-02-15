package at.christian.kassabuch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailyRateDao {
    @Query("SELECT * FROM daily_rate_history ORDER BY validFrom DESC")
    fun observeAll(): Flow<List<DailyRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rate: DailyRateEntity)

    @Update
    suspend fun update(rate: DailyRateEntity)

    @Query("SELECT * FROM daily_rate_history WHERE validTo IS NULL AND validFrom <= :validFrom ORDER BY validFrom DESC LIMIT 1")
    suspend fun findOpenRateBefore(validFrom: LocalDate): DailyRateEntity?
}
