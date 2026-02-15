package at.christian.kassabuch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_category")
data class IncomeCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)
