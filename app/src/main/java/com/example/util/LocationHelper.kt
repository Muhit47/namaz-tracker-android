package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun isLocationEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    suspend fun detectCurrentCity(context: Context): CityLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) return@withContext null
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return@withContext null

        val gpsLoc = try { lm.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (_: Exception) { null }
        val netLoc = try { lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (_: Exception) { null }

        val bestLocation: Location? = when {
            gpsLoc != null && netLoc != null -> if (gpsLoc.time > netLoc.time) gpsLoc else netLoc
            gpsLoc != null -> gpsLoc
            netLoc != null -> netLoc
            else -> null
        } ?: return@withContext null

        val lat = bestLocation.latitude
        val lng = bestLocation.longitude

        var detectedNameEn: String? = null
        var detectedNameBn: String? = null

        try {
            if (Geocoder.isPresent()) {
                val geocoderEn = Geocoder(context, Locale.ENGLISH)
                @Suppress("DEPRECATION")
                val addresses = geocoderEn.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    detectedNameEn = addr.locality ?: addr.subAdminArea ?: addr.adminArea
                }

                val geocoderBn = Geocoder(context, Locale("bn", "BD"))
                @Suppress("DEPRECATION")
                val addressesBn = geocoderBn.getFromLocation(lat, lng, 1)
                if (!addressesBn.isNullOrEmpty()) {
                    val addrBn = addressesBn[0]
                    detectedNameBn = addrBn.locality ?: addrBn.subAdminArea ?: addrBn.adminArea
                }
            }
        } catch (_: Exception) {}

        val nearest = PrayerTimeCalculator.findNearestCity(lat, lng)

        val finalNameEn = if (!detectedNameEn.isNullOrBlank()) {
            "$detectedNameEn (${nearest.nameEn.split(" ")[0]})"
        } else {
            "${nearest.nameEn.split(" ")[0]} (Auto)"
        }

        val finalNameBn = if (!detectedNameBn.isNullOrBlank()) {
            "$detectedNameBn (${nearest.nameBn})"
        } else {
            "${nearest.nameBn} (অটো)"
        }

        CityLocation(
            nameEn = finalNameEn,
            nameBn = finalNameBn,
            latitude = lat,
            longitude = lng,
            timezoneOffsetHours = 6.0
        )
    }
}
