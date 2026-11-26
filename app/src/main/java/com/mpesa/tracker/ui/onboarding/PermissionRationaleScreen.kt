package com.mpesa.tracker.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PermissionRationaleScreen(
    onGrantPermissionClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(scheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.MarkEmailRead,
                contentDescription = "SMS Tracking",
                modifier = Modifier.size(48.dp),
                tint = scheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Track Your Finances Automatically",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = scheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Biller reads your M-Pesa, Equity, I&M, and Absa transaction SMS locally on your device to calculate balances, track fees, exclude internal transfers, and monitor budgets.\n\nYour SMS data never leaves your phone.",
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onGrantPermissionClick,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Grant SMS Permission", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
