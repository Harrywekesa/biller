package com.mpesa.tracker.ui.overview

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LabelImportant
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.domain.parser.AccountSource
import com.mpesa.tracker.ui.components.AddManualTransactionSheet
import com.mpesa.tracker.ui.components.TransactionDetailBottomSheet
import com.mpesa.tracker.ui.components.TransactionLedgerCard
import com.mpesa.tracker.ui.theme.BillerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    onNavigateToActivity: (categoryId: Int?, natureFilter: String?) -> Unit,
    onNavigateToPlan: (openBillsTab: Boolean) -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: OverviewViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val activeSimIds by viewModel.activeSimIds.collectAsState()
    val selectedSimId by viewModel.selectedSimId.collectAsState()
    val selectedAccountFilter by viewModel.selectedAccountFilter.collectAsState()

    var selectedTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var showAddManualSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme
    val semantic = BillerTheme.colors

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Biller", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text(
                            text = "${state.todayTxCount} transactions today",
                            style = MaterialTheme.typography.labelSmall,
                            color = scheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.togglePrivacyMode() }) {
                        Icon(
                            imageVector = if (isPrivacyMode) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle Privacy",
                            tint = if (isPrivacyMode) scheme.primary else scheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { viewModel.syncNow() }, enabled = !isSyncing) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Sync, contentDescription = "Sync SMS", tint = scheme.primary)
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = scheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = scheme.background)
            )
        },
        containerColor = scheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // Optional Multi-SIM filter row
            if (activeSimIds.size > 1) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            }

            // Hero Balance & Net Cashflow Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(semantic.heroStart, semantic.heroEnd)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "M-PESA BALANCE",
                                color = semantic.onHero.copy(alpha = 0.72f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.Black.copy(alpha = 0.22f)
                            ) {
                                Text(
                                    text = if (isPrivacyMode) "Pace ••••/day" else "Pace Ksh ${state.dailyAveragePace}/day",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    color = semantic.onHero.copy(alpha = 0.9f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Text(
                            text = if (isPrivacyMode) "Ksh ••••••" else "Ksh ${state.mpesaBalance}",
                            color = if (!isPrivacyMode && state.isNegativeBalance) semantic.warning else semantic.onHero,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )

                        HorizontalDivider(color = semantic.onHero.copy(alpha = 0.14f))

                        // True Money In vs True Money Out (Internal transfers excluded)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = Color.Black.copy(alpha = 0.2f),
                                onClick = { onNavigateToActivity(null, "INCOME") }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.ArrowDownward, null, tint = Color(0xFF4ADE80), modifier = Modifier.size(16.dp))
                                    Column {
                                        Text("True Income", color = semantic.onHero.copy(alpha = 0.7f), fontSize = 10.sp)
                                        Text(
                                            text = if (isPrivacyMode) "Ksh ••••" else "Ksh ${state.monthIncome}",
                                            color = Color(0xFF4ADE80),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                color = Color.Black.copy(alpha = 0.2f),
                                onClick = { onNavigateToActivity(null, "EXPENSES") }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.ArrowUpward, null, tint = Color(0xFFFF8A80), modifier = Modifier.size(16.dp))
                                    Column {
                                        Text("True Spend", color = semantic.onHero.copy(alpha = 0.7f), fontSize = 10.sp)
                                        Text(
                                            text = if (isPrivacyMode) "Ksh ••••" else "Ksh ${state.monthSpend}",
                                            color = Color(0xFFFF8A80),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Fees & Internal Transfers transparency footer
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isPrivacyMode) "Fees: ••••" else "Fees Counted: Ksh ${state.monthFees}",
                                color = semantic.onHero.copy(alpha = 0.8f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (isPrivacyMode) "Transfers: ••••" else "Transfers (Excl.): Ksh ${state.monthTransfers}",
                                color = semantic.onHero.copy(alpha = 0.68f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Connected Wallets & Banks Strip (filter recent transactions by tapping)
            if (state.wallets.size > 1) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            val isSelected = selectedAccountFilter == null
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setAccountFilter(null) },
                                label = { Text("All Accounts") }
                            )
                        }
                        items(state.wallets, key = { it.source.name }) { wallet ->
                            val isSelected = selectedAccountFilter == wallet.source.name
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setAccountFilter(if (isSelected) null else wallet.source.name)
                                },
                                label = {
                                    val detail = if (isPrivacyMode && wallet.balanceLabel != null) "••••"
                                    else wallet.balanceLabel ?: "${wallet.txCount} tx"
                                    Text("${wallet.source.shortLabel} · $detail")
                                }
                            )
                        }
                    }
                }
            }

            // Attention / Action Chips Row
            if (state.uncategorizedCount > 0 || state.overBudgetCount > 0 || state.upcomingBillsCount > 0) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (state.uncategorizedCount > 0) {
                            item {
                                AssistChip(
                                    onClick = { onNavigateToActivity(-1, null) },
                                    leadingIcon = {
                                        Icon(Icons.AutoMirrored.Filled.LabelImportant, null, tint = semantic.warning, modifier = Modifier.size(16.dp))
                                    },
                                    label = { Text("${state.uncategorizedCount} uncategorized") }
                                )
                            }
                        }
                        if (state.overBudgetCount > 0) {
                            item {
                                AssistChip(
                                    onClick = { onNavigateToPlan(false) },
                                    leadingIcon = {
                                        Icon(Icons.Filled.Warning, null, tint = semantic.expense, modifier = Modifier.size(16.dp))
                                    },
                                    label = { Text("${state.overBudgetCount} over budget") }
                                )
                            }
                        }
                        if (state.upcomingBillsCount > 0) {
                            item {
                                AssistChip(
                                    onClick = { onNavigateToPlan(true) },
                                    leadingIcon = {
                                        Icon(Icons.Filled.EventRepeat, null, tint = scheme.primary, modifier = Modifier.size(16.dp))
                                    },
                                    label = { Text("${state.upcomingBillsCount} bills due soon") }
                                )
                            }
                        }
                    }
                }
            }

            // Recent Activity Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedAccountFilter != null) {
                            "Recent · ${AccountSource.fromName(selectedAccountFilter).displayName}"
                        } else "Recent Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilledTonalButton(
                            onClick = { showAddManualSheet = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Filled.Add, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cash", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        TextButton(onClick = { onNavigateToActivity(null, "ALL") }) {
                            Text("See All", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (state.recentTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = scheme.surface),
                        border = BorderStroke(1.dp, scheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ReceiptLong, null, tint = scheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No transactions synced yet", color = scheme.onSurfaceVariant)
                            Spacer(Modifier.height(8.dp))
                            FilledTonalButton(onClick = { viewModel.syncNow() }) {
                                Text("Sync M-Pesa & Bank SMS")
                            }
                        }
                    }
                }
            } else {
                items(state.recentTransactions, key = { it.receiptNumber }) { tx ->
                    val category = categories.find { it.id == tx.categoryId }
                    TransactionLedgerCard(
                        transaction = tx,
                        category = category,
                        isPrivacyMode = isPrivacyMode,
                        onClick = { selectedTransaction = tx }
                    )
                }
            }

            // Subtle quick-jump footer into Insights
            item {
                Card(
                    onClick = onNavigateToInsights,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Filled.AutoAwesome, null, tint = scheme.primary)
                            Column {
                                Text("Cashflow Insights & AI Co-Pilot", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("Monthly trends, anomaly alerts & ask AI", fontSize = 11.sp, color = scheme.onSurfaceVariant)
                            }
                        }
                        Icon(Icons.Filled.ChevronRight, null, tint = scheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    selectedTransaction?.let { tx ->
        TransactionDetailBottomSheet(
            transaction = tx,
            categories = categories,
            onDismiss = { selectedTransaction = null },
            onChangeNature = { newNature, saveRule ->
                viewModel.updateTransactionNature(tx.receiptNumber, newNature, saveRule)
                selectedTransaction = tx.copy(
                    transactionNature = newNature.name,
                    categoryId = if (newNature == com.mpesa.tracker.data.local.entities.TransactionNature.EXPENSE) tx.categoryId else null
                )
                Toast.makeText(context, "Updated to ${newNature.name.lowercase()}", Toast.LENGTH_SHORT).show()
            },
            onSelectCategory = { catId, applyAll ->
                viewModel.updateTransactionCategory(tx.receiptNumber, catId, tx.recipientName, applyAll)
                selectedTransaction = tx.copy(categoryId = catId)
            },
            onCreateCategory = { name -> viewModel.addCategory(name) },
            onSaveNote = { note ->
                viewModel.updateUserNote(tx.receiptNumber, note)
                selectedTransaction = tx.copy(userNote = note?.trim()?.takeIf { it.isNotEmpty() })
            },
            onDelete = {
                viewModel.deleteTransaction(tx.receiptNumber)
                selectedTransaction = null
                Toast.makeText(context, "Transaction deleted", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showAddManualSheet) {
        AddManualTransactionSheet(
            categories = categories,
            onDismiss = { showAddManualSheet = false },
            onSave = { merchant, amount, fee, nature, source, catId, note, dateTs, businessCode ->
                viewModel.addManualTransaction(merchant, amount, fee, nature, source, catId, note, dateTs, businessCode)
                showAddManualSheet = false
                Toast.makeText(context, "Saved entry", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
