package com.mpesa.tracker.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mpesa.tracker.R
import com.mpesa.tracker.ui.theme.PrimaryGreen

@Composable
fun PermissionRationaleScreen(
    onGrantPermissionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // We'll use a placeholder icon for now till we add genuine assets
        Icon(
            painter = painterResource(id = android.R.drawable.sym_action_email), // Placeholder
            contentDescription = "SMS Parsing",
            modifier = Modifier.size(100.dp),
            tint = PrimaryGreen
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Track Your Spending Automatically",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "To automatically categorize your expenses and calculate your balance, this app needs permission to read your M-Pesa SMS messages.\n\nYour data never leaves your device and is only used to build your financial dashboard.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onGrantPermissionClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
        ) {
            Text("Grant SMS Permission", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
