package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Namaz Tracker", appName)
  }

  @Test
  fun `validate pre-packaged database schema`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getDatabase(context)
    assertNotNull(db)

    // Verify Surahs count is 114
    val surahs = db.quranDao().getAllSurahs().first()
    assertEquals(114, surahs.size)

    // Verify prayers_log query works
    val prayers = db.prayerLogDao().getPrayersForDate("2026-10-24").first()
    assertNotNull(prayers)
  }
}
