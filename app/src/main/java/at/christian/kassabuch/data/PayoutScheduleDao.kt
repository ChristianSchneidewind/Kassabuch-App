package at.christian.kassabuch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PayoutScheduleDao {
    @Query("SELECT * FROM payout_schedule ORDER BY month ASC")
    fun observeAll(): Flow<List<PayoutScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: PayoutScheduleEntity)

    @Update
    suspend fun update(schedule: PayoutScheduleEntity)
}
