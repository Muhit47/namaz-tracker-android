package com.example

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.widget.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WidgetProvidersTest {

    @Test
    fun `validate WidgetHelper numeral conversion and countdown formatting`() {
        val converted = WidgetHelper.toBanglaDigits("12:34 PM")
        assertEquals("১২:৩৪ PM", converted)

        val countdownBn = WidgetHelper.formatCountdown(82, isBangla = true)
        assertTrue("Bangla countdown should contain 'ঘ' and 'মি'", countdownBn.contains("ঘ") && countdownBn.contains("মি"))

        val countdownEn = WidgetHelper.formatCountdown(82, isBangla = false)
        assertEquals("1h 22m left", countdownEn)

        val shortCountdown = WidgetHelper.formatCountdown(25, isBangla = false)
        assertEquals("25m left", shortCountdown)
    }

    @Test
    fun `validate HadithWidgetProvider curated list and shuffle action`() {
        val list = HadithWidgetProvider.curatedHadiths
        assertTrue("Curated hadiths must contain at least 5 authentic hadiths", list.size >= 5)

        for (item in list) {
            assertTrue("Hadith Bangla text must not be empty", item.textBn.isNotEmpty())
            assertTrue("Hadith English text must not be empty", item.textEn.isNotEmpty())
            assertTrue("Hadith Source must not be empty", item.sourceBn.isNotEmpty())
        }

        // Test Shuffle Intent
        val context = ApplicationProvider.getApplicationContext<Context>()
        val provider = HadithWidgetProvider()
        val shuffleIntent = Intent(HadithWidgetProvider.ACTION_SHUFFLE_HADITH)
        provider.onReceive(context, shuffleIntent)

        val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
        val idx = prefs.getInt("widget_hadith_idx", 0)
        assertTrue("Hadith index should be valid", idx in 0 until list.size)
    }

    @Test
    fun `validate all 4 widget providers update successfully`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appWidgetManager = AppWidgetManager.getInstance(context)
        assertNotNull(appWidgetManager)

        // 1. Active Waqt Capsule
        ActiveWaqtWidgetProvider.updateWidget(context, appWidgetManager, 101)

        // 2. 5-Waqt Daily Board
        PrayerWidgetProvider.updateAppWidget(context, appWidgetManager, 102)

        // 3. Ramadan Fasting Timer
        RamadanWidgetProvider.updateWidget(context, appWidgetManager, 103)

        // 4. Daily Hadith Reader
        HadithWidgetProvider.updateWidget(context, appWidgetManager, 104)

        // Unified Update
        WidgetHelper.updateAllWidgets(context)
    }
}
