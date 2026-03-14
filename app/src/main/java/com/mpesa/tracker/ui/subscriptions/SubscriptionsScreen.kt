package com.mpesa.tracker.ui.subscriptions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mpesa.tracker.data.local.entities.SubscriptionEntity
import com.mpesa.tracker.ui.theme.PrimaryGreen
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SubscriptionsViewModel = hiltViewModel()
) {
    val subscriptions by viewModel.activeSubscriptions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscriptions & Bills", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (subscriptions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.EventRepeat, 
                            contentDescription = "Empty",
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No active subscriptions found.", color = Color.Gray)
                        Text("Apps will be automatically detected.", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subscriptions) { sub ->
                        SubscriptionCard(sub)
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionCard(subscription: SubscriptionEntity) {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply { minimumFractionDigits = 2 }
    val amountStr = formatter.format(subscription.amount)
    
    val dateStr = if (subscription.expectedNextPaymentDate != null) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        "Next payment: " + sdf.format(subscription.expectedNextPaymentDate)
    } else {
        "Every ${subscription.billingCycleDays} days"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = subscription.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = dateStr, color = Color.Gray, fontSize = 14.sp)
            }
            Text(
                text = "Ksh $amountStr", 
                fontWeight = FontWeight.Bold, 
                fontSize = 16.sp,
                color = PrimaryGreen
            )
        }
    }
}
