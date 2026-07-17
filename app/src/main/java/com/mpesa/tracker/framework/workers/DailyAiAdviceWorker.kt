package com.mpesa.tracker.framework.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.domain.intelligence.FinancialIntelligenceEngine
import com.mpesa.tracker.domain.models.ReportPeriod
import com.mpesa.tracker.framework.services.NotificationService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

@HiltWorker
class DailyAiAdviceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val transactionRepository: TransactionRepository,
    private val financialEngine: FinancialIntelligenceEngine,
    private val notificationService: NotificationService
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            val calendar = Calendar.getInstance()
            // Calendar.DAY_OF_WEEK gives 1 for Sunday, 7 for Saturday. Wait, Gemini prompt maps 0-6.
            // Let's use standard Java Calendar subtracting 1 to get "0" to "6".
            val todayDayOfWeekInt = calendar.get(Calendar.DAY_OF_WEEK) - 1
            val todayDayOfWeekStr = todayDayOfWeekInt.toString()

            // Calculate overall daily average (all time)
            val allDailySpending = transactionRepository.getDailySpendingTrend(ReportPeriod.ALL_TIME).firstOrNull() ?: emptyList()
            var recentDailyAverage = 0.0
            if (allDailySpending.isNotEmpty()) {
                val totalAllTime = allDailySpending.sumOf { it.totalAmount }
                recentDailyAverage = totalAllTime / allDailySpending.size
            }

            // Retrieve today's historical spending
            // But DayOfWeek is stored as 0 (Sunday) to 6 (Saturday) by strftime('%w')
            val allSpendingByDay = transactionRepository.getSpendingByCategoryAndDayOfWeek(ReportPeriod.ALL_TIME).firstOrNull() ?: emptyList()
            
            // Filter to only today's day of the week
            val todayHistoricalSpending = allSpendingByDay.filter { it.dayOfWeek == todayDayOfWeekStr }

            // Ask Gemini for advice
            val advice = financialEngine.generateDailyAdvice(
                dayOfWeek = todayDayOfWeekStr,
                todayHistoricalSpending = todayHistoricalSpending,
                recentDailyAverage = recentDailyAverage
            )

            // Show Notification
            if (!advice.isNullOrBlank()) {
                notificationService.showAiAdvice(advice)
            }

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }
}
