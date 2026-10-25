# Namaz Tracker (নামায ট্র্যাকার) - Android App

A modern, offline-first Islamic companion Android application built with **Kotlin** and **Jetpack Compose**. Track your daily 5 prayers, calculate accurate prayer times, explore the Holy Qur'an with translations and transliterations, browse authentic Hadith collections, maintain your Qaza prayer log, utilize a tactile digital Tasbih, and find the Qibla direction with a real-time compass.

---

## 🌟 Key Features

- **🕌 Daily 5 Waqt Prayer Tracker**:
  - Track Fajr, Dhuhr, Asr, Maghrib, and Isha with one-tap status (Jama'at, Alone, Late, Qaza, Missed).
  - Track Sunnah prayers and add custom prayer notes.
  - Ramadan Sehri & Iftar timings and daily Roza tracker.
  - Automatic dynamic calculation based on user's city location and Hanafi / Shafi'i Asr methods.

- **📖 Holy Al-Qur'an (আল কুরআনুল কারীম)**:
  - Complete 114 Surahs and 6,236 Ayahs stored offline in SQLite/Room database.
  - Arabic text, Bangla translation, English translation, and phonetic transliterations.
  - Search by Surah name, number, or meaning.
  - Bookmark favorite Ayahs and quick resume from Last Read position.

- **📚 Authentic Hadith Collections (সহিহ হাদীস সম্ভার)**:
  - 8 Major Hadith Books: Sahih al-Bukhari, Sahih Muslim, Sunan an-Nasa'i, Sunan Abi Dawud, Jami' at-Tirmidhi, Sunan Ibn Majah, Muwatta Malik, and Musnad Ahmad.
  - Categorized browsing (Faith, Prayer, Ramadan, Duas, etc.), book chapters, and full Hadith reader.
  - Arabic text, Bangla/English translations, Sanad grades, and Hadith bookmarking.

- **📊 Qaza Prayer Counter & Analytics (কাযা ও আমলনামা)**:
  - Keep track of missed prayers across all 5 daily Waqts + Witr.
  - Quick adjust buttons (+1, +5, Prayed) and manual direct count entry.
  - Monthly consistency streak tracker and weekly completion analytics.

- **📿 Digital Haptic Tasbih (ডিজিটাল তাসবীহ)**:
  - Interactive tactile counter with vibration milestones at cycle targets (33, 100, etc.).
  - Masnoon Dhikr presets and custom user-defined Dhikrs.

- **🧭 Real-Time Qibla Compass (কিবলা কম্পাস)**:
  - Dynamic sensor-driven compass pointing accurately toward the Holy Kaaba in Makkah based on GPS/city coordinates.

- **🌐 Bilingual Support (বাংলা ⇌ English)**:
  - Instant one-tap language toggle across the entire application with persistent user preferences.

- **📴 100% Offline-First**:
  - Bundled with a pre-packaged SQLite Room database (`namaz_tracker.db`) containing complete Quran and Hadith data. No internet connection required.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Persistence**: Room Database (SQLite pre-packaged asset) + SharedPreferences
- **Async & Reactive**: Kotlin Coroutines & StateFlow
- **Design Style**: Modern Glassmorphism & Framer-inspired Bento Cards

---

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone -b android https://github.com/Muhit47/namaz-tracker.git
   ```
2. Open the project in **Android Studio** (Koala / Ladybug or newer recommended).
3. Let Gradle sync and download dependencies.
4. Run on an Android device or emulator running **Android 8.0 (API 26)** or higher.

---

## 📜 License
Developed with devotion for Muslims worldwide. Open source under the MIT License.
