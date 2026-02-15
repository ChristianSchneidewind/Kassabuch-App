package at.christian.kassabuch.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.YearMonth

@Entity(tableName = "payout_schedule")
data class PayoutScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val month: YearMonth,
    val payoutDate: LocalDate
)
