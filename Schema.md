# Database Schema (Schema) - Namaz Tracker (নামায ট্র্যাকার)
**Database Engine:** SQLite (managed via AndroidX Room Database 2.6.1)  
**Database File:** Embedded prepackaged asset `app/src/main/assets/namaz_tracker.db`  
**App Version:** `v2.7.0` (Build 9)

---

## 1. Table Summary (15 Relational Tables)

| Table Name | Room Entity Class | Description |
| :--- | :--- | :--- |
| `prayers_log` | `PrayerLogEntity` | Daily prayer records (Fard status, Sunnah flag, notes) |
| `qaza_records` | `QazaRecordEntity` | Lifetime missed and completed Qaza counts per prayer |
| `quran_surahs` | `QuranSurahEntity` | Catalog of 114 Surahs with metadata |
| `quran_ayahs` | `QuranAyahEntity` | Ayah text, Arabic, Bengali/English transliteration & translation |
| `quran_bookmarks` | `QuranBookmarkEntity` | User saved Ayah bookmarks |
| `quran_last_read` | `QuranLastReadEntity` | Singleton tracking last read Surah and Ayah position |
| `hadith_categories` | `HadithCategoryEntity` | 8 Thematic Hadith categories with icons |
| `hadith_books` | `HadithBookEntity` | 6 Canonical Hadith books metadata |
| `hadiths` | `HadithEntity` | Thematic Hadith entries with Arabic & Bengali text |
| `hadith_bookmarks` | `HadithBookmarkEntity` | User saved Hadith bookmarks |
| `hadith_chapters` | `HadithChapterEntity` | Book chapters with hadith counts and ranges |
| `hadith_full_collection` | `HadithFullEntity` | Complete Hadith collection records |
| `hadith_last_read` | `HadithLastReadEntity` | Singleton tracking last read Hadith book and chapter |
| `settings` | `SettingEntity` | Key-value store for app settings (city, language, theme) |
| `tasbih_counters` | `TasbihCounterEntity` | Digital Tasbih dhikr presets and user custom counters |

---

## 2. Table Definitions (Room Schema)

### 2.1 `prayers_log`
```sql
CREATE TABLE IF NOT EXISTS `prayers_log` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `date` TEXT NOT NULL,                     -- ISO format: 'YYYY-MM-DD'
    `prayer_name` TEXT NOT NULL,              -- 'Fajr', 'Dhuhr', 'Asr', 'Maghrib', 'Isha'
    `status` TEXT NOT NULL,                   -- 'pending', 'jamaah', 'on_time', 'late', 'qaza', 'missed'
    `is_sunnah` INTEGER NOT NULL,             -- 0 = No, 1 = Yes
    `notes` TEXT,                             -- Optional note
    `updated_at` TEXT                         -- Timestamp
);
CREATE UNIQUE INDEX IF NOT EXISTS `index_prayers_log_date_prayer_name` 
ON `prayers_log` (`date`, `prayer_name`);
```

### 2.2 `qaza_records`
```sql
CREATE TABLE IF NOT EXISTS `qaza_records` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `prayer_name` TEXT NOT NULL,              -- 'Fajr', 'Dhuhr', 'Asr', 'Maghrib', 'Isha'
    `total_missed` INTEGER NOT NULL,          -- Cumulative missed prayers count
    `completed` INTEGER NOT NULL,             -- Recovered/prayed count
    `updated_at` TEXT                         -- Timestamp
);
```

### 2.3 `quran_surahs` & `quran_ayahs`
```sql
CREATE TABLE IF NOT EXISTS `quran_surahs` (
    `surah_no` INTEGER NOT NULL,
    `name_ar` TEXT NOT NULL,
    `name_bn` TEXT NOT NULL,
    `name_en` TEXT NOT NULL,
    `meaning_bn` TEXT NOT NULL,
    `meaning_en` TEXT,
    `total_ayah` INTEGER NOT NULL,
    `revelation_type` TEXT NOT NULL,          -- 'Meccan' or 'Medinan'
    PRIMARY KEY(`surah_no`)
);

CREATE TABLE IF NOT EXISTS `quran_ayahs` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `surah_no` INTEGER NOT NULL,
    `ayah_no` INTEGER NOT NULL,
    `text_ar` TEXT NOT NULL,
    `translit_bn` TEXT NOT NULL,
    `trans_bn` TEXT NOT NULL,
    `translit_en` TEXT,
    `trans_en` TEXT
);
```

### 2.4 `hadiths` & `hadith_books`
```sql
CREATE TABLE IF NOT EXISTS `hadith_books` (
    `id` TEXT NOT NULL,
    `name_bn` TEXT NOT NULL,
    `name_ar` TEXT NOT NULL,
    `name_en` TEXT,
    PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `hadiths` (
    `id` INTEGER NOT NULL,
    `category_id` TEXT NOT NULL,
    `category_bn` TEXT NOT NULL,
    `book_id` TEXT NOT NULL,
    `book_name_bn` TEXT NOT NULL,
    `chapter_bn` TEXT NOT NULL,
    `narrator_bn` TEXT NOT NULL,
    `text_ar` TEXT NOT NULL,
    `text_bn` TEXT NOT NULL,
    `grade_bn` TEXT NOT NULL,
    `reference_bn` TEXT NOT NULL,
    `translit_bn` TEXT,
    `translit_en` TEXT,
    `text_en` TEXT,
    `category_en` TEXT,
    `book_name_en` TEXT,
    `chapter_en` TEXT,
    `narrator_en` TEXT,
    `grade_en` TEXT,
    `reference_en` TEXT,
    PRIMARY KEY(`id`)
);
```

### 2.5 `tasbih_counters`
```sql
CREATE TABLE IF NOT EXISTS `tasbih_counters` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `title` TEXT NOT NULL,
    `arabicText` TEXT NOT NULL,
    `transliteration` TEXT NOT NULL,
    `translationBn` TEXT NOT NULL,
    `translationEn` TEXT NOT NULL,
    `currentCount` INTEGER NOT NULL,
    `targetCount` INTEGER NOT NULL,
    `totalRecited` INTEGER NOT NULL,
    `isPreset` INTEGER NOT NULL
);
```
