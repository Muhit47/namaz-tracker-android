package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class PrayerType(
    val keyName: String,
    val englishName: String,
    val banglaName: String,
    val arabicName: String,
    val isFardh: Boolean,
    val rakahInfoEn: String,
    val rakahInfoBn: String
) {
    FAJR(
        keyName = "Fajr",
        englishName = "Fajr",
        banglaName = "ফজর",
        arabicName = "الفجر",
        isFardh = true,
        rakahInfoEn = "2 Sunnah + 2 Fardh",
        rakahInfoBn = "২ সুন্নাত + ২ ফরয"
    ),
    DHUHR(
        keyName = "Dhuhr",
        englishName = "Dhuhr",
        banglaName = "যোহর",
        arabicName = "الظهر",
        isFardh = true,
        rakahInfoEn = "4 Sunnah + 4 Fardh + 2 Sunnah + 2 Nafl",
        rakahInfoBn = "৪ সুন্নাত + ৪ ফরয + ২ সুন্নাত + ২ নফল"
    ),
    ASR(
        keyName = "Asr",
        englishName = "Asr",
        banglaName = "আসর",
        arabicName = "العصر",
        isFardh = true,
        rakahInfoEn = "4 Sunnah + 4 Fardh",
        rakahInfoBn = "৪ সুন্নাত + ৪ ফরয"
    ),
    MAGHRIB(
        keyName = "Maghrib",
        englishName = "Maghrib",
        banglaName = "মাগরিব",
        arabicName = "المغرب",
        isFardh = true,
        rakahInfoEn = "3 Fardh + 2 Sunnah + 2 Nafl (Awwabin)",
        rakahInfoBn = "৩ ফরয + ২ সুন্নাত + ২ নফল (আউয়াবিন)"
    ),
    ISHA(
        keyName = "Isha",
        englishName = "Isha",
        banglaName = "ইশা",
        arabicName = "العشاء",
        isFardh = true,
        rakahInfoEn = "4 Sunnah + 4 Fardh + 2 Sunnah + 2 Nafl + 3 Witr",
        rakahInfoBn = "৪ সুন্নাত + ৪ ফরয + ২ সুন্নাত + ২ নফল + ৩ বিতর"
    ),
    WITR(
        keyName = "Witr",
        englishName = "Witr",
        banglaName = "বিতর",
        arabicName = "الوتر",
        isFardh = false,
        rakahInfoEn = "3 Wajib",
        rakahInfoBn = "৩ ওয়াজিব"
    )
}

enum class PrayerStatus(
    val code: String,
    val englishTitle: String,
    val banglaTitle: String,
    val activeBg: Color,
    val activeBorder: Color,
    val activeText: Color
) {
    JAMAAH("jamaah", "Jama'at", "জামাতে", StatusJamatBg, StatusJamatBorder, StatusJamatText),
    ON_TIME("on_time", "On Time", "একাকী", StatusAloneBg, StatusAloneBorder, StatusAloneText),
    LATE("late", "Late", "দেরিতে", StatusQazaBg, StatusQazaBorder, StatusQazaText),
    QAZA("qaza", "Qaza", "কাযা", StatusQazaBg, StatusQazaBorder, StatusQazaText),
    MISSED("missed", "Missed", "মিস", StatusMissedBg, StatusMissedBorder, StatusMissedText),
    PENDING("pending", "Pending", "বাকি", StatusDefaultBg, StatusDefaultBorder, StatusDefaultText);

    companion object {
        fun fromCode(code: String): PrayerStatus {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: PENDING
        }
    }
}
