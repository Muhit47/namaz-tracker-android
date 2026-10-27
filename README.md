<div align="center">

# 🕌 Namaz Tracker
### Modern Offline-First Islamic Companion & Spiritual Habit Tracker for Android

[![Version](https://img.shields.io/badge/version-2.7.0%20(Build%209)-emerald?style=for-the-badge&logo=android)](app/build.gradle.kts)
[![Android](https://img.shields.io/badge/Android-API%2024%20to%2036-green?style=for-the-badge&logo=android)](app/build.gradle.kts)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-purple?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room DB](https://img.shields.io/badge/Room%20DB-100%25%20Offline-orange?style=for-the-badge&logo=sqlite)](app/src/main/assets/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

<br/>

<img src="docs/screenshots/00_hero_showcase.png" alt="Namaz Tracker Hero Showcase" width="100%" style="border-radius: 18px; box-shadow: 0 10px 30px rgba(0,0,0,0.4);" />

</div>

---

## 📱 App Screenshots (Per-Tab Visual Showcase)

| 🕌 Daily Prayers & Waqt | 📖 Holy Qur'an & Hadith | 🤲 Ibadah & Tasbih |
| :---: | :---: | :---: |
| <img src="docs/screenshots/01_namaz_tab.png" width="280" alt="Prayers Tab Screenshot" /> | <img src="docs/screenshots/02_quran_hadith_tab.png" width="280" alt="Quran and Hadith Tab Screenshot" /> | <img src="docs/screenshots/03_ibadah_tab.png" width="280" alt="Ibadah and Tasbih Tab Screenshot" /> |
| **1. Prayers Tab (Home)**<br/>Active Waqt hero card, live countdown timer, one-tap status logging (Jama'at / Alone), and 5 daily prayers | **2. Qur'an & Hadith Tab**<br/>Complete 114 Surahs offline, Arabic text, translations, last read bento card & authentic Hadith books | **3. Ibadah & Dhikr Tab**<br/>Tactile haptic digital Tasbih dial (33/100 milestones), 360° Qibla compass & authentic Masnoon Duas |

<br/>

| 📊 Habits & Analytics | ⚙️ Settings & Smart Azan | 📱 Nothing OS Home Widgets |
| :---: | :---: | :---: |
| <img src="docs/screenshots/04_tracker_tab.png" width="280" alt="Habits and Analytics Tab Screenshot" /> | <img src="docs/screenshots/05_more_tab.png" width="280" alt="Settings and Azan Tab Screenshot" /> | <img src="docs/screenshots/06_home_widgets.png" width="280" alt="Nothing OS Home Screen Widgets Screenshot" /> |
| **4. Tracker & Analytics**<br/>21-day consistency streak, weekly completion bar chart, and lifetime Qaza debt counter | **5. Settings & Smart Azan**<br/>Offline Azan foreground service, Muezzin selection (Makkah/Madinah) & one-tap JSON backup | **6. Home Screen Widgets Suite**<br/>4 Nothing OS-inspired Frosted Glass widgets (Active Waqt, 5-Waqt board, Ramadan & Hadith) |

---

## 🌟 Key Features

### 🕌 1. Daily 5 Waqt Prayer Tracker & Live Countdown
- **Accurate Astronomical Calculation:** Dynamic prayer times for all 64 districts of Bangladesh and global GPS coordinates supporting Hanafi and Shafi'i calculation methods.
- **One-Tap Status Logging:** Record prayer status instantly as *Jama'at*, *Alone*, *Late*, *Qaza*, or *Missed*.
- **Smart Background Azan Service:** Foreground media playback service with real embedded offline audio (Makkah & Madinah) and an interactive **Stop Azan** notification action.

### 📖 2. Complete Al-Qur'an & Authentic Hadith Library
- **Complete 114 Surahs Offline:** Full Quranic text stored locally in SQLite Room DB with English and Bengali translations and phonetic transliterations.
- **Last Read Resume:** Instant jump-back card to continue reciting from your exact last position.
- **8 Major Hadith Collections:** Sahih al-Bukhari, Sahih Muslim, Sunan an-Nasa'i, Sunan Abi Dawud, Jami' at-Tirmidhi, Sunan Ibn Majah, Muwatta Malik, and Musnad Ahmad.

### 🤲 3. Tactile Haptic Tasbih & Qibla Compass
- **Haptic Digital Counter:** Responsive touch-down spring animations with custom vibration milestone pulses at 33 and 100 counts.
- **Real-Time Sensor Compass:** Device magnetometer-driven 3D compass pointing accurately toward the Holy Kaaba in Makkah.
- **Masnoon Duas & Ramadan Fasting:** Daily Fortress of the Muslim supplications and Sehri & Iftar countdown timer.

### 📱 4. Nothing OS & iOS Frosted Glass Widgets Suite
1. **Active Waqt Capsule (4x1 & 2x1):** Current prayer name, prayer time, and live reverse countdown with Nothing OS pulsating dot.
2. **5-Waqt Daily Board (4x2):** Full daily prayer timetable with an emerald glowing frosted pill highlight on the active prayer.
3. **Ramadan Fasting Timer (4x2 / 3x2):** Today's Sehri end time and Iftar time with live countdown dock.
4. **Interactive Hadith Reader (4x2):** Daily authentic Hadith with an interactive in-widget **Next Hadith ↻** shuffle button.

### 🔒 5. 100% Offline-First & Privacy-Focused
- **Zero Cloud Tracking:** All logs, prayers, and personal reflections remain 100% on your device with no required accounts or analytics tracking.
- **One-Tap JSON Backup & Restore:** Export your complete spiritual habit database to a local JSON file and restore it anytime on any device.

---

## 🛠️ Technical Specifications (Tech Stack)

| Component | Specification |
| :--- | :--- |
| **Language** | Kotlin 2.0+ |
| **UI Toolkit** | Jetpack Compose (Material 3 + Custom iOS Frosted Glass Primitives) |
| **Design System** | iOS Frosted Glassmorphism (`IosGlassPill.kt`) & Nothing OS Dot-Matrix Widgets |
| **Architecture** | Clean Architecture + MVVM + StateFlow |
| **Database** | AndroidX Room Database 2.6.1 (Pre-packaged asset `namaz_tracker.db`) |
| **Background Service** | Foreground Media Playback Service (`AzanPlaybackService.kt`) |
| **Testing** | Robolectric 4.16+, JUnit4, Compose UI Test Rule (`createComposeRule`) |
| **Package Name** | `com.namaztracker.app` |
| **Target SDK** | Android 14/15+ (API 36, minSdk 24) |

---

## 🚀 Getting Started & Installation

1. Clone the repository:
   ```bash
   git clone -b android https://github.com/Muhit47/namaz-tracker.git
   ```
2. Open the project in **Android Studio** (Koala / Ladybug or newer recommended).
3. Allow Gradle to sync dependencies.
4. Run the app on a physical Android device or emulator running **Android 8.0 (API 24)** or higher.

---

## 📜 License
Developed with devotion for Muslims worldwide. Licensed under the [MIT License](LICENSE).
