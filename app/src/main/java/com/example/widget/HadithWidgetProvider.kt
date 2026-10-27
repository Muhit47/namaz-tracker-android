package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.R

class HadithWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_SHUFFLE_HADITH) {
            val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
            val currentIdx = prefs.getInt("widget_hadith_idx", 0)
            prefs.edit().putInt("widget_hadith_idx", (currentIdx + 1) % curatedHadiths.size).apply()
            updateAllWidgets(context)
        } else if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            updateAllWidgets(context)
        }
    }

    companion object {
        const val ACTION_SHUFFLE_HADITH = "com.example.widget.ACTION_SHUFFLE_HADITH"

        data class HadithItem(
            val textBn: String,
            val textEn: String,
            val sourceBn: String,
            val sourceEn: String
        )

        val curatedHadiths = listOf(
            HadithItem(
                textBn = "রাসূলুল্লাহ (ﷺ) বলেছেন: যে ব্যক্তি খাঁটি অন্তরে সময়মতো পাঁচ ওয়াক্ত নামায আদায় করে, তার জন্য জান্নাতের ওয়াদা রয়েছে।",
                textEn = "The Messenger of Allah (ﷺ) said: Whoever observes the five daily prayers on time with sincere devotion is promised Paradise.",
                sourceBn = "সহীহ বুখারী • হাদিস নং ৫২৮",
                sourceEn = "Sahih Bukhari • Hadith 528"
            ),
            HadithItem(
                textBn = "রাসূলুল্লাহ (ﷺ) বলেছেন: বান্দা ও কুফরের মাঝে পার্থক্য হলো নামায বর্জন করা।",
                textEn = "The Messenger of Allah (ﷺ) said: Between a servant and disbelief is the abandonment of prayer.",
                sourceBn = "সহীহ মুসলিম • হাদিস নং ৮২",
                sourceEn = "Sahih Muslim • Hadith 82"
            ),
            HadithItem(
                textBn = "রাসূলুল্লাহ (ﷺ) বলেছেন: যে ব্যক্তি জামাআতের সাথে এশার নামায পড়ল, সে যেন অর্ধরাত নামায পড়ল। আর যে ফজরের নামাযও জামাআতে পড়ল, সে যেন সারারাত নামায পড়ল।",
                textEn = "The Messenger of Allah (ﷺ) said: One who performs Isha in congregation is like one who prayed for half the night; and one who also performs Fajr in congregation is like one who prayed the entire night.",
                sourceBn = "সহীহ মুসলিম • হাদিস নং ৬৫৬",
                sourceEn = "Sahih Muslim • Hadith 656"
            ),
            HadithItem(
                textBn = "রাসূলুল্লাহ (ﷺ) বলেছেন: উত্তম আমল কোনটি? তিনি বললেন: ওয়াক্তের শুরুতে নামায আদায় করা।",
                textEn = "The Messenger of Allah (ﷺ) was asked: Which deed is best? He said: Performing prayer at its earliest time.",
                sourceBn = "সুনান আত-তিরমিযী • হাদিস নং ১৭০",
                sourceEn = "Jami at-Tirmidhi • Hadith 170"
            ),
            HadithItem(
                textBn = "রাসূলুল্লাহ (ﷺ) বলেছেন: কিয়ামতের দিন বান্দার আমলসমূহের মধ্যে সর্বপ্রথম নামাযের হিসাব নেওয়া হবে।",
                textEn = "The Messenger of Allah (ﷺ) said: The first matter that the slave will be brought to account for on the Day of Judgment is the prayer.",
                sourceBn = "সুনান আন-নাসায়ী • হাদিস নং ৪৬৫",
                sourceEn = "Sunan an-Nasa'i • Hadith 465"
            ),
            HadithItem(
                textBn = "রাসূলুল্লাহ (ﷺ) বলেছেন: দুটি বাক্য যা জিহ্বায় সহজ, দাঁড়িপাল্লায় ভারী এবং দয়াময় আল্লাহর নিকট অত্যন্ত প্রিয়: 'সুবহানাল্লাহি ওয়া বিহামদিহি, সুবহানাল্লাহিল আযীম'।",
                textEn = "The Prophet (ﷺ) said: Two words are light on the tongue, heavy on the scale, and beloved to the Most Merciful: 'Subhanallahi wa bihamdihi, Subhanallahil Azeem'.",
                sourceBn = "সহীহ বুখারী • হাদিস নং ৬৬৮২",
                sourceEn = "Sahih Bukhari • Hadith 6682"
            )
        )

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val isBangla = WidgetHelper.isBangla(context)
            val prefs = context.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)
            val idx = prefs.getInt("widget_hadith_idx", 0) % curatedHadiths.size
            val hadith = curatedHadiths[idx]

            val views = RemoteViews(context.packageName, R.layout.widget_hadith_layout)

            views.setTextViewText(
                R.id.widget_hadith_title,
                if (isBangla) "দৈনিক হাদিস ও নসিহত" else "Daily Hadith & Wisdom"
            )
            views.setTextViewText(
                R.id.widget_hadith_shuffle_btn,
                if (isBangla) "নতুন হাদিস ↻" else "Next Hadith ↻"
            )

            views.setTextViewText(
                R.id.widget_hadith_text,
                if (isBangla) hadith.textBn else hadith.textEn
            )
            views.setTextViewText(
                R.id.widget_hadith_source,
                if (isBangla) hadith.sourceBn else hadith.sourceEn
            )
            views.setTextViewText(
                R.id.widget_hadith_open_label,
                if (isBangla) "অ্যাপে পড়ুন ↗" else "Read in App ↗"
            )

            // 1. Shuffle Button PendingIntent
            val shuffleIntent = Intent(context, HadithWidgetProvider::class.java).apply {
                action = ACTION_SHUFFLE_HADITH
            }
            val shufflePendingIntent = PendingIntent.getBroadcast(
                context,
                101,
                shuffleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_hadith_shuffle_btn, shufflePendingIntent)

            // 2. Body tap opens Quran/Hadith tab in app
            views.setOnClickPendingIntent(
                R.id.widget_hadith_root,
                WidgetHelper.createOpenAppPendingIntent(context, "quran")
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
            val componentName = ComponentName(context, HadithWidgetProvider::class.java)
            val ids = appWidgetManager.getAppWidgetIds(componentName)
            for (id in ids) {
                updateWidget(context, appWidgetManager, id)
            }
        }
    }
}
