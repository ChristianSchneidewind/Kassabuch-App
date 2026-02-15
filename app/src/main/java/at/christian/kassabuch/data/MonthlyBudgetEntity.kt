package at.christian.kassabuch.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.YearMonth

@Entity(tableName = "monthly_budget")
data class MonthlyBudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val month: YearMonth,
    val amount: Double
)
