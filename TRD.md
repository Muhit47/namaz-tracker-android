# Technical Requirements Document (TRD) - Namaz Tracker (নামায ট্র্যাকার)
**Target Platform:** Android (Native)  
**App Version:** `v2.4.0` (Build 6)  
**Package Name:** `com.aistudio.namaztracker.pqkzlm`

---

## 1. System Architecture

```
┌────────────────────────────────────────────────────────┐
│                   Presentation Layer                   │
│  Jetpack Compose, Material3, iOS Frosted Glass Pills   │
│  FinancyNavBar (Floating Dock), Vector Drawables       │
│  Screen Composables (Home, Quran, Ibadah, Tracker...)  │
└───────────────────────────┬────────────────────────────┘
                            │ StateFlow / Coroutines
┌───────────────────────────▼────────────────────────────┐
│                  ViewModel Layer (MVVM)                │
│  NamazViewModel + NamazUiState                         │
│  Location/Calculation Engine (PrayerTimeCalculator)    │
│  Haptic Engine (HapticUtil) & Audio Player             │
└───────────────────────────┬────────────────────────────┘
                            │ Suspended Repository Calls
┌───────────────────────────▼────────────────────────────┐
│                    Repository Layer                    │
│  NamazRepository (Abstraction over Room DB & Settings) │
└───────────────────────────┬────────────────────────────┘
                            │ Room DAO Queries
┌───────────────────────────▼────────────────────────────┐
│                 Room Database Engine                   │
│  NamazDatabase (SQLite) via prepackaged asset          │
│  namaz_tracker.db (15 Relational Tables)               │
└────────────────────────────────────────────────────────┘
```

---

## 2. Technology Stack & Key Dependencies

- **Language**: Kotlin 2.0+
- **Min SDK / Target SDK**: `minSdk = 24` (Android 7.0), `targetSdk = 36` (Android 14+)
- **UI Toolkit**: Jetpack Compose BOM (`2024.09.00`) with Material 3
- **Architecture**: MVVM with Jetpack Lifecycle (`androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5`)
- **Database**: AndroidX Room Database 2.6.1 (`androidx.room:room-runtime`, `androidx.room:room-ktx`)
- **Coroutines & Asynchrony**: Kotlin Coroutines Android (`org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1`)
- **JSON Serialization**: Google Gson 2.11.0 (for reliable export/import payloads)
- **Audio Recitation**: Android MediaPlayer with streaming audio caching
- **Sensors**: Android SensorManager (`TYPE_ACCELEROMETER`, `TYPE_MAGNETIC_FIELD`) for Qibla compass

---

## 3. Database Layer & Schema Management

- **Asset Prepackaging**: `createFromAsset("namaz_tracker.db")` initialized in `NamazDatabase`.
- **Database Schema**: 15 Room entities registered in `NamazDatabase`:
  1. `PrayerLogEntity` (`prayers_log`)
  2. `QazaRecordEntity` (`qaza_records`)
  3. `QuranSurahEntity` (`quran_surahs`)
  4. `QuranAyahEntity` (`quran_ayahs`)
  5. `QuranBookmarkEntity` (`quran_bookmarks`)
  6. `QuranLastReadEntity` (`quran_last_read`)
  7. `HadithCategoryEntity` (`hadith_categories`)
  8. `HadithBookEntity` (`hadith_books`)
  9. `HadithEntity` (`hadiths`)
  10. `HadithBookmarkEntity` (`hadith_bookmarks`)
  11. `HadithChapterEntity` (`hadith_chapters`)
  12. `HadithFullEntity` (`hadith_full_collection`)
  13. `HadithLastReadEntity` (`hadith_last_read`)
  14. `SettingEntity` (`settings`)
  15. `TasbihCounterEntity` (`tasbih_counters`)
- **Concurrency & Thread Safety**: All DAO writes and bulk queries execute within Kotlin Coroutines on `Dispatchers.IO`.

---

## 4. UI Rendering & Glassmorphism Engine

- **Glassmorphic Capsules**: Implemented purely in Jetpack Compose modifiers using multi-stop `Brush.linearGradient` borders and translucent background alpha stops.
- **Micro-Interactions**:
  - `animateFloatAsState` targeting `0.95f` scale on press with `Spring.DampingRatioMediumBouncy`.
  - Haptic feedback tick emitted via `LocalContext.current` vibrator / haptic feedback constants.
- **Pure Vector Drawables**: Zero PNG/WebP raster assets in navigation. Clean XML Android Vector Drawables with explicit viewport (`24x24` or `512x512`) ensure crisp rendering across all screen densities (mdpi to xxxhdpi).

---

## 5. Security & Privacy
- **100% On-Device Storage**: No telemetry, analytics tracking, or external ad trackers.
- **Zero Cloud Lock-in**: Full JSON backup and restore with transaction verification.
- **Offline Resilient**: Complete Quran and Hadith texts load instantly from local SQLite without requiring internet connectivity.
