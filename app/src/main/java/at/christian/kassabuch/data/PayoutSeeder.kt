package at.christian.kassabuch.data

import java.time.LocalDate
import java.time.YearMonth

object PayoutSeeder {
    val defaultSchedules: List<PayoutScheduleEntity> = listOf(
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 2),
            payoutDate = LocalDate.of(2026, 3, 3)
        ),
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 3),
            payoutDate = LocalDate.of(2026, 4, 3)
        ),
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 4),
            payoutDate = LocalDate.of(2026, 5, 6)
        ),
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 5),
            payoutDate = LocalDate.of(2026, 6, 3)
        ),
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 6),
            payoutDate = LocalDate.of(2026, 7, 3)
        ),
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 7),
            payoutDate = LocalDate.of(2026, 8, 5)
        ),
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 8),
            payoutDate = LocalDate.of(2026, 9, 4)
        ),
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 9),
            payoutDate = LocalDate.of(2026, 10, 6)
        ),
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 10),
            payoutDate = LocalDate.of(2026, 11, 5)
        ),
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 11),
            payoutDate = LocalDate.of(2026, 12, 3)
        ),
        PayoutScheduleEntity(
            month = YearMonth.of(2026, 12),
            payoutDate = LocalDate.of(2027, 1, 5)
        )
    )
}
