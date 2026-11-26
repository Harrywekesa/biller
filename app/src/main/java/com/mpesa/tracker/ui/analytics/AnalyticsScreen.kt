package com.mpesa.tracker.ui.analytics

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                    var selectedSliceIdx by remember(expenses) { mutableStateOf<Int?>(null) }
                    val selectedExpense = selectedSliceIdx?.let { expenses.getOrNull(it) }
                    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { minimumFractionDigits = 2; maximumFractionDigits = 2 } }
                    val intFmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
                    val totalSpend = expenses.sumOf { it.totalAmount }

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
                                    selectedIndex = selectedSliceIdx,
                                    onSelectSlice = { idx ->
                                        selectedSliceIdx = if (selectedSliceIdx == idx) null else idx
                                    },
                                    isPrivacyMode = isPrivacyMode,
                                    modifier = Modifier.fillMaxWidth().height(195.dp)
                                )

                                AnimatedVisibility(visible = selectedExpense != null) {
                                    selectedExpense?.let { sel ->
                                        val share = if (totalSpend > 0) ((sel.totalAmount / totalSpend) * 100.0).toInt() else 0
                                        val selColor = parseCategoryColor(sel.colorCode, scheme.primary)
                                        val catEntity = categories.find { it.id == sel.categoryId }
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = selColor.copy(alpha = 0.12f),
                                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(
                                                        categoryIconFor(catEntity?.iconName),
                                                        null,
                                                        tint = selColor,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                    Column {
                                                        Text(
                                                            sel.categoryName ?: "Uncategorized",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.sp,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                        Text(
                                                            if (isPrivacyMode) "$share% of spend"
                                                            else "Ksh ${intFmt.format(sel.totalAmount)} · $share% of spend",
                                                            fontSize = 11.sp,
                                                            color = scheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                                TextButton(
                                                    onClick = { onNavigateToTransactions(sel.categoryId ?: -1) },
                                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                                ) {
                                                    Text("View Tx →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text("No expense spend recorded in ${selectedPeriod.displayName}", color = scheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 20.dp))
                            }

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
    val fullFmt = remember { SimpleDateFormat("MMM yyyy", Locale.US) }
    val numFmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val semantic = BillerTheme.colors
    val scheme = MaterialTheme.colorScheme

    var selectedIdx by remember(points) { mutableIntStateOf(points.lastIndex) }
    val selectedPoint = points.getOrNull(selectedIdx)

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
                    Text("True Income vs True Spend (tap month)", fontSize = 11.sp, color = scheme.onSurfaceVariant)
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
                points.forEachIndexed { idx, pt ->
                    val incRatio = (pt.income / maxVal).toFloat().coerceIn(0.04f, 1f)
                    val expRatio = (pt.expense / maxVal).toFloat().coerceIn(0.04f, 1f)
                    val label = try {
                        inFmt.parse(pt.monthKey)?.let { outFmt.format(it) } ?: pt.monthKey
                    } catch (_: Exception) { pt.monthKey }
                    val isSel = idx == selectedIdx

                    val expLabel = if (isPrivacyMode) "••" else if (pt.expense >= 1000) "${String.format(Locale.US, "%.1f", pt.expense / 1000)}k" else "${pt.expense.toInt()}"

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) scheme.surfaceVariant.copy(alpha = 0.5f) else Color.Transparent)
                            .clickable { selectedIdx = idx }
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            expLabel,
                            fontSize = 10.sp,
                            color = if (isSel) scheme.onSurface else scheme.onSurfaceVariant,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height((80f * incRatio).dp)
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(semantic.income.copy(alpha = if (isSel) 1f else 0.6f))
                            )
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height((80f * expRatio).dp)
                                    .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                    .background(semantic.expense.copy(alpha = if (isSel) 1f else 0.6f))
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            label,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) scheme.primary else scheme.onSurface
                        )
                    }
                }
            }

            if (selectedPoint != null) {
                val monthTitle = try {
                    inFmt.parse(selectedPoint.monthKey)?.let { fullFmt.format(it) } ?: selectedPoint.monthKey
                } catch (_: Exception) { selectedPoint.monthKey }
                val net = selectedPoint.income - selectedPoint.expense
                Spacer(Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = scheme.surfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(monthTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (isPrivacyMode) "In •••• · Out ••••"
                            else "+${numFmt.format(selectedPoint.income)} / -${numFmt.format(selectedPoint.expense)}",
                            fontSize = 11.sp,
                            color = scheme.onSurfaceVariant
                        )
                        Text(
                            text = if (isPrivacyMode) "Net ••••"
                            else "Net ${if (net >= 0) "+" else "-"}${numFmt.format(kotlin.math.abs(net))}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (net >= 0) semantic.income else semantic.expense
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveDailySpendCard(dailySpend: List<DailySpend>, isPrivacyMode: Boolean) {
    var selectedIdx by remember(dailySpend) { mutableIntStateOf(dailySpend.lastIndex) }
    var chartWidthPx by remember { mutableFloatStateOf(1f) }
    val selectedDay = dailySpend.getOrNull(selectedIdx)
    val peakDay = dailySpend.maxByOrNull { it.totalAmount }
    val avgDaily = if (dailySpend.isNotEmpty()) dailySpend.sumOf { it.totalAmount } / dailySpend.size else 0.0
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val scheme = MaterialTheme.colorScheme
    val semantic = BillerTheme.colors
    val maxSpend = dailySpend.maxOfOrNull { it.totalAmount }?.coerceAtLeast(1.0) ?: 1.0

    fun indexForOffsetX(x: Float): Int {
        if (dailySpend.isEmpty() || chartWidthPx <= 0f) return 0
        val ratio = (x / chartWidthPx).coerceIn(0f, 0.9999f)
        return (ratio * dailySpend.size).toInt().coerceIn(0, dailySpend.lastIndex)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        border = BorderStroke(1.dp, scheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

            // Finger-scrubbable bar chart with dashed Average line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(108.dp)
                    .onSizeChanged { chartWidthPx = it.width.toFloat().coerceAtLeast(1f) }
                    .pointerInput(dailySpend) {
                        detectTapGestures { offset ->
                            selectedIdx = indexForOffsetX(offset.x)
                        }
                    }
                    .pointerInput(dailySpend) {
                        detectHorizontalDragGestures(
                            onDragStart = { offset -> selectedIdx = indexForOffsetX(offset.x) },
                            onHorizontalDrag = { change, _ ->
                                change.consume()
                                selectedIdx = indexForOffsetX(change.position.x)
                            }
                        )
                    }
            ) {
                // Background average dashed line
                val avgFraction = (avgDaily / maxSpend).toFloat().coerceIn(0.05f, 1f)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val yAvg = size.height * (1f - avgFraction)
                    drawLine(
                        color = semantic.warning.copy(alpha = 0.65f),
                        start = Offset(0f, yAvg),
                        end = Offset(size.width, yAvg),
                        strokeWidth = 2.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    dailySpend.forEachIndexed { index, item ->
                        val ratio = (item.totalAmount / maxSpend).toFloat().coerceIn(0.06f, 1f)
                        val isSel = index == selectedIdx
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (isSel) {
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 2.dp)
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(scheme.primary)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(ratio)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(
                                        when {
                                            isSel -> scheme.primary
                                            item.totalAmount > avgDaily -> scheme.primary.copy(alpha = 0.5f)
                                            else -> scheme.primary.copy(alpha = 0.25f)
                                        }
                                    )
                            )
                        }
                    }
                }
            }

            // Axis labels + interaction hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(dailySpend.first().dateString.takeLast(5), fontSize = 10.sp, color = scheme.onSurfaceVariant)
                Text("Drag or tap bars to scrub · Dashed = Avg", fontSize = 10.sp, color = scheme.onSurfaceVariant)
                Text(dailySpend.last().dateString.takeLast(5), fontSize = 10.sp, color = scheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SpendingDonutChart(
    expenses: List<CategoryExpense>,
    selectedIndex: Int?,
    onSelectSlice: (Int) -> Unit,
    isPrivacyMode: Boolean,
    modifier: Modifier = Modifier
) {
    val totalSpend = expenses.sumOf { it.totalAmount }
    val scheme = MaterialTheme.colorScheme
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val selectedExpense = selectedIndex?.let { expenses.getOrNull(it) }

    val sliceAngles = remember(expenses, totalSpend) {
        var cursor = -90f
        expenses.map { expense ->
            val sweep = if (totalSpend > 0) ((expense.totalAmount / totalSpend) * 360f).toFloat() else 0f
            val start = cursor
            cursor += sweep
            start to sweep
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(164.dp)
                .pointerInput(sliceAngles) {
                    detectTapGestures { offset ->
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val dx = offset.x - cx
                        val dy = offset.y - cy
                        val dist = kotlin.math.hypot(dx, dy)
                        val minR = size.width * 0.22f
                        val maxR = size.width * 0.56f
                        if (dist in minR..maxR) {
                            var deg = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
                            if (deg < 0f) deg += 360f
                            val hitIdx = sliceAngles.indexOfFirst { (start, sweep) ->
                                val relStart = (start + 90f)
                                deg >= relStart && deg < (relStart + sweep)
                            }
                            if (hitIdx != -1) {
                                onSelectSlice(hitIdx)
                            }
                        }
                    }
                }
        ) {
            val basePad = 14f
            sliceAngles.forEachIndexed { idx, (startAngle, sweep) ->
                val expense = expenses[idx]
                val rawColor = parseCategoryColor(expense.colorCode, Color.Gray)
                val isSelected = selectedIndex == idx
                val sliceColor = when {
                    selectedIndex == null || isSelected -> rawColor
                    else -> rawColor.copy(alpha = 0.30f)
                }
                val gap = if (expenses.size <= 1) 0f else minOf(2.5f, sweep * 0.25f)
                val drawSweep = (sweep - gap).coerceAtLeast(1.2f)
                val strokeW = if (isSelected) 44f else 30f
                val inset = if (isSelected) basePad - 5f else basePad

                drawArc(
                    color = sliceColor,
                    startAngle = startAngle,
                    sweepAngle = drawSweep,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = Size(size.minDimension - inset * 2, size.minDimension - inset * 2),
                    style = Stroke(width = strokeW, cap = StrokeCap.Butt)
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(105.dp)
        ) {
            if (selectedExpense != null) {
                val share = if (totalSpend > 0) ((selectedExpense.totalAmount / totalSpend) * 100.0).toInt() else 0
                val selColor = parseCategoryColor(selectedExpense.colorCode, scheme.primary)
                Text(
                    text = (selectedExpense.categoryName ?: "Other").uppercase(),
                    color = selColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (isPrivacyMode) "Ksh ••••" else "Ksh ${fmt.format(selectedExpense.totalAmount)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    maxLines = 1
                )
                Text("$share% share", color = scheme.onSurfaceVariant, fontSize = 10.sp)
            } else {
                Text("TRUE SPEND", color = scheme.onSurfaceVariant, fontSize = 10.sp, letterSpacing = 0.8.sp)
                Text(
                    text = if (isPrivacyMode) "Ksh ••••••" else "Ksh ${fmt.format(totalSpend)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
                Text("Tap slice", color = scheme.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 9.sp)
            }
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
