package com.example.notification

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.PrayerType

class AzanPlaybackService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action ?: return START_NOT_STICKY

        when (action) {
            ACTION_PLAY_AZAN -> {
                val prayerKey = intent.getStringExtra(EXTRA_PRAYER_KEY) ?: "fajr"
                val timeStr = intent.getStringExtra(EXTRA_TIME_STR) ?: ""
                val muezzin = intent.getStringExtra(EXTRA_MUEZZIN) ?: "makkah"
                val isBangla = intent.getBooleanExtra(EXTRA_IS_BANGLA, true)
                playAzan(prayerKey, timeStr, muezzin, isBangla)
            }
            ACTION_STOP_AZAN -> {
                stopPlayback()
            }
        }

        return START_NOT_STICKY
    }

    private fun playAzan(prayerKey: String, timeStr: String, muezzin: String, isBangla: Boolean) {
        stopCurrentPlayer()

        val rawRes = when (muezzin.lowercase()) {
            "madinah" -> R.raw.azan_madinah
            "takbeer" -> R.raw.takbeer
            else -> R.raw.azan_makkah
        }

        val prayerType = PrayerType.fromKey(prayerKey) ?: PrayerType.FAJR
        val prayerName = if (isBangla) prayerType.banglaName else prayerType.englishName

        val title = if (isBangla) {
            "🕌 ${prayerName}ের ওয়াক্ত হয়েছে ($timeStr)"
        } else {
            "🕌 $prayerName Prayer Time ($timeStr)"
        }

        val bodyText = if (isBangla) {
            "🔊 সুমধুর আজান বাজছে... নামাযের প্রস্তুতি গ্রহণ করুন।"
        } else {
            "🔊 Azan is playing... Prepare for Salah."
        }

        PrayerNotificationHelper.createNotificationChannel(this)

        // PendingIntent to launch app
        val contentIntent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            prayerType.ordinal,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // PendingIntent to STOP Azan audio
        val stopIntent = Intent(this, AzanPlaybackService::class.java).apply {
            this.action = ACTION_STOP_AZAN
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            9988,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopLabel = if (isBangla) "⏹️ আজান বন্ধ করুন (Stop)" else "⏹️ Stop Azan"

        val notification: Notification = NotificationCompat.Builder(this, PrayerNotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(bodyText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bodyText))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(contentPendingIntent)
            .addAction(android.R.drawable.ic_media_pause, stopLabel, stopPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        try {
            mediaPlayer = MediaPlayer.create(this, rawRes).apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setOnCompletionListener {
                    Log.d(TAG, "Azan audio playback finished.")
                    isAzanPlaying = false
                    stopForeground(STOP_FOREGROUND_DETACH)
                    stopSelf()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    isAzanPlaying = false
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                    true
                }
                start()
                isAzanPlaying = true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start Azan audio", e)
            isAzanPlaying = false
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun stopPlayback() {
        stopCurrentPlayer()
        isAzanPlaying = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun stopCurrentPlayer() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping media player", e)
        } finally {
            mediaPlayer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopCurrentPlayer()
        isAzanPlaying = false
    }

    companion object {
        private const val TAG = "AzanPlaybackService"
        const val NOTIFICATION_ID = 8881

        const val ACTION_PLAY_AZAN = "com.example.notification.ACTION_PLAY_AZAN"
        const val ACTION_STOP_AZAN = "com.example.notification.ACTION_STOP_AZAN"

        const val EXTRA_PRAYER_KEY = "extra_prayer_key"
        const val EXTRA_TIME_STR = "extra_time_str"
        const val EXTRA_MUEZZIN = "extra_muezzin"
        const val EXTRA_IS_BANGLA = "extra_is_bangla"

        var isAzanPlaying = false
            private set

        fun startAzan(
            context: Context,
            prayerKey: String,
            timeStr: String,
            muezzin: String = "makkah",
            isBangla: Boolean = true
        ) {
            val intent = Intent(context, AzanPlaybackService::class.java).apply {
                action = ACTION_PLAY_AZAN
                putExtra(EXTRA_PRAYER_KEY, prayerKey)
                putExtra(EXTRA_TIME_STR, timeStr)
                putExtra(EXTRA_MUEZZIN, muezzin)
                putExtra(EXTRA_IS_BANGLA, isBangla)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopAzan(context: Context) {
            val intent = Intent(context, AzanPlaybackService::class.java).apply {
                action = ACTION_STOP_AZAN
            }
            context.startService(intent)
        }
    }
}
