package com.mpesa.tracker.ui.plan

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mpesa.tracker.data.local.entities.SubscriptionEntity
import com.mpesa.tracker.domain.intelligence.PotentialSubscription
import com.mpesa.tracker.ui.components.categoryIconFor
import com.mpesa.tracker.ui.components.parseCategoryColor
import com.mpesa.tracker.ui.theme.BillerTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    initialTab: String? = null,
    onNavigateToTransactions: (categoryId: Int) -> Unit,
    viewModel: PlanViewModel = hiltViewModel()
) {
    val activeSubTab by viewModel.activeSubTab.collectAsState()
    val budgetItems by viewModel.budgetItems.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val budgetFilter by viewModel.filter.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()
    val detectedPatterns by viewModel.detectedPatterns.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()

    var editingCategory by remember { mutableStateOf<BudgetItemUi?>(null) }
    var showAddBillDialog by remember { mutableStateOf(false) }

    val scheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    LaunchedEffect(initialTab) {
        when (initialTab) {
            "BILLS" -> viewModel.selectSubTab(PlanSubTab.BILLS)
            "BUDGETS" -> viewModel.selectSubTab(PlanSubTab.BUDGETS)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plan", fontWeight = FontWeight.ExtraBold) },
                actions = {
                    IconButton(onClick = { viewModel.togglePrivacyMode() }) {
                        Icon(
                            imageVector = if (isPrivacyMode) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Privacy",
                            tint = if (isPrivacyMode) scheme.primary else scheme.onSurfaceVariant
                        )
                    }
                    if (activeSubTab == PlanSubTab.BILLS) {
                        IconButton(onClick = { showAddBillDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Bill", tint = scheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = scheme.background)
            )
        },
        containerColor = scheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // M3 Segmented Pill Switcher: Budgets vs Bills & Subs
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                PlanSubTab.entries.forEachIndexed { index, tab ->
                    SegmentedButton(
                        selected = activeSubTab == tab,
                        onClick = { viewModel.selectSubTab(tab) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = PlanSubTab.entries.size)
                    ) {
                        Text(tab.label, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            if (activeSubTab == PlanSubTab.BUDGETS) {
                BudgetsTabContent(
                    summary = summary,
                    budgetItems = budgetItems,
                    activeFilter = budgetFilter,
                    isPrivacyMode = isPrivacyMode,
                    onFilterChange = viewModel::setFilter,
                    onClickItem = onNavigateToTransactions,
                    onEditItem = { editingCategory = it }
                )
            } else {
                BillsTabContent(
                    subscriptions = subscriptions,
                    detectedPatterns = detectedPatterns,
                    isPrivacyMode = isPrivacyMode,
                    onTrackPattern = { p ->
                        viewModel.trackRecurringBill(p.merchantName, p.merchantName, p.suggestedAmount)
                        Toast.makeText(context, "Reminder scheduled for ${p.merchantName}", Toast.LENGTH_SHORT).show()
                    },
                    onToggleActive = viewModel::toggleSubscriptionActive,
                    onDelete = viewModel::deleteSubscription
                )
            }
        }
    }

    editingCategory?.let { item ->
        SetBudgetDialog(
            item = item,
            onDismiss = { editingCategory = null },
            onSave = { limit ->
                viewModel.setBudget(item.categoryId, limit)
                editingCategory = null
            },
            onDelete = {
                viewModel.removeBudget(item.categoryId)
                editingCategory = null
            }
        )
    }

    if (showAddBillDialog) {
        AddRecurringBillDialog(
            onDismiss = { showAddBillDialog = false },
            onSave = { name, amount, cycle ->
                viewModel.trackRecurringBill(name, name, amount, cycle)
                showAddBillDialog = false
                Toast.makeText(context, "Recurring bill added", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun BudgetsTabContent(
    summary: BudgetSummaryUi,
    budgetItems: List<BudgetItemUi>,
    activeFilter: BudgetFilter,
    isPrivacyMode: Boolean,
    onFilterChange: (BudgetFilter) -> Unit,
    onClickItem: (Int) -> Unit,
    onEditItem: (BudgetItemUi) -> Unit
) {
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val semantic = BillerTheme.colors
    val scheme = MaterialTheme.colorScheme
    val animatedProgress by animateFloatAsState(summary.overallProgress, tween(500), label = "overallBudget")
    val barColor = when {
        summary.overBudgetCount > 0 -> semantic.expense
        summary.overallProgress >= 0.8f -> semantic.warning
        else -> semantic.income
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Overview Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(listOf(semantic.heroStart, semantic.heroEnd)))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "MONTHLY BUDGETS",
                        color = semantic.onHero.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    if (summary.activeBudgetCount > 0) {
                        Surface(shape = RoundedCornerShape(50), color = Color.Black.copy(alpha = 0.22f)) {
                            Text(
                                "${summary.activeBudgetCount} active limits",
                                color = semantic.onHero,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            if (isPrivacyMode) "Ksh ••••••" else "Ksh ${formatter.format(summary.totalSpentInBudgeted)}",
                            color = semantic.onHero,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            if (summary.totalBudgetCap > 0) {
                                if (isPrivacyMode) "of Ksh •••• limit" else "of Ksh ${formatter.format(summary.totalBudgetCap)} budgeted"
                            } else "Tap + on any category to set a monthly limit",
                            color = semantic.onHero.copy(alpha = 0.72f),
                            fontSize = 12.sp
                        )
                    }
                    if (summary.totalBudgetCap > 0) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                if (isPrivacyMode) "••••" else "Ksh ${formatter.format(summary.totalRemaining)}",
                                color = Color(0xFF4ADE80),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Remaining", color = semantic.onHero.copy(alpha = 0.7f), fontSize = 11.sp)
                        }
                    }
                }

                if (summary.totalBudgetCap > 0) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = barColor,
                        trackColor = Color.White.copy(alpha = 0.16f)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                FilterChip(
                    selected = activeFilter == BudgetFilter.ALL,
                    onClick = { onFilterChange(BudgetFilter.ALL) },
                    label = { Text("All (${budgetItems.size})") }
                )
            }
            item {
                FilterChip(
                    selected = activeFilter == BudgetFilter.ACTIVE_ONLY,
                    onClick = { onFilterChange(BudgetFilter.ACTIVE_ONLY) },
                    label = { Text("Active (${summary.activeBudgetCount})") }
                )
            }
            if (summary.overBudgetCount > 0) {
                item {
                    FilterChip(
                        selected = activeFilter == BudgetFilter.OVER_BUDGET,
                        onClick = { onFilterChange(BudgetFilter.OVER_BUDGET) },
                        label = { Text("Over Limit (${summary.overBudgetCount})") }
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(budgetItems, key = { it.categoryId }) { item ->
                val catColor = parseCategoryColor(item.colorCode, scheme.primary)
                val progressColor by animateColorAsState(
                    when {
                        item.isOverBudget -> semantic.expense
                        item.progress >= 0.8f -> semantic.warning
                        else -> semantic.income
                    },
                    label = "catBudgetColor"
                )

                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onClickItem(item.categoryId) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surface),
                    border = BorderStroke(
                        1.dp,
                        if (item.isOverBudget) semantic.expense.copy(alpha = 0.55f) else scheme.outlineVariant
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(catColor.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(categoryIconFor(item.iconName), null, tint = catColor, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text(item.categoryName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        if (isPrivacyMode) "Ksh •••• spent" else "Ksh ${formatter.format(item.spentThisMonth)} spent",
                                        color = scheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (item.hasBudget) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = if (item.isOverBudget) "Over limit"
                                            else if (isPrivacyMode) "•••• left"
                                            else "Ksh ${formatter.format(item.remainingAmount)} left",
                                            color = progressColor,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            if (isPrivacyMode) "Limit ••••" else "Limit Ksh ${formatter.format(item.monthlyLimit ?: 0.0)}",
                                            color = scheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                IconButton(onClick = { onEditItem(item) }) {
                                    Icon(
                                        if (item.hasBudget) Icons.Filled.Edit else Icons.Filled.Add,
                                        contentDescription = "Edit Budget",
                                        tint = if (item.hasBudget) scheme.onSurfaceVariant else scheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        if (item.hasBudget) {
                            Spacer(Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { item.progress.coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = progressColor,
                                trackColor = scheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BillsTabContent(
    subscriptions: List<SubscriptionEntity>,
    detectedPatterns: List<PotentialSubscription>,
    isPrivacyMode: Boolean,
    onTrackPattern: (PotentialSubscription) -> Unit,
    onToggleActive: (SubscriptionEntity) -> Unit,
    onDelete: (Int) -> Unit
) {
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }
    val semantic = BillerTheme.colors
    val scheme = MaterialTheme.colorScheme

    val totalMonthly = subscriptions.filter { it.isActive }.sumOf {
        it.amount * (30.0 / it.billingCycleDays.coerceAtLeast(1))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(semantic.heroStart, semantic.heroEnd)))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "EST. MONTHLY RECURRING",
                            color = semantic.onHero.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            if (isPrivacyMode) "Ksh ••••••" else "Ksh ${fmt.format(totalMonthly)}",
                            color = semantic.onHero,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "${subscriptions.count { it.isActive }} active reminders",
                            color = Color(0xFF4ADE80),
                            fontSize = 12.sp
                        )
                    }
                    Icon(Icons.Filled.NotificationsActive, null, tint = Color(0xFF4ADE80), modifier = Modifier.size(28.dp))
                }
            }
        }

        if (subscriptions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surface),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No recurring bills scheduled yet", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Tap + above or Track a detected pattern below to get due reminders.",
                            color = scheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(subscriptions, key = { it.id }) { sub ->
                val dateLabel = sub.expectedNextPaymentDate?.let {
                    "Next due: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)}"
                } ?: "Every ${sub.billingCycleDays} days"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surface),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sub.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(dateLabel, color = scheme.onSurfaceVariant, fontSize = 12.sp)
                            Text(
                                if (isPrivacyMode) "Ksh ••••" else "Ksh ${fmt.format(sub.amount)} / ${sub.billingCycleDays}d",
                                color = scheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = sub.isActive,
                                onCheckedChange = { onToggleActive(sub) }
                            )
                            IconButton(onClick = { onDelete(sub.id) }) {
                                Icon(Icons.Filled.DeleteOutline, "Delete", tint = scheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        if (detectedPatterns.isNotEmpty()) {
            item {
                Text(
                    "DETECTED FROM YOUR TRANSACTIONS",
                    color = scheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            items(detectedPatterns, key = { it.merchantName }) { pattern ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(pattern.merchantName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(
                                if (isPrivacyMode) "${pattern.occurrences} similar payments"
                                else "~Ksh ${fmt.format(pattern.suggestedAmount)} · ${pattern.occurrences} payments",
                                color = scheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                        FilledTonalButton(onClick = { onTrackPattern(pattern) }) {
                            Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Track")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetBudgetDialog(
    item: BudgetItemUi,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit,
    onDelete: () -> Unit
) {
    var amountText by remember { mutableStateOf(item.monthlyLimit?.toInt()?.toString() ?: "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item.hasBudget) "Edit Budget" else "Set Monthly Budget") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Monthly spending limit for ${item.categoryName}:", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    label = { Text("Limit (Ksh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(2000, 5000, 10000, 20000).forEach { preset ->
                        AssistChip(
                            onClick = { amountText = preset.toString() },
                            label = { Text("${preset / 1000}k") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { amountText.toDoubleOrNull()?.takeIf { it > 0 }?.let(onSave) }) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                if (item.hasBudget) {
                    TextButton(onClick = onDelete) {
                        Text("Remove", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }
        }
    )
}

@Composable
private fun AddRecurringBillDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, amount: Double, cycleDays: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var cycleDays by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Recurring Bill") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Bill Name (e.g. Zuku, Rent)") }, singleLine = true)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Expected Amount (Ksh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = cycleDays,
                    onValueChange = { cycleDays = it.filter { c -> c.isDigit() } },
                    label = { Text("Billing Cycle (days, e.g. 30)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            val amt = amount.toDoubleOrNull()
            val cyc = cycleDays.toIntOrNull()?.coerceAtLeast(1) ?: 30
            Button(
                onClick = { if (name.isNotBlank() && amt != null && amt > 0) onSave(name.trim(), amt, cyc) },
                enabled = name.isNotBlank() && amt != null && amt > 0
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
