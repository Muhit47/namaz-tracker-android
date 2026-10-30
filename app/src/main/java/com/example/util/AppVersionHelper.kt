package com.example.util

import com.example.BuildConfig

/**
 * Single source of truth for dynamically rendering application version.
 * Automatically synchronizes with versionName and versionCode in app/build.gradle.kts.
 */
object AppVersionHelper {
    val versionName: String
        get() = try {
            BuildConfig.VERSION_NAME
        } catch (e: Throwable) {
            "3.3.1"
        }

    val versionCode: Int
        get() = try {
            BuildConfig.VERSION_CODE
        } catch (e: Throwable) {
            22
        }

    fun getDisplayVersion(isBangla: Boolean): String {
        val version = versionName
        return if (isBangla) {
            val banglaDigits = version.map { ch ->
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
            "ভার্সন $banglaDigits"
        } else {
            "v$version"
        }
    }
}
