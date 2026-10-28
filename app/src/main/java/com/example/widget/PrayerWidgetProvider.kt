package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.R
import com.example.data.model.PrayerType
import com.example.util.CityLocation
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
            val isBangla = WidgetHelper.isBangla(context)
            val isHanafi = WidgetHelper.isHanafi(context)
            val city = WidgetHelper.getSelectedCity(context)

            val now = Calendar.getInstance()
            val times = PrayerTimeCalculator.calculateTimes(now, city, isHanafi)
            val activeWaqt = PrayerTimeCalculator.getActiveWaqtInfo(now, times)
            val currentActive = activeWaqt?.currentPrayer

            val views = RemoteViews(context.packageName, R.layout.widget_daily_prayer_layout)

            // Header info
            views.setTextViewText(
                R.id.widget_daily_title,
                if (isBangla) "৫ ওয়াক্ত নামাজের সময়সূচী" else "5 Daily Prayer Times"
            )
            views.setTextViewText(
                R.id.widget_daily_city,
                if (isBangla) "📍 ${city.nameBn}" else "📍 ${city.nameEn.split(" ")[0]}"
            )

            // Labels
            views.setTextViewText(R.id.widget_label_fajr, if (isBangla) "ফজর" else "Fajr")
            views.setTextViewText(R.id.widget_label_dhuhr, if (isBangla) "যোহর" else "Dhuhr")
            views.setTextViewText(R.id.widget_label_asr, if (isBangla) "আসর" else "Asr")
            views.setTextViewText(R.id.widget_label_maghrib, if (isBangla) "মাগরিব" else "Maghrib")
            views.setTextViewText(R.id.widget_label_isha, if (isBangla) "ইশা" else "Isha")

            // Times
            fun cleanTime(t: String): String {
                val base = t.replace(" AM", "").replace(" PM", "")
                return if (isBangla) WidgetHelper.toBanglaDigits(base) else base
            }

            views.setTextViewText(R.id.widget_time_fajr, cleanTime(times.fajr))
            views.setTextViewText(R.id.widget_time_dhuhr, cleanTime(times.dhuhr))
            views.setTextViewText(R.id.widget_time_asr, cleanTime(times.asr))
            views.setTextViewText(R.id.widget_time_maghrib, cleanTime(times.maghrib))
            views.setTextViewText(R.id.widget_time_isha, cleanTime(times.isha))

            // Highlight active column
            views.setInt(
                R.id.widget_col_fajr,
                "setBackgroundResource",
                if (currentActive == PrayerType.FAJR) R.drawable.widget_glass_pill_active else R.drawable.widget_glass_pill_item
            )
            views.setInt(
                R.id.widget_col_dhuhr,
                "setBackgroundResource",
                if (currentActive == PrayerType.DHUHR) R.drawable.widget_glass_pill_active else R.drawable.widget_glass_pill_item
            )
            views.setInt(
                R.id.widget_col_asr,
                "setBackgroundResource",
                if (currentActive == PrayerType.ASR) R.drawable.widget_glass_pill_active else R.drawable.widget_glass_pill_item
            )
            views.setInt(
                R.id.widget_col_maghrib,
                "setBackgroundResource",
                if (currentActive == PrayerType.MAGHRIB) R.drawable.widget_glass_pill_active else R.drawable.widget_glass_pill_item
            )
            views.setInt(
                R.id.widget_col_isha,
                "setBackgroundResource",
                if (currentActive == PrayerType.ISHA) R.drawable.widget_glass_pill_active else R.drawable.widget_glass_pill_item
            )

            // Intent to open app directly to Namaz tab
            views.setOnClickPendingIntent(
                R.id.widget_daily_root,
                WidgetHelper.createOpenAppPendingIntent(context, "namaz")
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, PrayerWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            for (id in ids) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }
}
