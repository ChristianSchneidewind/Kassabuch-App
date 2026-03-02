package at.christian.kassabuch.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import at.christian.kassabuch.R

object PayoutNotificationConfig {
    const val CHANNEL_ID = "payout_reminders"
    const val EXTRA_PAYOUT_DATE = "extra_payout_date"
    const val EXTRA_SCHEDULE_ID = "extra_schedule_id"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name = context.getString(R.string.notification_channel_name)
        val description = context.getString(R.string.notification_channel_description)
        val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
            this.description = description
        }
        manager.createNotificationChannel(channel)
    }
}
