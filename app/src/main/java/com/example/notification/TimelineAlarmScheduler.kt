package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

class TimelineAlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules an alarm at a specific trigger time in milliseconds.
     */
    fun scheduleAlarm(
        requestCode: Int,
        triggerTimeMillis: Long,
        title: String,
        message: String,
        isUrgent: Boolean = false
    ) {
        val intent = Intent(context, TimelineAlarmReceiver::class.java).apply {
            putExtra(TimelineAlarmReceiver.EXTRA_TITLE, title)
            putExtra(TimelineAlarmReceiver.EXTRA_MESSAGE, message)
            putExtra(
                TimelineAlarmReceiver.EXTRA_CHANNEL_ID,
                if (isUrgent) NotificationManagerHelper.CHANNEL_ID_URGENT
                else NotificationManagerHelper.CHANNEL_ID_ROUTINES
            )
            putExtra(TimelineAlarmReceiver.EXTRA_NOTIF_ID, requestCode)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // In Android 12+, exact alarm permission might not be granted; fallback to inexact
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        }
    }

    /**
     * Cancels a previously scheduled alarm.
     */
    fun cancelAlarm(requestCode: Int) {
        val intent = Intent(context, TimelineAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
