package com.mpesa.tracker.ui.transactions

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.domain.parser.AccountSource
import com.mpesa.tracker.ui.components.AddManualTransactionSheet
import com.mpesa.tracker.ui.components.TransactionDetailBottomSheet
import com.mpesa.tracker.ui.components.TransactionLedgerCard
import com.mpesa.tracker.ui.theme.BillerTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionsScreen(
    initialCategoryId: Int? = null,
    initialNature: String? = null,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val natureFilter by viewModel.natureFilter.collectAsState()
    val uncategorizedOnly by viewModel.filterUncategorizedOnly.collectAsState()
    val activeCategoryFilterId by viewModel.filterByCategoryId.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val selectedIds by viewModel.selectedTransactionIds.collectAsState()
    val selectedSimId by viewModel.selectedSimId.collectAsState()
    val activeSimIds by viewModel.activeSimIds.collectAsState()
    val selectedAccountSource by viewModel.selectedAccountSource.collectAsState()
    val activeAccountSources by viewModel.activeAccountSources.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()

    var detailTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var quickCategoryTx by remember { mutableStateOf<TransactionEntity?>(null) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var showBulkCategorySheet by remember { mutableStateOf(false) }
    var showBulkDeleteConfirm by remember { mutableStateOf(false) }
    var showAddManualSheet by remember { mutableStateOf(false) }

    val scheme = MaterialTheme.colorScheme
    val semantic = BillerTheme.colors
    val context = LocalContext.current
    val fmt = remember { NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 } }

    LaunchedEffect(initialCategoryId, initialNature) {
        when {
            initialCategoryId == -1 -> viewModel.applyUncategorizedFilter()
            initialCategoryId != null -> viewModel.setCategoryFilter(initialCategoryId)
            initialNature == "INCOME" -> viewModel.applyNatureDeepLink(NatureFilter.INCOME)
            initialNature == "EXPENSES" -> viewModel.applyNatureDeepLink(NatureFilter.EXPENSES)
            initialNature == "TRANSFERS" -> viewModel.applyNatureDeepLink(NatureFilter.TRANSFERS)
            initialNature == "ALL" -> viewModel.applyNatureDeepLink(NatureFilter.ALL)
        }
    }

    // Group transactions by Calendar Day string for clean ledger readability
    val groupedByDay = remember(transactions) {
        val dayKeyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        transactions.groupBy { dayKeyFormat.format(Date(it.dateTimestamp)) }
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text("${selectedIds.size} Selected", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            isSelectionMode = false
                            viewModel.clearSelection()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cancel")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { if (selectedIds.isNotEmpty()) showBulkCategorySheet = true }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Label, contentDescription = "Categorize")
                        }
                        IconButton(
                            onClick = {
                                if (selectedIds.isNotEmpty()) {
                                    viewModel.markSelectedAsTransfers()
                                    isSelectionMode = false
                                    Toast.makeText(context, "Marked as transfers", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(Icons.Filled.SwapHoriz, contentDescription = "Mark Transfer", tint = semantic.transfer)
                        }
                        IconButton(onClick = { if (selectedIds.isNotEmpty()) showBulkDeleteConfirm = true }) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = scheme.error)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = scheme.primaryContainer)
                )
            } else {
                TopAppBar(
                    title = {
                        Column {
                            Text("Activity", fontWeight = FontWeight.ExtraBold)
                            Text(
                                text = "${transactions.size} entries",
                                style = MaterialTheme.typography.labelSmall,
                                color = scheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.togglePrivacyMode() }) {
                            Icon(
                                if (isPrivacyMode) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle Privacy",
                                tint = if (isPrivacyMode) scheme.primary else scheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { isSelectionMode = true }) {
                            Icon(Icons.Filled.Checklist, contentDescription = "Bulk Select", tint = scheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { viewModel.syncNewSms() }, enabled = !isSyncing) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Filled.Sync, contentDescription = "Sync New SMS", tint = scheme.primary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = scheme.background)
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                ExtendedFloatingActionButton(
                    onClick = { showAddManualSheet = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Log Cash") }
                )
            }
        },
        containerColor = scheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                placeholder = { Text("Search merchant, receipt, account, note...") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Filled.Close, "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(NatureFilter.entries) { filter ->
                    FilterChip(
                        selected = natureFilter == filter && !uncategorizedOnly && activeCategoryFilterId == null,
                        onClick = {
                            if (uncategorizedOnly) viewModel.toggleUncategorizedFilter()
                            viewModel.setCategoryFilter(null)
                            viewModel.setNatureFilter(filter)
                        },
                        label = { Text(filter.label) }
                    )
                }

                if (activeAccountSources.size > 1) {
                    items(activeAccountSources) { srcName ->
                        val src = AccountSource.fromName(srcName)
                        val isSel = selectedAccountSource == srcName
                        FilterChip(
                            selected = isSel,
                            onClick = { viewModel.setAccountSourceFilter(if (isSel) null else srcName) },
                            label = { Text(src.shortLabel) }
                        )
                    }
                }

                if (activeSimIds.size > 1) {
                    items(activeSimIds) { simId ->
                        val isSel = selectedSimId == simId
                        FilterChip(
                            selected = isSel,
                            onClick = { viewModel.setSimFilter(if (isSel) null else simId) },
                            label = { Text("SIM ${activeSimIds.indexOf(simId) + 1}") }
                        )
                    }
                }

                item {
                    FilterChip(
                        selected = uncategorizedOnly,
                        onClick = { viewModel.toggleUncategorizedFilter() },
                        label = { Text("Uncategorized") }
                    )
                }

                if (activeCategoryFilterId != null) {
                    item {
                        val catName = categories.find { it.id == activeCategoryFilterId }?.name ?: "Category"
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.setCategoryFilter(null) },
                            label = { Text("✕ $catName") }
                        )
                    }
                }
            }

            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.AutoMirrored.Filled.ReceiptLong, null, tint = scheme.onSurfaceVariant, modifier = Modifier.size(44.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No matching transactions", color = scheme.onSurfaceVariant)
                        if (searchQuery.isNotEmpty() || natureFilter != NatureFilter.ALL || uncategorizedOnly || activeCategoryFilterId != null || selectedAccountSource != null) {
                            Spacer(Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.clearAllFilters() }) {
                                Text("Clear Filters")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 84.dp)
                ) {
                    groupedByDay.forEach { (dayKey, dayTxs) ->
                        val sampleTs = dayTxs.first().dateTimestamp
                        val daySpent = dayTxs.filter { it.natureEnum == TransactionNature.EXPENSE }.sumOf { it.amount }
                        val headerTitle = formatDayHeader(sampleTs)

                        stickyHeader(key = "header_$dayKey") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(scheme.background)
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = headerTitle,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = scheme.onSurfaceVariant
                                )
                                if (daySpent > 0) {
                                    Text(
                                        text = if (isPrivacyMode) "Spent ••••" else "-Ksh ${fmt.format(daySpent)}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = semantic.expense,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        items(dayTxs, key = { it.receiptNumber }) { tx ->
                            val category = categories.find { it.id == tx.categoryId }
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { dismissValue ->
                                    if (dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                                        quickCategoryTx = tx
                                        false
                                    } else false
                                },
                                positionalThreshold = { it * 0.35f }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = tx.natureEnum == TransactionNature.EXPENSE && !isSelectionMode,
                                enableDismissFromEndToStart = false,
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(scheme.primaryContainer, RoundedCornerShape(16.dp))
                                            .padding(start = 20.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.AutoMirrored.Filled.Label, null, tint = scheme.onPrimaryContainer)
                                            Spacer(Modifier.width(8.dp))
                                            Text("Categorize", color = scheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            ) {
                                TransactionLedgerCard(
                                    transaction = tx,
                                    category = category,
                                    isPrivacyMode = isPrivacyMode,
                                    isSelected = selectedIds.contains(tx.receiptNumber),
                                    onClick = {
                                        if (isSelectionMode) {
                                            viewModel.toggleSelection(tx.receiptNumber)
                                        } else {
                                            detailTransaction = tx
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    detailTransaction?.let { tx ->
        TransactionDetailBottomSheet(
            transaction = tx,
            categories = categories,
            onDismiss = { detailTransaction = null },
            onChangeNature = { newNature, saveRule ->
                viewModel.updateTransactionNature(tx.receiptNumber, newNature, saveRule)
                detailTransaction = tx.copy(
                    transactionNature = newNature.name,
                    categoryId = if (newNature == TransactionNature.EXPENSE) tx.categoryId else null
                )
                Toast.makeText(context, "Updated to ${newNature.name.lowercase()}", Toast.LENGTH_SHORT).show()
            },
            onSelectCategory = { catId, applyAll ->
                viewModel.updateTransactionCategory(tx.receiptNumber, catId, tx.recipientName, applyAll)
                detailTransaction = tx.copy(categoryId = catId)
            },
            onCreateCategory = { name -> viewModel.addCategory(name) },
            onSaveNote = { note ->
                viewModel.updateUserNote(tx.receiptNumber, note)
                detailTransaction = tx.copy(userNote = note?.trim()?.takeIf { it.isNotEmpty() })
            },
            onDelete = {
                viewModel.deleteTransaction(tx.receiptNumber)
                detailTransaction = null
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

    if (showBulkCategorySheet) {
        ModalBottomSheet(onDismissRequest = { showBulkCategorySheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Categorize ${selectedIds.size} Selected", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        Surface(
                            onClick = {
                                viewModel.categorizeSelected(category.id)
                                showBulkCategorySheet = false
                                isSelectionMode = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = scheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(category.name, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    quickCategoryTx?.let { tx ->
        ModalBottomSheet(onDismissRequest = { quickCategoryTx = null }) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Categorize ${tx.recipientName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        Surface(
                            onClick = {
                                viewModel.updateTransactionCategory(tx.receiptNumber, category.id, tx.recipientName, true)
                                quickCategoryTx = null
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = scheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(category.name, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showBulkDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showBulkDeleteConfirm = false },
            title = { Text("Delete ${selectedIds.size} Entries?") },
            text = { Text("Removed entries are excluded from totals immediately. Phone SMS inbox is untouched.") },
            confirmButton = {
                Button(
                    onClick = {
                        showBulkDeleteConfirm = false
                        viewModel.deleteSelected()
                        isSelectionMode = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = scheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showBulkDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

private fun formatDayHeader(timestampMs: Long): String {
    val cal = Calendar.getInstance()
    val todayYear = cal.get(Calendar.YEAR)
    val todayDay = cal.get(Calendar.DAY_OF_YEAR)

    val txCal = Calendar.getInstance().apply { timeInMillis = timestampMs }
    val txYear = txCal.get(Calendar.YEAR)
    val txDay = txCal.get(Calendar.DAY_OF_YEAR)

    return when {
        txYear == todayYear && txDay == todayDay -> "Today"
        txYear == todayYear && txDay == todayDay - 1 -> "Yesterday"
        else -> SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date(timestampMs))
    }
}
