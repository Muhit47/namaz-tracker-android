import sqlite3

db_path = 'app/src/main/assets/namaz_tracker.db'
conn = sqlite3.connect(db_path)
c = conn.cursor()

# Get Room CREATE TABLE and CREATE INDEX Statements from Room
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

tables = [
    'prayers_log', 'qaza_records', 'quran_surahs', 'quran_ayahs', 'quran_bookmarks',
    'quran_last_read', 'hadith_categories', 'hadith_books', 'hadiths', 'hadith_bookmarks',
    'hadith_chapters', 'hadith_full_collection', 'hadith_last_read', 'settings', 'tasbih_counters'
]

# Backup existing data into memory
data_backup = {}
for t in tables:
    try:
        c.execute(f'SELECT * FROM `{t}`')
        rows = c.fetchall()
        c.execute(f'PRAGMA table_info(`{t}`)')
        cols = [col[1] for col in c.fetchall()]
        data_backup[t] = (cols, rows)
        print(f'Backed up {t}: {len(rows)} rows, cols={cols}')
    except Exception as e:
        print(f'Table {t} backup error:', e)

# Drop existing tables
for t in tables:
    c.execute(f'DROP TABLE IF EXISTS `{t}`')

# Execute Room CREATE statements
for sql in room_sqls:
    c.execute(sql)

# Re-insert data matching column names
for t in tables:
    if t in data_backup:
        cols, rows = data_backup[t]
        if not rows:
            continue
        c.execute(f'PRAGMA table_info(`{t}`)')
        new_cols = [col[1] for col in c.fetchall()]
        
        # Match old cols to new cols
        common_cols = [col for col in cols if col in new_cols]
        if not common_cols:
            continue
            
        col_indices = [cols.index(col) for col in common_cols]
        
        placeholders = ', '.join(['?'] * len(common_cols))
        col_names = ', '.join([f'`{col}`' for col in common_cols])
        insert_sql = f'INSERT INTO `{t}` ({col_names}) VALUES ({placeholders})'
        
        reordered_rows = []
        for r in rows:
            reordered_rows.append(tuple(r[i] for i in col_indices))
            
        c.executemany(insert_sql, reordered_rows)
        print(f'Re-inserted {t}: {len(rows)} rows')

# Re-create room_master_table with Room identity hash
c.execute('CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)')
c.execute('INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, "b04ecd4d900636a09389ef5138524775")')

conn.commit()
conn.close()
print('SUCCESSFULLY ALIGNED ASSET DATABASE SCHEMA WITH ROOM EXPECTATIONS!')
