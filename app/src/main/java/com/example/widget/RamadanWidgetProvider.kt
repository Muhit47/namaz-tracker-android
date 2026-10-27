package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.R
import com.example.util.PrayerTimeCalculator
import java.util.Calendar

class RamadanWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            updateAllWidgets(context)
        }
    }

    companion object {
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val isBangla = WidgetHelper.isBangla(context)
            val isHanafi = WidgetHelper.isHanafi(context)
            val city = WidgetHelper.getSelectedCity(context)

            val now = Calendar.getInstance()
            val times = PrayerTimeCalculator.calculateTimes(now, city, isHanafi)

            val views = RemoteViews(context.packageName, R.layout.widget_ramadan_layout)

            views.setTextViewText(
                R.id.widget_ramadan_title,
                if (isBangla) "মাহে রমজান ও রোজা টাইমার" else "Ramadan Fasting Timer"
            )
            views.setTextViewText(
                R.id.widget_ramadan_date,
                if (isBangla) "🌙 রমজান ১৪৪৭" else "🌙 Ramadan 1447"
            )

            views.setTextViewText(
                R.id.widget_label_sehri,
                if (isBangla) "🍽️ সেহরি শেষ" else "🍽️ Sehri Ends"
            )
            views.setTextViewText(
                R.id.widget_label_iftar,
                if (isBangla) "🥣 ইফতার টাইম" else "🥣 Iftar Time"
            )

            val sehriTime = if (isBangla) WidgetHelper.toBanglaDigits(times.fajr) else times.fajr
            val iftarTime = if (isBangla) WidgetHelper.toBanglaDigits(times.maghrib) else times.maghrib

            views.setTextViewText(R.id.widget_time_sehri, sehriTime)
            views.setTextViewText(R.id.widget_time_iftar, iftarTime)

            // Calculate countdown to Iftar (Maghrib)
            val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
            val (maghribHour, maghribMinute) = parseTimeTo24h(times.maghrib)
            val maghribTotalMinutes = maghribHour * 60 + maghribMinute

            val diff = maghribTotalMinutes - currentMinutes
            if (diff > 0) {
                views.setTextViewText(
                    R.id.widget_ramadan_target_label,
                    if (isBangla) "ইফতারের বাকি:" else "Iftar in:"
                )
                views.setTextViewText(
                    R.id.widget_ramadan_countdown,
                    WidgetHelper.formatCountdown(diff, isBangla)
                )
            } else {
                views.setTextViewText(
                    R.id.widget_ramadan_target_label,
                    if (isBangla) "আজকের ইফতার সম্পন্ন" else "Iftar Completed"
                )
                views.setTextViewText(
                    R.id.widget_ramadan_countdown,
                    if (isBangla) "আলহামদুলিল্লাহ" else "Alhamdulillah"
                )
            }

            // Tap open Dua / Ramadan section
            views.setOnClickPendingIntent(
                R.id.widget_ramadan_root,
                WidgetHelper.createOpenAppPendingIntent(context, "ibadah")
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun parseTimeTo24h(timeStr: String): Pair<Int, Int> {
            val isPm = timeStr.contains("PM", ignoreCase = true)
            val clean = timeStr.replace(" AM", "").replace(" PM", "").trim()
            val parts = clean.split(":")
            var hour = parts[0].toIntOrNull() ?: 18
            val minute = if (parts.size > 1) parts[1].toIntOrNull() ?: 0 else 0
            if (isPm && hour < 12) hour += 12
            if (!isPm && hour == 12) hour = 0
            return Pair(hour, minute)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, RamadanWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            for (id in ids) {
                updateWidget(context, appWidgetManager, id)
            }
        }
    }
}
