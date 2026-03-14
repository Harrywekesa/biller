package com.mpesa.tracker.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mpesa.tracker.R
import com.mpesa.tracker.ui.theme.PrimaryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToSubscriptions: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("M-Pesa Tracker", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = PrimaryGreen
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App Logo Placeholder (assuming ic_launcher is available)
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_round),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            Text(
                text = "Welcome Back!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Select an module below to manage your finances.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Navigation Grid
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NavigationCard(
                        title = "Dashboard",
                        subtitle = "Overview & Balance",
                        icon = Icons.Filled.Dashboard,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDashboard
                    )
                    NavigationCard(
                        title = "Transactions",
                        subtitle = "Raw SMS History",
                        icon = Icons.Filled.ListAlt,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToTransactions
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NavigationCard(
                        title = "Subscriptions",
                        subtitle = "Recurring Bills",
                        icon = Icons.Filled.EventRepeat,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSubscriptions
                    )
                    NavigationCard(
                        title = "Analytics",
                        subtitle = "Spending Charts",
                        icon = Icons.Filled.Analytics,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToAnalytics
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat modern look
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
