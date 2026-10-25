package com.example.data.database

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PrayerLogDao {
    @Query("SELECT * FROM prayers_log WHERE date = :date ORDER BY id")
    fun getPrayersForDate(date: String): Flow<List<PrayerLogEntity>>

    @Query("SELECT * FROM prayers_log WHERE date = :date AND prayer_name = :prayerName LIMIT 1")
    suspend fun getPrayer(date: String, prayerName: String): PrayerLogEntity?

    @Query("SELECT * FROM prayers_log ORDER BY date DESC")
    fun getAllLogs(): Flow<List<PrayerLogEntity>>

    @Query("SELECT * FROM prayers_log WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getLogsBetweenDates(startDate: String, endDate: String): Flow<List<PrayerLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLog(log: PrayerLogEntity)
}

@Dao
interface QazaDao {
    @Query("SELECT * FROM qaza_records ORDER BY id ASC")
    fun getAllQaza(): Flow<List<QazaRecordEntity>>

    @Query("SELECT * FROM qaza_records WHERE prayer_name = :prayerName LIMIT 1")
    suspend fun getQaza(prayerName: String): QazaRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQaza(record: QazaRecordEntity)
}

@Dao
interface QuranDao {
    @Query("SELECT * FROM quran_surahs ORDER BY surah_no ASC")
    fun getAllSurahs(): Flow<List<QuranSurahEntity>>

    @Query("SELECT * FROM quran_surahs WHERE surah_no = :surahNo LIMIT 1")
    suspend fun getSurah(surahNo: Int): QuranSurahEntity?

    @Query("SELECT * FROM quran_ayahs WHERE surah_no = :surahNo ORDER BY ayah_no ASC")
    fun getAyahsForSurah(surahNo: Int): Flow<List<QuranAyahEntity>>

    @Query("SELECT * FROM quran_ayahs WHERE trans_bn LIKE '%' || :query || '%' OR translit_bn LIKE '%' || :query || '%' OR trans_en LIKE '%' || :query || '%' OR text_ar LIKE '%' || :query || '%' LIMIT 100")
    fun searchAyahs(query: String): Flow<List<QuranAyahEntity>>

    @Query("SELECT * FROM quran_bookmarks ORDER BY id DESC")
    fun getBookmarks(): Flow<List<QuranBookmarkEntity>>

    @Query("SELECT COUNT(*) FROM quran_bookmarks WHERE surah_no = :surahNo AND ayah_no = :ayahNo")
    suspend fun isBookmarked(surahNo: Int, ayahNo: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: QuranBookmarkEntity)

    @Query("DELETE FROM quran_bookmarks WHERE surah_no = :surahNo AND ayah_no = :ayahNo")
    suspend fun removeBookmark(surahNo: Int, ayahNo: Int)

    @Query("SELECT * FROM quran_last_read WHERE id = 1 LIMIT 1")
    fun getLastRead(): Flow<QuranLastReadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setLastRead(lastRead: QuranLastReadEntity)
}

@Dao
interface HadithDao {
    @Query("SELECT * FROM hadith_categories ORDER BY id ASC")
    fun getCategories(): Flow<List<HadithCategoryEntity>>

    @Query("SELECT * FROM hadith_books ORDER BY id ASC")
    fun getBooks(): Flow<List<HadithBookEntity>>

    @Query("SELECT * FROM hadiths ORDER BY id ASC")
    fun getAllHadiths(): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE category_id = :categoryId ORDER BY id ASC")
    fun getHadithsByCategory(categoryId: String): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadiths WHERE id = :id LIMIT 1")
    suspend fun getHadithById(id: Long): HadithEntity?

    @Query("SELECT * FROM hadiths ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomDailyHadith(): HadithEntity?

    @Query("SELECT * FROM hadiths WHERE text_bn LIKE '%' || :query || '%' OR chapter_bn LIKE '%' || :query || '%' OR narrator_bn LIKE '%' || :query || '%' OR text_en LIKE '%' || :query || '%'")
    fun searchHadiths(query: String): Flow<List<HadithEntity>>

    @Query("SELECT * FROM hadith_bookmarks ORDER BY id DESC")
    fun getBookmarks(): Flow<List<HadithBookmarkEntity>>

    @Query("SELECT COUNT(*) FROM hadith_bookmarks WHERE hadith_id = :hadithId")
    suspend fun isBookmarked(hadithId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: HadithBookmarkEntity)

    @Query("DELETE FROM hadith_bookmarks WHERE hadith_id = :hadithId")
    suspend fun removeBookmark(hadithId: Long)
}

@Dao
interface HadithChapterDao {
    @Query("SELECT * FROM hadith_chapters WHERE book_id = :bookId ORDER BY chapter_no ASC")
    fun getChaptersForBook(bookId: String): Flow<List<HadithChapterEntity>>

    @Query("SELECT * FROM hadith_chapters WHERE book_id = :bookId AND chapter_no = :chapterNo LIMIT 1")
    suspend fun getChapter(bookId: String, chapterNo: Int): HadithChapterEntity?
}

@Dao
interface HadithFullDao {
    @Query("SELECT * FROM hadith_full_collection WHERE book_id = :bookId AND chapter_no = :chapterNo ORDER BY hadith_no ASC")
    fun getHadithsForChapter(bookId: String, chapterNo: Int): Flow<List<HadithFullEntity>>

    @Query("SELECT * FROM hadith_full_collection WHERE text_bn LIKE '%' || :query || '%' OR text_ar LIKE '%' || :query || '%' OR text_en LIKE '%' || :query || '%' LIMIT 100")
    fun searchFullHadiths(query: String): Flow<List<HadithFullEntity>>

    @Query("SELECT * FROM hadith_full_collection WHERE id = :id LIMIT 1")
    suspend fun getFullHadithById(id: Long): HadithFullEntity?
}

@Dao
interface HadithLastReadDao {
    @Query("SELECT * FROM hadith_last_read WHERE id = 1 LIMIT 1")
    fun getLastRead(): Flow<HadithLastReadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setLastRead(lastRead: HadithLastReadEntity)
}

@Dao
interface TasbihDao {
    @Query("SELECT * FROM tasbih_counters ORDER BY id ASC")
    fun getAllTasbihs(): Flow<List<TasbihCounterEntity>>

    @Query("SELECT * FROM tasbih_counters WHERE id = :id LIMIT 1")
    suspend fun getTasbih(id: Long): TasbihCounterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTasbih(item: TasbihCounterEntity)

    @Delete
    suspend fun deleteTasbih(item: TasbihCounterEntity)
}

@Dao
interface SettingsDao {
    @Query("SELECT value FROM settings WHERE key = :key LIMIT 1")
    suspend fun getSetting(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingEntity)
}
