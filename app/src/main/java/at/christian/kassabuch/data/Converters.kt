package at.christian.kassabuch.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.YearMonth

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
    fun toYearMonth(value: String?): YearMonth? {
        return value?.let { YearMonth.parse(it) }
    }

    @TypeConverter
    fun fromYearMonth(value: YearMonth?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toIncomeType(value: String?): IncomeType? {
        return value?.let { IncomeType.valueOf(it) }
    }

    @TypeConverter
    fun fromIncomeType(type: IncomeType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toExpenseType(value: String?): ExpenseType? {
        return value?.let { ExpenseType.valueOf(it) }
    }

    @TypeConverter
    fun fromExpenseType(type: ExpenseType?): String? {
        return type?.name
    }
}
