package com.mpesa.tracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.ui.dashboard.TransactionMockItem
import com.mpesa.tracker.ui.theme.PrimaryGreen
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onNavigateBack: () -> Unit,
    initialCategoryId: Int? = null,
    initialIncomeOnly: Boolean = false,
    viewModel: TransactionsViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filterIncomeOnly by viewModel.filterIncomeOnly.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val selectedIds by viewModel.selectedTransactionIds.collectAsState()
    
    var selectedTransaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var showBulkCategorySheet by remember { mutableStateOf(false) }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bulkSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var showCreateCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    LaunchedEffect(initialCategoryId, initialIncomeOnly) {
        if (initialIncomeOnly) {
            // First time load: if they came from "Money In", instantly toggle the Income filter on
            if (!viewModel.filterIncomeOnly.value) {
                viewModel.toggleIncomeFilter()
            }
        } else if (initialCategoryId == -1) {
            viewModel.toggleUncategorizedFilter()
        } else if (initialCategoryId != null) {
            viewModel.setCategoryFilter(initialCategoryId)
        }
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
                            Icon(Icons.Filled.Clear, contentDescription = "Close Selection")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryGreen.copy(alpha = 0.1f),
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        IconButton(
                            onClick = { 
                                if (selectedIds.isNotEmpty()) {
                                    showBulkCategorySheet = true 
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FilterList, 
                                contentDescription = "Categorize Selected",
                                tint = if (selectedIds.isNotEmpty()) PrimaryGreen else Color.Gray
                            )
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { Text("All Transactions", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    actions = {
                        IconButton(onClick = { isSelectionMode = true }) {
                            Icon(
                                imageVector = Icons.Filled.Check, 
                                contentDescription = "Select Multiple"
                            )
                        }
                        IconButton(onClick = { viewModel.clearAndResyncSms() }, enabled = !isSyncing) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = PrimaryGreen)
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Sync, 
                                    contentDescription = "Clear & Resync SMS",
                                    tint = PrimaryGreen
                                )
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                placeholder = { Text("Search merchant or receipt...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen
                )
            )
            
            val uncategorizedOnlySelected by viewModel.filterUncategorizedOnly.collectAsState()
            val fulizaOnlySelected by viewModel.filterFulizaOnly.collectAsState()
            val activeCategoryFilterId by viewModel.filterByCategoryId.collectAsState()

            // Filter Chips
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = filterIncomeOnly,
                        onClick = { viewModel.toggleIncomeFilter() },
                        label = { Text("Income Only") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryGreen
                        )
                    )
                }
                
                item {
                    FilterChip(
                        selected = uncategorizedOnlySelected,
                        onClick = { viewModel.toggleUncategorizedFilter() },
                        label = { Text("Uncategorized") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryGreen
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = fulizaOnlySelected,
                        onClick = { viewModel.toggleFulizaFilter() },
                        label = { Text("Fuliza") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryGreen
                        )
                    )
                }

                if (activeCategoryFilterId != null) {
                    item {
                        val catName = categories.find { it.id == activeCategoryFilterId }?.name ?: "Category"
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.setCategoryFilter(null) }, // Tap to clear
                            label = { Text("X  $catName") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                                selectedLabelColor = PrimaryGreen
                            )
                        )
                    }
                }
            }
            


            Spacer(modifier = Modifier.height(8.dp))

            // Transactions List
            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No transactions found", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(transactions) { transaction ->
                        val formatter = NumberFormat.getNumberInstance(Locale.US).apply { 
                            minimumFractionDigits = 2; maximumFractionDigits = 2 
                        }
                        val amountStr = formatter.format(transaction.amount)
                        val sdf = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
                        val dateStr = sdf.format(transaction.dateTimestamp)

                        val categoryName = categories.find { it.id == transaction.categoryId }?.name ?: "Uncategorized"

                        TransactionMockItem(
                            name = transaction.recipientName,
                            amount = (if (transaction.isIncome) "+ Ksh " else "- Ksh ") + amountStr,
                            category = categoryName,
                            date = dateStr,
                            isIncome = transaction.isIncome,
                            fulizaAmount = transaction.fulizaAmount,
                            isSelected = selectedIds.contains(transaction.receiptNumber),
                            onClick = { 
                                if (isSelectionMode) {
                                    viewModel.toggleSelection(transaction.receiptNumber)
                                } else {
                                    selectedTransaction = transaction 
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showBulkCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showBulkCategorySheet = false },
            sheetState = bulkSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Categorize ${selectedIds.size} Transactions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "This will assign a custom rule for all selected merchants.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedButton(
                    onClick = { showCreateCategoryDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen)
                ) {
                    Text("+ Create New Category")
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        Surface(
                            onClick = {
                                viewModel.categorizeSelected(category.id)
                                showBulkCategorySheet = false
                                isSelectionMode = false
                            },
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (selectedTransaction != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedTransaction = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Category for ${selectedTransaction?.recipientName}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "This will assign a custom rule for all future receipts from this merchant.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                OutlinedButton(
                    onClick = { showCreateCategoryDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen)
                ) {
                    Text("+ Create New Category")
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        Surface(
                            onClick = {
                                selectedTransaction?.let { tx ->
                                    viewModel.updateTransactionCategory(
                                        tx.receiptNumber,
                                        category.id,
                                        tx.recipientName
                                    )
                                }
                                selectedTransaction = null
                            },
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    if (showCreateCategoryDialog) {
        AlertDialog(
            onDismissRequest = { 
                showCreateCategoryDialog = false
                newCategoryName = "" 
            },
            title = { Text("New Category") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Category Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        focusedLabelColor = PrimaryGreen
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            viewModel.addCategory(name = newCategoryName.trim())
                            showCreateCategoryDialog = false
                            newCategoryName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showCreateCategoryDialog = false
                    newCategoryName = ""
                }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}
