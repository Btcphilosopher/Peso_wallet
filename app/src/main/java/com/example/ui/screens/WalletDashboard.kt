package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssetEntity
import com.example.data.model.TransactionEntity
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.T
import com.example.ui.viewmodel.PesoWalletViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletDashboard(viewModel: PesoWalletViewModel, modifier: Modifier = Modifier) {
    val lang by viewModel.currentLanguage.collectAsState()
    val currency by viewModel.currencyDisplay.collectAsState()
    val network by viewModel.defaultNetwork.collectAsState()
    val assets by viewModel.assets.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var showNetworkSelector by remember { mutableStateOf(false) }
    var selectedAssetForDetail by remember { mutableStateOf<AssetEntity?>(null) }
    var hideBalances by remember { mutableStateOf(false) }

    // Math calculation for total portfolio value in USD, and then in ARS
    val usdtoArsRate = 1250.0 // Pegged average Blue / financial rate
    val totalUsdValue = assets.sumOf {
        val assetValue = it.balance * it.priceUsd
        assetValue
    }
    val totalArsValue = totalUsdValue * usdtoArsRate

    // Dynamic Daily drift (average of assets change)
    val averageChange = assets.map { it.priceChange24h }.average().let { if (it.isNaN()) 0.0 else it }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // --- HEADER ROW ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User Circle Avatar + Hi text
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(colors = listOf(Color(0xFF2196F3), Color(0xFF00E5FF)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AR",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Hola, Tom!",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        // Network badge trigger dropdown
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E293B))
                                .clickable { showNetworkSelector = true }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00FFCC))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = network,
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Rapid Spanish/English toggle pill + notification icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Fast Language switcher
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF162035))
                            .clickable {
                                viewModel.changeLanguage(if (lang == AppLanguage.ES) AppLanguage.EN else AppLanguage.ES)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (lang == AppLanguage.ES) "EN" else "ES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF007AFA)
                        )
                    }

                    // Eye Icon
                    IconButton(onClick = { hideBalances = !hideBalances }) {
                        Icon(
                            imageVector = if (hideBalances) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Hide",
                            tint = Color.White
                        )
                    }
                }
            }

            // Network Selector Menu Dropdown
            if (showNetworkSelector) {
                AlertDialog(
                    onDismissRequest = { showNetworkSelector = false },
                    title = { Text(text = T.translate(lang, "network_selection")) },
                    text = {
                        Column {
                            val nets = listOf("Stellar", "TRON", "Polygon", "Ethereum Mainnet", "Local Bank System")
                            nets.forEach { n ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.selectNetwork(n)
                                            showNetworkSelector = false
                                        }
                                        .padding(vertical = 12.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(selected = network == n, onClick = {
                                        viewModel.selectNetwork(n)
                                        showNetworkSelector = false
                                    })
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = n)
                                }
                            }
                        }
                    },
                    confirmButton = {}
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- PORTFOLIO BALANCE CARD ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F1E36),
                                Color(0xFF070F1E)
                            )
                        )
                    )
                    .clickable { viewModel.toggleCurrencyDisplay() }
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = T.translate(lang, "total_balance"),
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val displayAmount = if (hideBalances) "••••••" else {
                        if (currency == "ARS") {
                            "$ " + String.format(Locale.GERMANY, "%,.2f", totalArsValue) + " ARS"
                        } else {
                            "$ " + String.format(Locale.US, "%,.2f", totalUsdValue) + " USD"
                        }
                    }

                    Text(
                        text = displayAmount,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (averageChange >= 0) Color(0xFF1A3828) else Color(0xFF401C1C)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = (if (averageChange >= 0) "+" else "") + String.format("%.2f", averageChange) + "%",
                                color = if (averageChange >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = T.translate(lang, "today_change"),
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = if (currency == "ARS") "Cotiz. dólar: $1.250" else "Display ARS/USD ⇄",
                            color = Color(0xFF007AFA),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- QUICK ACTION HUB ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickActionButton(
                    title = T.translate(lang, "send"),
                    icon = Icons.Outlined.ArrowUpward,
                    backgroundColor = Color(0xFF007AFA),
                    testTag = "action_send"
                ) {
                    viewModel.navigateTo("send")
                }

                QuickActionButton(
                    title = T.translate(lang, "receive"),
                    icon = Icons.Outlined.ArrowDownward,
                    backgroundColor = Color(0xFF1E293B),
                    testTag = "action_receive"
                ) {
                    viewModel.navigateTo("receive")
                }

                QuickActionButton(
                    title = "Scan",
                    icon = Icons.Outlined.QrCodeScanner,
                    backgroundColor = Color(0xFF1E293B),
                    testTag = "action_qr"
                ) {
                    viewModel.navigateTo("qr")
                }

                QuickActionButton(
                    title = T.translate(lang, "vault"),
                    icon = Icons.Outlined.Savings,
                    backgroundColor = Color(0xFF1E293B),
                    testTag = "action_savings"
                ) {
                    viewModel.navigateTo("savings")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- MAIN DASHBOARD CONTENT (ASSETS / RECENT TRANSACTIONS) ---
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // APY Callout Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF0D253F), Color(0xFF1E3A5F))
                                )
                            )
                            .clickable { viewModel.navigateTo("savings") }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF00FFCC),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Interés Digital: Hasta 9.2% APY",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Ahorrá en dólares UXD/USDT y ganá rendimientos segundo a segundo.",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Sector: Assets Header
                item {
                    Text(
                        text = T.translate(lang, "assets_title"),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                // Assets Lists
                items(assets) { asset ->
                    AssetCard(
                        asset = asset,
                        currency = currency,
                        hideBalances = hideBalances,
                        usdtoArsRate = usdtoArsRate
                    ) {
                        selectedAssetForDetail = asset
                    }
                }

                // Sector: Recent Transactions Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = T.translate(lang, "recent_tx"),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = T.translate(lang, "view_all"),
                            color = Color(0xFF007AFA),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.navigateTo("db_audit") } // Let's navigate to audit/tx logs view!
                        )
                    }
                }

                // Transaction list
                val recentTxs = transactions.take(3)
                if (recentTxs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay transacciones registradas.",
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }
                } else {
                    items(recentTxs) { tx ->
                        TransactionRow(tx = tx)
                    }
                }
            }
        }
    }

    // Asset Detail Modal Dialog
    selectedAssetForDetail?.let { asset ->
        AlertDialog(
            onDismissRequest = { selectedAssetForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF007AFA).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = asset.symbol.take(2), fontWeight = FontWeight.Bold, color = Color(0xFF007AFA), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = asset.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "En Red: ${asset.networkName}", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Saldo total", color = Color.Gray)
                        Text(
                            text = "${asset.balance} ${asset.symbol}",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Valor en Pesos (ARS)", color = Color.Gray)
                        Text(
                            text = "$ " + String.format(Locale.GERMANY, "%,.2f", asset.balance * asset.priceUsd * if (asset.symbol == "ARS") 1.0 else usdtoArsRate) + " ARS",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Precio USD", color = Color.Gray)
                        Text(text = "$${asset.priceUsd}", fontWeight = FontWeight.SemiBold)
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))
                    
                    Text(
                        text = "Esta stablecoin está respaldada y opera bajo principios sin custodia. Podés transferirla instantáneamente over network sin cargos adicionales mas allá del gas de la red.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedAssetForDetail = null
                            viewModel.navigateTo("send")
                        }
                    ) {
                        Text("Enviar ${asset.symbol}")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFA)),
                        onClick = {
                            selectedAssetForDetail = null
                            // Navigate to Swap view/screen
                            viewModel.navigateTo("price_monitoring")
                        }
                    ) {
                        Text("Swap")
                    }
                }
            }
        )
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(testTag)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun AssetCard(
    asset: AssetEntity,
    currency: String,
    hideBalances: Boolean,
    usdtoArsRate: Double,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (asset.symbol) {
                            "ARS" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                            "UXD" -> Color(0xFF2196F3).copy(alpha = 0.2f)
                            "USDT" -> Color(0xFF009688).copy(alpha = 0.2f)
                            "USDC" -> Color(0xFF3F51B5).copy(alpha = 0.2f)
                            else -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                val iconChar = when (asset.symbol) {
                    "ARS" -> "$"
                    "UXD" -> "U"
                    "USDT" -> "T"
                    "USDC" -> "C"
                    else -> "P"
                }
                Text(
                    text = iconChar,
                    fontWeight = FontWeight.Bold,
                    color = when (asset.symbol) {
                        "ARS" -> Color(0xFF4CAF50)
                        "UXD" -> Color(0xFF2196F3)
                        "USDT" -> Color(0xFF009688)
                        "USDC" -> Color(0xFF3F51B5)
                        else -> Color(0xFFFF9800)
                    },
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = asset.symbol,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = asset.name,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Holdings value & converted
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val valInUsd = asset.balance * asset.priceUsd
                val displayPrimary = if (hideBalances) "••••" else {
                    "${asset.balance} ${asset.symbol}"
                }
                Text(
                    text = displayPrimary,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )

                val displaySecondary = if (hideBalances) "••••" else {
                    if (currency == "ARS") {
                        val arsVal = valInUsd * (if (asset.symbol == "ARS") 1.0 else usdtoArsRate)
                        "$ " + String.format(Locale.GERMANY, "%,.0f", arsVal) + " ARS"
                    } else {
                        "$ " + String.format(Locale.US, "%,.2f", valInUsd) + " USD"
                    }
                }
                Text(
                    text = displaySecondary,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun TransactionRow(tx: TransactionEntity) {
    val df = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateStr = df.format(Date(tx.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131B2A))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon circular showing input/output
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    when (tx.type) {
                        "RECEIVED" -> Color(0xFF1A3828)
                        "SENT" -> Color(0xFF402222)
                        else -> Color(0xFF1E293B)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (tx.type) {
                    "RECEIVED" -> Icons.Default.Add
                    "SENT" -> Icons.Default.Remove
                    "CONVERTED" -> Icons.Default.SwapHoriz
                    else -> Icons.Default.Lock
                },
                contentDescription = tx.type,
                tint = when (tx.type) {
                    "RECEIVED" -> Color(0xFF4CAF50)
                    "SENT" -> Color(0xFFF44336)
                    else -> Color(0xFF2196F3)
                },
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = tx.counterpartyName,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$dateStr • ${tx.note}",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Transaction Amount
        Column(
            horizontalAlignment = Alignment.End
        ) {
            val symbolChar = when (tx.type) {
                "RECEIVED" -> "+"
                "SENT" -> "-"
                else -> ""
            }
            Text(
                text = "$symbolChar${tx.amount} ${tx.assetSymbol}",
                fontWeight = FontWeight.Bold,
                color = when (tx.type) {
                    "RECEIVED" -> Color(0xFF4CAF50)
                    "SENT" -> Color(0xFFF44336)
                    else -> Color.White
                },
                fontSize = 14.sp
            )
            Text(
                text = tx.status,
                color = if (tx.status == "COMPLETED") Color(0xFF00FFCC) else Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
