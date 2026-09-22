package com.example

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.example.notification.NotificationManagerHelper
import com.example.notification.TimelineAlarmReceiver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NotificationManagerHelperTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun `test notification channels creation`() {
        NotificationManagerHelper.createNotificationChannels(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val urgentChannel = notificationManager.getNotificationChannel(NotificationManagerHelper.CHANNEL_ID_URGENT)
        val routineChannel = notificationManager.getNotificationChannel(NotificationManagerHelper.CHANNEL_ID_ROUTINES)

        assertNotNull(urgentChannel)
        assertEquals(NotificationManager.IMPORTANCE_HIGH, urgentChannel.importance)
        assertEquals("هشدارهای ضروری", urgentChannel.name)

        assertNotNull(routineChannel)
        assertEquals(NotificationManager.IMPORTANCE_DEFAULT, routineChannel.importance)
        assertEquals("یادآورهای روتین", routineChannel.name)
    }

    @Test
    fun `test mock notification scenarios trigger without throwing`() {
        // Test Food Expiry Alert
        NotificationManagerHelper.sendMockFoodExpiryAlert(context)

        // Test 15-min Hydrogel project reminder
        NotificationManagerHelper.sendMockProject15MinReminder(context)

        // Test Thesis Meeting reminder
        NotificationManagerHelper.sendMockThesisMeetingReminder(context)

        // Test Python Coding reminder
        NotificationManagerHelper.sendMockPythonCodingReminder(context)

        // Test Daily Worker Summary
        NotificationManagerHelper.sendDailyWorkerSummaryNotification(context, "تست خلاصه روزانه")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // In Robolectric/Android, notifications can be queried
        val activeNotifications = notificationManager.activeNotifications
        assertTrue(activeNotifications.isNotEmpty())
    }

    @Test
    fun `test alarm broadcast receiver dispatch`() {
        val receiver = TimelineAlarmReceiver()
        val intent = Intent().apply {
            putExtra(TimelineAlarmReceiver.EXTRA_TITLE, "تست آلارم")
            putExtra(TimelineAlarmReceiver.EXTRA_MESSAGE, "پیام تستی آلارم")
            putExtra(TimelineAlarmReceiver.EXTRA_CHANNEL_ID, NotificationManagerHelper.CHANNEL_ID_ROUTINES)
            putExtra(TimelineAlarmReceiver.EXTRA_NOTIF_ID, 777)
        }

        receiver.onReceive(context, intent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = notificationManager.activeNotifications.firstOrNull { it.id == 777 }
        assertNotNull(notif)
    }
}
