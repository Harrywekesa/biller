package com.mpesa.tracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mpesa.tracker.data.local.entities.TransactionEntity
import com.mpesa.tracker.ui.theme.PrimaryGreen
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTransactions: (incomeOnly: Boolean) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    var isBalanceVisible by remember { mutableStateOf(false) }
    val totalBalance by viewModel.totalBalance.collectAsState()
    val monthSpend by viewModel.monthSpend.collectAsState()
    val monthIncome by viewModel.monthIncome.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Financial Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            
            BalanceCard(
            balance = if (isBalanceVisible) "Ksh $totalBalance" else "Ksh ****",
            monthSpend = if (isBalanceVisible) "Ksh $monthSpend" else "Ksh ****",
            monthIncome = if (isBalanceVisible) "Ksh $monthIncome" else "Ksh ****",
            isBalanceVisible = isBalanceVisible,
            onToggleVisibility = { isBalanceVisible = !isBalanceVisible },
            onNavigateToTransactions = onNavigateToTransactions
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = { onNavigateToTransactions(false) }) {
                Text("See All", color = PrimaryGreen, fontWeight = FontWeight.Bold)
            }
        }
        
        // Placeholder for the transactions list
        LazyColumn {
            items(recentTransactions) { transaction ->
                val formatter = NumberFormat.getNumberInstance(Locale.US).apply { 
                    minimumFractionDigits = 2; maximumFractionDigits = 2 
                }
                val amountStr = formatter.format(transaction.amount) // Formatted Amount
                
                // Extremely simple date formatting
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val dateStr = sdf.format(transaction.dateTimestamp)

                TransactionMockItem(
                    name = transaction.recipientName, 
                    amount = (if (transaction.isIncome) "+ Ksh " else "- Ksh ") + amountStr, 
                    category = transaction.type.name, 
                    date = dateStr,
                    isIncome = transaction.isIncome,
                    fulizaAmount = transaction.fulizaAmount,
                    onClick = { /* Navigate to details later */ }
                )
            }
        }
    }
}
}
@Composable
fun BalanceCard(
    balance: String, 
    monthSpend: String, 
    monthIncome: String,
    isBalanceVisible: Boolean, 
    onToggleVisibility: () -> Unit,
    onNavigateToTransactions: (incomeOnly: Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(), // Removed hardcoded 180.dp height
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Removed fillMaxSize
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PrimaryGreen, Color(0xFF00695C))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total M-Pesa Balance",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            imageVector = if (isBalanceVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Balance Visibility",
                            tint = Color.White
                        )
                    }
                }
                
                Text(
                    text = balance,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Money In (Income)
                    Surface(
                        color = Color.Transparent,
                        onClick = { onNavigateToTransactions(true) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column {
                            Text(text = "Money In", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text(text = monthIncome, color = Color(0xFF4CAF50), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    
                    // Money Out (Spend)
                    Surface(
                        color = Color.Transparent,
                        onClick = { onNavigateToTransactions(false) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Money Out", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text(text = monthSpend, color = Color(0xFFE53935), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionMockItem(
    name: String, 
    amount: String, 
    category: String, 
    date: String,
    isIncome: Boolean = false,
    fulizaAmount: Double? = null,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = category, color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount, 
                    fontWeight = FontWeight.Bold, 
                    color = if (isIncome) Color(0xFF4CAF50) else Color(0xFFE53935)
                )
                if (fulizaAmount != null) {
                    val fmt = NumberFormat.getNumberInstance(Locale.US).apply { minimumFractionDigits = 2; maximumFractionDigits = 2 }
                    Text(
                        text = "(Ksh ${fmt.format(fulizaAmount)} via Fuliza)",
                        color = Color(0xFFE53935),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(text = date, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
