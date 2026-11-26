package com.mpesa.tracker.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mpesa.tracker.data.local.entities.CategoryEntity
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.data.local.entities.TransactionNature
import com.mpesa.tracker.data.local.entities.TransactionType
import com.mpesa.tracker.domain.dictionary.BusinessDictionary
import com.mpesa.tracker.domain.parser.AccountSource
import com.mpesa.tracker.ui.theme.BillerTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun accountSourceLabel(sourceName: String): AccountSource = AccountSource.fromName(sourceName)

fun formatTransactionType(type: TransactionType): String = when (type) {
    TransactionType.PAYBILL -> "Paybill / Payment"
    TransactionType.BUY_GOODS -> "Buy Goods (Till)"
    TransactionType.SEND_MONEY -> "Send Money"
    TransactionType.RECEIVED_MONEY -> "Money Received"
    TransactionType.WITHDRAW_CASH -> "Cash Withdrawal"
    TransactionType.BUY_AIRTIME -> "Airtime & Data"
    TransactionType.FULIZA_DRAW -> "Fuliza Draw"
    TransactionType.FULIZA_REPAYMENT -> "Fuliza Repayment"
    TransactionType.UNKNOWN -> "Transfer / Other"
}

/**
 * Adaptive Material 3 transaction ledger card displaying nature semantics (Expense, Income,
 * Internal Transfer, Loan, Reversal), category icons, account source badge, fees, and note preview.
 */
@Composable
fun TransactionLedgerCard(
    transaction: TransactionEntity,
    category: CategoryEntity?,
    isPrivacyMode: Boolean,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val semantic = BillerTheme.colors
    val scheme = MaterialTheme.colorScheme
    val nature = transaction.natureEnum
    val source = transaction.sourceEnum

    val formatter = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }
    val timeStr = remember(transaction.dateTimestamp) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(transaction.dateTimestamp)
    }

    val accentColor = when (nature) {
        TransactionNature.INCOME -> semantic.income
        TransactionNature.TRANSFER -> semantic.transfer
        TransactionNature.LOAN_DRAW, TransactionNature.LOAN_REPAY -> semantic.warning
        TransactionNature.REVERSAL -> scheme.outline
        TransactionNature.EXPENSE -> if (transaction.fulizaAmount != null) semantic.warning else semantic.expense
    }

    val prefix = when (nature) {
        TransactionNature.INCOME -> "+"
        TransactionNature.TRANSFER -> "⇄ "
        TransactionNature.LOAN_DRAW, TransactionNature.LOAN_REPAY -> ""
        TransactionNature.REVERSAL -> "↺ "
        TransactionNature.EXPENSE -> "-"
    }

    val catColor = category?.let { parseCategoryColor(it.colorCode, accentColor) } ?: accentColor
    val amountText = if (isPrivacyMode) "${prefix}Ksh ••••" else "${prefix}Ksh ${formatter.format(transaction.amount)}"

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) scheme.primaryContainer.copy(alpha = 0.55f) else scheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) scheme.primary else scheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon Badge (Category icon for expenses, Transfer/Loan/Income icon otherwise)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            nature == TransactionNature.EXPENSE && category != null -> catColor.copy(alpha = 0.14f)
                            else -> accentColor.copy(alpha = 0.14f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    nature == TransactionNature.TRANSFER -> Icon(
                        Icons.Filled.SwapHoriz,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    nature == TransactionNature.LOAN_DRAW || nature == TransactionNature.LOAN_REPAY -> Icon(
                        Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(19.dp)
                    )
                    nature == TransactionNature.REVERSAL -> Icon(
                        Icons.AutoMirrored.Filled.Undo,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(19.dp)
                    )
                    category != null -> Icon(
                        categoryIconFor(category.iconName),
                        contentDescription = category.name,
                        tint = catColor,
                        modifier = Modifier.size(20.dp)
                    )
                    else -> Text(
                        text = transaction.recipientName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            // Title & metadata pills
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = transaction.recipientName,
                    color = if (nature == TransactionNature.REVERSAL) scheme.onSurfaceVariant else scheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (nature == TransactionNature.REVERSAL) TextDecoration.LineThrough else TextDecoration.None
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "$timeStr · ${source.shortLabel}",
                        color = scheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )

                    val badgeLabel = when (nature) {
                        TransactionNature.TRANSFER -> "Transfer"
                        TransactionNature.LOAN_DRAW -> "Fuliza Draw"
                        TransactionNature.LOAN_REPAY -> "Fuliza Repay"
                        TransactionNature.REVERSAL -> "Reversed"
                        TransactionNature.INCOME -> "Income"
                        TransactionNature.EXPENSE -> category?.name
                    }

                    if (!badgeLabel.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = (if (nature == TransactionNature.EXPENSE && category != null) catColor else accentColor)
                                .copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = badgeLabel,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                color = if (nature == TransactionNature.EXPENSE && category != null) catColor else accentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (!transaction.userNote.isNullOrBlank()) {
                    Text(
                        text = transaction.userNote,
                        color = scheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Amount + fee column
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amountText,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textDecoration = if (nature == TransactionNature.REVERSAL) TextDecoration.LineThrough else TextDecoration.None
                )
                if (!isPrivacyMode && transaction.transactionCost > 0) {
                    Text(
                        text = "Fee Ksh ${formatter.format(transaction.transactionCost)}",
                        color = scheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailBottomSheet(
    transaction: TransactionEntity,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onChangeNature: (newNature: TransactionNature, saveRule: Boolean) -> Unit,
    onSelectCategory: (categoryId: Int, applyToAll: Boolean) -> Unit,
    onCreateCategory: (name: String) -> Unit,
    onSaveNote: (note: String?) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scheme = MaterialTheme.colorScheme
    val semantic = BillerTheme.colors

    val formatter = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }
    val dateStr = remember(transaction.dateTimestamp) {
        SimpleDateFormat("EEE, MMM dd yyyy · hh:mm a", Locale.getDefault())
            .format(transaction.dateTimestamp)
    }

    val source = transaction.sourceEnum
    val nature = transaction.natureEnum
    val accentColor = when (nature) {
        TransactionNature.INCOME -> semantic.income
        TransactionNature.TRANSFER -> semantic.transfer
        TransactionNature.LOAN_DRAW, TransactionNature.LOAN_REPAY -> semantic.warning
        TransactionNature.REVERSAL -> scheme.outline
        TransactionNature.EXPENSE -> semantic.expense
    }
    val prefix = when (nature) {
        TransactionNature.INCOME -> "+"
        TransactionNature.TRANSFER -> "⇄ "
        TransactionNature.EXPENSE -> "-"
        else -> ""
    }

    var noteText by remember(transaction.receiptNumber) { mutableStateOf(transaction.userNote ?: "") }
    var showRawSms by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var applyRuleToMerchant by remember { mutableStateOf(true) }
    var rememberTransferRule by remember { mutableStateOf(false) }
    var showNewCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val currentCategory = categories.find { it.id == transaction.categoryId }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = scheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (nature) {
                            TransactionNature.TRANSFER -> Icons.Filled.SwapHoriz
                            TransactionNature.INCOME -> Icons.Filled.ArrowDownward
                            else -> currentCategory?.let { categoryIconFor(it.iconName) } ?: Icons.AutoMirrored.Filled.ReceiptLong
                        },
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.recipientName,
                        color = scheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = scheme.secondaryContainer
                        ) {
                            Text(
                                text = source.displayName,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = scheme.onSecondaryContainer,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = accentColor.copy(alpha = 0.14f)
                        ) {
                            Text(
                                text = formatTransactionType(transaction.type),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Amount card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = scheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, scheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (nature) {
                                TransactionNature.TRANSFER -> "INTERNAL TRANSFER (EXCLUDED FROM SPEND/INCOME)"
                                TransactionNature.INCOME -> "TRUE INCOME"
                                TransactionNature.LOAN_DRAW -> "OVERDRAFT / LOAN DRAW"
                                TransactionNature.LOAN_REPAY -> "LOAN REPAYMENT"
                                TransactionNature.REVERSAL -> "REVERSED"
                                TransactionNature.EXPENSE -> "SPENDING AMOUNT"
                            },
                            color = scheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.6.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "${prefix}Ksh ${formatter.format(transaction.amount)}",
                            color = accentColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    if (transaction.transactionCost > 0) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text("FEE (COUNTED)", color = scheme.onSurfaceVariant, fontSize = 10.sp)
                            Text(
                                text = "Ksh ${formatter.format(transaction.transactionCost)}",
                                color = scheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Classification Nature Switcher (lets user mark any tx as Transfer vs Expense vs Income!)
            if (nature != TransactionNature.LOAN_DRAW && nature != TransactionNature.LOAN_REPAY && nature != TransactionNature.REVERSAL) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = scheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Accounting Classification",
                            color = scheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                TransactionNature.EXPENSE to "Expense",
                                TransactionNature.INCOME to "Income",
                                TransactionNature.TRANSFER to "Transfer"
                            ).forEach { (option, label) ->
                                val selected = nature == option
                                FilterChip(
                                    selected = selected,
                                    onClick = { onChangeNature(option, rememberTransferRule) },
                                    label = { Text(label, fontSize = 12.sp) }
                                )
                            }
                        }
                        if (!transaction.isManualEntry) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { rememberTransferRule = !rememberTransferRule }
                            ) {
                                Checkbox(
                                    checked = rememberTransferRule,
                                    onCheckedChange = { rememberTransferRule = it },
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Remember for all transactions with ${transaction.recipientName.take(24)}",
                                    color = scheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Receipt & metadata
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = scheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, scheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Receipt / Ref", color = scheme.onSurfaceVariant, fontSize = 12.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                clipboard.setText(AnnotatedString(transaction.receiptNumber))
                                Toast.makeText(context, "Receipt copied", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text(
                                text = transaction.receiptNumber,
                                color = scheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(Modifier.width(6.dp))
                            Icon(Icons.Filled.ContentCopy, "Copy", tint = scheme.primary, modifier = Modifier.size(14.dp))
                        }
                    }

                    DetailRow("Date & Time", dateStr)
                    if (!transaction.recipientNumber.isNullOrBlank()) {
                        DetailRow("Account / Till / Phone", transaction.recipientNumber)
                        BusinessDictionary.lookupCode(transaction.recipientNumber)?.let { reg ->
                            DetailRow("Paybill/Till Registry", "${reg.merchantName} (${reg.categoryName})", scheme.primary)
                        }
                    }
                    if (!transaction.linkedReceiptNumber.isNullOrBlank() && nature == TransactionNature.REVERSAL) {
                        DetailRow("Reversed Receipt Ref", transaction.linkedReceiptNumber, scheme.outline)
                    }
                    if (!transaction.transferGroupRef.isNullOrBlank()) {
                        val refLabel = if (nature == TransactionNature.REVERSAL) "Reversed By Receipt" else "Linked Transfer Ref"
                        val refColor = if (nature == TransactionNature.REVERSAL) scheme.outline else semantic.transfer
                        DetailRow(refLabel, transaction.transferGroupRef, refColor)
                    }
                    if (transaction.balance != null) {
                        val balLabel = if (transaction.type == TransactionType.FULIZA_DRAW) "Fuliza Outstanding" else "${source.shortLabel} Balance"
                        DetailRow(balLabel, "Ksh ${formatter.format(transaction.balance)}")
                    }
                    if (transaction.fulizaAmount != null && transaction.fulizaAmount > 0) {
                        DetailRow(
                            "Fuliza Utilized",
                            "Ksh ${formatter.format(transaction.fulizaAmount)}" +
                                (transaction.fulizaFee?.let { " (Fee: Ksh ${formatter.format(it)})" } ?: ""),
                            semantic.warning
                        )
                    }
                }
            }

            // Category picker (Expenses only)
            if (nature == TransactionNature.EXPENSE) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = scheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showCategoryPicker = !showCategoryPicker },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Category", color = scheme.onSurfaceVariant, fontSize = 11.sp)
                                Text(
                                    text = currentCategory?.name ?: "Uncategorized",
                                    color = scheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            TextButton(onClick = { showCategoryPicker = !showCategoryPicker }) {
                                Text(if (showCategoryPicker) "Hide" else "Change")
                            }
                        }

                        AnimatedVisibility(visible = showCategoryPicker) {
                            Column(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { applyRuleToMerchant = !applyRuleToMerchant }
                                ) {
                                    Checkbox(
                                        checked = applyRuleToMerchant,
                                        onCheckedChange = { applyRuleToMerchant = it },
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Apply to all expenses for ${transaction.recipientName.take(24)}",
                                        color = scheme.onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(categories, key = { it.id }) { cat ->
                                        FilterChip(
                                            selected = cat.id == transaction.categoryId,
                                            onClick = {
                                                onSelectCategory(cat.id, applyRuleToMerchant)
                                                showCategoryPicker = false
                                            },
                                            leadingIcon = {
                                                Icon(categoryIconFor(cat.iconName), null, modifier = Modifier.size(16.dp))
                                            },
                                            label = { Text(cat.name) }
                                        )
                                    }
                                    item {
                                        AssistChip(
                                            onClick = { showNewCategoryDialog = true },
                                            label = { Text("+ New Category") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Personal Note
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Personal Note") },
                placeholder = { Text("Add context (e.g. Groceries, Client lunch)") },
                maxLines = 2,
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    val hasChanges = noteText.trim() != (transaction.userNote ?: "").trim()
                    if (hasChanges) {
                        TextButton(onClick = {
                            onSaveNote(noteText)
                            Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )

            // Raw SMS Toggle
            if (!transaction.isManualEntry) {
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { showRawSms = !showRawSms },
                    shape = RoundedCornerShape(14.dp),
                    color = scheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Sms, null, tint = scheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Original SMS", color = scheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Icon(
                                if (showRawSms) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                null,
                                tint = scheme.onSurfaceVariant
                            )
                        }
                        AnimatedVisibility(visible = showRawSms) {
                            Text(
                                text = transaction.rawSmsBody,
                                modifier = Modifier.padding(top = 10.dp),
                                color = scheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, scheme.error.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = scheme.error)
            ) {
                Icon(Icons.Filled.DeleteOutline, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Delete Entry", fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Transaction?") },
            text = {
                Text(
                    "Removing ${transaction.recipientName} (Ksh ${formatter.format(transaction.amount)}) updates your totals and budgets immediately. Your phone's SMS inbox is not altered."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = scheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showNewCategoryDialog) {
        AlertDialog(
            onDismissRequest = {
                showNewCategoryDialog = false
                newCategoryName = ""
            },
            title = { Text("New Category") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        onCreateCategory(newCategoryName.trim())
                        showNewCategoryDialog = false
                        newCategoryName = ""
                    }
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNewCategoryDialog = false
                    newCategoryName = ""
                }) { Text("Cancel") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddManualTransactionSheet(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (
        merchant: String,
        amount: Double,
        fee: Double,
        nature: TransactionNature,
        source: AccountSource,
        categoryId: Int?,
        note: String?,
        dateTimestamp: Long,
        businessCode: String?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var merchant by remember { mutableStateOf("") }
    var businessCode by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var feeText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var nature by remember { mutableStateOf(TransactionNature.EXPENSE) }
    var source by remember { mutableStateOf(AccountSource.CASH) }
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id) }

    val now = remember { System.currentTimeMillis() }
    var selectedDateTimestamp by remember { mutableLongStateOf(now) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()) }

    val registrySuggestions = remember(businessCode, merchant) {
        val query = when {
            businessCode.isNotBlank() -> businessCode
            merchant.length >= 2 -> merchant
            else -> ""
        }
        if (query.isBlank()) emptyList() else BusinessDictionary.searchRegistry(query, limit = 6)
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateTimestamp)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { chosenUtcMidnight ->
                        val utcCal = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
                            timeInMillis = chosenUtcMidnight
                        }
                        val localCal = Calendar.getInstance().apply {
                            set(
                                utcCal.get(Calendar.YEAR),
                                utcCal.get(Calendar.MONTH),
                                utcCal.get(Calendar.DAY_OF_MONTH)
                            )
                        }
                        selectedDateTimestamp = localCal.timeInMillis
                    }
                    showDatePicker = false
                }) { Text("Select") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Log Manual Transaction", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Track cash spending, direct income, or custom account entries.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    TransactionNature.EXPENSE to "Expense",
                    TransactionNature.INCOME to "Income",
                    TransactionNature.TRANSFER to "Transfer"
                ).forEach { (option, label) ->
                    FilterChip(
                        selected = nature == option,
                        onClick = { nature = option },
                        label = { Text(label) }
                    )
                }
            }

            // Quick date selector
            Text(
                "Date: ${dateFormatter.format(Date(selectedDateTimestamp))}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val yesterdayTs = remember(now) { now - 24L * 60 * 60 * 1000L }
            val isTodayOrYesterday = dateFormatter.format(Date(selectedDateTimestamp)) in setOf(
                dateFormatter.format(Date(now)),
                dateFormatter.format(Date(yesterdayTs))
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = dateFormatter.format(Date(selectedDateTimestamp)) == dateFormatter.format(Date(now)),
                    onClick = { selectedDateTimestamp = now },
                    label = { Text("Today") }
                )
                FilterChip(
                    selected = dateFormatter.format(Date(selectedDateTimestamp)) == dateFormatter.format(Date(yesterdayTs)),
                    onClick = { selectedDateTimestamp = yesterdayTs },
                    label = { Text("Yesterday") }
                )
                AssistChip(
                    onClick = { showDatePicker = true },
                    leadingIcon = { Icon(Icons.Filled.CalendarToday, null, modifier = Modifier.size(15.dp)) },
                    label = {
                        Text(if (isTodayOrYesterday) "Pick Date" else dateFormatter.format(Date(selectedDateTimestamp)))
                    }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    label = { Text(if (nature == TransactionNature.INCOME) "Source / Sender" else "Merchant / Item") },
                    singleLine = true,
                    modifier = Modifier.weight(1.4f)
                )
                OutlinedTextField(
                    value = businessCode,
                    onValueChange = { businessCode = it.trim() },
                    label = { Text("Till / Paybill") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            if (registrySuggestions.isNotEmpty()) {
                Text(
                    "Paybill / Till Suggestions (tap to fill):",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(registrySuggestions, key = { it.code }) { entry ->
                        AssistChip(
                            onClick = {
                                merchant = entry.merchantName
                                businessCode = entry.code
                                categories.firstOrNull { it.name.equals(entry.categoryName, ignoreCase = true) }?.let {
                                    selectedCategoryId = it.id
                                }
                            },
                            label = { Text("${entry.code} · ${entry.merchantName}") }
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Amount (Ksh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = feeText,
                    onValueChange = { feeText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    label = { Text("Fee (Ksh)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(0.7f)
                )
            }

            Text("Wallet / Account", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf(AccountSource.CASH, AccountSource.MPESA, AccountSource.IM_BANK, AccountSource.ABSA, AccountSource.EQUITY)) { acct ->
                    FilterChip(
                        selected = source == acct,
                        onClick = { source = acct },
                        label = { Text(acct.displayName) }
                    )
                }
            }

            if (nature == TransactionNature.EXPENSE && categories.isNotEmpty()) {
                Text("Category", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories, key = { it.id }) { cat ->
                        FilterChip(
                            selected = selectedCategoryId == cat.id,
                            onClick = { selectedCategoryId = cat.id },
                            leadingIcon = { Icon(categoryIconFor(cat.iconName), null, modifier = Modifier.size(16.dp)) },
                            label = { Text(cat.name) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            val parsedAmt = amountText.toDoubleOrNull()
            Button(
                onClick = {
                    if (merchant.isNotBlank() && parsedAmt != null && parsedAmt > 0) {
                        onSave(
                            merchant.trim(),
                            parsedAmt,
                            feeText.toDoubleOrNull() ?: 0.0,
                            nature,
                            source,
                            if (nature == TransactionNature.EXPENSE) selectedCategoryId else null,
                            note.trim().takeIf { it.isNotEmpty() },
                            selectedDateTimestamp,
                            businessCode.trim().takeIf { it.isNotEmpty() }
                        )
                    }
                },
                enabled = merchant.isNotBlank() && parsedAmt != null && parsedAmt > 0,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Entry", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
