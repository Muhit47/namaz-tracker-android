package com.example.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.PrayerType
import com.example.util.PrayerTimeCalculator
import java.util.Calendar

object PrayerNotificationHelper {
    const val CHANNEL_ID = "namaz_waqt_channel_v1"
    private const val CHANNEL_NAME = "ওয়াক্তের নোটিফিকেশন (Prayer Times)"
    private const val CHANNEL_DESC = "ওয়াক্ত শুরু হওয়ার সঠিক সময়ে আজান ও স্মরণিকা নোটিফিকেশন"

    const val CHANNEL_ONGOING_ID = "namaz_ongoing_waqt_channel"
    private const val CHANNEL_ONGOING_NAME = "চলমান ওয়াক্ত ট্র্যাকার (Ongoing Waqt)"
    private const val CHANNEL_ONGOING_DESC = "লক-স্ক্রিন ও স্ট্যাটাস বারে চলমান ওয়াক্ত এবং অবশিষ্ট সময়ের লাইভ কাউন্টডাউন"

    const val ONGOING_NOTIFICATION_ID = 9001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Primary Waqt Channel
            val existing = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESC
                    enableLights(true)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 200, 500)
                    setSound(soundUri, null)
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Ongoing Silent Waqt Channel
            val ongoingExisting = notificationManager.getNotificationChannel(CHANNEL_ONGOING_ID)
            if (ongoingExisting == null) {
                val ongoingChannel = NotificationChannel(
                    CHANNEL_ONGOING_ID,
                    CHANNEL_ONGOING_NAME,
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = CHANNEL_ONGOING_DESC
                    enableLights(false)
                    enableVibration(false)
                    setSound(null, null)
                }
                notificationManager.createNotificationChannel(ongoingChannel)
            }
        }
    }

    /**
     * Dispatches notification with user's selected Sound Mode (Full Azan, Short Takbeer, System Tone, Silent).
     */
    fun showPrayerNotification(
        context: Context,
        prayerKey: String,
        timeStr: String,
        isBangla: Boolean = false
    ) {
        createNotificationChannel(context)

        val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
        val globalSoundMode = prefs.getString("notification_sound_mode", "full_azan") ?: "full_azan"
        val muezzin = prefs.getString("selected_muezzin", "makkah") ?: "makkah"
        val overrideMode = prefs.getString("override_$prayerKey", null)
        val activeMode = overrideMode ?: globalSoundMode

        when (activeMode) {
            "full_azan" -> {
                // Play authentic offline Azan via Foreground Service with Stop button
                AzanPlaybackService.startAzan(
                    context = context,
                    prayerKey = prayerKey,
                    timeStr = timeStr,
                    muezzin = muezzin,
                    isBangla = isBangla
                )
            }
            "short_takbeer" -> {
                // Play short 5s Takbeer audio via service and auto-dismiss
                AzanPlaybackService.startAzan(
                    context = context,
                    prayerKey = prayerKey,
                    timeStr = timeStr,
                    muezzin = "takbeer",
                    isBangla = isBangla
                )
            }
            "system_tone" -> {
                // Device default notification ringtone
                showStandardNotification(context, prayerKey, timeStr, isBangla, silent = false)
            }
            "silent" -> {
                // Silent banner with vibration only
                showStandardNotification(context, prayerKey, timeStr, isBangla, silent = true)
            }
            else -> {
                AzanPlaybackService.startAzan(context, prayerKey, timeStr, muezzin, isBangla)
            }
        }

        // Update lockscreen ongoing tracker if enabled
        val isOngoingEnabled = prefs.getBoolean("lockscreen_tracker_enabled", false)
        if (isOngoingEnabled) {
            updateOngoingTrackerNotification(context, isBangla)
        }
    }

    private fun showStandardNotification(
        context: Context,
        prayerKey: String,
        timeStr: String,
        isBangla: Boolean,
        silent: Boolean
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val prayerType = PrayerType.fromKey(prayerKey) ?: PrayerType.FAJR
        val prayerName = if (isBangla) prayerType.banglaName else prayerType.englishName

        val title = if (isBangla) {
            "🕌 ${prayerName}ের ওয়াক্ত শুরু হয়েছে ($timeStr)"
        } else {
            "🕌 $prayerName Prayer Time ($timeStr)"
        }

        val motivationalText = when (prayerType) {
            PrayerType.FAJR -> if (isBangla) "আস-সালাতু খাইরুম মিনান নাওম। এখনই ফজরের নামাজ আদায় করুন।" else "Prayer is better than sleep. Rise and pray Fajr."
            PrayerType.DHUHR -> if (isBangla) "যোহরের ওয়াক্ত হয়েছে। দুনিয়ার ব্যস্ততা থামিয়ে রবের সমীপে সেজদা দিন।" else "It is Dhuhr time. Take a break to connect with Allah."
            PrayerType.ASR -> if (isBangla) "আসরের ওয়াক্ত সমাগত। বরকতময় সময়ে জামাতে নামাজ আদায় করুন।" else "Asr prayer time has arrived. Guard your late afternoon prayer."
            PrayerType.MAGHRIB -> if (isBangla) "সূর্যাস্ত হয়েছে, মাগরিবের ওয়াক্ত শুরু। দেরি না করে নামাজ আদায় করুন।" else "Sunset has arrived, Maghrib prayer is ready."
            PrayerType.ISHA -> if (isBangla) "ইশার ওয়াক্ত শুরু হয়েছে। দিনশেষে শান্তিময় নামাজে নিজেকে সমর্পণ করুন।" else "Isha prayer time is in. Conclude your day in worship."
            else -> if (isBangla) "নামাযের ওয়াক্ত হয়েছে। যথাসময়ে নামায আদায় করুন।" else "It is time for prayer. Pray on time."
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerType.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val soundUri = if (silent) null else RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(motivationalText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(motivationalText))
            .setPriority(if (silent) NotificationCompat.PRIORITY_DEFAULT else NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .apply {
                if (soundUri != null) {
                    setSound(soundUri)
                } else {
                    setSilent(true)
                }
            }
            .setVibrate(if (silent) longArrayOf(0, 300) else longArrayOf(0, 500, 200, 500))
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(prayerType.ordinal + 100, notification)
    }

    /**
     * Updates ongoing persistent notification on the lockscreen & status bar with active Waqt countdown.
     */
    fun updateOngoingTrackerNotification(context: Context, isBangla: Boolean = true) {
        createNotificationChannel(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
        val isOngoingEnabled = prefs.getBoolean("lockscreen_tracker_enabled", false)
        if (!isOngoingEnabled) {
            cancelOngoingTrackerNotification(context)
            return
        }

        val cityName = prefs.getString("selected_city_en", "Dhaka (ঢাকা)")
        val isHanafi = prefs.getBoolean("is_hanafi_asr", true)
        val city = PrayerTimeCalculator.predefinedCities.find {
            it.nameEn == cityName || it.nameEn.startsWith(cityName ?: "Dhaka")
        } ?: PrayerTimeCalculator.predefinedCities[0]

        val now = Calendar.getInstance()
        val times = PrayerTimeCalculator.calculateTimes(now, city, isHanafi)
        val activeInfo = PrayerTimeCalculator.getActiveWaqtInfo(now, times)

        val activeName = if (activeInfo != null) {
            if (isBangla) activeInfo.currentPrayer.banglaName else activeInfo.currentPrayer.englishName
        } else {
            if (isBangla) "ফজর" else "Fajr"
        }

        val remainingMinutes = activeInfo?.minutesRemaining ?: 0
        val remainingFormatted = PrayerTimeCalculator.formatRemainingTime(remainingMinutes, isBangla)

        val nextName = if (activeInfo != null) {
            if (isBangla) activeInfo.nextPrayer.banglaName else activeInfo.nextPrayer.englishName
        } else {
            if (isBangla) "যোহর" else "Dhuhr"
        }

        val title = if (isBangla) {
            "🕌 $activeName ওয়াক্ত চলমান"
        } else {
            "🕌 $activeName Waqt is Ongoing"
        }

        val message = if (isBangla) {
            "$nextName শুরু হতে বাকি $remainingFormatted"
        } else {
            "$nextName starts in $remainingFormatted"
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            ONGOING_NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(context, CHANNEL_ONGOING_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setSubText(if (isBangla) "চলমান ট্র্যাকার" else "Waqt Tracker")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(ONGOING_NOTIFICATION_ID, notification)
    }

    fun cancelOngoingTrackerNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ONGOING_NOTIFICATION_ID)
    }

    fun showTestNotification(context: Context, isBangla: Boolean = false) {
        val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
        val soundMode = prefs.getString("notification_sound_mode", "full_azan") ?: "full_azan"
        val muezzin = prefs.getString("selected_muezzin", "makkah") ?: "makkah"

        if (soundMode == "full_azan" || soundMode == "short_takbeer") {
            // Test actual Azan audio and display the notification with Stop action button
            AzanPlaybackService.startAzan(
                context = context,
                prayerKey = "dhuhr",
                timeStr = "01:15 PM",
                muezzin = if (soundMode == "short_takbeer") "takbeer" else muezzin,
                isBangla = isBangla
            )
        } else {
            // Standard test notification
            createNotificationChannel(context)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val title = if (isBangla) "🕌 নামায ট্র্যাকার নোটিফিকেশন টেস্ট" else "🕌 Namaz Tracker Notification Test"
            val message = if (isBangla) {
                "আলহামদুলিল্লাহ! ওয়াক্তের নোটিফিকেশন সার্ভিস সঠিকভাবে কাজ করছে।"
            } else {
                "Alhamdulillah! Prayer time push notification service is working properly."
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                999,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val soundUri = if (soundMode == "silent") null else RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .apply {
                    if (soundUri != null) setSound(soundUri) else setSilent(true)
                }
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(999, notification)
        }
    }
}
