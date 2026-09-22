package com.example

import android.app.Application
import com.example.notification.DailyReminderWorker
import com.example.notification.NotificationManagerHelper

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Initialize notification channels (Urgent Alerts & Routine Reminders)
        NotificationManagerHelper.createNotificationChannels(this)

        // 2. Schedule Daily Background Worker for morning notifications
        try {
            DailyReminderWorker.scheduleDailyWork(this)
        } catch (e: Exception) {
            // Background initialization safety
        }
    }
}
