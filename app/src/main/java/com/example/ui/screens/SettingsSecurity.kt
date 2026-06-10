package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.T
import com.example.ui.viewmodel.PesoWalletViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: PesoWalletViewModel, modifier: Modifier = Modifier) {
    val lang by viewModel.currentLanguage.collectAsState()
    val isDark by viewModel.isThemeDark.collectAsState()
    val currencyDisplay by viewModel.currencyDisplay.collectAsState()
    val defaultNetwork by viewModel.defaultNetwork.collectAsState()

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
                    text = T.translate(lang, "settings"),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Prefernces Category Item List
                Text(text = "Preferencia General", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                // Language selection card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = Color(0xFF007AFA))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = T.translate(lang, "language"), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AppLanguage.entries.forEach { l ->
                                val isSel = lang == l
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.changeLanguage(l) },
                                    border = BorderStroke(1.dp, if (isSel) Color(0xFF007AFA) else Color(0xFF1E293B)),
                                    colors = CardDefaults.cardColors(containerColor = if (isSel) Color(0xFF0E2540) else Color(0xFF131B2A))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(vertical = 12.dp)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = l.label, color = if (isSel) Color(0xFF4FA9FB) else Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Balance display setting
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF007AFA))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = T.translate(lang, "currency_display"), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Button(
                            onClick = { viewModel.toggleCurrencyDisplay() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = currencyDisplay, color = Color(0xFF4FA9FB), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Theme Mode Setup
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.BrightnessMedium, contentDescription = null, tint = Color(0xFF007AFA))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = T.translate(lang, "theme"), fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Switch(checked = isDark, onCheckedChange = { /* Toggle */ })
                    }
                }

                // SECURITY CENTER HUB NAVIGATION BUTTON
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0E2140))
                        .clickable { viewModel.navigateTo("security_center") }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF00FFCC))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = T.translate(lang, "security_center"), fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "Respaldo de seed phrases, PIN y dispositivos.", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                }

                // Merchant Setup NAVIGATION BUTTON
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF131B2A))
                        .clickable { viewModel.navigateTo("merchant_setup") }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Store, contentDescription = null, tint = Color(0xFF4FA9FB))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = T.translate(lang, "merchant_mode") + " (Argentine POS)", fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "Habilitá cobros rápidos con liquidación instantánea.", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                        }
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                }

                // SANDBOX DEV ZONE
                Text(text = T.translate(lang, "dev_options"), color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1116))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.simulatePriceMarketTick() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B1E26)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = T.translate(lang, "dev_simulate"), color = Color.White)
                        }

                        Button(
                            onClick = { viewModel.hardDataReset() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A1E24)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dev_reset_db")
                        ) {
                            Text(text = T.translate(lang, "dev_seed"), color = Color.White)
                        }
                    }
                }

                // Live Help Desk footer explanation
                Text(
                    text = T.translate(lang, "help_desc"),
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SecurityCenterScreen(viewModel: PesoWalletViewModel, modifier: Modifier = Modifier) {
    val lang by viewModel.currentLanguage.collectAsState()
    val isBiometricsActive by viewModel.isBiometricsEnabled.collectAsState()
    var showSeedPhrase by remember { mutableStateOf(false) }

    val clipboard = LocalClipboardManager.current
    val mockSeed = "pampa mate pampa gaucho asado azul tango sol lago luna patio cardo"

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
                IconButton(onClick = { viewModel.navigateTo("settings") }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = T.translate(lang, "security_center"),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Biometrics setting toggles
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = Color(0xFF00FFCC))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = T.translate(lang, "biometrics_active"), fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = T.translate(lang, "finger_face"), color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                        Switch(checked = isBiometricsActive, onCheckedChange = { /* Toggle simulated variable */ })
                    }
                }

                // Device binding Info banner
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Hardware, contentDescription = null, tint = Color(0xFF007AFA))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = T.translate(lang, "connected_devices"), fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = T.translate(lang, "connected_devices_info"), color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                }

                // Wallet backup sheet trigger
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, tint = Color(0xFFFF9800))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = T.translate(lang, "backup_wallet"), fontWeight = FontWeight.Bold, color = Color.White)
                                Text(text = T.translate(lang, "backup_desc"), color = Color.Gray, fontSize = 11.sp)
                            }
                        }

                        if (!showSeedPhrase) {
                            Button(
                                onClick = { showSeedPhrase = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800).copy(alpha = 0.2f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("show_seed_btn")
                            ) {
                                Text(text = "Revelar Frase Mnemónica", color = Color(0xFFFF9800), fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Render seed words inside cute cards grid
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val words = mockSeed.split(" ")
                                words.chunked(3).forEach { chunk ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        chunk.forEach { w ->
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFF1E293B))
                                                    .padding(8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(text = w, color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        clipboard.setText(AnnotatedString(mockSeed))
                                        showSeedPhrase = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "Copiar y Cerrar Respaldo", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuditTerminalScreen(viewModel: PesoWalletViewModel, modifier: Modifier = Modifier) {
    val lang by viewModel.currentLanguage.collectAsState()
    val logs by viewModel.auditLogs.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1524))
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
                    text = T.translate(lang, "dev_logs"),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Auditable Timeline logs
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(logs) { log ->
                    val df = SimpleDateFormat("HH:mm:ss.S", Locale.getDefault())
                    val timeStr = df.format(Date(log.timestamp))

                    val color = when (log.severity) {
                        "WARNING" -> Color(0xFFFF9800)
                        "SECURITY" -> Color(0xFFF44336)
                        else -> Color(0xFF00FFCC)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "[$timeStr]",
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = log.eventName,
                                color = color,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = log.details,
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
