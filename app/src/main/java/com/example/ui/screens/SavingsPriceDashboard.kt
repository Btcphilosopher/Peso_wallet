package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PriceAlertEntity
import com.example.data.model.SavingsVaultEntity
import com.example.ui.theme.T
import com.example.ui.viewmodel.PesoWalletViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsVaultScreen(viewModel: PesoWalletViewModel, modifier: Modifier = Modifier) {
    val lang by viewModel.currentLanguage.collectAsState()
    val vaults by viewModel.savingsVaults.collectAsState()

    var showDepositDialog by remember { mutableStateOf<SavingsVaultEntity?>(null) }
    var showWithdrawDialog by remember { mutableStateOf<SavingsVaultEntity?>(null) }
    var amountText by remember { mutableStateOf("") }
    var actionError by remember { mutableStateOf<String?>(null) }

    val totalInterestAccrued = vaults.sumOf { it.totalInterestEarned }
    val totalVaultsBalance = vaults.sumOf { it.balance }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo("dashboard") }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = T.translate(lang, "savings_vault"),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Vault Balance Display with continuous live APY counter!
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A30)),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = T.translate(lang, "vault_balance") + " (US Dollars)", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$ " + String.format(Locale.US, "%,.6f", totalVaultsBalance) + " USD",
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = (-1).sp),
                        color = Color(0xFF00FFCC)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${T.translate(lang, "accruing_interest")}: $ " + String.format(Locale.US, "%,.6f", totalInterestAccrued) + " USD",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Vault explanation text
            Text(
                text = T.translate(lang, "savings_desc"),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Individual vault assets
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vaults) { vault ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF007AFA).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = vault.assetSymbol.take(2), color = Color(0xFF007AFA), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${vault.assetSymbol} Vault", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                                Text(text = "${T.translate(lang, "apy_stable")}: ${vault.interestRateApy}% APY", color = Color(0xFF00FFCC), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "$ " + String.format(Locale.US, "%,.4f", vault.balance) + " USD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    // Withdraw Button
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF1E293B))
                                            .clickable {
                                                amountText = ""
                                                actionError = null
                                                showWithdrawDialog = vault
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = T.translate(lang, "withdraw"), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }

                                    // Deposit Button
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF007AFA))
                                            .clickable {
                                                amountText = ""
                                                actionError = null
                                                showDepositDialog = vault
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = T.translate(lang, "deposit"), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Deposit dialog modal
    showDepositDialog?.let { vault ->
        AlertDialog(
            onDismissRequest = { showDepositDialog = null },
            title = { Text(text = "${T.translate(lang, "vault_deposit_title")} (${vault.assetSymbol})") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Se transferirán los fondos desde tu saldo disponible a la bóveda de interés.", fontSize = 13.sp)
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text(text = T.translate(lang, "amount")) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF007AFA),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    actionError?.let {
                        Text(text = it, color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull()
                        if (amt == null || amt <= 0) {
                            actionError = "Monto inválido."
                        } else {
                            viewModel.depositVault(vault.assetSymbol, amt) { result ->
                                if (result.isSuccess) {
                                    showDepositDialog = null
                                } else {
                                    actionError = result.exceptionOrNull()?.message ?: "Saldo insuficiente en el wallet."
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFA))
                ) {
                    Text(text = T.translate(lang, "confirm"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = null }) {
                    Text(text = T.translate(lang, "cancel"))
                }
            }
        )
    }

    // Withdraw dialog modal
    showWithdrawDialog?.let { vault ->
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = null },
            title = { Text(text = "${T.translate(lang, "vault_withdraw_title")} (${vault.assetSymbol})") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Se retornarán los fondos generados a tu saldo principal disponible.", fontSize = 13.sp)
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text(text = T.translate(lang, "amount")) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF007AFA),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                    actionError?.let {
                        Text(text = it, color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull()
                        if (amt == null || amt <= 0) {
                            actionError = "Monto inválido."
                        } else {
                            viewModel.withdrawVault(vault.assetSymbol, amt) { result ->
                                if (result.isSuccess) {
                                    showWithdrawDialog = null
                                } else {
                                    actionError = result.exceptionOrNull()?.message ?: "Saldo de bóveda insuficiente."
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFA))
                ) {
                    Text(text = T.translate(lang, "confirm"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = null }) {
                    Text(text = T.translate(lang, "cancel"))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceDashboardScreen(viewModel: PesoWalletViewModel, modifier: Modifier = Modifier) {
    val lang by viewModel.currentLanguage.collectAsState()
    val priceAlerts by viewModel.priceAlerts.collectAsState()

    var selectedCoinForAlert by remember { mutableStateOf("UXD") }
    var targetPriceInput by remember { mutableStateOf("") }
    var alertAbove by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.navigateTo("dashboard") }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = T.translate(lang, "price_dashboard"),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Premium Historical Canvas Chart
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Ruta histórica: UXD Stablecoin (7D)", fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 12.sp)
                    Text(text = "$1.0005 USD", fontWeight = FontWeight.Bold, color = Color(0xFF00FFCC), fontSize = 18.sp)
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    // Live drawing path of prices
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        val points = listOf(
                            Offset(0f, size.height * 0.5f),
                            Offset(size.width * 0.15f, size.height * 0.6f),
                            Offset(size.width * 0.3f, size.height * 0.45f),
                            Offset(size.width * 0.45f, size.height * 0.52f),
                            Offset(size.width * 0.6f, size.height * 0.3f),
                            Offset(size.width * 0.75f, size.height * 0.65f),
                            Offset(size.width * 0.9f, size.height * 0.48f),
                            Offset(size.width, size.height * 0.4f)
                        )

                        val path = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }

                        // Gradient stroke line
                        drawPath(
                            path = path,
                            brush = Brush.linearGradient(colors = listOf(Color(0xFF2196F3), Color(0xFF00E5FF))),
                            style = Stroke(width = 4f)
                        )

                        // Highlight dots
                        points.forEach { pt ->
                            drawCircle(
                                color = Color(0xFF00FFCC),
                                radius = 6f,
                                center = pt
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Create Alert Control Column
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = T.translate(lang, "create_alert"), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)

                    // Choose asset simple button toggle
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("UXD", "USDT", "ARS").forEach { sym ->
                            val isSel = selectedCoinForAlert == sym
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF007AFA) else Color(0xFF1E293B))
                                    .clickable { selectedCoinForAlert = sym }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(text = sym, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    // Alert above/below toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = T.translate(lang, "notify_when"), color = Color.Gray, fontSize = 13.sp)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (alertAbove) Color(0xFF007AFA) else Color.Transparent)
                                    .clickable { alertAbove = true }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = T.translate(lang, "goes_above"), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (!alertAbove) Color(0xFF007AFA) else Color.Transparent)
                                    .clickable { alertAbove = false }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = T.translate(lang, "goes_below"), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Price input
                    OutlinedTextField(
                        value = targetPriceInput,
                        onValueChange = { targetPriceInput = it },
                        label = { Text(text = "Precio Objetivo (USD)") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF007AFA),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("alert_price_input")
                    )

                    Button(
                        onClick = {
                            val price = targetPriceInput.toDoubleOrNull()
                            if (price != null && price > 0) {
                                viewModel.createPriceSignalAlert(selectedCoinForAlert, price, alertAbove)
                                targetPriceInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFA)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(text = T.translate(lang, "create_alert"), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active list of Alerts
            Text(text = T.translate(lang, "active_alerts"), fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (priceAlerts.isEmpty()) {
                    item {
                        Text(text = "No tenés alertas activas.", color = Color.Gray, modifier = Modifier.padding(8.dp))
                    }
                } else {
                    items(priceAlerts) { alert ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF131B2A))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (alert.isAbove) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = if (alert.isAbove) Color(0xFF4CAF50) else Color(0xFFF44336),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${alert.assetSymbol} " + (if (alert.isAbove) "≥" else "≤") + " $${alert.targetPriceUsd}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(checked = alert.isActive, onCheckedChange = { /* Toggle */ })
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { viewModel.removePriceAlert(alert) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
