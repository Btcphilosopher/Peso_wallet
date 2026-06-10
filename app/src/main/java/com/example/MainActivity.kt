package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.T
import com.example.ui.viewmodel.PesoWalletViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: PesoWalletViewModel = viewModel()
                
                // Real-time toast notifications listener
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    viewModel.notification.collectLatest { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                PesoWalletApp(viewModel)
            }
        }
    }
}

@Composable
fun PesoWalletApp(viewModel: PesoWalletViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    // Determine if we should display bottom navigation scaffold
    val showScaffold = currentScreen in listOf(
        "dashboard", "savings", "price_monitoring", "settings", "merchant_dashboard", "merchant_setup", "security_center", "db_audit"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color(0xFF090D16)),
        bottomBar = {
            if (showScaffold) {
                NavigationBar(
                    modifier = Modifier.height(72.dp).testTag("bottom_selector"),
                    containerColor = Color(0xFF131B2A),
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = currentScreen == "dashboard",
                        onClick = { viewModel.navigateTo("dashboard") },
                        icon = { Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = "Home") },
                        label = { Text(text = "Inicio", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF007AFA),
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color(0xFF007AFA).copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == "savings",
                        onClick = { viewModel.navigateTo("savings") },
                        icon = { Icon(imageVector = Icons.Default.Savings, contentDescription = "Vault") },
                        label = { Text(text = T.translate(lang, "vault"), style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF007AFA),
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color(0xFF007AFA).copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == "price_monitoring",
                        onClick = { viewModel.navigateTo("price_monitoring") },
                        icon = { Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Precios") },
                        label = { Text(text = "Precios", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF007AFA),
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color(0xFF007AFA).copy(alpha = 0.15f)
                        )
                    )

                    NavigationBarItem(
                        selected = currentScreen == "settings" || currentScreen == "merchant_setup" || currentScreen == "merchant_dashboard" || currentScreen == "security_center" || currentScreen == "db_audit",
                        onClick = { viewModel.navigateTo("settings") },
                        icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text(text = "Ajustes", style = MaterialTheme.typography.labelSmall) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF007AFA),
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color(0xFF007AFA).copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // Master Animation screen transitions
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
            },
            modifier = Modifier.fillMaxSize().padding(bottom = if (showScaffold) innerPadding.calculateBottomPadding() else 0.dp)
        ) { screen ->
            when (screen) {
                "splash" -> SplashScreen(viewModel)
                "onboarding" -> OnboardingScreen(viewModel)
                "auth" -> AuthenticationScreen(viewModel)
                "dashboard" -> WalletDashboard(viewModel)
                "send" -> SendScreen(viewModel)
                "receive" -> ReceiveScreen(viewModel)
                "qr" -> QrPaymentsScreen(viewModel)
                "savings" -> SavingsVaultScreen(viewModel)
                "price_monitoring" -> PriceDashboardScreen(viewModel)
                "merchant_setup" -> MerchantFeaturesScreen(viewModel)
                "merchant_dashboard" -> MerchantFeaturesScreen(viewModel)
                "security_center" -> SecurityCenterScreen(viewModel)
                "db_audit" -> AuditTerminalScreen(viewModel)
                "settings" -> SettingsScreen(viewModel)
                else -> WalletDashboard(viewModel)
            }
        }
    }
}
