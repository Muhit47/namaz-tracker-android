package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

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
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "namaz_tracker.db"
                )
                    .createFromAsset("namaz_tracker.db")
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
