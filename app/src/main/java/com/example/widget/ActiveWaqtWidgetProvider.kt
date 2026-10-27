package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.R
import com.example.data.model.PrayerType
import com.example.util.PrayerTimeCalculator
import java.util.Calendar

class ActiveWaqtWidgetProvider : AppWidgetProvider() {

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
            val activeWaqt = PrayerTimeCalculator.getActiveWaqtInfo(now, times)

            val views = RemoteViews(context.packageName, R.layout.widget_active_waqt_layout)

            val currentPrayer = activeWaqt?.currentPrayer ?: PrayerType.FAJR
            val currentName = if (isBangla) "${currentPrayer.banglaName} (${currentPrayer.englishName})" else currentPrayer.englishName
            val currentTime = when (currentPrayer) {
                PrayerType.FAJR -> times.fajr
                PrayerType.DHUHR -> times.dhuhr
                PrayerType.ASR -> times.asr
                PrayerType.MAGHRIB -> times.maghrib
                PrayerType.ISHA -> times.isha
                else -> times.fajr
            }

            views.setTextViewText(R.id.widget_active_waqt_name, currentName)
            views.setTextViewText(
                R.id.widget_active_waqt_status,
                if (isBangla) "ওয়াক্ত চলছে • শেষ হতে বাকি" else "Active Waqt • Time remaining"
            )

            val displayTime = if (isBangla) WidgetHelper.toBanglaDigits(currentTime) else currentTime
            views.setTextViewText(R.id.widget_active_waqt_time, displayTime)

            val minutesRemaining = activeWaqt?.minutesRemaining ?: 45
            views.setTextViewText(
                R.id.widget_active_countdown,
                WidgetHelper.formatCountdown(minutesRemaining, isBangla)
            )

            // Tap open Namaz tab
            views.setOnClickPendingIntent(
                R.id.widget_active_root,
                WidgetHelper.createOpenAppPendingIntent(context, "namaz")
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, ActiveWaqtWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            for (id in ids) {
                updateWidget(context, appWidgetManager, id)
            }
        }
    }
}
