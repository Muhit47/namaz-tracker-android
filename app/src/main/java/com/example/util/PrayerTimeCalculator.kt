package com.example.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

data class CityLocation(
    val nameEn: String,
    val nameBn: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double
)

data class DailyPrayerTimes(
    val fajr: String,
    val sunrise: String,
    val ishraq: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val tahajjudEnd: String
)

object PrayerTimeCalculator {
    val predefinedCities = listOf(
        CityLocation("Dhaka (ঢাকা)", "ঢাকা", 23.8103, 90.4125, 6.0),
        CityLocation("Chittagong (চট্টগ্রাম)", "চট্টগ্রাম", 22.3569, 91.7832, 6.0),
        CityLocation("Sylhet (সিলেট)", "সিলেট", 24.8949, 91.8687, 6.0),
        CityLocation("Rajshahi (রাজশাহী)", "রাজশাহী", 24.3745, 88.6042, 6.0),
        CityLocation("Khulna (খুলনা)", "খুলনা", 22.8456, 89.5403, 6.0),
        CityLocation("Barisal (বরিশাল)", "বরিশাল", 22.7010, 90.3535, 6.0),
        CityLocation("Rangpur (রংপুর)", "রংপুর", 25.7439, 89.2752, 6.0),
        CityLocation("Makkah (মক্কা)", "মক্কা", 21.4225, 39.8262, 3.0),
        CityLocation("Madinah (মদিনা)", "মদিনা", 24.5247, 39.5692, 3.0),
        CityLocation("London (লন্ডন)", "লন্ডন", 51.5074, -0.1278, 1.0),
        CityLocation("New York (নিউইয়র্ক)", "নিউইয়র্ক", 40.7128, -74.0060, -4.0)
    )

    fun findNearestCity(latitude: Double, longitude: Double): CityLocation {
        return predefinedCities.minByOrNull { city ->
            val dLat = Math.toRadians(city.latitude - latitude)
            val dLng = Math.toRadians(city.longitude - longitude)
            val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(latitude)) * cos(Math.toRadians(city.latitude)) * sin(dLng / 2).pow(2)
            2 * atan2(sqrt(a), sqrt(1 - a))
        } ?: predefinedCities[0]
    }

    fun calculateTimes(
        calendar: Calendar,
        city: CityLocation = predefinedCities[0],
        isHanafiAsr: Boolean = true
    ): DailyPrayerTimes {
        val lat = city.latitude
        val lng = city.longitude
        val tz = city.timezoneOffsetHours

        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        // Fractional year in radians
        val gamma = 2.0 * Math.PI / 365.0 * (dayOfYear - 1)

        // Equation of time in minutes
        val eqtime = 229.18 * (0.000075 + 0.001868 * cos(gamma) - 0.032077 * sin(gamma) -
                0.014615 * cos(2 * gamma) - 0.040849 * sin(2 * gamma))

        // Solar declination in radians
        val decl = 0.006918 - 0.399912 * cos(gamma) + 0.070257 * sin(gamma) -
                0.006758 * cos(2 * gamma) + 0.000907 * sin(2 * gamma) -
                0.002697 * cos(3 * gamma) + 0.00148 * sin(3 * gamma)

        val latRad = Math.toRadians(lat)

        // Solar noon in hours local standard time
        val solarNoon = (720.0 - 4.0 * lng - eqtime + tz * 60.0) / 60.0

        // Sunrise and sunset zenith is 90.833 degrees
        val sunZenith = Math.toRadians(90.833)
        val cosHourAngleSun = (cos(sunZenith) - sin(latRad) * sin(decl)) / (cos(latRad) * cos(decl))
        val clampedCosSun = cosHourAngleSun.coerceIn(-1.0, 1.0)
        val sunHourAngle = Math.toDegrees(acos(clampedCosSun)) / 15.0 // in hours

        val sunriseHours = solarNoon - sunHourAngle
        val sunsetHours = solarNoon + sunHourAngle

        // Fajr angle: 18 degrees below horizon (zenith = 108 deg)
        val fajrZenith = Math.toRadians(108.0)
        val cosFajr = (cos(fajrZenith) - sin(latRad) * sin(decl)) / (cos(latRad) * cos(decl))
        val fajrHourAngle = Math.toDegrees(acos(cosFajr.coerceIn(-1.0, 1.0))) / 15.0
        val fajrHours = solarNoon - fajrHourAngle

        // Isha angle: 18 degrees below horizon
        val ishaZenith = Math.toRadians(108.0)
        val cosIsha = (cos(ishaZenith) - sin(latRad) * sin(decl)) / (cos(latRad) * cos(decl))
        val ishaHourAngle = Math.toDegrees(acos(cosIsha.coerceIn(-1.0, 1.0))) / 15.0
        val ishaHours = solarNoon + ishaHourAngle

        // Asr calculation: shadow factor 1 (Shafi'i) or 2 (Hanafi)
        val shadowFactor = if (isHanafiAsr) 2.0 else 1.0
        val asrZenith = atan(1.0 / (shadowFactor + tan(abs(latRad - decl))))
        val cosAsr = (sin(asrZenith) - sin(latRad) * sin(decl)) / (cos(latRad) * cos(decl))
        val asrHourAngle = Math.toDegrees(acos(cosAsr.coerceIn(-1.0, 1.0))) / 15.0
        val asrHours = solarNoon + asrHourAngle

        val ishraqHours = sunriseHours + (15.0 / 60.0) // 15 mins after sunrise

        return DailyPrayerTimes(
            fajr = formatHour(fajrHours),
            sunrise = formatHour(sunriseHours),
            ishraq = formatHour(ishraqHours),
            dhuhr = formatHour(solarNoon + (2.0 / 60.0)),
            asr = formatHour(asrHours),
            maghrib = formatHour(sunsetHours + (1.0 / 60.0)),
            isha = formatHour(ishaHours),
            tahajjudEnd = formatHour(fajrHours - (30.0 / 60.0))
        )
    }

    private fun formatHour(decimalHours: Double): String {
        var h = decimalHours
        while (h < 0) h += 24.0
        while (h >= 24) h -= 24.0

        val totalMinutes = (h * 60.0).roundToInt()
        val hour24 = (totalMinutes / 60) % 24
        val minute = totalMinutes % 60

        val ampm = if (hour24 >= 12) "PM" else "AM"
        val hour12 = when (hour24) {
            0 -> 12
            in 1..12 -> hour24
            else -> hour24 - 12
        }

        return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, minute, ampm)
    }
}
