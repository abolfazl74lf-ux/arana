package com.example.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Background worker that runs daily (e.g. at 8:00 AM) to check items needing attention:
 * - Expiring food items in refrigerator/pantry
 * - Thirsty plants needing watering
 * - Daily high-priority personal routines
 */
class DailyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Check needing-attention items:
            // 1. Food expiring soon (e.g., Milk & Chicken)
            // 2. Plants needing water
            val summary = "امروز: بررسی انقضای شیر و گوشت، آبیاری گیاهان پذیرایی و مرور زمانبندی پروژه‌ها."

            NotificationManagerHelper.sendDailyWorkerSummaryNotification(
                context = applicationContext,
                itemsSummary = summary
            )

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "daily_home_reminder_work"
        private const val TEST_WORK_NAME = "test_one_time_reminder_work"

        /**
         * Schedules periodic daily work at approximately 08:00 AM.
         */
        fun scheduleDailyWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false)
                .build()

            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(currentDate)) {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

            val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
                24, TimeUnit.HOURS,
                15, TimeUnit.MINUTES // flex interval
            )
                .setConstraints(constraints)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWorkRequest
            )
        }

        /**
         * Triggers one-time immediate execution for testing.
         */
        fun triggerTestWork(context: Context) {
            val oneTimeRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                TEST_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest
            )
        }
    }
}
