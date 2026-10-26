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
    version = 4,
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
        private const val CURRENT_DB_VERSION = 4 // Version 4: Aligned Room-validated schema with 114 Surahs, 6236 Ayahs, 34932 Hadiths
        private const val PREFS_NAME = "namaz_tracker_db_meta"
        private const val KEY_INSTALLED_VERSION = "installed_db_version"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val dbFile = context.getDatabasePath("namaz_tracker.db")
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val installedVer = prefs.getInt(KEY_INSTALLED_VERSION, 0)

                var needExtract = !dbFile.exists() || installedVer < CURRENT_DB_VERSION
                if (!needExtract && dbFile.exists()) {
                    try {
                        val checkDb = android.database.sqlite.SQLiteDatabase.openDatabase(
                            dbFile.path, null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY
                        )
                        val cursor = checkDb.rawQuery("SELECT count(*) FROM quran_surahs", null)
                        if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
                            needExtract = true
                        }
                        cursor.close()
                        checkDb.close()
                    } catch (e: Exception) {
                        needExtract = true
                    }
                }

                if (needExtract) {
                    val extracted = extractCompressedDb(context, dbFile)
                    if (extracted) {
                        prefs.edit().putInt(KEY_INSTALLED_VERSION, CURRENT_DB_VERSION).apply()
                    }
                }

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "namaz_tracker.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun extractCompressedDb(context: Context, destFile: File): Boolean {
            return try {
                val gzInputStream = try {
                    context.assets.open("namaz_tracker.db.gz")
                } catch (e: Exception) {
                    null
                }

                if (gzInputStream != null) {
                    gzInputStream.use { gzIn ->
                        destFile.parentFile?.mkdirs()
                        if (destFile.exists()) destFile.delete()
                        File(destFile.path + "-journal").delete()
                        File(destFile.path + "-wal").delete()
                        File(destFile.path + "-shm").delete()

                        GZIPInputStream(gzIn).use { decompressedIn ->
                            FileOutputStream(destFile).use { fileOut ->
                                decompressedIn.copyTo(fileOut, bufferSize = 65536)
                            }
                        }
                    }
                    Log.i("AppDatabase", "Successfully extracted 34,932 hadiths complete database from assets gz.")
                    true
                } else {
                    val rawInputStream = try {
                        context.assets.open("namaz_tracker.db")
                    } catch (e: Exception) {
                        null
                    }
                    if (rawInputStream != null) {
                        rawInputStream.use { rawIn ->
                            destFile.parentFile?.mkdirs()
                            if (destFile.exists()) destFile.delete()
                            File(destFile.path + "-journal").delete()
                            File(destFile.path + "-wal").delete()
                            File(destFile.path + "-shm").delete()

                            FileOutputStream(destFile).use { fileOut ->
                                rawIn.copyTo(fileOut, bufferSize = 65536)
                            }
                        }
                        Log.i("AppDatabase", "Successfully copied uncompressed asset database.")
                        true
                    } else {
                        false
                    }
                }
            } catch (e: Exception) {
                Log.e("AppDatabase", "Error extracting compressed database asset: ${e.message}", e)
                false
            }
        }
    }
}
