package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.model.PrayerType
import com.example.util.PrayerTimeCalculator
import java.util.Calendar

class PrayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            updateAllWidgets(context)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
            val isBangla = prefs.getBoolean("is_bangla", true)
            val isHanafi = prefs.getBoolean("is_hanafi_asr", true)
            val cityName = prefs.getString("selected_city_en", "Dhaka (ঢাকা)")

            val city = PrayerTimeCalculator.predefinedCities.find {
                it.nameEn == cityName || it.nameEn.startsWith(cityName ?: "Dhaka")
            } ?: PrayerTimeCalculator.predefinedCities[0]

            val now = Calendar.getInstance()
            val times = PrayerTimeCalculator.calculateTimes(now, city, isHanafi)
            val activeWaqt = PrayerTimeCalculator.getActiveWaqtInfo(now, times)

            val views = RemoteViews(context.packageName, R.layout.prayer_widget_layout)

            // Header info
            views.setTextViewText(R.id.widget_app_title, if (isBangla) "🕌 নামায ট্র্যাকার" else "🕌 Namaz Tracker")
            views.setTextViewText(R.id.widget_city_name, if (isBangla) city.nameBn else city.nameEn.split(" ")[0])

            // Next Prayer Banner
            val nextPrayer = activeWaqt?.nextPrayer ?: PrayerType.FAJR
            val nextPrayerName = if (isBangla) nextPrayer.banglaName else nextPrayer.englishName
            val nextPrayerTime = when (nextPrayer) {
                PrayerType.FAJR -> times.fajr
                PrayerType.DHUHR -> times.dhuhr
                PrayerType.ASR -> times.asr
                PrayerType.MAGHRIB -> times.maghrib
                PrayerType.ISHA -> times.isha
                else -> times.fajr
            }

            views.setTextViewText(
                R.id.widget_next_prayer_title,
                if (isBangla) "পরবর্তী ওয়াক্ত: $nextPrayerName" else "Next Waqt: $nextPrayerName"
            )
            views.setTextViewText(R.id.widget_next_prayer_time, nextPrayerTime)

            // Labels
            views.setTextViewText(R.id.widget_fajr_label, if (isBangla) "ফজর" else "Fajr")
            views.setTextViewText(R.id.widget_dhuhr_label, if (isBangla) "যোহর" else "Dhuhr")
            views.setTextViewText(R.id.widget_asr_label, if (isBangla) "আসর" else "Asr")
            views.setTextViewText(R.id.widget_maghrib_label, if (isBangla) "মাগরিব" else "Maghrib")
            views.setTextViewText(R.id.widget_isha_label, if (isBangla) "ইশা" else "Isha")

            // Times (split to remove AM/PM for compact row if desired, or show standard)
            views.setTextViewText(R.id.widget_fajr_time, times.fajr.replace(" AM", "").replace(" PM", ""))
            views.setTextViewText(R.id.widget_dhuhr_time, times.dhuhr.replace(" AM", "").replace(" PM", ""))
            views.setTextViewText(R.id.widget_asr_time, times.asr.replace(" AM", "").replace(" PM", ""))
            views.setTextViewText(R.id.widget_maghrib_time, times.maghrib.replace(" AM", "").replace(" PM", ""))
            views.setTextViewText(R.id.widget_isha_time, times.isha.replace(" AM", "").replace(" PM", ""))

            // Intent to open app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, PrayerWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (id in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }
}
