package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.example.MainActivity
import com.example.data.model.CityLocation
import com.example.util.PrayerTimeCalculator
import java.util.Calendar

object WidgetHelper {

    fun getSelectedCity(context: Context): CityLocation {
        val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
        val cityName = prefs.getString("selected_city_en", "Dhaka (ঢাকা)")
        return PrayerTimeCalculator.predefinedCities.find {
            it.nameEn == cityName || it.nameEn.startsWith(cityName ?: "Dhaka")
        } ?: PrayerTimeCalculator.predefinedCities[0]
    }

    fun isBangla(context: Context): Boolean {
        val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("is_bangla", true)
    }

    fun isHanafi(context: Context): Boolean {
        val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("is_hanafi_asr", true)
    }

    fun toBanglaDigits(str: String): String {
        return str.map { ch ->
            when (ch) {
                '0' -> '০'
                '1' -> '১'
                '2' -> '২'
                '3' -> '৩'
                '4' -> '৪'
                '5' -> '৫'
                '6' -> '৬'
                '7' -> '৭'
                '8' -> '৮'
                '9' -> '৯'
                else -> ch
            }
        }.joinToString("")
    }

    fun formatCountdown(minutesRemaining: Int, isBangla: Boolean): String {
        val hours = minutesRemaining / 60
        val mins = minutesRemaining % 60
        return if (isBangla) {
            if (hours > 0) {
                "${toBanglaDigits(hours.toString())}ঘ ${toBanglaDigits(mins.toString())}মি বাকি"
            } else {
                "${toBanglaDigits(mins.toString())} মিনিট বাকি"
            }
        } else {
            if (hours > 0) {
                "${hours}h ${mins}m left"
            } else {
                "${mins}m left"
            }
        }
    }

    fun createOpenAppPendingIntent(context: Context, targetTab: String = "namaz"): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("target_tab", targetTab)
        }
        return PendingIntent.getActivity(
            context,
            targetTab.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun updateAllWidgets(context: Context) {
        // 1. Active Waqt Capsule
        ActiveWaqtWidgetProvider.updateAllWidgets(context)

        // 2. 5-Waqt Daily Board
        PrayerWidgetProvider.updateAllWidgets(context)

        // 3. Ramadan Widget
        RamadanWidgetProvider.updateAllWidgets(context)

        // 4. Hadith Widget
        HadithWidgetProvider.updateAllWidgets(context)
    }
}
