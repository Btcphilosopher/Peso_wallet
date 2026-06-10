package com.example.data.repository

import android.util.Log
import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import kotlin.random.Random

class WalletRepository(private val db: PesoWalletDatabase) {

    private val assetDao = db.assetDao()
    private val transactionDao = db.transactionDao()
    private val contactDao = db.contactDao()
    private val savingsDao = db.savingsDao()
    private val merchantDao = db.merchantDao()
    private val priceAlertDao = db.priceAlertDao()
    private val auditDao = db.auditDao()

    val assets: Flow<List<AssetEntity>> = assetDao.getAllAssets()
    val transactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val contacts: Flow<List<ContactEntity>> = contactDao.getAllContacts()
    val savingsVaults: Flow<List<SavingsVaultEntity>> = savingsDao.getAllSavingsVaults()
    val merchantProfile: Flow<MerchantProfileEntity?> = merchantDao.getMerchantProfileFlow()
    val priceAlerts: Flow<List<PriceAlertEntity>> = priceAlertDao.getAllAlerts()
    val auditLogs: Flow<List<AuditLogEntity>> = auditDao.getAllLogs()

    suspend fun seedDefaultDataIfEmpty() {
        // Log auditing initialization
        if (auditDao.getAllLogs().first().isEmpty()) {
            auditDao.insertLog(AuditLogEntity(eventName = "SYSTEM_INITIALIZED", details = "Peso Wallet system initialized.", severity = "INFO"))
        }

        // Check if database contains assets
        val existingAssets = assetDao.getAllAssets().first()
        if (existingAssets.isEmpty()) {
            Log.d("WalletRepository", "Seeding default data...")
            
            // Default assets
            val defaultAssets = listOf(
                AssetEntity("UXD", "US Dollar Stablecoin", 350.0, 1.0, 0.0, "ic_usd", "Ethereum Mainnet", true),
                AssetEntity("USDT", "Tether USDT", 120.0, 1.0, -0.05, "ic_tether", "TRON", true),
                AssetEntity("USDC", "USD Coin", 75.0, 1.0, 0.02, "ic_usdc", "Polygon", true),
                AssetEntity("ARS", "Argentine Peso", 450000.0, 0.0008, -1.2, "ic_peso", "Local Bank System", false),
                AssetEntity("crypto-ARS", "Digital Stable Peso", 25000.0, 0.0008, -1.2, "ic_scars", "Stellar", true)
            )
            assetDao.insertAssets(defaultAssets)
            auditDao.insertLog(AuditLogEntity(eventName = "DEFAULT_ASSETS_SEEDED", details = "Populated 5 default payment assets.", severity = "INFO"))

            // Sample savings vault
            val defaultSavings = listOf(
                SavingsVaultEntity("UXD", 100.0, 8.5),
                SavingsVaultEntity("USDT", 50.0, 9.2),
                SavingsVaultEntity("USDC", 10.0, 7.8)
            )
            for (sv in defaultSavings) {
                savingsDao.insertSavingsVault(sv)
            }

            // Default Merchant profile
            val defaultMerchant = MerchantProfileEntity(
                businessName = "Kiosco El Pampero",
                cuit = "30-71458932-5",
                category = "Kiosco/Almacén",
                alias = "el.pampero.mercado",
                cbuOrCvu = "0000003100012345678901",
                isMerchantModeActive = false
            )
            merchantDao.insertMerchantProfile(defaultMerchant)

            // Seed sample contacts
            val defaultContacts = listOf(
                ContactEntity(name = "Estanislao Cortínez", walletAddress = "0x71C...45F8", alias = "estanislao.pago", cbuOrCvu = "0000003100098765432101", notes = "Alquiler departamento", isFavorite = true),
                ContactEntity(name = "Mariana Sanguinetti", walletAddress = "0x82C...32A1", alias = "mariana.usdt", cbuOrCvu = "0000003100011111111112", notes = "Profesor de inglés", isFavorite = true),
                ContactEntity(name = "Agustín Cabrera", walletAddress = "0x93C...55B2", alias = "cabrera.usd", cbuOrCvu = "0000003100022222222223", notes = "Servicio de flete", isFavorite = false)
            )
            for (c in defaultContacts) {
                contactDao.insertContact(c)
            }

            // Seed sample transaction history
            val defaultTx = listOf(
                TransactionEntity(
                    timestamp = System.currentTimeMillis() - 7200000,
                    type = "RECEIVED",
                    assetSymbol = "UXD",
                    amount = 50.0,
                    fee = 0.0,
                    status = "COMPLETED",
                    counterpartyName = "Estanislao Cortínez",
                    counterpartyAddress = "0x71C...45F8",
                    note = "Pago clases remite",
                    category = "Servicios",
                    txHash = "0x" + UUID.randomUUID().toString().replace("-", ""),
                    network = "Ethereum Mainnet"
                ),
                TransactionEntity(
                    timestamp = System.currentTimeMillis() - 86400000,
                    type = "SENT",
                    assetSymbol = "ARS",
                    amount = 15000.0,
                    fee = 0.0,
                    status = "COMPLETED",
                    counterpartyName = "Kiosco El Pampero",
                    counterpartyAddress = "el.pampero.mercado",
                    note = "Desayuno medialunas",
                    category = "Alimentos",
                    txHash = "Internal transfer",
                    network = "Local CVU Ledger"
                ),
                TransactionEntity(
                    timestamp = System.currentTimeMillis() - 172800000,
                    type = "CONVERTED",
                    assetSymbol = "ARS",
                    amount = 80000.0,
                    fee = 240.0,
                    status = "COMPLETED",
                    counterpartyName = "Conversion Engine",
                    counterpartyAddress = "USDT Vault",
                    note = "Compra dolares digitales",
                    category = "Intercambio",
                    txHash = "0x" + UUID.randomUUID().toString().replace("-", ""),
                    network = "Local Exchange Swap"
                )
            )
            for (tx in defaultTx) {
                transactionDao.insertTransaction(tx)
            }
            
            // Add some default alerts
            priceAlertDao.insertAlert(PriceAlertEntity(assetSymbol = "UXD", targetPriceUsd = 1.02, isAbove = true))
            priceAlertDao.insertAlert(PriceAlertEntity(assetSymbol = "USDT", targetPriceUsd = 0.98, isAbove = false))
        }
    }

    // --- Core Operations ---

    suspend fun addContact(contact: ContactEntity) {
        contactDao.insertContact(contact)
        auditDao.insertLog(AuditLogEntity(eventName = "CONTACT_ADDED", details = "Contact ${contact.name} added.", severity = "INFO"))
    }

    suspend fun deleteContact(contact: ContactEntity) {
        contactDao.deleteContact(contact)
        auditDao.insertLog(AuditLogEntity(eventName = "CONTACT_DELETED", details = "Contact ${contact.name} removed.", severity = "INFO"))
    }

    suspend fun addPriceAlert(alert: PriceAlertEntity) {
        priceAlertDao.insertAlert(alert)
        auditDao.insertLog(AuditLogEntity(eventName = "PRICE_ALERT_CREATED", details = "Alert created for ${alert.assetSymbol} at $${alert.targetPriceUsd}.", severity = "INFO"))
    }

    suspend fun deletePriceAlert(alert: PriceAlertEntity) {
        priceAlertDao.deleteAlert(alert)
        auditDao.insertLog(AuditLogEntity(eventName = "PRICE_ALERT_DELETED", details = "Alert removed.", severity = "INFO"))
    }

    suspend fun sendAsset(
        symbol: String,
        recipientName: String,
        recipientAddress: String,
        amount: Double,
        fee: Double,
        note: String,
        networkName: String,
        category: String
    ): Result<Boolean> {
        if (amount <= 0) {
            return Result.failure(Exception("Amount must be greater than zero."))
        }

        val asset = assetDao.getAssetBySymbol(symbol) ?: return Result.failure(Exception("Asset $symbol not found."))

        if (asset.balance < (amount + fee)) {
            // Log failure
            auditDao.insertLog(
                AuditLogEntity(
                    eventName = "TRANSACTION_FAILED",
                    details = "Failed to send $amount $symbol to $recipientName: Insufficient balance.",
                    severity = "WARNING"
                )
            )
            return Result.failure(Exception("Insufficient balance ($symbol)"))
        }

        // Deduct balance
        val updatedAsset = asset.copy(balance = asset.balance - (amount + fee))
        assetDao.updateAsset(updatedAsset)

        // Insert transaction
        val tx = TransactionEntity(
            timestamp = System.currentTimeMillis(),
            type = "SENT",
            assetSymbol = symbol,
            amount = amount,
            fee = fee,
            status = "COMPLETED",
            counterpartyName = recipientName,
            counterpartyAddress = recipientAddress,
            note = note,
            category = category,
            txHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(32),
            network = networkName
        )
        transactionDao.insertTransaction(tx)

        // Log success
        auditDao.insertLog(
            AuditLogEntity(
                eventName = "PAYMENT_SENT",
                details = "Sent $amount $symbol to $recipientName ($recipientAddress) over $networkName.",
                severity = "INFO"
            )
        )

        return Result.success(true)
    }

    suspend fun receiveAsset(
        symbol: String,
        senderName: String,
        senderAddress: String,
        amount: Double,
        note: String,
        networkName: String
    ) {
        val asset = assetDao.getAssetBySymbol(symbol) ?: return
        
        // Add balance
        val updatedAsset = asset.copy(balance = asset.balance + amount)
        assetDao.updateAsset(updatedAsset)

        // Add Transaction
        val tx = TransactionEntity(
            timestamp = System.currentTimeMillis(),
            type = "RECEIVED",
            assetSymbol = symbol,
            amount = amount,
            fee = 0.0,
            status = "COMPLETED",
            counterpartyName = senderName,
            counterpartyAddress = senderAddress,
            note = note,
            category = "Transferencia",
            txHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(32),
            network = networkName
        )
        transactionDao.insertTransaction(tx)

        // Log
        auditDao.insertLog(
            AuditLogEntity(
                eventName = "PAYMENT_RECEIVED",
                details = "Received $amount $symbol from $senderName.",
                severity = "INFO"
            )
        )
    }

    suspend fun convertAsset(
        fromSymbol: String,
        toSymbol: String,
        amountFrom: Double,
        amountTo: Double
    ): Result<Boolean> {
        val sourceAsset = assetDao.getAssetBySymbol(fromSymbol) ?: return Result.failure(Exception("Source asset not found."))
        val targetAsset = assetDao.getAssetBySymbol(toSymbol) ?: return Result.failure(Exception("Target asset not found."))

        if (sourceAsset.balance < amountFrom) {
            return Result.failure(Exception("Insufficient balance in source asset."))
        }

        // Deduct from source, add to target
        assetDao.updateAsset(sourceAsset.copy(balance = sourceAsset.balance - amountFrom))
        assetDao.updateAsset(targetAsset.copy(balance = targetAsset.balance + amountTo))

        // Create transaction
        val tx = TransactionEntity(
            timestamp = System.currentTimeMillis(),
            type = "CONVERTED",
            assetSymbol = "$fromSymbol ➜ $toSymbol",
            amount = amountFrom,
            fee = amountFrom * 0.002, // 0.2% fee
            status = "COMPLETED",
            counterpartyName = "Conversion Engine",
            counterpartyAddress = "Internal Swap",
            note = "Intercambio $fromSymbol a $toSymbol",
            category = "Intercambio",
            txHash = "0x" + UUID.randomUUID().toString().replace("-", "").take(32),
            network = "Smart Route"
        )
        transactionDao.insertTransaction(tx)

        auditDao.insertLog(
            AuditLogEntity(
                eventName = "ASSET_CONVERTED",
                details = "Swapped $amountFrom $fromSymbol for $amountTo $toSymbol.",
                severity = "INFO"
            )
        )

        return Result.success(true)
    }

    // --- Savings (Yield Farming) ---

    suspend fun depositToSavings(symbol: String, amount: Double): Result<Boolean> {
        val asset = assetDao.getAssetBySymbol(symbol) ?: return Result.failure(Exception("Asset not found."))
        if (asset.balance < amount) {
            return Result.failure(Exception("Insufficient funds in wallet wallet."))
        }

        // Deduct from main balance
        assetDao.updateAsset(asset.copy(balance = asset.balance - amount))

        // Check if vault exists
        val vault = savingsDao.getSavingsVaultBySymbol(symbol)
        if (vault != null) {
            savingsDao.updateSavingsVault(vault.copy(balance = vault.balance + amount))
        } else {
            savingsDao.insertSavingsVault(SavingsVaultEntity(symbol, amount, 8.5))
        }

        // Transact
        transactionDao.insertTransaction(
            TransactionEntity(
                timestamp = System.currentTimeMillis(),
                type = "SAVED",
                assetSymbol = symbol,
                amount = amount,
                fee = 0.0,
                status = "COMPLETED",
                counterpartyName = "Cuenta de Ahorros",
                counterpartyAddress = "Ahorros $symbol",
                note = "Depósito en Bóveda",
                category = "Inversión",
                txHash = "Vault Lock",
                network = "Yield Vault"
            )
        )

        auditDao.insertLog(AuditLogEntity(eventName = "MONEY_SAVED", details = "Deposited $amount $symbol to $symbol Savings Vault.", severity = "INFO"))
        return Result.success(true)
    }

    suspend fun withdrawFromSavings(symbol: String, amount: Double): Result<Boolean> {
        val vault = savingsDao.getSavingsVaultBySymbol(symbol) ?: return Result.failure(Exception("No savings vault for $symbol exists."))
        if (vault.balance < amount) {
            return Result.failure(Exception("Insufficient balance in savings vault."))
        }

        val asset = assetDao.getAssetBySymbol(symbol) ?: return Result.failure(Exception("Asset not found."))

        // Deduct vault, add asset
        savingsDao.updateSavingsVault(vault.copy(balance = vault.balance - amount))
        assetDao.updateAsset(asset.copy(balance = asset.balance + amount))

        // Transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                timestamp = System.currentTimeMillis(),
                type = "RECEIVED",
                assetSymbol = symbol,
                amount = amount,
                fee = 0.0,
                status = "COMPLETED",
                counterpartyName = "Bóveda de Ahorros",
                counterpartyAddress = "Retiro $symbol",
                note = "Retiro de Bóveda",
                category = "Inversión",
                txHash = "Vault Unlock",
                network = "Yield Vault"
            )
        )

        auditDao.insertLog(AuditLogEntity(eventName = "MONEY_WITHDRAWN", details = "Withdrew $amount $symbol from savings.", severity = "INFO"))
        return Result.success(true)
    }

    suspend fun accrueInterest() {
        val vaults = savingsDao.getAllSavingsVaults().first()
        for (v in vaults) {
            if (v.balance > 0) {
                // Yield = Balance * (APY / 100) / 365 / 1000 (just mock rapid increment in-app!)
                val yield = v.balance * (v.interestRateApy / 100.0) / 365.0 * Random.nextDouble(1.0, 5.0) // Speeds up so user sees APY increasing!
                val roundedYield = Math.round(yield * 100000.0) / 100000.0
                if (roundedYield > 0) {
                    savingsDao.updateSavingsVault(
                        v.copy(
                            balance = v.balance + roundedYield,
                            totalInterestEarned = v.totalInterestEarned + roundedYield,
                            lastAccruedTimestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
    }

    // --- Market Prices Updates Simulation ---

    suspend fun simulateMarketMovement() {
        val assetsList = assetDao.getAllAssets().first()
        for (asset in assetsList) {
            if (asset.symbol == "ARS" || asset.symbol == "crypto-ARS") {
                // High Argentine inflation: peso drops slightly or moves relative to USD!
                val change = Random.nextDouble(-0.15, 0.05)
                val newPrice = asset.priceUsd * (1 + change / 100.0)
                assetDao.updateAsset(asset.copy(
                    priceUsd = newPrice,
                    priceChange24h = asset.priceChange24h + change
                ))
            } else {
                // Stablecoin pegged to USD (small drift up to 1.01 or down to 0.99)
                val changePercent = Random.nextDouble(-0.1, 0.1)
                val newPrice = 1.0 + (changePercent / 100.0)
                assetDao.updateAsset(asset.copy(
                    priceUsd = newPrice,
                    priceChange24h = changePercent
                ))

                // Check Price Alerts
                checkPriceAlerts(asset.symbol, newPrice)
            }
        }
    }

    private suspend fun checkPriceAlerts(symbol: String, priceUsd: Double) {
        val alerts = priceAlertDao.getAllAlerts().first()
        for (alert in alerts) {
            if (alert.isActive && alert.assetSymbol == symbol) {
                val trigger = (alert.isAbove && priceUsd >= alert.targetPriceUsd) ||
                        (!alert.isAbove && priceUsd <= alert.targetPriceUsd)
                
                if (trigger) {
                    // Trigger alert and turn off
                    priceAlertDao.updateAlert(alert.copy(isActive = false))
                    
                    // Log audit
                    auditDao.insertLog(
                        AuditLogEntity(
                            eventName = "PRICE_ALERT_TRIGGERED",
                            details = "Price alert triggered: $symbol price reached $${String.format("%.4f", priceUsd)} (Target was $${alert.targetPriceUsd})",
                            severity = "WARNING"
                        )
                    )
                }
            }
        }
    }

    // --- Merchant Services ---

    suspend fun updateMerchantProfile(profile: MerchantProfileEntity) {
        merchantDao.insertMerchantProfile(profile)
        auditDao.insertLog(
            AuditLogEntity(
                eventName = "PROFILE_UPDATED",
                details = "Merchant profile updated: ${profile.businessName} (CUIT: ${profile.cuit})",
                severity = "INFO"
            )
        )
    }
}
