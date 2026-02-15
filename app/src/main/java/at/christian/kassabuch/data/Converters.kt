package at.christian.kassabuch.data

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toIncomeType(value: String?): IncomeType? {
        return value?.let { IncomeType.valueOf(it) }
    }

    @TypeConverter
    fun fromIncomeType(type: IncomeType?): String? {
        return type?.name
    }
}
