# Application Flow & Navigation (AppFlow) - Namaz Tracker
**App Version:** `v2.5.0` (Build 7)  
**Navigation Model:** Single-Activity Compose Architecture with Floating Frosted Glass Dock

---

## 1. Top-Level Navigation Map (5 Tabs)

```
                       [ App Entry / Splash ]
                                 │
                                 ▼
                     [ MainActivity (Compose) ]
                                 │
         ┌───────────────┬───────┴───────┬───────────────┬───────────────┐
         ▼               ▼               ▼               ▼               ▼
     [ Tab 1 ]       [ Tab 2 ]       [ Tab 3 ]       [ Tab 4 ]       [ Tab 5 ]
      Prayers          Quran          Ibadah          Tracker          More
    (Daily Waqt)    (Quran/Hadith)  (Dhikr/Dua)     (Analytics)     (Settings)
```

---

## 2. Screen-by-Screen User Flows

### 2.1 Tab 1: Prayers (`HomeScreen.kt`)
1. **Header Bar**:
   - App branding and current Gregorian + Hijri date.
   - Interactive City Badge: Tap opens `CitySelectionDialog` with quick search across 64 Bangladesh districts & global coordinates.
2. **Next Waqt Hero Card**:
   - Live countdown timer showing remaining minutes until next prayer.
   - Dynamic prayer window badge.
3. **5 Daily Prayer Cards (`PrayerCard.kt`)**:
   - Displays Waqt name, start time, and end time.
   - Interactive Status Pill: Quick tap cycles through `Jama'at`, `On-Time`, `Late`, `Qaza`, `Missed`.
   - Sunnah toggle pill switches companion Sunnah status.
4. **Additional Prayers**:
   - Expanding row for Tahajjud, Ishraq, Chasht, and Awwabin timings.

---

### 2.2 Tab 2: Quran & Hadith (`QuranHadithScreen.kt`)
1. **Top Segmented Control**:
   - `[Al-Quran]` (আল-কুরআন) vs `[Hadith Sharif]` (হাদীস শরীফ).
2. **Quran Flow (`QuranScreen.kt`)**:
   - Search bar for instant Surah filtering by name or number.
   - Last Read card with one-tap "পড়ুন" (Resume) button.
   - 114 Surah list items: Tap opens full-screen **Surah Ayah View**.
   - Ayah View: Play audio recitation, copy Ayah text, toggle bookmark, or mark as last read. Back button returns to Surah index.
3. **Hadith Flow (`HadithScreen.kt`)**:
   - 4-pill dock: `Categories`, `Books`, `Search`, `Saved`.
   - Books drill-down: Select Book (e.g. Sahih Bukhari) -> Chapter index -> Hadith detail with Arabic, Bengali translation, and narrator grade.

---

### 2.3 Tab 3: Ibadah & Tools (`IbadahScreen.kt`)
1. **Daily Dhikr Card**:
   - Featured Dhikr with one-tap count button.
2. **Spiritual Tools Grid**:
   - **Digital Tasbih**: Opens `TasbihScreen.kt` with full-screen circular touch area, lap counter, preset picker (33, 99, 100), and custom dhikr creator.
   - **Du'as & Adhkar**: Opens `DuaAdhkarScreen.kt` with filter chips (Morning, Evening, After Salah, Protection) and repetition counters.
   - **Asmaul Husna**: Opens `AsmaulHusnaScreen.kt` showing 99 Names grid with dialog detail for virtues and meaning.

---

### 2.4 Tab 4: Tracker & Analytics (`TrackerScreen.kt`)
1. **Period Switcher**:
   - iOS Glass Segmented Control: `Weekly`, `Monthly`, `Yearly`, `History`.
2. **Consistency Analytics**:
   - Completion rate bar and breakdown ring (Jama'at, Solitary, Missed).
   - Historical date inspection: Calendar picker allows viewing prayer logs for any past date.
3. **Qaza Umri Manager (`QazaScreen.kt`)**:
   - Accessible directly from Tracker tab.
   - Waqt steppers: Tap `+1 Missed` to increment or `Prayed ✓` to mark recovered.
   - Lifetime calculator modal calculates total missed prayers based on years and gender rules.

---

### 2.5 Tab 5: Settings & Guides (`MoreScreen.kt`)
1. **Top Bar**:
   - App title with live **`v2.2.0`** status pill badge.
2. **Utilities**:
   - **Learning Guides (`GuidesScreen.kt`)**: Step-by-step prayer & wudu instructions.
   - **Qibla Compass (`QiblaScreen.kt`)**: Sensor-driven compass pointing to the Kaaba.
   - **Language Toggle**: Instant switch between English and Bengali without app restart.
   - **Notification Manager**: Configure Waqt reminders and test system notifications.
   - **Backup & Restore**: Export full JSON payload or restore from clipboard/file.
