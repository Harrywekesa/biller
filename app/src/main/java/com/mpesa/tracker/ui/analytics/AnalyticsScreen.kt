package com.mpesa.tracker.ui.analytics

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mpesa.tracker.data.local.entities.CategoryEntity
import com.mpesa.tracker.data.local.entities.CategoryExpense
import com.mpesa.tracker.data.local.entities.DailySpend
import com.mpesa.tracker.data.local.entities.MonthlyTotal
import com.mpesa.tracker.domain.intelligence.FinancialScore
import com.mpesa.tracker.domain.intelligence.SpendingAnomaly
import com.mpesa.tracker.domain.models.ReportPeriod
import com.mpesa.tracker.domain.parser.AccountSource
import com.mpesa.tracker.ui.components.categoryIconFor
import com.mpesa.tracker.ui.components.parseCategoryColor
import com.mpesa.tracker.ui.theme.BillerTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateToTransactions: (Int?) -> Unit,
    onNavigateToChat: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val expenses by viewModel.expensesByCategory.collectAsState()
    val dailySpend by viewModel.dailySpendTrend.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val selectedSimId by viewModel.selectedSimId.collectAsState()
    val activeSimIds by viewModel.activeSimIds.collectAsState()
    val totalFees by viewModel.totalFees.collectAsState()
    val totalTransfers by viewModel.totalTransfers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val personTotalSpent by viewModel.personTotalSpent.collectAsState()
    val personTotalIncome by viewModel.personTotalIncome.collectAsState()
    val personTotalFees by viewModel.personTotalFees.collectAsState()
    val financialScore by viewModel.financialScore.collectAsState()
    val anomalies by viewModel.spendingAnomalies.collectAsState()
    val monthlyComparison by viewModel.monthlyComparison.collectAsState()
    val accountSourceExpenses by viewModel.accountSourceExpenses.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()

    val aiViewModel: AiInsightsViewModel = hiltViewModel()
    val aiState by aiViewModel.uiState.collectAsState()
    var showAiBottomSheet by remember { mutableStateOf(false) }

    val dateRangePickerState = rememberDateRangePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val scheme = MaterialTheme.colorScheme
    val semantic = BillerTheme.colors
    val context = LocalContext.current

    LaunchedEffect(exportState) {
        when (exportState) {
            is ExportState.Success -> {
                Toast.makeText(context, (exportState as ExportState.Success).message, Toast.LENGTH_LONG).show()
                viewModel.resetExportState()
            }
            is ExportState.Error -> {
                Toast.makeText(context, (exportState as ExportState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetExportState()
            }
            else -> {}
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
                viewModel.onPeriodSelected(ReportPeriod.THIS_MONTH)
            },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    if (dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null) {
                        viewModel.setCustomDateRange(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    } else {
                        viewModel.onPeriodSelected(ReportPeriod.THIS_MONTH)
                    }
                }) { Text("Apply") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    viewModel.onPeriodSelected(ReportPeriod.THIS_MONTH)
                }) { Text("Cancel") }
            }
        ) {
            DateRangePicker(state = dateRangePickerState, showModeToggle = false, modifier = Modifier.weight(1f))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insights", fontWeight = FontWeight.ExtraBold) },
                actions = {
                    IconButton(onClick = { viewModel.togglePrivacyMode() }) {
                        Icon(
                            if (isPrivacyMode) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Privacy",
                            tint = if (isPrivacyMode) scheme.primary else scheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { viewModel.exportData(context) },
                        enabled = exportState !is ExportState.Exporting
                    ) {
                        if (exportState is ExportState.Exporting) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Download, contentDescription = "Export CSV", tint = scheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = scheme.background)
            )
        },
        containerColor = scheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(
                selectedTabIndex = ReportPeriod.entries.indexOf(selectedPeriod),
                edgePadding = 16.dp,
                containerColor = scheme.background,
                divider = {}
            ) {
                ReportPeriod.entries.forEach { period ->
                    Tab(
                        selected = selectedPeriod == period,
                        onClick = {
                            if (period == ReportPeriod.CUSTOM) showDatePicker = true
                            else viewModel.onPeriodSelected(period)
                        },
                        text = {
                            Text(
                                period.displayName,
                                fontWeight = if (selectedPeriod == period) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (activeSimIds.size > 1) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSimId == null,
                            onClick = { viewModel.setSimFilter(null) },
                            label = { Text("All Lines") }
                        )
                    }
                    items(activeSimIds.size) { index ->
                        val simId = activeSimIds[index]
                        FilterChip(
                            selected = selectedSimId == simId,
                            onClick = { viewModel.setSimFilter(simId) },
                            label = { Text("SIM ${index + 1}") }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                // AI Co-Pilot Dual Launcher Row (Chat + Generate Report)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            onClick = onNavigateToChat,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = scheme.primaryContainer)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Filled.AutoAwesome, null, tint = scheme.onPrimaryContainer)
                                Column(verticalArrangement = Arrangement.Center) {
                                    Text("Ask AI Chat", fontWeight = FontWeight.Bold, color = scheme.onPrimaryContainer, fontSize = 14.sp)
                                    Text("Q&A on your cashflow", fontSize = 11.sp, color = scheme.onPrimaryContainer.copy(alpha = 0.75f))
                                }
                            }
                        }

                        Card(
                            onClick = {
                                showAiBottomSheet = true
                                aiViewModel.generateInsights(selectedPeriod)
                            },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = scheme.surface),
                            border = BorderStroke(1.dp, scheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Filled.Assessment, null, tint = scheme.primary)
                                Column(verticalArrangement = Arrangement.Center) {
                                    Text("AI Report", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(selectedPeriod.displayName, fontSize = 11.sp, color = scheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }

                // Health Score Card
                financialScore?.let { score ->
                    item { FinancialScoreCard(score = score) }
                }

                // Anomalies Banner
                if (anomalies.isNotEmpty() && selectedPeriod == ReportPeriod.THIS_MONTH) {
                    item { AnomalyAlertBanner(anomalies = anomalies, isPrivacyMode = isPrivacyMode) }
                }

                // Income vs Spending 4-Month Comparison Bar Chart
                if (monthlyComparison.isNotEmpty()) {
                    item { MonthlyComparisonCard(points = monthlyComparison, isPrivacyMode = isPrivacyMode) }
                }

                // Donut Chart + Fees & Transfers Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = scheme.surface),
                        border = BorderStroke(1.dp, scheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (expenses.isNotEmpty()) {
                                SpendingDonutChart(
                                    expenses = expenses,
                                    isPrivacyMode = isPrivacyMode,
                                    modifier = Modifier.fillMaxWidth().height(185.dp)
                                )
                            } else {
                                Text("No expense spend recorded in ${selectedPeriod.displayName}", color = scheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 20.dp))
                            }

                            val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { minimumFractionDigits = 2; maximumFractionDigits = 2 } }
                            if ((totalFees ?: 0.0) > 0 || (totalTransfers ?: 0.0) > 0) {
                                Spacer(Modifier.height(10.dp))
                                HorizontalDivider(color = scheme.outlineVariant)
                                Spacer(Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, null, tint = scheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                                        Text(
                                            text = if (isPrivacyMode) "Fees: ••••" else "Fees: Ksh ${fmt.format(totalFees ?: 0.0)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Text(
                                        text = if (isPrivacyMode) "Transfers: ••••" else "Transfers: Ksh ${fmt.format(totalTransfers ?: 0.0)}",
                                        fontSize = 12.sp,
                                        color = semantic.transfer,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // Interactive Daily Spending Trend Chart
                if (dailySpend.isNotEmpty() && selectedPeriod != ReportPeriod.ALL_TIME) {
                    item {
                        InteractiveDailySpendCard(dailySpend = dailySpend, isPrivacyMode = isPrivacyMode)
                    }
                }

                // Multi-Account Spending Split
                if (accountSourceExpenses.size > 1) {
                    item { AccountSourceBreakdownCard(sources = accountSourceExpenses, isPrivacyMode = isPrivacyMode) }
                }

                // Person / Merchant Lookup
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Lookup Person, Paybill or Till") },
                        placeholder = { Text("e.g. Naivas, KPLC, John") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = { Icon(Icons.Filled.Search, null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Filled.Close, "Clear")
                                }
                            }
                        }
                    )

                    if (searchQuery.isNotBlank() && (personTotalSpent != null || personTotalIncome != null || personTotalFees != null)) {
                        val fmt = NumberFormat.getNumberInstance(Locale.US).apply { minimumFractionDigits = 2; maximumFractionDigits = 2 }
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = scheme.surfaceVariant.copy(alpha = 0.45f)),
                            border = BorderStroke(1.dp, scheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Summary for \"$searchQuery\"", fontWeight = FontWeight.Bold)
                                Text(if (isPrivacyMode) "Sent: ••••" else "True Spend: Ksh ${fmt.format(personTotalSpent ?: 0.0)}", color = semantic.expense)
                                Text(if (isPrivacyMode) "Received: ••••" else "True Income: Ksh ${fmt.format(personTotalIncome ?: 0.0)}", color = semantic.income)
                                Text(if (isPrivacyMode) "Fees: ••••" else "Fees Paid: Ksh ${fmt.format(personTotalFees ?: 0.0)}", color = scheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // Category Breakdown
                if (expenses.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Spending by Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${expenses.size} categories", color = scheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                    items(expenses) { expense ->
                        val catEntity = categories.find { it.id == expense.categoryId }
                        CategoryBreakdownRow(
                            expense = expense,
                            catEntity = catEntity,
                            isPrivacyMode = isPrivacyMode,
                            onClick = { onNavigateToTransactions(expense.categoryId ?: -1) }
                        )
                    }
                }
            }
        }
    }

    if (showAiBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showAiBottomSheet = false
                aiViewModel.resetState()
            },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp).padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AutoAwesome, null, tint = scheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("AI Financial Report", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(Modifier.height(16.dp))
                when (aiState) {
                    is AiReportState.Idle -> {}
                    is AiReportState.Loading -> {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Analyzing your finances...", color = scheme.onSurfaceVariant, fontSize = 13.sp)
                    }
                    is AiReportState.Success -> {
                        val report = (aiState as AiReportState.Success).reportMarkdown
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            item { Text(report, lineHeight = 22.sp, fontSize = 14.sp) }
                        }
                    }
                    is AiReportState.Error -> {
                        Text("Error: ${(aiState as AiReportState.Error).message}", color = scheme.error, fontSize = 13.sp)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { aiViewModel.generateInsights(selectedPeriod) }) { Text("Retry") }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialScoreCard(score: FinancialScore) {
    val semantic = BillerTheme.colors
    val scheme = MaterialTheme.colorScheme
    val gradeColor = when (score.grade) {
        "A", "B" -> semantic.income
        "C" -> semantic.warning
        else -> semantic.expense
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        border = BorderStroke(1.dp, gradeColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(54.dp).clip(CircleShape).background(gradeColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(score.grade, color = gradeColor, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Text("${score.score}/100", color = gradeColor, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("MONTHLY HEALTH SCORE", color = scheme.onSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.6.sp)
                Text(score.summary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                if (score.tips.isNotEmpty()) {
                    Spacer(Modifier.height(3.dp))
                    Text(score.tips.first(), color = scheme.primary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun AnomalyAlertBanner(anomalies: List<SpendingAnomaly>, isPrivacyMode: Boolean) {
    val top = anomalies.first()
    val fmt = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }
    val semantic = BillerTheme.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = semantic.warning.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, semantic.warning.copy(alpha = 0.45f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Filled.Warning, null, tint = semantic.warning, modifier = Modifier.size(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Spending Spike Detected", color = semantic.warning, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                    if (isPrivacyMode) "${top.categoryName} is ${String.format(Locale.US, "%.1f", top.multiplier)}x above usual average"
                    else "${top.categoryName} is ${String.format(Locale.US, "%.1f", top.multiplier)}x usual (Ksh ${fmt.format(top.currentAmount)} vs avg Ksh ${fmt.format(top.averageAmount)})",
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun MonthlyComparisonCard(points: List<MonthlyComparisonPoint>, isPrivacyMode: Boolean) {
    val maxVal = points.maxOfOrNull { maxOf(it.income, it.expense) }?.coerceAtLeast(1.0) ?: 1.0
    val inFmt = remember { SimpleDateFormat("yyyy-MM", Locale.US) }
    val outFmt = remember { SimpleDateFormat("MMM", Locale.US) }
    val semantic = BillerTheme.colors
    val scheme = MaterialTheme.colorScheme

    val momDelta: Int? = if (points.size >= 2) {
        val prev = points[points.size - 2].expense
        val curr = points.last().expense
        if (prev > 0) (((curr - prev) / prev) * 100).toInt() else null
    } else null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        border = BorderStroke(1.dp, scheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Monthly Cashflow", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("True Income vs True Spend", fontSize = 11.sp, color = scheme.onSurfaceVariant)
                }
                if (momDelta != null) {
                    val isDown = momDelta <= 0
                    val col = if (isDown) semantic.income else semantic.expense
                    Surface(shape = RoundedCornerShape(50), color = col.copy(alpha = 0.14f)) {
                        Text(
                            text = "${if (isDown) "▼" else "▲"} ${kotlin.math.abs(momDelta)}% Spend MoM",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            color = col,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth().height(135.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                points.forEach { pt ->
                    val incRatio = (pt.income / maxVal).toFloat().coerceIn(0.04f, 1f)
                    val expRatio = (pt.expense / maxVal).toFloat().coerceIn(0.04f, 1f)
                    val label = try {
                        inFmt.parse(pt.monthKey)?.let { outFmt.format(it) } ?: pt.monthKey
                    } catch (_: Exception) { pt.monthKey }

                    val expLabel = if (isPrivacyMode) "••" else if (pt.expense >= 1000) "${String.format(Locale.US, "%.1f", pt.expense / 1000)}k" else "${pt.expense.toInt()}"

                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(expLabel, fontSize = 10.sp, color = scheme.onSurfaceVariant, maxLines = 1)
                        Spacer(Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height((85f * incRatio).dp)
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(semantic.income.copy(alpha = 0.85f))
                            )
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height((85f * expRatio).dp)
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(semantic.expense.copy(alpha = 0.85f))
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveDailySpendCard(dailySpend: List<DailySpend>, isPrivacyMode: Boolean) {
    var selectedIdx by remember(dailySpend) { mutableIntStateOf(dailySpend.lastIndex) }
    val selectedDay = dailySpend.getOrNull(selectedIdx)
    val peakDay = dailySpend.maxByOrNull { it.totalAmount }
    val avgDaily = if (dailySpend.isNotEmpty()) dailySpend.sumOf { it.totalAmount } / dailySpend.size else 0.0
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val scheme = MaterialTheme.colorScheme
    val maxSpend = dailySpend.maxOfOrNull { it.totalAmount }?.coerceAtLeast(1.0) ?: 1.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        border = BorderStroke(1.dp, scheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Daily Spending Trend", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (isPrivacyMode) "Avg •••• / active day" else "Avg Ksh ${fmt.format(avgDaily)} · Peak Ksh ${fmt.format(peakDay?.totalAmount ?: 0.0)}",
                        fontSize = 11.sp,
                        color = scheme.onSurfaceVariant
                    )
                }
                if (selectedDay != null) {
                    Surface(shape = RoundedCornerShape(10.dp), color = scheme.primaryContainer) {
                        Text(
                            text = if (isPrivacyMode) "${selectedDay.dateString.takeLast(5)}: ••••"
                            else "${selectedDay.dateString.takeLast(5)}: Ksh ${fmt.format(selectedDay.totalAmount)}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = scheme.onPrimaryContainer,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Interactive bar row (tap any bar to inspect exact day)
            Row(
                modifier = Modifier.fillMaxWidth().height(95.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                dailySpend.forEachIndexed { index, item ->
                    val ratio = (item.totalAmount / maxSpend).toFloat().coerceIn(0.06f, 1f)
                    val isSel = index == selectedIdx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { selectedIdx = index },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(ratio)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isSel) scheme.primary else scheme.primary.copy(alpha = 0.35f))
                        )
                    }
                }
            }
            Text("Tap any bar to inspect date & amount", fontSize = 10.sp, color = scheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SpendingDonutChart(
    expenses: List<CategoryExpense>,
    isPrivacyMode: Boolean,
    modifier: Modifier = Modifier
) {
    val totalSpend = expenses.sumOf { it.totalAmount }
    val scheme = MaterialTheme.colorScheme
    val fmt = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(150.dp)) {
            var startAngle = -90f
            expenses.forEach { expense ->
                val sweep = if (totalSpend > 0) ((expense.totalAmount / totalSpend) * 360f).toFloat() else 0f
                val sliceColor = parseCategoryColor(expense.colorCode, Color.Gray)
                drawArc(
                    color = sliceColor,
                    startAngle = startAngle,
                    sweepAngle = (sweep - 2f).coerceAtLeast(1f),
                    useCenter = false,
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height),
                    style = Stroke(width = 34f)
                )
                startAngle += sweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TRUE SPEND", color = scheme.onSurfaceVariant, fontSize = 10.sp, letterSpacing = 0.8.sp)
            Text(
                text = if (isPrivacyMode) "Ksh ••••••" else "Ksh ${fmt.format(totalSpend)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
private fun CategoryBreakdownRow(
    expense: CategoryExpense,
    catEntity: CategoryEntity?,
    isPrivacyMode: Boolean,
    onClick: () -> Unit
) {
    val catName = expense.categoryName ?: "Uncategorized"
    val scheme = MaterialTheme.colorScheme
    val catColor = parseCategoryColor(expense.colorCode, scheme.primary)
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val semantic = BillerTheme.colors

    val limit = expense.budgetLimit
    val hasBudget = limit != null && limit > 0
    val progress = if (hasBudget) (expense.totalAmount / limit!!).toFloat() else 0f
    val progressColor = when {
        progress >= 1.0f -> semantic.expense
        progress >= 0.8f -> semantic.warning
        else -> semantic.income
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        border = BorderStroke(1.dp, scheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp).fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(catColor.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(categoryIconFor(catEntity?.iconName), null, tint = catColor, modifier = Modifier.size(18.dp))
                    }
                    Text(catName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        if (isPrivacyMode) "Ksh ••••" else "Ksh ${fmt.format(expense.totalAmount)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (hasBudget) {
                        Text(
                            if (isPrivacyMode) "of ••••" else "of Ksh ${fmt.format(limit)}",
                            fontSize = 11.sp,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (hasBudget) {
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = progressColor,
                    trackColor = scheme.surfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AccountSourceBreakdownCard(sources: List<MonthlyTotal>, isPrivacyMode: Boolean) {
    val total = sources.sumOf { it.totalAmount }.coerceAtLeast(1.0)
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val scheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        border = BorderStroke(1.dp, scheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Spending by Account", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            sources.forEach { entry ->
                val src = AccountSource.fromName(entry.monthString)
                val pct = (entry.totalAmount / total).toFloat().coerceIn(0f, 1f)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(src.displayName, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Text(
                            text = if (isPrivacyMode) "${(pct * 100).toInt()}%"
                            else "Ksh ${fmt.format(entry.totalAmount)} (${(pct * 100).toInt()}%)",
                            color = scheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                    LinearProgressIndicator(
                        progress = { pct },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }
    }
}
