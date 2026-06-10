package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val balance: Double,
    val priceUsd: Double,
    val priceChange24h: Double,
    val logoResName: String,
    val networkName: String,
    val isStablecoin: Boolean = true
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val type: String, // SENT, RECEIVED, CONVERTED, SAVED
    val assetSymbol: String,
    val amount: Double,
    val fee: Double,
    val status: String, // COMPLETED, PENDING, FAILED
    val counterpartyName: String,
    val counterpartyAddress: String, // Wallet address, CVU, CBU or Alias
    val note: String,
    val category: String, // Transfer, Food, Shopping, Salary, Investment, etc.
    val txHash: String,
    val network: String
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val walletAddress: String,
    val alias: String, // Argentina style alias: e.g. "pampa.sol.azul"
    val cbuOrCvu: String, // 22-digit Argentine bank/fintech identifier
    val notes: String = "",
    val isFavorite: Boolean = false,
    val lastUsedTimestamp: Long = 0
)

@Entity(tableName = "savings")
data class SavingsVaultEntity(
    @PrimaryKey val assetSymbol: String, // USDT, USDC, UXD, etc.
    val balance: Double,
    val interestRateApy: Double, // e.g. 8.5 for 8.5%
    val totalInterestEarned: Double = 0.0,
    val lastAccruedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "merchant_profiles")
data class MerchantProfileEntity(
    @PrimaryKey val id: String = "default_merchant",
    val businessName: String,
    val cuit: String, // Argentine tax ID e.g. 30-12345678-9
    val category: String, // Rubro: Gastronomía, Almacén, Indumentaria, etc.
    val alias: String,
    val cbuOrCvu: String,
    val isMerchantModeActive: Boolean = false,
    val dailyRevenueLimit: Double = 1000000.0,
    val defaultSettlementAsset: String = "UXD" // Preferred stablecoin settlement
)

@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assetSymbol: String,
    val targetPriceUsd: Double,
    val isAbove: Boolean, // Trigger when price goes above or below
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventName: String, // USER_REGISTERED, WALLET_CREATED, etc.
    val details: String,
    val severity: String = "INFO" // INFO, WARNING, SECURITY
)
