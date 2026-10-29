package com.example.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayFormatEn = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.US)

    fun getTodayIso(): String {
        return isoFormat.format(Date())
    }

    fun getIsoForCalendar(calendar: Calendar): String {
        return isoFormat.format(calendar.time)
    }

    fun getCalendarFromIso(iso: String): Calendar {
        val cal = Calendar.getInstance()
        try {
            val date = isoFormat.parse(iso)
            if (date != null) {
                cal.time = date
            }
        } catch (e: Exception) {
            // fallback to current
        }
        return cal
    }

    fun formatDisplayDate(iso: String, isBangla: Boolean): String {
        val cal = getCalendarFromIso(iso)
        if (!isBangla) {
            return displayFormatEn.format(cal.time)
        }

        val dayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> "শনিবার"
            Calendar.SUNDAY -> "রবিবার"
            Calendar.MONDAY -> "সোমবার"
            Calendar.TUESDAY -> "মঙ্গলবার"
            Calendar.WEDNESDAY -> "বুধবার"
            Calendar.THURSDAY -> "বৃহস্পতিবার"
            Calendar.FRIDAY -> "শুক্রবার"
            else -> ""
        }

        val month = when (cal.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "জানুয়ারি"
            Calendar.FEBRUARY -> "ফেব্রুয়ারি"
            Calendar.MARCH -> "মার্চ"
            Calendar.APRIL -> "এপ্রিল"
            Calendar.MAY -> "মে"
            Calendar.JUNE -> "জুন"
            Calendar.JULY -> "জুলাই"
            Calendar.AUGUST -> "আগস্ট"
            Calendar.SEPTEMBER -> "সেপ্টেম্বর"
            Calendar.OCTOBER -> "অক্টোবর"
            Calendar.NOVEMBER -> "নভেম্বর"
            Calendar.DECEMBER -> "ডিসেম্বর"
            else -> ""
        }

        val day = cal.get(Calendar.DAY_OF_MONTH)
        val year = cal.get(Calendar.YEAR)

        return "$dayOfWeek, ${toBanglaDigits(day.toString())} $month, ${toBanglaDigits(year.toString())}"
    }

    fun toBanglaDigits(input: String): String {
        val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(banglaDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    data class HijriDate(
        val day: Int,
        val monthIndex: Int,
        val monthNameBn: String,
        val monthNameEn: String,
        val year: Int
    )

    val hijriMonthsEn = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    val hijriMonthsBn = listOf(
        "মুহাররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
        "জমাদিউল আউয়াল", "জমাদিউস সানি", "রজব", "শা'বান",
        "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ্ব"
    )

    fun getHijriDate(calendar: Calendar): HijriDate {
        val jd = toJulianDay(calendar)
        val l = jd - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        val lPrime = l - 10631 * n + 354
        val j = (((10985 - lPrime) / 5316).toInt()) * (((50 * lPrime) / 17719).toInt()) +
                ((lPrime / 5670).toInt()) * (((43 * lPrime) / 15238).toInt())
        val lDoublePrime = lPrime - (((30 - j) / 15).toInt()) * (((17719 * j) / 50).toInt()) -
                ((j / 16).toInt()) * (((15238 * j) / 43).toInt()) + 29
        val m = ((24 * lDoublePrime) / 709).toInt()
        val day = (lDoublePrime - ((709 * m) / 24).toInt()).toInt()
        val year = 30 * n + j - 30
        val safeMonthIdx = (m - 1).coerceIn(0, 11)

        return HijriDate(
            day = day,
            monthIndex = safeMonthIdx,
            monthNameBn = hijriMonthsBn[safeMonthIdx],
            monthNameEn = hijriMonthsEn[safeMonthIdx],
            year = year
        )
    }

    fun getApproximateHijriDate(calendar: Calendar, isBangla: Boolean): String {
        val h = getHijriDate(calendar)
        return if (isBangla) {
            "${toBanglaDigits(h.day.toString())} ${h.monthNameBn}, ${toBanglaDigits(h.year.toString())} হিজরি"
        } else {
            "${h.day} ${h.monthNameEn} ${h.year} AH"
        }
    }

    fun getDaysUntilNextRamadan(calendar: Calendar = Calendar.getInstance()): Int {
        val currentHijri = getHijriDate(calendar)
        if (currentHijri.monthIndex == 8) {
            return 0
        }
        val cal = calendar.clone() as Calendar
        var days = 0
        while (days < 365) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            days++
            val h = getHijriDate(cal)
            if (h.monthIndex == 8) {
                return days
            }
        }
        return days
    }

    private fun toJulianDay(calendar: Calendar): Long {
        var y = calendar.get(Calendar.YEAR).toLong()
        var m = (calendar.get(Calendar.MONTH) + 1).toLong()
        val d = calendar.get(Calendar.DAY_OF_MONTH).toLong()

        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = y / 100
        val b = 2 - a + (a / 4)
        return (365.25 * (y + 4716)).toLong() + (30.6001 * (m + 1)).toLong() + d + b - 1524
    }
}
