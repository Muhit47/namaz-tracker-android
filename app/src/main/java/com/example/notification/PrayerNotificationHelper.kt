package com.example.notification

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

object PrayerNotificationHelper {
    const val CHANNEL_ID = "namaz_waqt_channel_v1"
    private const val CHANNEL_NAME = "ওয়াক্তের নোটিফিকেশন (Prayer Times)"
    private const val CHANNEL_DESC = "ওয়াক্ত শুরু হওয়ার সঠিক সময়ে আজান ও স্মরণিকা নোটিফিকেশন"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        }
    }

    fun showPrayerNotification(
        context: Context,
        prayerKey: String,
        timeStr: String,
        isBangla: Boolean = true
    ) {
        createNotificationChannel(context)
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

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(motivationalText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(motivationalText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(prayerType.ordinal + 100, notification)
    }

    fun showTestNotification(context: Context, isBangla: Boolean = true) {
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

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(999, notification)
    }
}
