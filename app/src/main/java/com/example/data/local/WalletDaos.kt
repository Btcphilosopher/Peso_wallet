package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE symbol = :symbol")
    suspend fun getAssetBySymbol(symbol: String): AssetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssets(assets: List<AssetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity)

    @Update
    suspend fun updateAsset(asset: AssetEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE assetSymbol = :symbol ORDER BY timestamp DESC")
    fun getTransactionsByAsset(symbol: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY timestamp DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY timestamp DESC")
    fun getTransactionsByStatus(status: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :query || '%' OR counterpartyName LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY isFavorite DESC, name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: Int): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)
}

@Dao
interface SavingsDao {
    @Query("SELECT * FROM savings")
    fun getAllSavingsVaults(): Flow<List<SavingsVaultEntity>>

    @Query("SELECT * FROM savings WHERE assetSymbol = :symbol")
    suspend fun getSavingsVaultBySymbol(symbol: String): SavingsVaultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsVault(vault: SavingsVaultEntity)

    @Update
    suspend fun updateSavingsVault(vault: SavingsVaultEntity)
}

@Dao
interface MerchantDao {
    @Query("SELECT * FROM merchant_profiles LIMIT 1")
    fun getMerchantProfileFlow(): Flow<MerchantProfileEntity?>

    @Query("SELECT * FROM merchant_profiles LIMIT 1")
    suspend fun getMerchantProfile(): MerchantProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMerchantProfile(profile: MerchantProfileEntity)

    @Update
    suspend fun updateMerchantProfile(profile: MerchantProfileEntity)
}

@Dao
interface PriceAlertDao {
    @Query("SELECT * FROM price_alerts ORDER BY createdAt DESC")
    fun getAllAlerts(): Flow<List<PriceAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: PriceAlertEntity)

    @Update
    suspend fun updateAlert(alert: PriceAlertEntity)

    @Delete
    suspend fun deleteAlert(alert: PriceAlertEntity)
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)
}
