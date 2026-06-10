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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MerchantProfileEntity
import com.example.ui.theme.T
import com.example.ui.viewmodel.PesoWalletViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantFeaturesScreen(viewModel: PesoWalletViewModel, modifier: Modifier = Modifier) {
    val lang by viewModel.currentLanguage.collectAsState()
    val merchantProfile by viewModel.merchantProfile.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var showBillingDialog by remember { mutableStateOf(false) }
    var billingAmount by remember { mutableStateOf("") }
    var billingNote by remember { mutableStateOf("") }
    var billingAsset by remember { mutableStateOf("UXD") }

    // Forms
    var businessName by remember { mutableStateOf("") }
    var cuitText by remember { mutableStateOf("") }
    var categoryText by remember { mutableStateOf("Kiosco/Almacén") }
    var aliasText by remember { mutableStateOf("") }
    var cvuText by remember { mutableStateOf("") }
    var settlementAsset by remember { mutableStateOf("UXD") }

    val isMerchantModeActive = merchantProfile?.isMerchantModeActive == true

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
                    text = if (isMerchantModeActive) T.translate(lang, "merchant_dashboard") else T.translate(lang, "setup_business"),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isMerchantModeActive) {
                // RENDER MERCHANT SETUP REGISTRY FORM
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Vendele a clientes Peso Wallet y liquidá tus ingresos en stablecoin de manera automática libre de inflación.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                    )

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text(text = T.translate(lang, "business_name"), color = Color.White.copy(alpha = 0.6f)) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF007AFA),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("setup_name")
                    )

                    OutlinedTextField(
                        value = cuitText,
                        onValueChange = { cuitText = it },
                        label = { Text(text = T.translate(lang, "cuit") + " (CUIT)", color = Color.White.copy(alpha = 0.6f)) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF007AFA),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cvuText,
                        onValueChange = { cvuText = it },
                        label = { Text(text = "CBU / CVU de liquidación", color = Color.White.copy(alpha = 0.6f)) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF007AFA),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = aliasText,
                        onValueChange = { aliasText = it },
                        label = { Text(text = "Alias del negocio (e.g. cerveza.pago)", color = Color.White.copy(alpha = 0.6f)) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF007AFA),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // preferred settlement asset choice
                    Text(text = "Moneda de liquidación preferida:", color = Color.White)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("UXD", "USDT", "USDC").forEach { sym ->
                            val isSel = settlementAsset == sym
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF007AFA) else Color(0xFF1E293B))
                                    .clickable { settlementAsset = sym }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = sym, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (businessName.isNotEmpty() && cuitText.isNotEmpty()) {
                                viewModel.registerStoreProfile(
                                    businessName, cuitText, categoryText, aliasText.ifEmpty { "mi.negocio" }, cvuText.ifEmpty { "000000000000" }, 5000000.0, settlementAsset
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFA)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(text = "Crear Cuenta Comercial", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // RENDER MERCHANT ACTIVE PANEL
                val profile = merchantProfile!!
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile banner details
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A30)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2196F3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = profile.businessName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                                Text(text = "CUIT: ${profile.cuit} • Alias: ${profile.alias}", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                            }
                        }
                    }

                    // Stat dashboard boxes
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = T.translate(lang, "daily_revenue"), fontSize = 11.sp, color = Color.Gray)
                                Text(text = "$ 143.200 ARS", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2A)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = T.translate(lang, "settlements"), fontSize = 11.sp, color = Color.Gray)
                                Text(text = "114.50 ${profile.defaultSettlementAsset}", fontWeight = FontWeight.Bold, color = Color(0xFF00FFCC), fontSize = 16.sp)
                            }
                        }
                    }

                    // Generate Invoice checkout QR Button
                    Button(
                        onClick = { showBillingDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFA)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Icon(imageVector = Icons.Default.QrCode, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = T.translate(lang, "generate_invoice"), fontWeight = FontWeight.Bold)
                    }

                    Divider(color = Color.White.copy(alpha = 0.05f))

                    // Daily Sales transactions log
                    Text(text = T.translate(lang, "assets_title") + " Ventas", fontWeight = FontWeight.Bold, color = Color.White)

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Filter transactions representing RECEIVED payments from Client checkout
                        val sales = transactions.filter { it.type == "RECEIVED" }
                        if (sales.isEmpty()) {
                            item {
                                Text(text = "Aún no has recibido ventas hoy.", color = Color.Gray, modifier = Modifier.padding(12.dp))
                            }
                        } else {
                            items(sales) { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF131B2A))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "Orden #${item.txHash.take(6).uppercase()} • ${item.note}", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                        Text(text = "Liquidado a ${profile.defaultSettlementAsset}", color = Color.Gray, fontSize = 11.sp)
                                    }
                                    Text(text = "+${item.amount} ${item.assetSymbol}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Log out / de-register button
                    TextButton(
                        onClick = { viewModel.disableMerchantMode() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Cerrar Panel Comercial ⇾", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Billing Invoice QR dialogue creator
    if (showBillingDialog) {
        AlertDialog(
            onDismissRequest = { showBillingDialog = false },
            title = { Text(text = T.translate(lang, "generate_invoice") + " QR") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "Ingresar los detalles del cobro. Se generará un código QR dinámico que el cliente escaneará para pagar.")
                    
                    OutlinedTextField(
                        value = billingAmount,
                        onValueChange = { billingAmount = it },
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

                    OutlinedTextField(
                        value = billingNote,
                        onValueChange = { billingNote = it },
                        label = { Text(text = "Detalle (Concepto)") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF007AFA),
                            unfocusedBorderColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = billingAmount.toDoubleOrNull()
                        if (amt != null && amt > 0) {
                            showBillingDialog = false
                            // Inject mock checkout payment complete
                            viewModel.simulateMerchantPaymentReceived(amt, billingNote.ifEmpty { "Venta General" }, billingAsset)
                            billingAmount = ""
                            billingNote = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFA))
                ) {
                    Text(text = "Crear Factura")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBillingDialog = false }) {
                    Text(text = T.translate(lang, "cancel"))
                }
            }
        )
    }
}
