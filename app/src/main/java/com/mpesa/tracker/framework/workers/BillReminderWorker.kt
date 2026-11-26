package com.mpesa.tracker.framework.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mpesa.tracker.data.local.dao.SubscriptionDao
import com.mpesa.tracker.data.repository.UserPreferencesRepository
import com.mpesa.tracker.framework.services.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.text.NumberFormat
import java.util.Locale

@HiltWorker
class BillReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val subscriptionDao: SubscriptionDao,
    private val notificationService: NotificationService,
    private val userPreferences: UserPreferencesRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (!userPreferences.billRemindersEnabled.value) return Result.success()

        return try {
            val now = System.currentTimeMillis()
            val threeDaysMs = 3L * 24 * 60 * 60 * 1000L
            val subscriptions = subscriptionDao.getActiveSubscriptions().firstOrNull() ?: emptyList()
            val fmt = NumberFormat.getNumberInstance(Locale.US).apply {
                minimumFractionDigits = 2; maximumFractionDigits = 2
            }

            var remindersShown = 0
            for (sub in subscriptions) {
                val nextDueMs = sub.expectedNextPaymentDate ?: continue
                if (nextDueMs in now..(now + threeDaysMs)) {
                    val daysUntilDue = ((nextDueMs - now) / (24 * 60 * 60 * 1000L)).toInt()
                    val dueLabel = when (daysUntilDue) {
                        0 -> "today"
                        1 -> "tomorrow"
                        else -> "in $daysUntilDue days"
                    }
                    notificationService.showBillReminder(
                        merchantName = sub.name,
                        amount = "Ksh ${fmt.format(sub.amount)}",
                        dueLabel = dueLabel,
                        notificationId = sub.id.hashCode()
                    )
                    remindersShown++
                }
            }
            Log.d("BillReminderWorker", "Checked ${subscriptions.size} bills; showed $remindersShown reminders.")
            Result.success()
        } catch (e: Exception) {
            Log.e("BillReminderWorker", "Failed to check bill reminders", e)
            Result.retry()
        }
    }
}
