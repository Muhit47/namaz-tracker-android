package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.notification.AzanPlaybackService
import com.example.notification.PrayerNotificationHelper
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NotificationServiceTest {

    @Test
    fun `validate notification channels creation`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        PrayerNotificationHelper.createNotificationChannel(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val primaryChannel = notificationManager.getNotificationChannel(PrayerNotificationHelper.CHANNEL_ID)
        assertNotNull("Primary Waqt channel should be created", primaryChannel)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, primaryChannel.importance)

        val ongoingChannel = notificationManager.getNotificationChannel(PrayerNotificationHelper.CHANNEL_ONGOING_ID)
        assertNotNull("Ongoing Waqt channel should be created", ongoingChannel)
        assertEquals(NotificationManager.IMPORTANCE_LOW, ongoingChannel.importance)
    }

    @Test
    fun `validate offline azan raw audio resources exist`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // Assert resource IDs exist
        assertTrue(R.raw.azan_makkah > 0)
        assertTrue(R.raw.azan_madinah > 0)
        assertTrue(R.raw.takbeer > 0)

        // Assert resources can be opened as input streams
        val makkahStream = context.resources.openRawResource(R.raw.azan_makkah)
        assertNotNull(makkahStream)
        assertTrue(makkahStream.available() > 0)
        makkahStream.close()

        val madinahStream = context.resources.openRawResource(R.raw.azan_madinah)
        assertNotNull(madinahStream)
        assertTrue(madinahStream.available() > 0)
        madinahStream.close()

        val takbeerStream = context.resources.openRawResource(R.raw.takbeer)
        assertNotNull(takbeerStream)
        assertTrue(takbeerStream.available() > 0)
        takbeerStream.close()
    }

    @Test
    fun `validate AzanPlaybackService actions and constants`() {
        assertEquals("com.example.notification.ACTION_PLAY_AZAN", AzanPlaybackService.ACTION_PLAY_AZAN)
        assertEquals("com.example.notification.ACTION_STOP_AZAN", AzanPlaybackService.ACTION_STOP_AZAN)
        assertFalse(AzanPlaybackService.isAzanPlaying)
    }
}
