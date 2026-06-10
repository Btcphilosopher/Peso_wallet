package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        AssetEntity::class,
        TransactionEntity::class,
        ContactEntity::class,
        SavingsVaultEntity::class,
        MerchantProfileEntity::class,
        PriceAlertEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PesoWalletDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun transactionDao(): TransactionDao
    abstract fun contactDao(): ContactDao
    abstract fun savingsDao(): SavingsDao
    abstract fun merchantDao(): MerchantDao
    abstract fun priceAlertDao(): PriceAlertDao
    abstract fun auditDao(): AuditDao

    companion object {
        @Volatile
        private var INSTANCE: PesoWalletDatabase? = null

        fun getDatabase(context: Context): PesoWalletDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PesoWalletDatabase::class.java,
                    "peso_wallet_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
