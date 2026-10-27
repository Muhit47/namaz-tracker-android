# Product Requirements Document (PRD) - Namaz Tracker (নামায ট্র্যাকার)
**App Version:** `v2.5.0` (Build 7)  
**Platform:** Native Android (Kotlin + Jetpack Compose)  
**Target SDK:** 36 (Android 14+ compatible, minSdk 24)

---

## 1. Overview & Vision
**Namaz Tracker** (নামায ট্র্যাকার) is a privacy-first, bilingual (Bengali & English) native Android application crafted for Muslim daily life and spiritual habit tracking. Designed with an **iOS-style Frosted Glassmorphic & Rounded Pill UI design system**, the app operates completely offline with an embedded SQLite Room database containing the complete Al-Quran (114 Surahs with Bengali/English translations and audio), 6 authentic Hadith collections, lifetime Qaza prayer management, digital Tasbih counters, comprehensive daily Duas, and detailed prayer guidelines.

All user logs and spiritual progress remain 100% on-device with zero required cloud logins, offering zero data lock-in via one-tap JSON backup and restore.

---

## 2. Target Audience & Personas
- **Daily Practicing Muslims**: Track 5 daily prayers (Fajr, Dhuhr, Asr, Maghrib, Isha) with Jama'at status, on-time completion, and accompanying Sunnah prayers.
- **Qaza Rectifiers**: Systematically calculate, track, and decrement lifetime missed (Qaza Umri) prayers with waqt-by-waqt quick action steppers.
- **Quran & Hadith Students**: Read, search, recite, and bookmark 114 Surahs and authentic Hadith books offline.
- **Daily Dhikr & Dua Performers**: Digital tactile Tasbih counter with target milestones, haptic feedback, and categorized morning/evening Adhkar.
- **Bilingual Users**: Seamless instant toggle between native Bengali (বাংলা) and English across all tabs and components.

---

## 3. Core Architecture & Navigation (5 Main Tabs)

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Namaz Tracker v2.2.0                            │
├────────────────────────────────────────────────────────────────────────┤
│  [1. Prayers]    [2. Quran]    [3. Ibadah]    [4. Tracker]   [5. More] │
│   (Home Tab)    (Surah/Hadith) (Dhikr/Dua)     (History)    (Settings) │
└────────────────────────────────────────────────────────────────────────┘
```

### 3.1 Tab 1: Prayers & Today (`HomeScreen.kt`, `PrayerCard.kt`)
- **Next/Current Waqt Hero Card**: Real-time remaining time countdown, current prayer window detection, and city location selector (Dhaka and all BD districts + worldwide).
- **5 Daily Prayer Cards**: Fajr, Dhuhr, Asr, Maghrib, Isha with dual status tracking:
  - Fard Prayer Status: `Pending`, `Jama'at` (জামাত), `On-Time` (আদায়), `Late` (দেরিতে), `Qaza` (কাযা), `Missed` (মিস).
  - Sunnah Toggle: Companion Sunnah indicator.
  - Tactile iOS Pill Status Selector with instant haptic ticks and micro-spring scale animations.
- **Additional Prayers Card**: Tahajjud, Ishraq, Chasht, and Awwabin prayer guidelines and timings.
- **Friday Jumu'ah Indicator**: Contextual Friday prayer mode replacing Dhuhr when active.

### 3.2 Tab 2: Al-Quran & Authentic Hadith (`QuranHadithScreen.kt`)
- **iOS Frosted Glass Sub-Tab Switcher**: Seamless switching between **Al-Quran** and **Hadith Sharif**.
- **Al-Quran Reader (`QuranScreen.kt`)**:
  - Full catalog of 114 Surahs with Arabic script, Bengali/English phonetics, revelation types (Makki/Madani), and Ayah counts.
  - Last read resume indicator pill.
  - Interactive Ayah Reader: Ayah-by-ayah Arabic text, Bengali transliteration, Bengali/English translations, and audio player integration.
  - Ayah bookmarking and clipboard copy.
- **Authentic Hadith Collections (`HadithScreen.kt`)**:
  - 8 Thematic Categories (Faith, Prayer, Character, Knowledge, etc.).
  - 6 Canonical Books: Sahih Bukhari, Sahih Muslim, Sunan an-Nasa'i, Sunan Abi Dawud, Jami' at-Tirmidhi, Sunan Ibn Majah.
  - Fast offline full-text search across narrators, Arabic text, and Bengali translations.
  - Hadith bookmarking and chapter drill-downs.

### 3.3 Tab 3: Ibadah & Spiritual Tools (`IbadahScreen.kt`)
- **Featured Daily Dhikr Card**: One-tap quick counter with preset targets (e.g., 100 times).
- **Spiritual Tool Suite**:
  1. **Digital Tasbih (`TasbihScreen.kt`)**: Tactile full-screen bead/counter with haptic feedback, audio click option, lap counters, reset controls, and custom dhikr creator.
  2. **Daily Du'as & Adhkar (`DuaAdhkarScreen.kt`)**: Categorized Duas (Morning, Evening, Namaz, Protection) with target repetitions and completion badges.
  3. **Asmaul Husna (`AsmaulHusnaScreen.kt`)**: 99 Beautiful Names of Allah with Arabic calligraphy, Bengali phonetics, and detailed spiritual virtues.

### 3.4 Tab 4: Tracker & Consistency Analytics (`TrackerScreen.kt`, `QazaScreen.kt`)
- **iOS Glass Segmented Control**: Fast switching between Weekly, Monthly, Yearly, and History periods.
- **Visual Progress Graphs**: Completion percentage bar, breakdown metrics (Jama'at vs Solitary vs Missed), and consistency streaks.
- **Interactive Calendar Log**: Date picker to inspect historical prayer logs for any past date.
- **Qaza Umri Hub (`QazaScreen.kt`)**:
  - Dedicated steppers for all 5 prayers: `+1 Missed` and `Prayed ✓` quick actions.
  - Lifetime missed prayers calculator based on years of missed obligations.
  - Recovery progress percentage and remaining count tracking.

### 3.5 Tab 5: Settings & Utilities (`MoreScreen.kt`)
- **App Version & Build Status**: Displays **`v2.2.0` (Build 4)** in top bar badge and footer.
- **Step-by-Step Learning Guides (`GuidesScreen.kt`)**: Detailed prayer and wudu visual postures, conditions, and invalidators.
- **Qibla Compass (`QiblaScreen.kt`)**: Device sensor-based compass pointing toward the Kaaba in Makkah.
- **Language Switcher**: Instant app-wide toggle between Bengali and English.
- **App Theme**: Modern Emerald Light and Dark modes.
- **Notifications & Reminders**: Daily prayer notification scheduler with test notification trigger.
- **Data Safety**: Offline JSON export and import for seamless multi-device backup and recovery.

---

## 4. Non-Functional Requirements
- **Performance**: Zero-latency offline operation; Room DB queries dispatched via Kotlin Coroutines on `Dispatchers.IO`.
- **UI & UX Quality**: iOS-style frosted glassmorphism, 999.dp capsule rounded pills, multi-stop crystalline rim gradient borders, and `Spring.DampingRatioMediumBouncy` press animations.
- **Zero Cloud Dependence**: User prayer records, bookmarks, and counters remain confidential on local storage.
