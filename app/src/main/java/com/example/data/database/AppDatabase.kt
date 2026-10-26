package com.example.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*
import java.io.File
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream

@Database(
    entities = [
        PrayerLogEntity::class,
        QazaRecordEntity::class,
        QuranSurahEntity::class,
        QuranAyahEntity::class,
        QuranBookmarkEntity::class,
        QuranLastReadEntity::class,
        HadithCategoryEntity::class,
        HadithBookEntity::class,
        HadithEntity::class,
        HadithBookmarkEntity::class,
        HadithChapterEntity::class,
        HadithFullEntity::class,
        HadithLastReadEntity::class,
        SettingEntity::class,
        TasbihCounterEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prayerLogDao(): PrayerLogDao
    abstract fun qazaDao(): QazaDao
    abstract fun quranDao(): QuranDao
    abstract fun hadithDao(): HadithDao
    abstract fun hadithChapterDao(): HadithChapterDao
    abstract fun hadithFullDao(): HadithFullDao
    abstract fun hadithLastReadDao(): HadithLastReadDao
    abstract fun tasbihDao(): TasbihDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        private const val CURRENT_DB_VERSION = 2 // Version 2: Complete 34,932 Hadiths + Quran + Tasbih
        private const val PREFS_NAME = "namaz_tracker_db_meta"
        private const val KEY_INSTALLED_VERSION = "installed_db_version"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val dbFile = context.getDatabasePath("namaz_tracker.db")
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val installedVer = prefs.getInt(KEY_INSTALLED_VERSION, 0)

                if (!dbFile.exists() || installedVer < CURRENT_DB_VERSION) {
                    val extracted = extractCompressedDb(context, dbFile)
                    if (extracted) {
                        prefs.edit().putInt(KEY_INSTALLED_VERSION, CURRENT_DB_VERSION).apply()
                    }
                }

                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "namaz_tracker.db"
                )

                if (!dbFile.exists()) {
                    try {
                        builder.createFromAsset("namaz_tracker.db")
                    } catch (e: Exception) {
                        Log.w("AppDatabase", "createFromAsset fallback skipped: ${e.message}")
                    }
                }

                val instance = builder
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun extractCompressedDb(context: Context, destFile: File): Boolean {
            return try {
                val assetList = context.assets.list("") ?: emptyArray()
                if (assetList.contains("namaz_tracker.db.gz")) {
                    destFile.parentFile?.mkdirs()
                    context.assets.open("namaz_tracker.db.gz").use { gzIn ->
                        GZIPInputStream(gzIn).use { decompressedIn ->
                            FileOutputStream(destFile).use { fileOut ->
                                decompressedIn.copyTo(fileOut, bufferSize = 65536)
                            }
                        }
                    }
                    Log.i("AppDatabase", "Successfully extracted 34,932 hadiths complete database from assets gz.")
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("AppDatabase", "Error extracting compressed database asset: ${e.message}", e)
                false
            }
        }
    }
}
