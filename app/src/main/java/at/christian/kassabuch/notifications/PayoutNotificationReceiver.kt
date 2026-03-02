package at.christian.kassabuch.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import at.christian.kassabuch.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PayoutNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        PayoutNotificationConfig.ensureChannel(context)

        val payoutEpoch = intent.getLongExtra(PayoutNotificationConfig.EXTRA_PAYOUT_DATE, -1)
        if (payoutEpoch == -1L) return
        val payoutDate = LocalDate.ofEpochDay(payoutEpoch)
        val formattedDate = payoutDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        val title = context.getString(R.string.notification_payout_title)
        val text = context.getString(R.string.notification_payout_text, formattedDate)
        val notificationId = intent.getLongExtra(PayoutNotificationConfig.EXTRA_SCHEDULE_ID, payoutEpoch).toInt()

        val notification = NotificationCompat.Builder(context, PayoutNotificationConfig.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
