package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerKey = intent.getStringExtra(EXTRA_PRAYER_KEY) ?: return
        val timeStr = intent.getStringExtra(EXTRA_TIME_STR) ?: ""

        val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("prayer_notifications_enabled", true)
        val isBangla = prefs.getBoolean("is_bangla", false)

        if (notificationsEnabled) {
            PrayerNotificationHelper.showPrayerNotification(
                context = context,
                prayerKey = prayerKey,
                timeStr = timeStr,
                isBangla = isBangla
            )
            // Reschedule upcoming alarms
            PrayerAlarmScheduler.scheduleAllPrayers(context)
        }
    }

    companion object {
        const val EXTRA_PRAYER_KEY = "extra_prayer_key"
        const val EXTRA_TIME_STR = "extra_time_str"
    }
}
