package at.christian.kassabuch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_category")
data class ExpenseCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)
