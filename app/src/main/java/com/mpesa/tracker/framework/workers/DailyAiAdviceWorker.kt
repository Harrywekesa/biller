package com.mpesa.tracker.framework.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mpesa.tracker.data.repository.TransactionRepository
import com.mpesa.tracker.data.repository.UserPreferencesRepository
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
    private val notificationService: NotificationService,
    private val userPreferences: UserPreferencesRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!userPreferences.dailyAiAdviceEnabled.value) return Result.success()

        return try {
            val calendar = Calendar.getInstance()
            val todayDayOfWeekStr = (calendar.get(Calendar.DAY_OF_WEEK) - 1).toString()

            // Compute calendar-accurate 30-day daily average (dividing by 30 calendar days, not only active spend days)
            val thisMonthSpend = transactionRepository.getTotalSpentForPeriod(ReportPeriod.THIS_MONTH).firstOrNull() ?: 0.0
            val elapsedDaysThisMonth = calendar.get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
            val recentDailyAverage = thisMonthSpend / elapsedDaysThisMonth

            val allSpendingByDay = transactionRepository.getSpendingByCategoryAndDayOfWeek(ReportPeriod.ALL_TIME).firstOrNull() ?: emptyList()
            val todayHistoricalSpending = allSpendingByDay.filter { it.dayOfWeek == todayDayOfWeekStr }

            val advice = financialEngine.generateDailyAdvice(
                dayOfWeek = todayDayOfWeekStr,
                todayHistoricalSpending = todayHistoricalSpending,
                recentDailyAverage = recentDailyAverage
            )

            if (!advice.isNullOrBlank()) {
                notificationService.showAiAdvice(advice)
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("DailyAiAdviceWorker", "Failed to generate daily AI advice", e)
            Result.success()
        }
    }
}
