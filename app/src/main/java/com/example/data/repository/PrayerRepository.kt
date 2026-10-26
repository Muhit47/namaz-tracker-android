package com.example.data.repository

import com.example.data.database.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class PrayerRepository(
    private val prayerLogDao: PrayerLogDao,
    private val qazaDao: QazaDao,
    private val quranDao: QuranDao,
    private val hadithDao: HadithDao,
    private val hadithChapterDao: HadithChapterDao,
    private val hadithFullDao: HadithFullDao,
    private val hadithLastReadDao: HadithLastReadDao,
    private val tasbihDao: TasbihDao,
    private val settingsDao: SettingsDao
) {
    // 1. Prayers Log
    fun getPrayersForDate(date: String): Flow<List<PrayerLogEntity>> =
        prayerLogDao.getPrayersForDate(date)

    suspend fun updatePrayerStatus(
        date: String,
        prayerName: String,
        status: String,
        isSunnah: Int = 0,
        notes: String = ""
    ) {
        val existing = prayerLogDao.getPrayer(date, prayerName)
        val log = PrayerLogEntity(
            id = existing?.id ?: 0,
            date = date,
            prayerName = prayerName,
            status = status,
            isSunnah = isSunnah,
            notes = notes
        )
        prayerLogDao.upsertLog(log)
    }

    suspend fun toggleSunnah(date: String, prayerName: String) {
        val existing = prayerLogDao.getPrayer(date, prayerName)
        if (existing != null) {
            val newSunnah = if (existing.isSunnah == 1) 0 else 1
            prayerLogDao.upsertLog(existing.copy(isSunnah = newSunnah))
        } else {
            prayerLogDao.upsertLog(
                PrayerLogEntity(
                    date = date,
                    prayerName = prayerName,
                    status = "pending",
                    isSunnah = 1
                )
            )
        }
    }

    suspend fun updateNotes(date: String, prayerName: String, notes: String) {
        val existing = prayerLogDao.getPrayer(date, prayerName)
        if (existing != null) {
            prayerLogDao.upsertLog(existing.copy(notes = notes))
        } else {
            prayerLogDao.upsertLog(
                PrayerLogEntity(
                    date = date,
                    prayerName = prayerName,
                    status = "pending",
                    notes = notes
                )
            )
        }
    }

    fun getRecentLogs(days: Int = 30): Flow<List<PrayerLogEntity>> {
        val cal = Calendar.getInstance()
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val endDate = isoFormat.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -days)
        val startDate = isoFormat.format(cal.time)
        return prayerLogDao.getLogsBetweenDates(startDate, endDate)
    }

    // 2. Qaza
    val allQazaRecords: Flow<List<QazaRecordEntity>> = qazaDao.getAllQaza()

    suspend fun adjustQaza(prayerName: String, action: String) {
        val current = qazaDao.getQaza(prayerName) ?: QazaRecordEntity(prayerName = prayerName)
        val updated = when (action) {
            "missed" -> current.copy(totalMissed = current.totalMissed + 1)
            "missed_5" -> current.copy(totalMissed = current.totalMissed + 5)
            "prayed" -> {
                val newMissed = (current.totalMissed - 1).coerceAtLeast(0)
                val newCompleted = current.completed + 1
                current.copy(totalMissed = newMissed, completed = newCompleted)
            }
            "dec" -> current.copy(totalMissed = (current.totalMissed - 1).coerceAtLeast(0))
            else -> current
        }
        qazaDao.upsertQaza(updated)
    }

    suspend fun setQazaDirect(prayerName: String, missedCount: Int) {
        val current = qazaDao.getQaza(prayerName) ?: QazaRecordEntity(prayerName = prayerName)
        qazaDao.upsertQaza(current.copy(totalMissed = missedCount.coerceAtLeast(0)))
    }

    // 3. Al-Quran
    val allSurahs: Flow<List<QuranSurahEntity>> = quranDao.getAllSurahs()

    fun getAyahsForSurah(surahNo: Int): Flow<List<QuranAyahEntity>> =
        quranDao.getAyahsForSurah(surahNo)

    fun searchQuran(query: String): Flow<List<QuranAyahEntity>> =
        quranDao.searchAyahs(query)

    val quranBookmarks: Flow<List<QuranBookmarkEntity>> = quranDao.getBookmarks()

    suspend fun isQuranBookmarked(surahNo: Int, ayahNo: Int): Boolean =
        quranDao.isBookmarked(surahNo, ayahNo) > 0

    suspend fun toggleQuranBookmark(surahNo: Int, ayahNo: Int): Boolean {
        return if (isQuranBookmarked(surahNo, ayahNo)) {
            quranDao.removeBookmark(surahNo, ayahNo)
            false
        } else {
            quranDao.addBookmark(QuranBookmarkEntity(surahNo = surahNo, ayahNo = ayahNo))
            true
        }
    }

    val lastRead: Flow<QuranLastReadEntity?> = quranDao.getLastRead()

    suspend fun setLastRead(surahNo: Int, ayahNo: Int) {
        quranDao.setLastRead(QuranLastReadEntity(surahNo = surahNo, ayahNo = ayahNo))
    }

    // 4. Hadith Sharif
    val hadithCategories: Flow<List<HadithCategoryEntity>> = hadithDao.getCategories()
    val hadithBooks: Flow<List<HadithBookEntity>> = hadithDao.getBooks()
    val allHadiths: Flow<List<HadithEntity>> = hadithDao.getAllHadiths()

    fun getHadithsByCategory(catId: String): Flow<List<HadithEntity>> =
        hadithDao.getHadithsByCategory(catId)

    fun searchHadiths(query: String): Flow<List<HadithEntity>> =
        hadithDao.searchHadiths(query)

    fun getChaptersForBook(bookId: String): Flow<List<HadithChapterEntity>> =
        hadithChapterDao.getChaptersForBook(bookId)

    fun getHadithsForChapter(bookId: String, chapterNo: Int): Flow<List<HadithFullEntity>> =
        hadithFullDao.getHadithsForChapter(bookId, chapterNo)

    fun searchFullHadiths(query: String): Flow<List<HadithFullEntity>> =
        hadithFullDao.searchFullHadiths(query)

    val hadithLastRead: Flow<HadithLastReadEntity?> = hadithLastReadDao.getLastRead()

    suspend fun setHadithLastRead(bookId: String, chapterNo: Int, hadithNo: Int) {
        val lastRead = HadithLastReadEntity(
            id = 1,
            bookId = bookId,
            chapterNo = chapterNo,
            hadithNo = hadithNo,
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        )
        hadithLastReadDao.setLastRead(lastRead)
    }

    suspend fun getRandomDailyHadith(): HadithEntity? =
        hadithDao.getRandomDailyHadith()

    val hadithBookmarks: Flow<List<HadithBookmarkEntity>> = hadithDao.getBookmarks()

    suspend fun isHadithBookmarked(hadithId: Long): Boolean =
        hadithDao.isBookmarked(hadithId) > 0

    suspend fun toggleHadithBookmark(hadithId: Long): Boolean {
        return if (isHadithBookmarked(hadithId)) {
            hadithDao.removeBookmark(hadithId)
            false
        } else {
            hadithDao.addBookmark(HadithBookmarkEntity(hadithId = hadithId))
            true
        }
    }

    // 5. Tasbih
    val allTasbihs: Flow<List<TasbihCounterEntity>> = tasbihDao.getAllTasbihs()

    suspend fun incrementTasbih(id: Long) {
        val item = tasbihDao.getTasbih(id) ?: return
        tasbihDao.upsertTasbih(
            item.copy(
                currentCount = item.currentCount + 1,
                totalRecited = item.totalRecited + 1
            )
        )
    }

    suspend fun resetTasbih(id: Long) {
        val item = tasbihDao.getTasbih(id) ?: return
        tasbihDao.upsertTasbih(item.copy(currentCount = 0))
    }

    suspend fun addCustomTasbih(item: TasbihCounterEntity) {
        tasbihDao.upsertTasbih(item)
    }

    // 6. Settings
    suspend fun getSetting(key: String): String? = settingsDao.getSetting(key)
    suspend fun setSetting(key: String, value: String) =
        settingsDao.setSetting(SettingEntity(key = key, value = value))

    // 7. Backup & Restore
    suspend fun getAllPrayerLogsList(): List<PrayerLogEntity> = prayerLogDao.getAllLogsList()
    suspend fun restorePrayerLogs(logs: List<PrayerLogEntity>) = prayerLogDao.upsertLogs(logs)
    suspend fun getAllQazaList(): List<QazaRecordEntity> = qazaDao.getAllQazaList()
    suspend fun restoreQazaRecords(records: List<QazaRecordEntity>) = qazaDao.upsertQazas(records)
}
