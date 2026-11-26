package com.mpesa.tracker.ui.settings

import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mpesa.tracker.data.local.entities.CategoryEntity
import com.mpesa.tracker.data.repository.ThemeMode
import com.mpesa.tracker.ui.components.categoryIconFor
import com.mpesa.tracker.ui.components.categoryIconOptions
import com.mpesa.tracker.ui.components.parseCategoryColor
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val dynamicColor by viewModel.dynamicColorEnabled.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()
    val appLockEnabled by viewModel.appLockEnabled.collectAsState()
    val budgetAlertsEnabled by viewModel.budgetAlertsEnabled.collectAsState()
    val billRemindersEnabled by viewModel.billRemindersEnabled.collectAsState()
    val dailyAiAdviceEnabled by viewModel.dailyAiAdviceEnabled.collectAsState()
    val selfIdentifiers by viewModel.selfIdentifiers.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val customRules by viewModel.customRules.collectAsState()
    val transferRules by viewModel.transferRules.collectAsState()
    val unparsedSms by viewModel.unparsedSms.collectAsState()
    val backups by viewModel.backups.collectAsState()
    val isRescanning by viewModel.isRescanning.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val rescanProgress by viewModel.rescanProgress.collectAsState()

    var showSelfIdDialog by remember { mutableStateOf(false) }
    var showCategoriesSheet by remember { mutableStateOf(false) }
    var showRulesSheet by remember { mutableStateOf(false) }
    var showUnparsedSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Vault", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = scheme.background)
            )
        },
        containerColor = scheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            // ── Appearance ────────────────────────────────────────────────────
            item { SectionHeader("Appearance (Adaptive Material 3)") }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surface),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Theme Mode", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            ThemeMode.entries.forEachIndexed { idx, mode ->
                                SegmentedButton(
                                    selected = themeMode == mode,
                                    onClick = { viewModel.setThemeMode(mode) },
                                    shape = SegmentedButtonDefaults.itemShape(idx, ThemeMode.entries.size)
                                ) {
                                    Text(mode.label.substringBefore(" "), fontSize = 12.sp)
                                }
                            }
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            ToggleRow(
                                title = "Material You Dynamic Color",
                                subtitle = "Adapt palette from your wallpaper",
                                checked = dynamicColor,
                                onCheckedChange = viewModel::setDynamicColor
                            )
                        }

                        ToggleRow(
                            title = "Hide Balances by Default",
                            subtitle = "Mask Ksh amounts across all tabs",
                            checked = isPrivacyMode,
                            onCheckedChange = viewModel::setPrivacyMode
                        )
                    }
                }
            }

            // ── Self-Transfer Accounts ────────────────────────────────────────
            item { SectionHeader("Internal Transfer Recognition") }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surface),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Your phone numbers, names, or account masks used to detect self-transfers automatically:",
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant
                        )
                        if (selfIdentifiers.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(selfIdentifiers.toList()) { id ->
                                    InputChip(
                                        selected = false,
                                        onClick = { viewModel.removeSelfIdentifier(id) },
                                        label = { Text(id) },
                                        trailingIcon = { Icon(Icons.Filled.Close, null, modifier = Modifier.size(14.dp)) }
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(onClick = { showSelfIdDialog = true }) {
                                Icon(Icons.Filled.Add, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Add Phone / Name")
                            }
                            OutlinedButton(onClick = { showRulesSheet = true }) {
                                Text("Transfer & Merchant Rules (${transferRules.size + customRules.size})")
                            }
                        }
                    }
                }
            }

            // ── Categories ────────────────────────────────────────────────────
            item { SectionHeader("Categories") }
            item {
                Card(
                    onClick = { showCategoriesSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surface),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Manage Categories", fontWeight = FontWeight.SemiBold)
                            Text("${categories.size} categories configured", style = MaterialTheme.typography.bodySmall, color = scheme.onSurfaceVariant)
                        }
                        Icon(Icons.Filled.ChevronRight, null)
                    }
                }
            }

            // ── Security & Alerts ─────────────────────────────────────────────
            item { SectionHeader("Security & Alerts") }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surface),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ToggleRow(
                            title = "Biometric App Lock",
                            subtitle = "Require fingerprint/PIN on app open",
                            checked = appLockEnabled,
                            onCheckedChange = viewModel::setAppLockEnabled
                        )
                        HorizontalDivider(color = scheme.outlineVariant)
                        ToggleRow(
                            title = "Budget Alerts (80% & 100%)",
                            subtitle = "Notify when monthly spending reaches thresholds",
                            checked = budgetAlertsEnabled,
                            onCheckedChange = viewModel::setBudgetAlertsEnabled
                        )
                        HorizontalDivider(color = scheme.outlineVariant)
                        ToggleRow(
                            title = "Upcoming Bill Reminders",
                            subtitle = "Notify within 3 days of due recurring bills",
                            checked = billRemindersEnabled,
                            onCheckedChange = viewModel::setBillRemindersEnabled
                        )
                        HorizontalDivider(color = scheme.outlineVariant)
                        ToggleRow(
                            title = "Morning AI Tips (7:00 AM)",
                            subtitle = "Optional daily Gemini insight notification",
                            checked = dailyAiAdviceEnabled,
                            onCheckedChange = viewModel::setDailyAiAdviceEnabled
                        )
                    }
                }
            }

            // ── Data, Backup & SMS Diagnostics ────────────────────────────────
            item { SectionHeader("Backup & SMS Diagnostics") }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = scheme.surface),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                viewModel.clearRescanProgress()
                                viewModel.runNonDestructiveRescan()
                            },
                            enabled = !isRescanning,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isRescanning) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = scheme.onPrimary)
                                Spacer(Modifier.width(8.dp))
                                Text("Scanning…")
                            } else {
                                Icon(Icons.Filled.Refresh, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Re-Scan SMS Inbox (Non-Destructive)")
                            }
                        }

                        // Live progress panel — visible during scan and after completion
                        val progress = rescanProgress
                        if (progress !is RescanProgress.Idle) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when (progress) {
                                    is RescanProgress.Done  -> scheme.secondaryContainer
                                    is RescanProgress.Error -> scheme.errorContainer
                                    else                    -> scheme.surfaceVariant
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    when (progress) {
                                        is RescanProgress.Phase -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    strokeWidth = 2.dp,
                                                    color = scheme.primary
                                                )
                                                Text(
                                                    progress.message,
                                                    fontSize = 12.sp,
                                                    color = scheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        is RescanProgress.Done -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    Icons.Filled.CheckCircle,
                                                    null,
                                                    tint = scheme.onSecondaryContainer,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                    "Done — ${progress.total} new entries imported",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 13.sp,
                                                    color = scheme.onSecondaryContainer
                                                )
                                            }
                                            Text(
                                                "M-Pesa: ${progress.mpesa}  ·  Bank (I&M / Absa / Equity): ${progress.bank}",
                                                fontSize = 11.sp,
                                                color = scheme.onSecondaryContainer.copy(alpha = 0.8f)
                                            )
                                            Text(
                                                "Existing transactions, notes & budgets untouched.",
                                                fontSize = 11.sp,
                                                color = scheme.onSecondaryContainer.copy(alpha = 0.65f)
                                            )
                                        }
                                        is RescanProgress.Error -> {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    Icons.Filled.ErrorOutline,
                                                    null,
                                                    tint = scheme.onErrorContainer,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Text(
                                                    progress.message,
                                                    fontSize = 12.sp,
                                                    color = scheme.onErrorContainer
                                                )
                                            }
                                        }
                                        else -> Unit
                                    }
                                }
                            }
                        }

                        if (rescanProgress is RescanProgress.Idle) {
                            Text(
                                "Re-reads M-Pesa & Equity / I&M / Absa SMS without deleting notes, budgets, or existing data.",
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.onSurfaceVariant
                            )
                        }

                        HorizontalDivider(color = scheme.outlineVariant)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(onClick = { viewModel.createBackup() }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Lock, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Encrypted Backup")
                            }
                            OutlinedButton(onClick = { showUnparsedSheet = true }, modifier = Modifier.weight(1f)) {
                                Text("Unparsed SMS (${unparsedSms.size})")
                            }
                        }

                        if (backups.isNotEmpty()) {
                            Text("Saved Encrypted Backups", style = MaterialTheme.typography.labelMedium, color = scheme.onSurfaceVariant)
                            backups.take(3).forEach { file ->
                                val dateStr = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(file.lastModified())
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(file.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text(dateStr, fontSize = 11.sp, color = scheme.onSurfaceVariant)
                                    }
                                    TextButton(onClick = { viewModel.restoreBackup(file) }) {
                                        Text("Restore")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSelfIdDialog) {
        var input by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSelfIdDialog = false },
            title = { Text("Add Self Identifier") },
            text = {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Phone, Name, or Account (e.g. 0741947264)") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (input.isNotBlank()) viewModel.addSelfIdentifier(input)
                    showSelfIdDialog = false
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showSelfIdDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showCategoriesSheet) {
        ManageCategoriesBottomSheet(
            categories = categories,
            onDismiss = { showCategoriesSheet = false },
            onAdd = viewModel::addCategory,
            onDelete = viewModel::deleteCategory
        )
    }

    if (showRulesSheet) {
        ModalBottomSheet(onDismissRequest = { showRulesSheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Saved Rules", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(transferRules, key = { "tr_${it.merchantKey}" }) { rule ->
                        ListItem(
                            headlineContent = { Text(rule.merchantKey) },
                            supportingContent = { Text("Always mark as ${rule.nature}") },
                            trailingContent = {
                                IconButton(onClick = { viewModel.deleteTransferRule(rule.merchantKey) }) {
                                    Icon(Icons.Filled.DeleteOutline, "Delete Rule")
                                }
                            }
                        )
                    }
                    items(customRules, key = { "cr_${it.merchantName}" }) { rule ->
                        val catName = categories.find { it.id == rule.categoryId }?.name ?: "Category #${rule.categoryId}"
                        ListItem(
                            headlineContent = { Text(rule.merchantName) },
                            supportingContent = { Text("Auto-category: $catName") },
                            trailingContent = {
                                IconButton(onClick = { viewModel.deleteCustomRule(rule.merchantName) }) {
                                    Icon(Icons.Filled.DeleteOutline, "Delete Rule")
                                }
                            }
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showUnparsedSheet) {
        ModalBottomSheet(onDismissRequest = { showUnparsedSheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Unparsed Financial SMS (${unparsedSms.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (unparsedSms.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearUnparsedSms() }) { Text("Clear Log") }
                    }
                }
                if (unparsedSms.isEmpty()) {
                    Text("All detected M-Pesa and bank SMS were recognized.", color = scheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 20.dp))
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(unparsedSms, key = { it.id }) { item ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = scheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(item.sender, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = scheme.primary)
                                    Spacer(Modifier.height(4.dp))
                                    Text(item.body, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageCategoriesBottomSheet(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onAdd: (name: String, iconName: String, colorHex: String) -> Unit,
    onDelete: (Int) -> Unit
) {
    var newName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("shopping_cart") }
    val scheme = MaterialTheme.colorScheme

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Manage Categories", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("New Category") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            onAdd(newName.trim(), selectedIcon, "#00C853")
                            newName = ""
                        }
                    }
                ) { Text("Add") }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categoryIconOptions) { opt ->
                    FilterChip(
                        selected = selectedIcon == opt.key,
                        onClick = { selectedIcon = opt.key },
                        leadingIcon = { Icon(opt.icon, null, modifier = Modifier.size(16.dp)) },
                        label = { Text(opt.label) }
                    )
                }
            }
            HorizontalDivider()
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories, key = { it.id }) { cat ->
                    val col = parseCategoryColor(cat.colorCode, scheme.primary)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(34.dp).clip(CircleShape).background(col.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(categoryIconFor(cat.iconName), null, tint = col, modifier = Modifier.size(18.dp))
                            }
                            Text(cat.name, fontWeight = FontWeight.Medium)
                        }
                        if (cat.name != "Uncategorized") {
                            IconButton(onClick = { onDelete(cat.id) }) {
                                Icon(Icons.Filled.DeleteOutline, "Delete", tint = scheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
