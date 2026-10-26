package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.data.model.PrayerType
import com.example.util.PrayerTimeCalculator
import java.util.*

object PrayerAlarmScheduler {

    fun scheduleAllPrayers(context: Context) {
        val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("prayer_notifications_enabled", true)
        if (!isEnabled) {
            cancelAllAlarms(context)
            return
        }

        val cityName = prefs.getString("selected_city_en", "Dhaka (ঢাকা)")
        val isHanafi = prefs.getBoolean("is_hanafi_asr", true)
        val city = PrayerTimeCalculator.predefinedCities.find {
            it.nameEn == cityName || it.nameEn.startsWith(cityName ?: "Dhaka")
        } ?: PrayerTimeCalculator.predefinedCities[0]

        val now = Calendar.getInstance()
        val times = PrayerTimeCalculator.calculateTimes(now, city, isHanafi)

        scheduleSinglePrayer(context, PrayerType.FAJR, times.fajr, 101)
        scheduleSinglePrayer(context, PrayerType.DHUHR, times.dhuhr, 102)
        scheduleSinglePrayer(context, PrayerType.ASR, times.asr, 103)
        scheduleSinglePrayer(context, PrayerType.MAGHRIB, times.maghrib, 104)
        scheduleSinglePrayer(context, PrayerType.ISHA, times.isha, 105)
    }

    private fun scheduleSinglePrayer(
        context: Context,
        prayer: PrayerType,
        timeStr: String,
        requestCode: Int
    ) {
        val minutes = PrayerTimeCalculator.parseTimeToMinutes(timeStr) ?: return
        val hour = minutes / 60
        val minute = minutes % 60

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If the time already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
            putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_KEY, prayer.keyName)
            putExtra(PrayerAlarmReceiver.EXTRA_TIME_STR, timeStr)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // In Android 12+ if exact alarm permission is not granted, fallback to normal set
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: Exception) {
            // Ignore other scheduling errors
        }
    }

    fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val requestCodes = listOf(101, 102, 103, 104, 105)
        for (code in requestCodes) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                code,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
