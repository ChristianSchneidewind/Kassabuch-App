package at.christian.kassabuch.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "daily_rate_history")
data class DailyRateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rateAmount: Double,
    val validFrom: LocalDate,
    val validTo: LocalDate? = null
)
