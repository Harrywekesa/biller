package com.mpesa.tracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MpesaTrackerApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
            
    override fun onCreate() {
        super.onCreate()
        scheduleAiAdviceWorker()
        scheduleBillReminderWorker()
    }
    
    private fun scheduleAiAdviceWorker() {
        val workManager = androidx.work.WorkManager.getInstance(this)
        
        // Calculate time to next 7:00 AM
        val currentDate = java.util.Calendar.getInstance()
        val dueDate = java.util.Calendar.getInstance()
        
        dueDate.set(java.util.Calendar.HOUR_OF_DAY, 7)
        dueDate.set(java.util.Calendar.MINUTE, 0)
        dueDate.set(java.util.Calendar.SECOND, 0)
        
        if (dueDate.before(currentDate)) {
            // It's after 7 AM today — advance by one calendar day (not 24h, which breaks on DST transitions)
            dueDate.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        
        val dailyWorkRequest = androidx.work.PeriodicWorkRequestBuilder<com.mpesa.tracker.framework.workers.DailyAiAdviceWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        )
        .setInitialDelay(timeDiff, java.util.concurrent.TimeUnit.MILLISECONDS)
        .setConstraints(
            androidx.work.Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                .build()
        )
        .build()
        
        workManager.enqueueUniquePeriodicWork(
            "DailyAiAdviceWorker",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    private fun scheduleBillReminderWorker() {
        val weeklyReminder = androidx.work.PeriodicWorkRequestBuilder<com.mpesa.tracker.framework.workers.BillReminderWorker>(
            7, java.util.concurrent.TimeUnit.DAYS
        )
        .setInitialDelay(1, java.util.concurrent.TimeUnit.HOURS) // First check 1 hour after app start
        .build()

        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BillReminderWorker",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            weeklyReminder
        )
    }
}
