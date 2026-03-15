package com.mpesa.tracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.mpesa.tracker.framework.security.BiometricPromptHelper
import com.mpesa.tracker.framework.services.SmsSyncService
import com.mpesa.tracker.data.local.dao.CategoryDao
import com.mpesa.tracker.data.local.dao.SubscriptionDao
import com.mpesa.tracker.data.local.AppDatabaseCallback
import com.mpesa.tracker.ui.theme.MpesaTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mpesa.tracker.ui.theme.PrimaryGreen

sealed class Screen {
    object Landing : Screen()
    object Dashboard : Screen()
    object Analytics : Screen()
    data class Transactions(val categoryId: Int? = null, val incomeOnly: Boolean = false) : Screen()
    object Subscriptions : Screen()
}

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var smsSyncService: SmsSyncService

    @Inject
    lateinit var categoryDao: CategoryDao
    
    @Inject
    lateinit var subscriptionDao: SubscriptionDao

    private var hasPermissionState = mutableStateOf(false)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasPermissionState.value = isGranted
            if (isGranted) {
                syncSms()
            } else {
                Toast.makeText(this, "SMS Permission Denied. Cannot read past transactions.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MpesaTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    hasPermissionState.value = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                    
                    if (hasPermissionState.value) {
                        var isAuthenticated by remember { mutableStateOf(!BiometricPromptHelper.canAuthenticate(this@MainActivity)) }
                        var currentScreen by remember { mutableStateOf<Screen>(Screen.Landing) }

                        val triggerBiometric = {
                            BiometricPromptHelper.showBiometricPrompt(
                                activity = this@MainActivity,
                                onSuccess = { isAuthenticated = true },
                                onError = { msg ->
                                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        // Trigger auth on first load if we need it
                        LaunchedEffect(Unit) {
                            if (!isAuthenticated && BiometricPromptHelper.canAuthenticate(this@MainActivity)) {
                                triggerBiometric()
                            }
                            syncSms()
                        }

                        if (!isAuthenticated) {
                            // Locked Screen UI
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Locked",
                                    modifier = Modifier.size(80.dp),
                                    tint = PrimaryGreen
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("M-Pesa Tracker is Locked", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                                Spacer(modifier = Modifier.height(32.dp))
                                Button(
                                    onClick = { triggerBiometric() },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                                ) {
                                    Text("Unlock")
                                }
                            }
                        } else {
                            // Authenticated: Show the real App
                            when (currentScreen) {
                                Screen.Landing -> {
                                    com.mpesa.tracker.ui.onboarding.LandingScreen(
                                        onNavigateToDashboard = { currentScreen = Screen.Dashboard },
                                        onNavigateToTransactions = { currentScreen = Screen.Transactions() },
                                        onNavigateToSubscriptions = { currentScreen = Screen.Subscriptions },
                                        onNavigateToAnalytics = { currentScreen = Screen.Analytics }
                                    )
                                }
                                Screen.Dashboard -> {
                                    com.mpesa.tracker.ui.dashboard.DashboardScreen(
                                        onNavigateBack = { currentScreen = Screen.Landing },
                                        onNavigateToTransactions = { incomeOnly -> currentScreen = Screen.Transactions(incomeOnly = incomeOnly) }
                                    )
                                }
                                Screen.Analytics -> {
                                    com.mpesa.tracker.ui.analytics.AnalyticsScreen(
                                        onNavigateBack = { currentScreen = Screen.Landing },
                                        onNavigateToTransactions = { categoryId -> currentScreen = Screen.Transactions(categoryId) }
                                    )
                                }
                                is Screen.Transactions -> {
                                    val txScreen = currentScreen as Screen.Transactions
                                    com.mpesa.tracker.ui.transactions.TransactionsScreen(
                                        onNavigateBack = { currentScreen = Screen.Landing },
                                        initialCategoryId = txScreen.categoryId,
                                        initialIncomeOnly = txScreen.incomeOnly
                                    )
                                }
                                Screen.Subscriptions -> {
                                    com.mpesa.tracker.ui.subscriptions.SubscriptionsScreen(
                                        onNavigateBack = { currentScreen = Screen.Landing }
                                    )
                                }
                            }
                        }
                    } else {
                        com.mpesa.tracker.ui.onboarding.PermissionRationaleScreen(
                            onGrantPermissionClick = {
                                requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun syncSms() {
        lifecycleScope.launch {
            try {
                // Failsafe: Re-seed categories if DB was wiped by a schema update
                val existingCategories = categoryDao.getAllCategories().first()
                if (existingCategories.isEmpty()) {
                    Log.d("MainActivity", "Database Categories are empty! Seeding defaults...")
                    AppDatabaseCallback.starterCategories.forEach { categoryDao.insertCategory(it) }
                    AppDatabaseCallback.starterSubs.forEach { subscriptionDao.insertSubscription(it) }
                }

                val syncedCount = smsSyncService.syncHistoricMessages()
                Log.d("MainActivity", "Synced $syncedCount messages.")
                if (syncedCount > 0) {
                     Toast.makeText(this@MainActivity, "Synced $syncedCount transactions", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to sync SMS or seed DB", e)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
