package com.mpesa.tracker.ui.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mpesa.tracker.data.local.entities.CategoryExpense
import com.mpesa.tracker.data.local.entities.DailySpend
import com.mpesa.tracker.domain.models.ReportPeriod
import com.mpesa.tracker.ui.theme.PrimaryGreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTransactions: (Int?) -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val expenses by viewModel.expensesByCategory.collectAsState()
    val dailySpend by viewModel.dailySpendTrend.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val exportState by viewModel.exportState.collectAsState()
    val selectedSimId by viewModel.selectedSimId.collectAsState()
    val activeSimIds by viewModel.activeSimIds.collectAsState()
    val context = LocalContext.current

    val dateRangePickerState = rememberDateRangePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

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
                viewModel.onPeriodSelected(ReportPeriod.THIS_MONTH) // Fallback if cancelled
            },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    if (dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null) {
                        viewModel.setCustomDateRange(
                            start = dateRangePickerState.selectedStartDateMillis,
                            end = dateRangePickerState.selectedEndDateMillis
                        )
                    } else {
                        viewModel.onPeriodSelected(ReportPeriod.THIS_MONTH)
                    }
                }) {
                    Text("Apply Range")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDatePicker = false
                    viewModel.onPeriodSelected(ReportPeriod.THIS_MONTH) 
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = { Text("Select Reporting Date Range", modifier = Modifier.padding(16.dp)) },
                showModeToggle = false,
                modifier = Modifier.weight(1f)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spending Reports", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.exportData(context) },
                containerColor = PrimaryGreen,
                contentColor = Color.White
            ) {
                if (exportState is ExportState.Exporting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Filled.Download, contentDescription = "Export CSV")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            
            // Period Filter Ribbon
            ScrollableTabRow(
                selectedTabIndex = ReportPeriod.values().indexOf(selectedPeriod),
                edgePadding = 16.dp,
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    if (ReportPeriod.values().indexOf(selectedPeriod) < tabPositions.size) {
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[ReportPeriod.values().indexOf(selectedPeriod)]),
                            color = PrimaryGreen
                        )
                    }
                }
            ) {
                ReportPeriod.values().forEach { period ->
                    Tab(
                        selected = selectedPeriod == period,
                        onClick = { 
                            if (period == ReportPeriod.CUSTOM) {
                                showDatePicker = true
                            } else {
                                viewModel.onPeriodSelected(period)
                            }
                        },
                        text = { 
                            Text(
                                text = period.displayName, 
                                fontWeight = if (selectedPeriod == period) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedPeriod == period) PrimaryGreen else Color.Gray
                            ) 
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SIM Selection Filter
            if (activeSimIds.isNotEmpty()) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSimId == null,
                            onClick = { viewModel.setSimFilter(null) },
                            label = { Text("All Lines") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                                selectedLabelColor = PrimaryGreen
                            )
                        )
                    }
                    items(activeSimIds.size) { index ->
                        val simId = activeSimIds[index]
                        FilterChip(
                            selected = selectedSimId == simId,
                            onClick = { viewModel.setSimFilter(simId) },
                            label = { Text("SIM ${index + 1}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                                selectedLabelColor = PrimaryGreen
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            if (expenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No expenses found for this period.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        // Pie Chart Visualization
                        SpendingPieChart(expenses = expenses, modifier = Modifier.fillMaxWidth().height(200.dp))
                    }
                    
                    item {
                        if (dailySpend.isNotEmpty() && selectedPeriod != ReportPeriod.ALL_TIME) {
                            Text(
                                text = "Daily Spending Trend",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                            DailySpendBarChart(
                                dailySpend = dailySpend, 
                                modifier = Modifier.fillMaxWidth().height(120.dp).padding(horizontal = 8.dp)
                            )
                        }
                    }
                    item {
                        Text(
                            text = "Category Breakdown",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                    }

                    items(expenses) { expense ->
                        CategoryExpenseCard(
                            expense = expense,
                            onSetBudget = { limit -> 
                                if (expense.categoryId != null) {
                                    viewModel.setBudget(expense.categoryId, limit) 
                                }
                            },
                            onClickRow = { 
                                // Drill down using either the Category ID, or route null for "Uncategorized" specific UI
                                onNavigateToTransactions(expense.categoryId ?: -1)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailySpendBarChart(dailySpend: List<DailySpend>, modifier: Modifier = Modifier) {
    if (dailySpend.isEmpty()) return
    
    val maxSpend = dailySpend.maxOfOrNull { it.totalAmount }?.toFloat() ?: 1f
    
    Canvas(modifier = modifier) {
        val barSpacing = 8.dp.toPx()
        val totalSpacing = barSpacing * (dailySpend.size - 1).coerceAtLeast(0)
        val barWidth = ((size.width - totalSpacing) / dailySpend.size.toFloat()).coerceAtLeast(4.dp.toPx())
        val maxBarHeight = size.height
        
        dailySpend.forEachIndexed { index, spend ->
            val barHeight = (spend.totalAmount.toFloat() / maxSpend) * maxBarHeight
            val xOffset = index * (barWidth + barSpacing)
            
            drawRect(
                color = PrimaryGreen.copy(alpha = 0.8f),
                topLeft = Offset(xOffset, maxBarHeight - barHeight),
                size = Size(barWidth, barHeight)
            )
        }
    }
}

@Composable
fun SpendingPieChart(expenses: List<CategoryExpense>, modifier: Modifier = Modifier) {
    val totalSpend = expenses.sumOf { it.totalAmount }
    
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(160.dp)) {
            var startAngle = -90f
            
            expenses.forEach { expense ->
                val sweepAngle = ((expense.totalAmount / totalSpend) * 360f).toFloat()
                val colorHex = expense.colorCode ?: "#9E9E9E"
                val sliceColor = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { Color.Gray }
                
                drawArc(
                    color = sliceColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height),
                    style = Stroke(width = 60f)
                )
                startAngle += sweepAngle
            }
        }
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total", color = Color.Gray, fontSize = 12.sp)
            val formatter = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }
            Text(
                "Ksh ${formatter.format(totalSpend)}", 
                fontWeight = FontWeight.Bold, 
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun CategoryExpenseCard(
    expense: CategoryExpense,
    onSetBudget: (Double) -> Unit,
    onClickRow: () -> Unit
) {
    val catName = expense.categoryName ?: "Uncategorized"
    val colorHex = expense.colorCode ?: "#9E9E9E" // Default gray
    
    var showBudgetDialog by remember { mutableStateOf(false) }
    var budgetInput by remember { mutableStateOf("") }
    
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Color.Gray
    }

    val formatter = NumberFormat.getNumberInstance(Locale.US).apply { 
        minimumFractionDigits = 2; maximumFractionDigits = 2 
    }
    val amountStr = "Ksh ${formatter.format(expense.totalAmount)}"
    
    val budgetLimit = expense.budgetLimit
    val hasBudget = budgetLimit != null && budgetLimit > 0
    val progress = if (hasBudget) (expense.totalAmount / budgetLimit!!).toFloat() else 0f
    
    val progressColor = when {
        progress >= 0.9f -> Color.Red
        progress >= 0.75f -> Color(0xFFFFA000) // Amber/Yellow
        else -> PrimaryGreen
    }

    if (showBudgetDialog) {
        AlertDialog(
            onDismissRequest = { showBudgetDialog = false },
            title = { Text("Set Monthly Budget") },
            text = {
                Column {
                    Text("Enter budget limit for $catName:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { budgetInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Amount (Ksh)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val limit = budgetInput.toDoubleOrNull()
                    if (limit != null) {
                        onSetBudget(limit)
                    }
                    showBudgetDialog = false
                    budgetInput = ""
                }) {
                    Text("Save", color = PrimaryGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBudgetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClickRow() }, // Drill down
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = catName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = amountStr,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (hasBudget) {
                            Text(
                                text = "of Ksh ${formatter.format(budgetLimit)}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showBudgetDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Set Budget",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            if (hasBudget) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}
