package at.christian.kassabuch.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "expense")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val amount: Double,
    val date: LocalDate,
    val type: ExpenseType,
    val note: String? = null
)

enum class ExpenseType {
    FIXED,
    VARIABLE
}
