package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "prayers_log",
    indices = [Index(value = ["date", "prayer_name"], unique = true)]
)
data class PrayerLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    @ColumnInfo(name = "prayer_name")
    val prayerName: String,
    val status: String = "pending", // "pending", "jamaah", "on_time", "late", "qaza", "missed"
    @ColumnInfo(name = "is_sunnah")
    val isSunnah: Int = 0,
    val notes: String? = "",
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)

@Entity(tableName = "qaza_records")
data class QazaRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "prayer_name")
    val prayerName: String,
    @ColumnInfo(name = "total_missed")
    val totalMissed: Int = 0,
    val completed: Int = 0,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)

@Entity(tableName = "quran_surahs")
data class QuranSurahEntity(
    @PrimaryKey
    @ColumnInfo(name = "surah_no")
    val surahNo: Int,
    @ColumnInfo(name = "name_ar")
    val nameAr: String,
    @ColumnInfo(name = "name_bn")
    val nameBn: String,
    @ColumnInfo(name = "name_en")
    val nameEn: String,
    @ColumnInfo(name = "meaning_bn")
    val meaningBn: String,
    @ColumnInfo(name = "meaning_en")
    val meaningEn: String? = "",
    @ColumnInfo(name = "total_ayah")
    val totalAyah: Int,
    @ColumnInfo(name = "revelation_type")
    val revelationType: String
)

@Entity(tableName = "quran_ayahs")
data class QuranAyahEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "surah_no")
    val surahNo: Int,
    @ColumnInfo(name = "ayah_no")
    val ayahNo: Int,
    @ColumnInfo(name = "text_ar")
    val textAr: String,
    @ColumnInfo(name = "translit_bn")
    val translitBn: String,
    @ColumnInfo(name = "trans_bn")
    val transBn: String,
    @ColumnInfo(name = "translit_en")
    val translitEn: String? = "",
    @ColumnInfo(name = "trans_en")
    val transEn: String? = ""
)

@Entity(tableName = "quran_bookmarks")
data class QuranBookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "surah_no")
    val surahNo: Int,
    @ColumnInfo(name = "ayah_no")
    val ayahNo: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null
)

@Entity(tableName = "quran_last_read")
data class QuranLastReadEntity(
    @PrimaryKey
    val id: Long = 1,
    @ColumnInfo(name = "surah_no")
    val surahNo: Int,
    @ColumnInfo(name = "ayah_no")
    val ayahNo: Int,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)

@Entity(tableName = "hadith_categories")
data class HadithCategoryEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "name_bn")
    val nameBn: String,
    val icon: String,
    @ColumnInfo(name = "name_en")
    val nameEn: String? = null
)

@Entity(tableName = "hadith_books")
data class HadithBookEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "name_bn")
    val nameBn: String,
    @ColumnInfo(name = "name_ar")
    val nameAr: String,
    @ColumnInfo(name = "name_en")
    val nameEn: String? = null
)

@Entity(tableName = "hadiths")
data class HadithEntity(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    @ColumnInfo(name = "category_bn")
    val categoryBn: String,
    @ColumnInfo(name = "book_id")
    val bookId: String,
    @ColumnInfo(name = "book_name_bn")
    val bookNameBn: String,
    @ColumnInfo(name = "chapter_bn")
    val chapterBn: String,
    @ColumnInfo(name = "narrator_bn")
    val narratorBn: String,
    @ColumnInfo(name = "text_ar")
    val textAr: String,
    @ColumnInfo(name = "text_bn")
    val textBn: String,
    @ColumnInfo(name = "grade_bn")
    val gradeBn: String,
    @ColumnInfo(name = "reference_bn")
    val referenceBn: String,
    @ColumnInfo(name = "translit_bn")
    val translitBn: String? = null,
    @ColumnInfo(name = "translit_en")
    val translitEn: String? = null,
    @ColumnInfo(name = "text_en")
    val textEn: String? = null,
    @ColumnInfo(name = "category_en")
    val categoryEn: String? = null,
    @ColumnInfo(name = "book_name_en")
    val bookNameEn: String? = null,
    @ColumnInfo(name = "chapter_en")
    val chapterEn: String? = null,
    @ColumnInfo(name = "narrator_en")
    val narratorEn: String? = null,
    @ColumnInfo(name = "grade_en")
    val gradeEn: String? = null,
    @ColumnInfo(name = "reference_en")
    val referenceEn: String? = null
)

@Entity(tableName = "hadith_bookmarks")
data class HadithBookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "hadith_id")
    val hadithId: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: String? = null
)

@Entity(tableName = "hadith_chapters")
data class HadithChapterEntity(
    @PrimaryKey
    val id: Long = 0,
    @ColumnInfo(name = "book_id")
    val bookId: String,
    @ColumnInfo(name = "chapter_no")
    val chapterNo: Int,
    @ColumnInfo(name = "name_bn")
    val nameBn: String,
    @ColumnInfo(name = "name_ar")
    val nameAr: String? = null,
    @ColumnInfo(name = "name_en")
    val nameEn: String? = null,
    @ColumnInfo(name = "total_hadith")
    val totalHadith: Int = 0,
    @ColumnInfo(name = "start_hadith")
    val startHadith: Int = 0,
    @ColumnInfo(name = "end_hadith")
    val endHadith: Int = 0
)

@Entity(tableName = "hadith_full_collection")
data class HadithFullEntity(
    @PrimaryKey
    val id: Long = 0,
    @ColumnInfo(name = "book_id")
    val bookId: String,
    @ColumnInfo(name = "chapter_no")
    val chapterNo: Int,
    @ColumnInfo(name = "hadith_no")
    val hadithNo: Int,
    @ColumnInfo(name = "arabic_no")
    val arabicNo: Int = 0,
    @ColumnInfo(name = "text_ar")
    val textAr: String? = null,
    @ColumnInfo(name = "text_bn")
    val textBn: String,
    @ColumnInfo(name = "grade_bn")
    val gradeBn: String? = null,
    @ColumnInfo(name = "translit_bn")
    val translitBn: String? = null,
    @ColumnInfo(name = "translit_en")
    val translitEn: String? = null,
    @ColumnInfo(name = "text_en")
    val textEn: String? = null
)

@Entity(tableName = "hadith_last_read")
data class HadithLastReadEntity(
    @PrimaryKey
    val id: Long = 1,
    @ColumnInfo(name = "book_id")
    val bookId: String,
    @ColumnInfo(name = "chapter_no")
    val chapterNo: Int,
    @ColumnInfo(name = "hadith_no")
    val hadithNo: Int,
    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null
)

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey
    val key: String,
    val value: String?
)

@Entity(tableName = "tasbih_counters")
data class TasbihCounterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val arabicText: String,
    val transliteration: String,
    val translationBn: String,
    val translationEn: String,
    val currentCount: Int = 0,
    val targetCount: Int = 33,
    val totalRecited: Long = 0,
    val isPreset: Boolean = true
)
