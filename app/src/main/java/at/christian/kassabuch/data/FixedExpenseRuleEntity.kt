package at.christian.kassabuch.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "fixed_expense_rule")
data class FixedExpenseRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,
    val amount: Double,
    val validFrom: LocalDate,
    val validTo: LocalDate? = null
)
