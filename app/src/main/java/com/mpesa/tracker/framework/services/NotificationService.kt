package com.mpesa.tracker.framework.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mpesa.tracker.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // General Alerts Channel
            val generalChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "Financial Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts for detected bills and budget limits"
            }

            notificationManager.createNotificationChannel(generalChannel)
        }
    }

    fun showBudgetAlert(message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            // Use a placeholder drawable until a real ic_launcher is available
            .setSmallIcon(android.R.drawable.ic_dialog_alert) 
            .setContentTitle("Budget Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BUDGET, notification)
    }

    fun showBillReminder(merchantName: String, expectedAmount: Double) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Upcoming Bill Detected")
            .setContentText("You might have a Ksh $expectedAmount bill coming up for $merchantName.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(merchantName.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_ALERTS = "financial_alerts_channel"
        const val NOTIFICATION_ID_BUDGET = 1001
    }
}
