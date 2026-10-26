import sqlite3
import os
import re

import sys

src_path = 'namaz_tracker.db' if os.path.exists('namaz_tracker.db') else ('temp_main_db.db' if os.path.exists('temp_main_db.db') else '/tmp/repo_check/namaz_tracker.db')
if len(sys.argv) > 1:
    src_path = sys.argv[1]
dst_path = 'app/src/main/assets/namaz_tracker.db'

# Find Room generated identity_hash
identity_hash = "b04ecd4d900636a09389ef5138524775"
impl_file = 'app/build/generated/ksp/debug/kotlin/com/example/data/database/AppDatabase_Impl.kt'
if os.path.exists(impl_file):
    with open(impl_file) as f:
        text = f.read()
        match = re.search(r'VALUES\(42,\s*[\'"]([a-f0-9]+)[\'"]\)', text)
        if match:
            identity_hash = match.group(1)
            print(f'Extracted Room identity_hash: {identity_hash}')

if os.path.exists(dst_path):
    os.remove(dst_path)

conn_src = sqlite3.connect(src_path)
c_src = conn_src.cursor()

conn_dst = sqlite3.connect(dst_path)
c_dst = conn_dst.cursor()

room_sqls = [
    '''CREATE TABLE IF NOT EXISTS `prayers_log` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `prayer_name` TEXT NOT NULL, `status` TEXT NOT NULL, `is_sunnah` INTEGER NOT NULL, `notes` TEXT, `updated_at` TEXT)''',
    '''CREATE UNIQUE INDEX IF NOT EXISTS `index_prayers_log_date_prayer_name` ON `prayers_log` (`date`, `prayer_name`)''',
    '''CREATE TABLE IF NOT EXISTS `qaza_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `prayer_name` TEXT NOT NULL, `total_missed` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `updated_at` TEXT)''',
    '''CREATE TABLE IF NOT EXISTS `quran_surahs` (`surah_no` INTEGER NOT NULL, `name_ar` TEXT NOT NULL, `name_bn` TEXT NOT NULL, `name_en` TEXT NOT NULL, `meaning_bn` TEXT NOT NULL, `meaning_en` TEXT, `total_ayah` INTEGER NOT NULL, `revelation_type` TEXT NOT NULL, PRIMARY KEY(`surah_no`))''',
    '''CREATE TABLE IF NOT EXISTS `quran_ayahs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `surah_no` INTEGER NOT NULL, `ayah_no` INTEGER NOT NULL, `text_ar` TEXT NOT NULL, `translit_bn` TEXT NOT NULL, `trans_bn` TEXT NOT NULL, `translit_en` TEXT, `trans_en` TEXT)''',
    '''CREATE TABLE IF NOT EXISTS `quran_bookmarks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `surah_no` INTEGER NOT NULL, `ayah_no` INTEGER NOT NULL, `created_at` TEXT)''',
    '''CREATE TABLE IF NOT EXISTS `quran_last_read` (`id` INTEGER NOT NULL, `surah_no` INTEGER NOT NULL, `ayah_no` INTEGER NOT NULL, `updated_at` TEXT, PRIMARY KEY(`id`))''',
    '''CREATE TABLE IF NOT EXISTS `hadith_categories` (`id` TEXT NOT NULL, `name_bn` TEXT NOT NULL, `icon` TEXT NOT NULL, `name_en` TEXT, PRIMARY KEY(`id`))''',
    '''CREATE TABLE IF NOT EXISTS `hadith_books` (`id` TEXT NOT NULL, `name_bn` TEXT NOT NULL, `name_ar` TEXT NOT NULL, `name_en` TEXT, PRIMARY KEY(`id`))''',
    '''CREATE TABLE IF NOT EXISTS `hadiths` (`id` INTEGER NOT NULL, `category_id` TEXT NOT NULL, `category_bn` TEXT NOT NULL, `book_id` TEXT NOT NULL, `book_name_bn` TEXT NOT NULL, `chapter_bn` TEXT NOT NULL, `narrator_bn` TEXT NOT NULL, `text_ar` TEXT NOT NULL, `text_bn` TEXT NOT NULL, `grade_bn` TEXT NOT NULL, `reference_bn` TEXT NOT NULL, `translit_bn` TEXT, `translit_en` TEXT, `text_en` TEXT, `category_en` TEXT, `book_name_en` TEXT, `chapter_en` TEXT, `narrator_en` TEXT, `grade_en` TEXT, `reference_en` TEXT, PRIMARY KEY(`id`))''',
    '''CREATE TABLE IF NOT EXISTS `hadith_bookmarks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `hadith_id` INTEGER NOT NULL, `created_at` TEXT)''',
    '''CREATE TABLE IF NOT EXISTS `hadith_chapters` (`id` INTEGER NOT NULL, `book_id` TEXT NOT NULL, `chapter_no` INTEGER NOT NULL, `name_bn` TEXT NOT NULL, `name_ar` TEXT, `name_en` TEXT, `total_hadith` INTEGER NOT NULL, `start_hadith` INTEGER NOT NULL, `end_hadith` INTEGER NOT NULL, PRIMARY KEY(`id`))''',
    '''CREATE TABLE IF NOT EXISTS `hadith_full_collection` (`id` INTEGER NOT NULL, `book_id` TEXT NOT NULL, `chapter_no` INTEGER NOT NULL, `hadith_no` INTEGER NOT NULL, `arabic_no` INTEGER NOT NULL, `text_ar` TEXT, `text_bn` TEXT NOT NULL, `grade_bn` TEXT, `translit_bn` TEXT, `translit_en` TEXT, `text_en` TEXT, PRIMARY KEY(`id`))''',
    '''CREATE TABLE IF NOT EXISTS `hadith_last_read` (`id` INTEGER NOT NULL, `book_id` TEXT NOT NULL, `chapter_no` INTEGER NOT NULL, `hadith_no` INTEGER NOT NULL, `updated_at` TEXT, PRIMARY KEY(`id`))''',
    '''CREATE TABLE IF NOT EXISTS `settings` (`key` TEXT NOT NULL, `value` TEXT, PRIMARY KEY(`key`))''',
    '''CREATE TABLE IF NOT EXISTS `tasbih_counters` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `arabicText` TEXT NOT NULL, `transliteration` TEXT NOT NULL, `translationBn` TEXT NOT NULL, `translationEn` TEXT NOT NULL, `currentCount` INTEGER NOT NULL, `targetCount` INTEGER NOT NULL, `totalRecited` INTEGER NOT NULL, `isPreset` INTEGER NOT NULL)'''
]

for sql in room_sqls:
    c_dst.execute(sql)

tables = [
    'prayers_log', 'qaza_records', 'quran_surahs', 'quran_ayahs', 'quran_bookmarks',
    'quran_last_read', 'hadith_categories', 'hadith_books', 'hadiths', 'hadith_bookmarks',
    'hadith_chapters', 'hadith_full_collection', 'hadith_last_read', 'settings', 'tasbih_counters'
]

for t in tables:
    try:
        c_src.execute(f'SELECT * FROM `{t}`')
        rows = c_src.fetchall()
        c_src.execute(f'PRAGMA table_info(`{t}`)')
        src_cols = [col[1] for col in c_src.fetchall()]

        c_dst.execute(f'PRAGMA table_info(`{t}`)')
        dst_cols = [col[1] for col in c_dst.fetchall()]

        common_cols = [col for col in src_cols if col in dst_cols]
        if not common_cols or not rows:
            print(f'Table `{t}`: 0 rows copied')
            continue

        src_indices = [src_cols.index(col) for col in common_cols]
        placeholders = ', '.join(['?'] * len(common_cols))
        col_names = ', '.join([f'`{col}`' for col in common_cols])

        insert_sql = f'INSERT INTO `{t}` ({col_names}) VALUES ({placeholders})'

        reordered_rows = [tuple(r[i] for i in src_indices) for r in rows]
        c_dst.executemany(insert_sql, reordered_rows)
        print(f'Table `{t}`: {len(rows)} rows copied successfully')
    except Exception as e:
        print(f'Table `{t}` error:', e)

# Add default tasbih counter presets if empty
c_dst.execute('SELECT COUNT(*) FROM tasbih_counters')
if c_dst.fetchone()[0] == 0:
    presets = [
        (1, 'সুবহানাল্লাহ', 'سُبْحَانَ اللَّهِ', 'Subhanallah', 'আল্লাহ পবিত্র ও সকল ত্রুটি থেকে মুক্ত।', 'Allah is free from all imperfection.', 0, 33, 0, 1),
        (2, 'আলহামদুলিল্লাহ', 'الْحَمْدُ لِلَّهِ', 'Alhamdulillah', 'সকল প্রশংসা একমাত্র আল্লাহর জন্য।', 'All praise is due to Allah alone.', 0, 33, 0, 1),
        (3, 'আল্লাহু আকবার', 'اللَّهُ أَكْبَرُ', 'Allahu Akbar', 'আল্লাহ সবচেয়ে মহান ও সর্বশ্রেষ্ঠ।', 'Allah is the Greatest.', 0, 34, 0, 1),
        (4, 'আস্তাগফিরুল্লাহ', 'أَسْتَغْفِرُ اللَّهَ', 'Astaghfirullah', 'আমি আল্লাহর নিকট ক্ষমা প্রার্থনা করছি।', 'I seek forgiveness from Allah.', 0, 100, 0, 1)
    ]
    c_dst.executemany(
        'INSERT INTO tasbih_counters (id, title, arabicText, transliteration, translationBn, translationEn, currentCount, targetCount, totalRecited, isPreset) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)',
        presets
    )
    print('Inserted 4 default Tasbih presets')

# Create room_master_table
c_dst.execute('CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)')
c_dst.execute(f'INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, "{identity_hash}")')

conn_dst.commit()
conn_src.close()
conn_dst.close()

print('ROOM ASSET DATABASE BUILT PERFECTLY WITH HASH:', identity_hash)
