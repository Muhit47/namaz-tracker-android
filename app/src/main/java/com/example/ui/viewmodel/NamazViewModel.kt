package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.PrayerRepository
import com.example.notification.PrayerAlarmScheduler
import com.example.notification.PrayerNotificationHelper
import com.example.util.BackupRestoreHelper
import com.example.util.CityLocation
import com.example.util.DateUtil
import com.example.util.HapticUtil
import com.example.util.PrayerTimeCalculator
import com.example.util.QiblaHelper
import com.example.widget.PrayerWidgetProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class NamazUiState(
    val selectedDate: String = DateUtil.getTodayIso(),
    val isBangla: Boolean = true,
    val selectedCity: CityLocation = PrayerTimeCalculator.predefinedCities[0], // Dhaka
    val isHanafiAsr: Boolean = true,
    val prayerTimes: com.example.util.DailyPrayerTimes = PrayerTimeCalculator.calculateTimes(
        Calendar.getInstance(),
        PrayerTimeCalculator.predefinedCities[0],
        true
    ),
    val currentDayLogs: List<PrayerLogEntity> = emptyList(),
    val qazaRecords: List<QazaRecordEntity> = emptyList(),
    val totalPrayedToday: Int = 0,
    val currentStreakDays: Int = 0,
    val weeklyCompletionRate: Int = 0,
    val isRozaObservedToday: Boolean = false,

    // Quran
    val surahs: List<QuranSurahEntity> = emptyList(),
    val selectedSurah: QuranSurahEntity? = null,
    val selectedSurahAyahs: List<QuranAyahEntity> = emptyList(),
    val quranBookmarks: List<QuranBookmarkEntity> = emptyList(),
    val quranLastRead: QuranLastReadEntity? = null,
    val quranSearchQuery: String = "",

    // Hadith
    val hadithCategories: List<HadithCategoryEntity> = emptyList(),
    val hadithBooks: List<HadithBookEntity> = emptyList(),
    val allHadiths: List<HadithEntity> = emptyList(),
    val selectedHadithCategory: String = "all",
    val selectedHadithBook: String = "all",
    val showOnlyHadithBookmarks: Boolean = false,
    val dailyHadith: HadithEntity? = null,
    val hadithBookmarks: List<HadithBookmarkEntity> = emptyList(),
    val hadithSearchQuery: String = "",

    // Hadith Multi-Book Expansion
    val selectedHadithBookChapters: List<HadithChapterEntity> = emptyList(),
    val selectedHadithChapter: HadithChapterEntity? = null,
    val selectedHadithChapterFullHadiths: List<HadithFullEntity> = emptyList(),
    val hadithLastRead: HadithLastReadEntity? = null,

    // Tasbih
    val tasbihs: List<TasbihCounterEntity> = emptyList(),
    val selectedTasbihId: Long = 1L,

    // Compass
    val compassHeading: Float = 0f,
    val qiblaBearing: Float = QiblaHelper.calculateQiblaDirection(23.8103, 90.4125),
    val isAlignedWithQibla: Boolean = false,

    // Notifications
    val prayerNotificationsEnabled: Boolean = true,
    val notificationSoundMode: String = "full_azan", // "full_azan", "short_takbeer", "system_tone", "silent"
    val selectedMuezzin: String = "makkah", // "makkah", "madinah"
    val isLockscreenTrackerEnabled: Boolean = false,
    val prayerOverrides: Map<String, String> = emptyMap(),

    // Dark Mode (Financy Fintech Adaptive Theme)
    val isDarkMode: Boolean = false
)

class NamazViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("namaz_app_prefs", Context.MODE_PRIVATE)

    private val database = AppDatabase.getDatabase(application)
    private val repository = PrayerRepository(
        database.prayerLogDao(),
        database.qazaDao(),
        database.quranDao(),
        database.hadithDao(),
        database.hadithChapterDao(),
        database.hadithFullDao(),
        database.hadithLastReadDao(),
        database.tasbihDao(),
        database.settingsDao()
    )

    private val _uiState = MutableStateFlow(
        NamazUiState(
            isBangla = prefs.getBoolean("is_bangla", true),
            isHanafiAsr = prefs.getBoolean("is_hanafi_asr", true),
            prayerNotificationsEnabled = prefs.getBoolean("prayer_notifications_enabled", true),
            notificationSoundMode = prefs.getString("notification_sound_mode", "full_azan") ?: "full_azan",
            selectedMuezzin = prefs.getString("selected_muezzin", "makkah") ?: "makkah",
            isLockscreenTrackerEnabled = prefs.getBoolean("lockscreen_tracker_enabled", false),
            prayerOverrides = run {
                val map = mutableMapOf<String, String>()
                listOf("fajr", "dhuhr", "asr", "maghrib", "isha").forEach { key ->
                    prefs.getString("override_$key", null)?.let { mode ->
                        map[key] = mode
                    }
                }
                map
            },
            isDarkMode = prefs.getBoolean("is_dark_mode", false),
            selectedCity = run {
                val savedName = prefs.getString("selected_city_en", "Dhaka")
                val found = PrayerTimeCalculator.predefinedCities.find {
                    it.nameEn == savedName || it.nameEn.startsWith(savedName ?: "Dhaka")
                }
                if (found != null) {
                    found
                } else if (prefs.contains("custom_city_lat")) {
                    val lat = prefs.getFloat("custom_city_lat", 23.8103f).toDouble()
                    val lng = prefs.getFloat("custom_city_lng", 90.4125f).toDouble()
                    val bn = prefs.getString("selected_city_bn", "ঢাকা") ?: "ঢাকা"
                    CityLocation(savedName ?: "Custom", bn, lat, lng, 6.0)
                } else {
                    PrayerTimeCalculator.predefinedCities[0]
                }
            }
        )
    )
    val uiState: StateFlow<NamazUiState> = _uiState.asStateFlow()

    init {
        // Create Notification Channel & Schedule Waqt Alarms
        PrayerNotificationHelper.createNotificationChannel(application)
        PrayerAlarmScheduler.scheduleAllPrayers(application)
        PrayerWidgetProvider.updateAllWidgets(application)

        // Calculate initial prayer times with restored city & Hanafi settings
        val initialCal = Calendar.getInstance()
        val initialTimes = PrayerTimeCalculator.calculateTimes(
            initialCal,
            _uiState.value.selectedCity,
            _uiState.value.isHanafiAsr
        )
        val initialBearing = QiblaHelper.calculateQiblaDirection(
            _uiState.value.selectedCity.latitude,
            _uiState.value.selectedCity.longitude
        )
        _uiState.update {
            it.copy(prayerTimes = initialTimes, qiblaBearing = initialBearing)
        }
        // Observe Qaza
        viewModelScope.launch {
            repository.allQazaRecords.collect { qazas ->
                _uiState.update { it.copy(qazaRecords = qazas) }
            }
        }

        // Observe Quran Surahs
        viewModelScope.launch {
            repository.allSurahs.collect { list ->
                _uiState.update { it.copy(surahs = list) }
            }
        }

        // Observe Quran Bookmarks & Last read
        viewModelScope.launch {
            repository.quranBookmarks.collect { bms ->
                _uiState.update { it.copy(quranBookmarks = bms) }
            }
        }
        viewModelScope.launch {
            repository.lastRead.collect { lr ->
                _uiState.update { it.copy(quranLastRead = lr) }
            }
        }

        // Observe Hadith Categories, Books, All Hadiths & Bookmarks
        viewModelScope.launch {
            repository.hadithCategories.collect { cats ->
                _uiState.update { it.copy(hadithCategories = cats) }
            }
        }
        viewModelScope.launch {
            repository.hadithBooks.collect { books ->
                _uiState.update { it.copy(hadithBooks = books) }
            }
        }
        viewModelScope.launch {
            repository.allHadiths.collect { hadiths ->
                _uiState.update { it.copy(allHadiths = hadiths) }
            }
        }
        viewModelScope.launch {
            val randomH = repository.getRandomDailyHadith()
            _uiState.update { it.copy(dailyHadith = randomH) }
        }
        viewModelScope.launch {
            repository.hadithBookmarks.collect { bms ->
                _uiState.update { it.copy(hadithBookmarks = bms) }
            }
        }
        viewModelScope.launch {
            repository.hadithLastRead.collect { hlr ->
                _uiState.update { it.copy(hadithLastRead = hlr) }
            }
        }

        // Observe Tasbihs
        viewModelScope.launch {
            repository.allTasbihs.collect { tasbihList ->
                _uiState.update { current ->
                    val selId = if (tasbihList.any { it.id == current.selectedTasbihId }) {
                        current.selectedTasbihId
                    } else {
                        tasbihList.firstOrNull()?.id ?: 1L
                    }
                    current.copy(tasbihs = tasbihList, selectedTasbihId = selId)
                }
            }
        }

        // Observe prayers for selected date
        observePrayersForDate()

        // Observe streak & analytics
        viewModelScope.launch {
            repository.getRecentLogs(30).collect { logs ->
                computeStreak(logs)
            }
        }

        // Streak & analytics observed on init
    }

    private var compassJob: Job? = null

    fun startCompass() {
        if (compassJob?.isActive == true) return
        compassJob = viewModelScope.launch {
            QiblaHelper.getCompassHeading(getApplication<Application>()).collect { heading ->
                _uiState.update { current ->
                    val bearing = current.qiblaBearing
                    var diff = Math.abs(heading - bearing)
                    if (diff > 180f) diff = 360f - diff
                    current.copy(
                        compassHeading = heading,
                        isAlignedWithQibla = diff < 6.0f
                    )
                }
            }
        }
    }

    fun stopCompass() {
        compassJob?.cancel()
        compassJob = null
    }

    private fun observePrayersForDate() {
        viewModelScope.launch {
            _uiState
                .map { it.selectedDate }
                .distinctUntilChanged()
                .flatMapLatest { date -> repository.getPrayersForDate(date) }
                .collect { logs ->
                    val fardhPrayers = setOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
                    val completed = logs.count {
                        it.prayerName in fardhPrayers && (it.status == "jamaah" || it.status == "on_time")
                    }
                    _uiState.update {
                        it.copy(
                            currentDayLogs = logs,
                            totalPrayedToday = completed
                        )
                    }
                }
        }
    }

    private fun computeStreak(logs: List<PrayerLogEntity>) {
        val fardh = setOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
        val byDate = logs.groupBy { it.date }

        val cal = Calendar.getInstance()
        var streak = 0
        val today = DateUtil.getTodayIso()

        val todayDone = (byDate[today] ?: emptyList()).count {
            it.prayerName in fardh && (it.status == "jamaah" || it.status == "on_time")
        }
        if (todayDone >= 5) streak++

        for (i in 1..30) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val d = DateUtil.getIsoForCalendar(cal)
            val done = (byDate[d] ?: emptyList()).count {
                it.prayerName in fardh && (it.status == "jamaah" || it.status == "on_time")
            }
            if (done >= 5) streak++ else break
        }

        val calW = Calendar.getInstance()
        var weekCount = 0
        for (i in 0..6) {
            val d = DateUtil.getIsoForCalendar(calW)
            weekCount += (byDate[d] ?: emptyList()).count {
                it.prayerName in fardh && (it.status == "jamaah" || it.status == "on_time")
            }
            calW.add(Calendar.DAY_OF_YEAR, -1)
        }
        val rate = ((weekCount.toFloat() / 35f) * 100).toInt().coerceIn(0, 100)

        _uiState.update {
            it.copy(
                currentStreakDays = streak,
                weeklyCompletionRate = rate
            )
        }
    }

    fun changeDateOffset(offset: Int) {
        val cal = DateUtil.getCalendarFromIso(_uiState.value.selectedDate)
        cal.add(Calendar.DAY_OF_YEAR, offset)
        val newDate = DateUtil.getIsoForCalendar(cal)
        val times = PrayerTimeCalculator.calculateTimes(
            cal,
            _uiState.value.selectedCity,
            _uiState.value.isHanafiAsr
        )
        _uiState.update {
            it.copy(selectedDate = newDate, prayerTimes = times)
        }
    }

    fun resetToToday() {
        val today = DateUtil.getTodayIso()
        val times = PrayerTimeCalculator.calculateTimes(
            Calendar.getInstance(),
            _uiState.value.selectedCity,
            _uiState.value.isHanafiAsr
        )
        _uiState.update {
            it.copy(selectedDate = today, prayerTimes = times)
        }
    }

    fun updatePrayerStatus(prayerName: String, status: String, context: Context) {
        HapticUtil.vibrateClick(context)
        viewModelScope.launch {
            val currDate = _uiState.value.selectedDate
            val existing = _uiState.value.currentDayLogs.find { it.prayerName == prayerName }
            val newStatus = if (existing?.status == status) "pending" else status
            repository.updatePrayerStatus(
                currDate,
                prayerName,
                newStatus,
                existing?.isSunnah ?: 0,
                existing?.notes ?: ""
            )
            if (newStatus == "jamaah" || newStatus == "on_time") {
                HapticUtil.vibrateMilestone(context)
            }
        }
    }

    fun toggleSunnah(prayerName: String, context: Context) {
        HapticUtil.vibrateClick(context)
        viewModelScope.launch {
            repository.toggleSunnah(_uiState.value.selectedDate, prayerName)
        }
    }

    fun updateNotes(prayerName: String, notes: String) {
        viewModelScope.launch {
            repository.updateNotes(_uiState.value.selectedDate, prayerName, notes)
        }
    }

    fun toggleRoza() {
        _uiState.update { it.copy(isRozaObservedToday = !it.isRozaObservedToday) }
    }

    // Qaza
    fun adjustQaza(prayerName: String, action: String, context: Context) {
        HapticUtil.vibrateClick(context)
        viewModelScope.launch {
            repository.adjustQaza(prayerName, action)
        }
    }

    fun setQazaDirect(prayerName: String, count: Int) {
        viewModelScope.launch {
            repository.setQazaDirect(prayerName, count)
        }
    }

    // Quran
    fun openSurah(surah: QuranSurahEntity) {
        _uiState.update { it.copy(selectedSurah = surah) }
        viewModelScope.launch {
            repository.getAyahsForSurah(surah.surahNo).collect { ayahs ->
                _uiState.update { it.copy(selectedSurahAyahs = ayahs) }
            }
        }
    }

    fun closeSurah() {
        _uiState.update { it.copy(selectedSurah = null, selectedSurahAyahs = emptyList()) }
    }

    fun toggleQuranBookmark(surahNo: Int, ayahNo: Int, context: Context) {
        HapticUtil.vibrateClick(context)
        viewModelScope.launch {
            repository.toggleQuranBookmark(surahNo, ayahNo)
        }
    }

    fun setQuranLastRead(surahNo: Int, ayahNo: Int, context: Context) {
        HapticUtil.vibrateClick(context)
        viewModelScope.launch {
            repository.setLastRead(surahNo, ayahNo)
        }
    }

    // Hadith
    fun selectHadithCategory(categoryId: String) {
        _uiState.update { it.copy(selectedHadithCategory = categoryId) }
    }

    fun selectHadithBook(bookId: String) {
        _uiState.update { it.copy(selectedHadithBook = bookId) }
        viewModelScope.launch {
            repository.getChaptersForBook(bookId).collect { chapters ->
                _uiState.update { it.copy(selectedHadithBookChapters = chapters) }
            }
        }
    }

    fun openHadithChapter(bookId: String, chapterNo: Int) {
        viewModelScope.launch {
            val chapters = _uiState.value.selectedHadithBookChapters
            val target = chapters.find { it.chapterNo == chapterNo }
            _uiState.update { it.copy(selectedHadithChapter = target) }
            repository.getHadithsForChapter(bookId, chapterNo).collect { fullHadiths ->
                _uiState.update { it.copy(selectedHadithChapterFullHadiths = fullHadiths) }
            }
        }
    }

    fun closeHadithChapter() {
        _uiState.update {
            it.copy(
                selectedHadithChapter = null,
                selectedHadithChapterFullHadiths = emptyList()
            )
        }
    }

    fun setHadithLastRead(bookId: String, chapterNo: Int, hadithNo: Int, context: Context) {
        HapticUtil.vibrateClick(context)
        viewModelScope.launch {
            repository.setHadithLastRead(bookId, chapterNo, hadithNo)
        }
    }

    fun toggleShowOnlyHadithBookmarks() {
        _uiState.update { it.copy(showOnlyHadithBookmarks = !it.showOnlyHadithBookmarks) }
    }

    fun resetHadithFilters() {
        _uiState.update {
            it.copy(
                selectedHadithCategory = "all",
                selectedHadithBook = "all",
                showOnlyHadithBookmarks = false
            )
        }
    }

    fun toggleHadithBookmark(hadithId: Long, context: Context) {
        HapticUtil.vibrateClick(context)
        viewModelScope.launch {
            repository.toggleHadithBookmark(hadithId)
        }
    }

    // Tasbih
    fun selectTasbih(id: Long) {
        _uiState.update { it.copy(selectedTasbihId = id) }
    }

    fun incrementTasbih(id: Long, context: Context) {
        viewModelScope.launch {
            val item = _uiState.value.tasbihs.find { it.id == id } ?: return@launch
            if ((item.currentCount + 1) % item.targetCount == 0) {
                HapticUtil.vibrateMilestone(context)
            } else {
                HapticUtil.vibrateClick(context)
            }
            repository.incrementTasbih(id)
        }
    }

    fun resetTasbih(id: Long, context: Context) {
        HapticUtil.vibrateClick(context)
        viewModelScope.launch {
            repository.resetTasbih(id)
        }
    }

    fun addCustomTasbih(
        title: String,
        arabic: String,
        translit: String,
        meaning: String,
        target: Int
    ) {
        viewModelScope.launch {
            repository.addCustomTasbih(
                TasbihCounterEntity(
                    title = title,
                    arabicText = arabic,
                    transliteration = translit,
                    translationBn = meaning,
                    translationEn = meaning,
                    targetCount = target,
                    isPreset = false
                )
            )
        }
    }

    fun addCustomTasbih(item: TasbihCounterEntity) {
        viewModelScope.launch {
            repository.addCustomTasbih(item)
        }
    }

    fun setLanguage(isBangla: Boolean) {
        if (_uiState.value.isBangla == isBangla) return
        prefs.edit().putBoolean("is_bangla", isBangla).apply()
        _uiState.update { it.copy(isBangla = isBangla) }
        PrayerWidgetProvider.updateAllWidgets(getApplication())
    }

    fun toggleLanguage() {
        setLanguage(!_uiState.value.isBangla)
    }

    fun selectCity(city: CityLocation) {
        prefs.edit()
            .putString("selected_city_en", city.nameEn)
            .putString("selected_city_bn", city.nameBn)
            .putFloat("custom_city_lat", city.latitude.toFloat())
            .putFloat("custom_city_lng", city.longitude.toFloat())
            .apply()
        val cal = DateUtil.getCalendarFromIso(_uiState.value.selectedDate)
        val times = PrayerTimeCalculator.calculateTimes(cal, city, _uiState.value.isHanafiAsr)
        val bearing = QiblaHelper.calculateQiblaDirection(city.latitude, city.longitude)
        _uiState.update {
            it.copy(
                selectedCity = city,
                prayerTimes = times,
                qiblaBearing = bearing
            )
        }
        PrayerAlarmScheduler.scheduleAllPrayers(getApplication())
        PrayerWidgetProvider.updateAllWidgets(getApplication())
    }

    fun toggleHanafiAsr() {
        val newHanafi = !_uiState.value.isHanafiAsr
        prefs.edit().putBoolean("is_hanafi_asr", newHanafi).apply()
        val cal = DateUtil.getCalendarFromIso(_uiState.value.selectedDate)
        val times = PrayerTimeCalculator.calculateTimes(cal, _uiState.value.selectedCity, newHanafi)
        _uiState.update {
            it.copy(
                isHanafiAsr = newHanafi,
                prayerTimes = times
            )
        }
        PrayerAlarmScheduler.scheduleAllPrayers(getApplication())
        PrayerWidgetProvider.updateAllWidgets(getApplication())
    }

    // Financy Theme: Dark / Light Mode Toggle
    fun toggleDarkMode() {
        val newDark = !_uiState.value.isDarkMode
        prefs.edit().putBoolean("is_dark_mode", newDark).apply()
        _uiState.update { it.copy(isDarkMode = newDark) }
    }

    // Notifications
    fun togglePrayerNotifications(context: Context) {
        val newVal = !_uiState.value.prayerNotificationsEnabled
        prefs.edit().putBoolean("prayer_notifications_enabled", newVal).apply()
        _uiState.update { it.copy(prayerNotificationsEnabled = newVal) }
        if (newVal) {
            PrayerAlarmScheduler.scheduleAllPrayers(context)
        } else {
            PrayerAlarmScheduler.cancelAllAlarms(context)
            AzanPlaybackService.stopAzan(context)
        }
    }

    fun setNotificationSoundMode(mode: String) {
        prefs.edit().putString("notification_sound_mode", mode).apply()
        _uiState.update { it.copy(notificationSoundMode = mode) }
    }

    fun setSelectedMuezzin(muezzin: String) {
        prefs.edit().putString("selected_muezzin", muezzin).apply()
        _uiState.update { it.copy(selectedMuezzin = muezzin) }
    }

    fun toggleLockscreenTracker(context: Context) {
        val newVal = !_uiState.value.isLockscreenTrackerEnabled
        prefs.edit().putBoolean("lockscreen_tracker_enabled", newVal).apply()
        _uiState.update { it.copy(isLockscreenTrackerEnabled = newVal) }
        if (newVal) {
            PrayerNotificationHelper.updateOngoingTrackerNotification(context, _uiState.value.isBangla)
        } else {
            PrayerNotificationHelper.cancelOngoingTrackerNotification(context)
        }
    }

    fun setPrayerOverride(prayerKey: String, soundMode: String?) {
        val current = _uiState.value.prayerOverrides.toMutableMap()
        if (soundMode != null) {
            prefs.edit().putString("override_$prayerKey", soundMode).apply()
            current[prayerKey] = soundMode
        } else {
            prefs.edit().remove("override_$prayerKey").apply()
            current.remove(prayerKey)
        }
        _uiState.update { it.copy(prayerOverrides = current) }
    }

    fun sendTestNotification(context: Context) {
        PrayerNotificationHelper.showTestNotification(context, _uiState.value.isBangla)
    }

    // Data Backup & Export
    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val logs = repository.getAllPrayerLogsList()
            val qazas = repository.getAllQazaList()
            val json = BackupRestoreHelper.createBackupJson(logs, qazas)
            onResult(json)
        }
    }

    fun restoreBackup(jsonString: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val parsed = BackupRestoreHelper.parseBackupJson(jsonString)
            if (parsed == null) {
                onComplete(false, if (_uiState.value.isBangla) "ভুল বা অকার্যকর JSON ফরম্যাট" else "Invalid JSON backup format")
                return@launch
            }
            try {
                if (parsed.prayerLogs.isNotEmpty()) {
                    repository.restorePrayerLogs(parsed.prayerLogs)
                }
                if (parsed.qazaRecords.isNotEmpty()) {
                    repository.restoreQazaRecords(parsed.qazaRecords)
                }
                PrayerWidgetProvider.updateAllWidgets(getApplication())
                val successMsg = if (_uiState.value.isBangla) {
                    "সফলভাবে ${parsed.prayerLogs.size}টি নামায রেকর্ড ও ${parsed.qazaRecords.size}টি কাযা রেকর্ড রিস্টোর হয়েছে!"
                } else {
                    "Successfully restored ${parsed.prayerLogs.size} prayer logs & ${parsed.qazaRecords.size} Qaza records!"
                }
                onComplete(true, successMsg)
            } catch (e: Exception) {
                onComplete(false, e.localizedMessage ?: "Restore failed")
            }
        }
    }
}
