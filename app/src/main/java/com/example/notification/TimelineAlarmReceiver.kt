package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimelineAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "یادآور وظیفه روزانه"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "زمان انجام فعالیت طبق برنامه فرا رسیده است."
        val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID) ?: NotificationManagerHelper.CHANNEL_ID_ROUTINES
        val notifId = intent.getIntExtra(EXTRA_NOTIF_ID, (System.currentTimeMillis() % 10000).toInt())

        NotificationManagerHelper.showNotification(
            context = context,
            channelId = channelId,
            notificationId = notifId,
            title = title,
            message = message
        )
    }

    companion object {
        const val EXTRA_TITLE = "extra_notif_title"
        const val EXTRA_MESSAGE = "extra_notif_message"
        const val EXTRA_CHANNEL_ID = "extra_channel_id"
        const val EXTRA_NOTIF_ID = "extra_notif_id"
    }
}
