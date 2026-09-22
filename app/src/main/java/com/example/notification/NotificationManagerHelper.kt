package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object NotificationManagerHelper {

    const val CHANNEL_ID_URGENT = "channel_urgent_alerts"
    const val CHANNEL_ID_ROUTINES = "channel_routine_reminders"

    private const val NOTIF_ID_FOOD_EXPIRY = 1001
    private const val NOTIF_ID_HYDROGEL = 1002
    private const val NOTIF_ID_THESIS_MEETING = 1003
    private const val NOTIF_ID_PYTHON_CODING = 1004
    private const val NOTIF_ID_DAILY_WORKER = 1005

    /**
     * Creates the two distinct notification channels required:
     * 1. Urgent Alerts (High Importance, Sound & Vibration)
     * 2. Routine Reminders (Default Importance)
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Channel 1: هشدارهای ضروری (High Importance)
            val urgentSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build()

            val urgentChannel = NotificationChannel(
                CHANNEL_ID_URGENT,
                "هشدارهای ضروری",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "هشدارهای فوری مانند سررسید انقضای مواد غذایی و کارهای ضروری"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setSound(urgentSoundUri, audioAttributes)
            }

            // Channel 2: یادآورهای روتین (Default Importance)
            val routineChannel = NotificationChannel(
                CHANNEL_ID_ROUTINES,
                "یادآورهای روتین",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "یادآوری کارهای روزانه، روتین‌های خانه‌داری و تایملاین شخصی"
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(listOf(urgentChannel, routineChannel))
        }
    }

    /**
     * Builds and sends a notification using NotificationCompat.
     */
    fun showNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        message: String,
        priority: Int = NotificationCompat.PRIORITY_DEFAULT
    ) {
        // Ensure channels exist
        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (channelId == CHANNEL_ID_URGENT) {
            builder.setVibrate(longArrayOf(0, 300, 200, 300))
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_LIGHTS)
        }

        try {
            val managerCompat = NotificationManagerCompat.from(context)
            managerCompat.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted by user yet
        }
    }

    // =========================================================================
    // Mock Scenarios requested by user
    // =========================================================================

    /**
     * هشدار خانه‌داری (ضروری):
     * "⚠️ هشدار انقضا: شیر و مرغ در یخچال فردا منقضی می‌شوند."
     */
    fun sendMockFoodExpiryAlert(context: Context) {
        showNotification(
            context = context,
            channelId = CHANNEL_ID_URGENT,
            notificationId = NOTIF_ID_FOOD_EXPIRY,
            title = "⚠️ هشدار انقضای مواد غذایی",
            message = "شیر و مرغ در یخچال فردا منقضی می‌شوند. لطفاً بررسی فرمایید.",
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    /**
     * یادآور تایملاین (پروژه):
     * "⏰ ۱۵ دقیقه تا شروع: سنتز هیدروژل در آزمایشگاه."
     */
    fun sendMockProject15MinReminder(context: Context) {
        showNotification(
            context = context,
            channelId = CHANNEL_ID_ROUTINES,
            notificationId = NOTIF_ID_HYDROGEL,
            title = "⏰ یادآور شروع پروژه (۱۵ دقیقه قبل)",
            message = "سنتز هیدروژل در آزمایشگاه به زودی آغاز می‌شود.",
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }

    /**
     * یادآور تایملاین (آکادمیک):
     * "⏰ زمان شروع: جلسه مشاوره پایان‌نامه با میرسلیمی."
     */
    fun sendMockThesisMeetingReminder(context: Context) {
        showNotification(
            context = context,
            channelId = CHANNEL_ID_ROUTINES,
            notificationId = NOTIF_ID_THESIS_MEETING,
            title = "⏰ زمان جلسه پایان‌نامه",
            message = "جلسه مشاوره پایان‌نامه با میرسلیمی هم‌اکنون آغاز شد.",
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }

    /**
     * یادآور تایملاین (شخصی/کدنویسی):
     * "💻 زمان شروع: بررسی کدهای پایتون پردازش تصویر."
     */
    fun sendMockPythonCodingReminder(context: Context) {
        showNotification(
            context = context,
            channelId = CHANNEL_ID_ROUTINES,
            notificationId = NOTIF_ID_PYTHON_CODING,
            title = "💻 زمان شروع توسعه فردی",
            message = "بررسی کدهای پایتون پردازش تصویر طبق برنامه روزانه.",
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }

    /**
     * یادآور تجمیعی روزانه WorkManager
     */
    fun sendDailyWorkerSummaryNotification(context: Context, itemsSummary: String) {
        showNotification(
            context = context,
            channelId = CHANNEL_ID_ROUTINES,
            notificationId = NOTIF_ID_DAILY_WORKER,
            title = "📋 مرور صبحگاهی وظایف خانه و روزانه",
            message = itemsSummary,
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }
}
