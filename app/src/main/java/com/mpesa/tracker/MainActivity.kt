package com.mpesa.tracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.mpesa.tracker.data.local.AppDatabaseCallback
import com.mpesa.tracker.data.local.dao.CategoryDao
import com.mpesa.tracker.data.local.dao.SubscriptionDao
import com.mpesa.tracker.data.repository.UserPreferencesRepository
import com.mpesa.tracker.framework.security.BiometricPromptHelper
import com.mpesa.tracker.framework.services.SmsSyncService
import com.mpesa.tracker.ui.navigation.AppNavHost
import com.mpesa.tracker.ui.onboarding.PermissionRationaleScreen
import com.mpesa.tracker.ui.theme.MpesaTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var smsSyncService: SmsSyncService
    @Inject lateinit var categoryDao: CategoryDao
    @Inject lateinit var subscriptionDao: SubscriptionDao
    @Inject lateinit var userPreferences: UserPreferencesRepository

    private var hasPermissionState = mutableStateOf(false)
    private var hasSyncedThisProcess = false

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val smsGranted = permissions[Manifest.permission.READ_SMS] == true
            hasPermissionState.value = smsGranted
            if (smsGranted) {
                syncSmsOnce()
            } else {
                Toast.makeText(this, "SMS Permission Denied. Cannot read past transactions.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeMode by userPreferences.themeMode.collectAsState()
            val dynamicColor by userPreferences.dynamicColorEnabled.collectAsState()
            val appLockEnabled by userPreferences.appLockEnabled.collectAsState()

            MpesaTrackerTheme(themeMode = themeMode, dynamicColor = dynamicColor) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    hasPermissionState.value = ContextCompat.checkSelfPermission(
                        this, Manifest.permission.READ_SMS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermissionState.value) {
                        val needsAuth = appLockEnabled && BiometricPromptHelper.canAuthenticate(this@MainActivity)
                        var isAuthenticated by remember { mutableStateOf(!needsAuth) }

                        val triggerBiometric = {
                            BiometricPromptHelper.showBiometricPrompt(
                                activity = this@MainActivity,
                                title = "Unlock Biller",
                                subtitle = "Verify your identity to access your finances",
                                onSuccess = {
                                    isAuthenticated = true
                                    syncSmsOnce()
                                },
                                onError = { msg ->
                                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        LaunchedEffect(Unit) {
                            if (!isAuthenticated && needsAuth) {
                                triggerBiometric()
                            } else {
                                syncSmsOnce()
                            }
                        }

                        if (!isAuthenticated) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(88.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "Locked",
                                        modifier = Modifier.size(40.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(Modifier.height(20.dp))
                                Text("Biller is Locked", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Authenticate to view M-Pesa & bank activity",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.height(28.dp))
                                Button(onClick = triggerBiometric) {
                                    Text("Unlock")
                                }
                            }
                        } else {
                            AppNavHost()
                        }
                    } else {
                        PermissionRationaleScreen(
                            onGrantPermissionClick = {
                                val permissions = mutableListOf(
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.RECEIVE_SMS
                                ).apply {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        add(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }.toTypedArray()
                                requestPermissionsLauncher.launch(permissions)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun syncSmsOnce() {
        if (hasSyncedThisProcess) return
        hasSyncedThisProcess = true
        lifecycleScope.launch {
            try {
                val existingCategories = categoryDao.getAllCategories().first()
                if (existingCategories.isEmpty()) {
                    AppDatabaseCallback.starterCategories.forEach { categoryDao.insertCategory(it) }
                    AppDatabaseCallback.starterSubs.forEach { subscriptionDao.insertSubscription(it) }
                }

                val syncedCount = smsSyncService.syncHistoricMessages()
                if (syncedCount > 0) {
                    Toast.makeText(this@MainActivity, "Synced $syncedCount new transactions", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to sync SMS or seed DB", e)
            }
        }
    }
}
