# Project Plan & Roadmap (Plan) - Namaz Tracker (নামায ট্র্যাকার)
**App Version:** `v2.6.0` (Build 8)  
**Branch:** `android`  
**Latest Remote Commit:** `d91a9eb`

---

## 1. Project Current Status
- **Architecture**: Native Android Application in Kotlin + Jetpack Compose (Material3 + Custom Frosted Glass Primitives).
- **Embedded Database**: AndroidX Room Database 2.6.1 (`namaz_tracker.db`) containing 114 Surahs, authentic Hadith collections, and user state.
- **UI Design System**: Unified **iOS Frosted Glassmorphism & Rounded Pill System** (`IosGlassPill.kt`, `FinancyNavBar.kt`) with `RoundedCornerShape(999.dp)`, crystalline rim gradients, and micro-spring touch feedback.
- **Working Tree**: Clean on branch `android`, synced with `origin/android`.

---

## 2. Testable Acceptance Criteria

| ID | Area | Acceptance Criterion | Verification Method |
|:---|:---|:---|:---|
| **AC-1** | App Versioning | `app/build.gradle.kts` specifies `applicationId = "com.namaztracker.app"`, `versionCode = 8` and `versionName = "2.6.0"`. `MoreScreen` displays dynamic version via `AppVersionHelper` (`v2.6.0` / `ভার্সন ২.৬.০`). | Verify `build.gradle.kts` and UI text match. |
| **AC-2** | Navigation UI | Floating bottom dock (`FinancyNavBar`) uses `RoundedCornerShape(999.dp)` capsule shape with 5 pure vector icon tabs. | Inspect `FinancyNavBar.kt` shape and icon assets. |
| **AC-3** | Pill Buttons & Chips | All primary/secondary action buttons and status chips use `IosGlassPillButton` / `IosGlassChip` with `RoundedCornerShape(999.dp)`. | Inspect Compose files across all 5 tabs. |
| **AC-4** | Segmented Docks | Sub-tab switchers (Quran/Hadith, Tracker periods, Qaza tabs) use `IosGlassSegmentedControl`. | Inspect `QuranHadithScreen.kt`, `TrackerScreen.kt`, `QazaScreen.kt`. |
| **AC-5** | Prayer Logging | Tapping quick status pills updates prayer status (`jamaah`, `on_time`, `late`, `missed`) with haptic feedback. | Test UI event dispatches to `NamazViewModel.updatePrayerStatus()`. |
| **AC-6** | Qaza Counter | Tap `+1 Missed` increments missed count; tap `Prayed ✓` decrements remaining count and increments completed count. | Verify `NamazViewModel.addOneMissed()` and `recordQazaCompleted()`. |
| **AC-7** | Localization | Toggling language in `MoreScreen` switches all strings between Bengali and English dynamically. | Verify `uiState.isBangla` propagates across all screens. |
| **AC-8** | Room Database | Room DB boots from `namaz_tracker.db` asset with 15 tables without schema mismatch. | Validate `NamazDatabase.kt` entity list and asset integrity. |

---

## 3. Completed Milestones

### Milestone 1: Core Android Migration & Room DB Integration (`v2.0.0`, Build 2)
- [x] Migrated from initial web prototype to native Kotlin + Jetpack Compose Android app.
- [x] Prepackaged SQLite database with full Al-Quran (114 Surahs with Bengali transliteration & translation) and Hadith books into Room DB.
- [x] Implemented MVVM architecture with `NamazViewModel` and `NamazRepository`.

### Milestone 2: Pure Vector Navigation & Asset Integration (`v2.1.0`, Build 3)
- [x] Replaced generic raster icons with pure XML Android Vector Drawables:
  - Al-Quran (Sacred Book with ribbon)
  - Namaz (Minaret & Dome Mosque)
  - Ibadah (Cupped Praying Hands with crescent & star)
  - Tracker (3-Bar upward trending analytics chart)
  - More (4-Tile grid)
- [x] Added dual filled (active) and outlined (inactive) vector states for seamless dark/light theme support.

### Milestone 3: Full iOS Frosted Glass & Rounded Pill Overhaul (`v2.2.0`, Build 4)
- [x] Created `IosGlassPill.kt` primitive library:
  - `IosGlassPillButton`: Translucent glass body, crystalline rim gradient border, bouncy spring scale (`0.95f`), and tactile haptic feedback.
  - `IosGlassChip`: Capsule status chip with subtle glow.
  - `IosGlassSegmentedControl`: Apple-inspired floating capsule dock switcher.
- [x] Upgraded Floating Bottom Dock (`FinancyNavBar.kt`) to `RoundedCornerShape(999.dp)` frosted glass capsule.
- [x] Eliminated all boxy rectangular buttons (`10.dp` / `12.dp`) across all 5 main tabs and secondary screens.
- [x] Bumped version to `v2.2.0` (Build 4) across `app/build.gradle.kts` and `MoreScreen.kt`.

---

## 4. Remaining Tasks & Roadmap

### Milestone 5: Nothing OS + iOS Frosted Glass Multi-Widget Suite (`v2.4.0`, Build 6)
- [x] Designed & implemented 4 Nothing OS-inspired Frosted Glass Android App Widgets:
  1. `ActiveWaqtWidgetProvider`: 4x1 & 2x1 capsule showing current waqt, status, and live countdown.
  2. `PrayerWidgetProvider`: 4x2 full daily 5-waqt board with active waqt glowing frosted highlight.
  3. `RamadanWidgetProvider`: 4x2 / 3x2 Ramadan timer showing Sehri end, Iftar, and remaining countdown.
  4. `HadithWidgetProvider`: 4x2 authentic daily Hadith reader with interactive in-widget shuffle button (`ACTION_SHUFFLE_HADITH`).
- [x] Centralized `WidgetHelper` with Bengali numeral translation, countdown formatter, and synchronized multi-widget updates.
- [x] Automated language synchronization: App-wide language toggle immediately updates all 4 widgets.
- [x] Added unit tests in `WidgetProvidersTest.kt` verifying layout updates, countdown formatting, and Hadith shuffling.
- [x] Enhanced `MoreScreen` with Nothing OS & iOS Frosted Glass Widget Hub guide.
- [x] Bumped app version to `v2.4.0` (Build 6).

---

## 4. Remaining Tasks & Roadmap
- [x] **Task 4.1**: Audio Recitation & Adhan Background Service Polish (Completed in `v2.2.0`)
- [x] **Task 4.2**: Automated Compose UI Test Suite Expansion (Completed in `v2.3.0`)
- [x] **Task 4.3**: Home Screen App Multi-Widget Suite Expansion (Completed in `v2.4.0`)
### Milestone 7: Visual Screenshot Gallery & GitHub README Overhaul (`v2.6.0`, Build 8)
- [x] Generated 7 high-resolution device screenshots in `docs/screenshots/`:
  - `00_hero_showcase.png`: Panoramic 1200x600 header banner.
  - `01_namaz_tab.png`: 5 Waqt tracker, active Waqt countdown & status pills.
  - `02_quran_hadith_tab.png`: Complete 114 Surahs, last read card & Hadith books.
  - `03_ibadah_tab.png`: Digital Haptic Tasbih, 360° Qibla compass & Masnoon Duas.
  - `04_tracker_tab.png`: 21-day streak, weekly completion bar chart & Qaza debt counter.
  - `05_more_tab.png`: Smart offline Azan, Muezzin selection, lockscreen ongoing tracker & widget hub.
  - `06_home_widgets.png`: 4 Nothing OS-inspired Frosted Glass widgets on Android launcher.
- [x] Overhauled `README.md` with visual preview gallery tables, technical badges, and bilingual documentation.
- [x] Bumped app version to `v2.6.0` (Build 8).
