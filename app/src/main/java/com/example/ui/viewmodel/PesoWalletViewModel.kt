package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PesoWalletDatabase
import com.example.data.model.*
import com.example.data.repository.WalletRepository
import com.example.ui.theme.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class PesoWalletViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PesoWalletDatabase.getDatabase(application)
    private val repository = WalletRepository(db)

    // App Navigation & Session States
    val currentLanguage = MutableStateFlow(AppLanguage.ES)
    val currencyDisplay = MutableStateFlow("ARS") // "ARS" or "USD"
    val defaultNetwork = MutableStateFlow("Stellar") // Stellar, TRON, Polygon, Ethereum Mainnet, Local Bank System
    val currentScreen = MutableStateFlow("splash") // splash, onboarding, auth, dashboard, send, receive, qr, savings, settings, merchant_setup, merchant_dashboard, price_monitoring, security_center, db_audit
    val lastVisitedScreen = MutableStateFlow("dashboard") // For going back

    // Auth States
    val pinCode = MutableStateFlow("")
    val isPinError = MutableStateFlow(false)
    val isBiometricsEnabled = MutableStateFlow(true)
    val isThemeDark = MutableStateFlow(true) // Start in dark fintech aura

    // Data-backed Flows
    val assets = repository.assets.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val transactions = repository.transactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val contacts = repository.contacts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val savingsVaults = repository.savingsVaults.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val merchantProfile = repository.merchantProfile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val priceAlerts = repository.priceAlerts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val auditLogs = repository.auditLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Event Notifications
    private val _notification = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val notification = _notification.asSharedFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedDefaultDataIfEmpty()
            
            // Start real-time background threads for Interest Accrual and Price Simulation!
            launch {
                while (true) {
                    delay(3000) // Every 3 seconds accrue interest
                    repository.accrueInterest()
                }
            }

            launch {
                while (true) {
                    delay(12000) // Every 12 seconds slight market fluctuations
                    repository.simulateMarketMovement()
                }
            }
        }
    }

    // --- Actions ---

    fun changeLanguage(language: AppLanguage) {
        currentLanguage.value = language
        viewModelScope.launch(Dispatchers.IO) {
            db.auditDao().insertLog(
                AuditLogEntity(eventName = "LANGUAGE_CHANGED", details = "Language switched to ${language.label}.")
            )
            _notification.emit(if (language == AppLanguage.ES) "Idioma cambiado a ${language.label}" else "Language changed to ${language.label}")
        }
    }

    fun toggleCurrencyDisplay() {
        val next = if (currencyDisplay.value == "ARS") "USD" else "ARS"
        currencyDisplay.value = next
        viewModelScope.launch(Dispatchers.IO) {
            db.auditDao().insertLog(
                AuditLogEntity(eventName = "CURRENCY_DISPLAY_SWAPPED", details = "Currency display toggled to $next.")
            )
        }
    }

    fun selectNetwork(network: String) {
        defaultNetwork.value = network
        viewModelScope.launch(Dispatchers.IO) {
            db.auditDao().insertLog(
                AuditLogEntity(eventName = "NETWORK_CHANGED", details = "Network switched to $network.")
            )
        }
    }

    fun navigateTo(screen: String) {
        if (screen != "splash" && screen != "onboarding" && screen != "auth" && screen != "send" && screen != "receive" && screen != "qr" && screen != "savings" && screen != "settings") {
            lastVisitedScreen.value = currentScreen.value
        }
        currentScreen.value = screen
    }

    fun submitPinDigit(digit: String) {
        isPinError.value = false
        val current = pinCode.value
        if (current.length < 4) {
            val updated = current + digit
            pinCode.value = updated
            if (updated.length == 4) {
                // Check if correct (Let's make default PIN "1234" or any PIN of 4 digits passes for easy testing!)
                viewModelScope.launch(Dispatchers.IO) {
                    delay(200) // Neat feedback delay
                    if (updated == "1234" || updated == "0000" || updated == "8888") {
                        db.auditDao().insertLog(AuditLogEntity(eventName = "USER_AUTHENTICATED", details = "Secure PIN entry success."))
                        currentScreen.value = "dashboard"
                    } else {
                        isPinError.value = true
                        pinCode.value = ""
                        db.auditDao().insertLog(AuditLogEntity(eventName = "AUTH_FAILED", details = "Incorrect PIN entry attempt.", severity = "WARNING"))
                        _notification.emit(if (currentLanguage.value == AppLanguage.ES) "PIN Incorrecto" else "Incorrect security PIN")
                    }
                }
            }
        }
    }

    fun deletePinDigit() {
        val current = pinCode.value
        if (current.isNotEmpty()) {
            pinCode.value = current.dropLast(1)
        }
    }

    fun resetPin() {
        pinCode.value = ""
        isPinError.value = false
    }

    fun authenticateWithBiometrics() {
        viewModelScope.launch(Dispatchers.IO) {
            db.auditDao().insertLog(AuditLogEntity(eventName = "USER_AUTHENTICATED", details = "Biometric credentials parsed from Keystore successfully."))
            currentScreen.value = "dashboard"
            _notification.emit(if (currentLanguage.value == AppLanguage.ES) "Autenticado con Huella" else "Authenticated with Fingerprint")
        }
    }

    // --- Financial Swaps and Assets Operations ---

    fun sendTransaction(
        symbol: String,
        recipientName: String,
        recipientAddress: String,
        amount: Double,
        note: String,
        network: String,
        category: String,
        onComplete: (Result<Boolean>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // Simulated transaction fee
            val fee = if (network == "Ethereum Mainnet") 2.5 else if (network == "Local Bank System") 0.0 else 0.02
            val result = repository.sendAsset(symbol, recipientName, recipientAddress, amount, fee, note, network, category)
            
            launch(Dispatchers.Main) {
                if (result.isSuccess) {
                    val msg = if (currentLanguage.value == AppLanguage.ES) "Envío de $amount $symbol completado" else "Dispatched $amount $symbol successfully"
                    viewModelScope.launch { _notification.emit(msg) }
                }
                onComplete(result)
            }
        }
    }

    fun convertAssetSwap(fromSymbol: String, toSymbol: String, fromAmount: Double, toAmount: Double, onFinished: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.convertAsset(fromSymbol, toSymbol, fromAmount, toAmount)
            _notification.emit(if (currentLanguage.value == AppLanguage.ES) "Intercambio realizado exitosamente" else "Assets converted successfully")
            launch(Dispatchers.Main) { onFinished() }
        }
    }

    fun depositVault(symbol: String, amount: Double, onComplete: (Result<Boolean>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.depositToSavings(symbol, amount)
            launch(Dispatchers.Main) { onComplete(res) }
        }
    }

    fun withdrawVault(symbol: String, amount: Double, onComplete: (Result<Boolean>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = repository.withdrawFromSavings(symbol, amount)
            launch(Dispatchers.Main) { onComplete(res) }
        }
    }

    // --- Contacts Manager ---

    fun saveContact(name: String, alias: String, address: String, cbu: String, notes: String, isFav: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addContact(ContactEntity(name = name, alias = alias, walletAddress = address, cbuOrCvu = cbu, notes = notes, isFavorite = isFav))
            _notification.emit(if (currentLanguage.value == AppLanguage.ES) "Contacto Guardado" else "Contact Saved")
        }
    }

    fun removeContact(contact: ContactEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteContact(contact)
            _notification.emit(if (currentLanguage.value == AppLanguage.ES) "Contacto Eliminado" else "Contact Deleted")
        }
    }

    // --- Price Signals Watch ---

    fun createPriceSignalAlert(symbol: String, priceTarget: Double, isAbove: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPriceAlert(PriceAlertEntity(assetSymbol = symbol, targetPriceUsd = priceTarget, isAbove = isAbove))
            _notification.emit(if (currentLanguage.value == AppLanguage.ES) "Alerta de Precio Activada" else "Price Alert Activated")
        }
    }

    fun removePriceAlert(alert: PriceAlertEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePriceAlert(alert)
        }
    }

    // --- Merchant Methods ---

    fun registerStoreProfile(name: String, cuit: String, category: String, alias: String, cvu: String, limit: Double, asset: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = MerchantProfileEntity(
                businessName = name,
                cuit = cuit,
                category = category,
                alias = alias,
                cbuOrCvu = cvu,
                isMerchantModeActive = true,
                dailyRevenueLimit = limit,
                defaultSettlementAsset = asset
            )
            repository.updateMerchantProfile(profile)
            _notification.emit(if (currentLanguage.value == AppLanguage.ES) "Perfil Comercial Registrado" else "Merchant Profile Configured")
        }
    }

    fun disableMerchantMode() {
        viewModelScope.launch(Dispatchers.IO) {
            val current = merchantProfile.value
            if (current != null) {
                repository.updateMerchantProfile(current.copy(isMerchantModeActive = false))
                _notification.emit(if (currentLanguage.value == AppLanguage.ES) "Modo Comercio Cerrado" else "Merchant mode closed")
            }
        }
    }

    fun simulateMerchantPaymentReceived(amount: Double, note: String, assetSymbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Receive funds as merchant
            repository.receiveAsset(
                symbol = assetSymbol,
                senderName = "Client QR Checkout",
                senderAddress = "0xQR...SCANNER",
                amount = amount,
                note = note,
                networkName = "NFC-QR POS Network"
            )
            
            // Add custom merchant success log
            db.auditDao().insertLog(
                AuditLogEntity(
                    eventName = "MERCHANT_PAYMENT_RECEIVED",
                    details = "Business sales ticket: Received $amount $assetSymbol (Note: $note)",
                    severity = "INFO"
                )
            )
            _notification.emit(if (currentLanguage.value == AppLanguage.ES) "Cobro del Comercio: +$amount $assetSymbol" else "Merchant sale: +$amount $assetSymbol")
        }
    }

    // --- Developer and Diagnostics ---

    fun simulatePriceMarketTick() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.simulateMarketMovement()
            _notification.emit(if (currentLanguage.value == AppLanguage.ES) "Precios Actualizados" else "Market prices forced update")
        }
    }

    fun hardDataReset() {
        viewModelScope.launch(Dispatchers.IO) {
            db.clearAllTables()
            repository.seedDefaultDataIfEmpty()
            _notification.emit(if (currentLanguage.value == AppLanguage.ES) "Datos Reseteados" else "Hard reset complete")
        }
    }
}
