package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.T
import com.example.ui.viewmodel.PesoWalletViewModel

@Composable
fun SplashScreen(viewModel: PesoWalletViewModel, modifier: Modifier = Modifier) {
    val lang by viewModel.currentLanguage.collectAsState()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        kotlinx.coroutines.delay(2000)
        viewModel.navigateTo("onboarding")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1E3D),
                        Color(0xFF00111A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1200)) + expandVertically(animationSpec = tween(1200)),
            exit = fadeOut(animationSpec = tween(800))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Main Logo
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2196F3), Color(0xFF00E5FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet Logo",
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = T.translate(lang, "app_name"),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = T.translate(lang, "tagline"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = Color(0xFF2196F3),
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen(viewModel: PesoWalletViewModel, modifier: Modifier = Modifier) {
    val lang by viewModel.currentLanguage.collectAsState()
    var currentStep by remember { mutableStateOf(0) }

    val slides = listOf(
        Triple(
            T.translate(lang, "onboarding_title_1"),
            T.translate(lang, "onboarding_desc_1"),
            Icons.Default.Savings
        ),
        Triple(
            T.translate(lang, "onboarding_title_2"),
            T.translate(lang, "onboarding_desc_2"),
            Icons.Default.QrCodeScanner
        ),
        Triple(
            T.translate(lang, "onboarding_title_3"),
            T.translate(lang, "onboarding_desc_3"),
            Icons.Default.SwapCalls
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF101726))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Language bar switch!
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Swap buttons
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E293B))
                        .padding(4.dp)
                ) {
                    AppLanguage.entries.forEach { language ->
                        val isSelected = lang == language
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF007AFA) else Color.Transparent)
                                .clickable { viewModel.changeLanguage(language) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = language.code.uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                TextButton(
                    onClick = { viewModel.navigateTo("auth") },
                    modifier = Modifier.testTag("skip_button")
                ) {
                    Text(
                        text = T.translate(lang, "skip"),
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Icon slide display
            val activeSlide = slides[currentStep]
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = activeSlide.third,
                    contentDescription = null,
                    tint = Color(0xFF4FA9FB),
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Texts
            Text(
                text = activeSlide.first,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = activeSlide.second,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.weight(0.8f))

            // Step indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                slides.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index == currentStep) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(if (index == currentStep) Color(0xFF007AFA) else Color.Gray.copy(alpha = 0.4f))
                            .animateContentSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = {
                    if (currentStep < slides.size - 1) {
                        currentStep++
                    } else {
                        viewModel.navigateTo("auth")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("onboarding_next_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007AFA)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (currentStep == slides.size - 1) T.translate(lang, "get_started") else T.translate(lang, "next"),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars))
        }
    }
}

@Composable
fun AuthenticationScreen(viewModel: PesoWalletViewModel, modifier: Modifier = Modifier) {
    val lang by viewModel.currentLanguage.collectAsState()
    val pin by viewModel.pinCode.collectAsState()
    val isError by viewModel.isPinError.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0F1D))
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Shield / Secure lock
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF162035)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = Color(0xFF007AFA),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = T.translate(lang, "auth_title"),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = T.translate(lang, "auth_subtitle"),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Circular bullet PIN indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isActive = i < pin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isError -> Color.Red
                                    isActive -> Color(0xFF007AFA)
                                    else -> Color.White.copy(alpha = 0.2f)
                                }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isError) {
                Text(
                    text = T.translate(lang, "auth_error"),
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Text(
                    text = "Demo PIN: 1234",
                    color = Color.White.copy(alpha = 0.3f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Keypad Grid 3x4
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("bio", "0", "del")
                )

                keys.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { key ->
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(if (key != "bio" && key != "del") Color(0xFF1E293B) else Color.Transparent)
                                    .clickable {
                                        when (key) {
                                            "bio" -> viewModel.authenticateWithBiometrics()
                                            "del" -> viewModel.deletePinDigit()
                                            else -> viewModel.submitPinDigit(key)
                                        }
                                    }
                                    .testTag("keypad_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                when (key) {
                                    "bio" -> Icon(
                                        imageVector = Icons.Outlined.Fingerprint,
                                        contentDescription = "Biometrics",
                                        tint = Color(0xFF007AFA),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    "del" -> Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "Backspace",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    else -> Text(
                                        text = key,
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars))
        }
    }
}
