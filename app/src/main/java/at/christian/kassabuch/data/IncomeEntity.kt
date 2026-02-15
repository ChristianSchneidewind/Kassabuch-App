package at.christian.kassabuch.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "income")
data class IncomeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val amount: Double,
    val date: LocalDate,
    val type: IncomeType,
    val note: String? = null
)

enum class IncomeType {
    ONE_TIME,
    RECURRING
}
