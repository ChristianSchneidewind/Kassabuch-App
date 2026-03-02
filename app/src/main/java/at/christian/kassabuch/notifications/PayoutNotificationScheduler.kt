package at.christian.kassabuch.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import at.christian.kassabuch.data.PayoutScheduleEntity
import java.time.LocalTime
import java.time.ZoneId

class PayoutNotificationScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminders(schedules: List<PayoutScheduleEntity>) {
        PayoutNotificationConfig.ensureChannel(context)
        val now = System.currentTimeMillis()
        schedules.forEach { schedule ->
            val requestCode = schedule.id.toInt()
            val intent = Intent(context, PayoutNotificationReceiver::class.java).apply {
                putExtra(PayoutNotificationConfig.EXTRA_PAYOUT_DATE, schedule.payoutDate.toEpochDay())
                putExtra(PayoutNotificationConfig.EXTRA_SCHEDULE_ID, schedule.id)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)

            val triggerDate = schedule.payoutDate.minusDays(2)
            val triggerMillis = triggerDate
                .atTime(LocalTime.of(9, 0))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            if (triggerMillis <= now) return@forEach

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }
    }
}
