package com.example.core

import android.content.Context
import android.util.Log
import com.example.core.backup.BackupEngine
import com.example.core.device.DeviceEngine
import com.example.core.license.LicenseEngine
import com.example.core.network.NetworkModule
import com.example.core.repository.impl.*
import com.example.core.security.SecurityEngine
import com.example.core.session.SessionManager
import com.example.core.settings.SettingsEngine
import com.example.core.sync.SyncEngine
import com.example.core.usecase.*
import com.example.database.CardRepository

class CoreContainer private constructor(private val context: Context) {

    init {
        Log.e("STARTUP", "STEP 2: CoreContainer constructor START")
    }

    val cardRepository by lazy {
        Log.e("STARTUP", "STEP 2a: CoreContainer.cardRepository lazy init START")
        try {
            val repo = CardRepository.getInstance(context)
            Log.e("STARTUP", "STEP 2a: CoreContainer.cardRepository lazy init SUCCESS")
            repo
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2a FAILED: CoreContainer.cardRepository init", e)
            throw e
        }
    }

    val deviceEngine by lazy {
        Log.e("STARTUP", "STEP 2b: CoreContainer.deviceEngine lazy init START")
        try {
            val engine = DeviceEngine(context)
            Log.e("STARTUP", "STEP 2b: CoreContainer.deviceEngine lazy init SUCCESS")
            engine
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2b FAILED: CoreContainer.deviceEngine init", e)
            throw e
        }
    }

    val settingsEngine by lazy {
        Log.e("STARTUP", "STEP 2c: CoreContainer.settingsEngine lazy init START")
        try {
            val engine = SettingsEngine(context)
            Log.e("STARTUP", "STEP 2c: CoreContainer.settingsEngine lazy init SUCCESS")
            engine
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2c FAILED: CoreContainer.settingsEngine init", e)
            throw e
        }
    }

    val sessionManager by lazy {
        Log.e("STARTUP", "STEP 2d: CoreContainer.sessionManager lazy init START")
        try {
            val manager = SessionManager(context, onLogout = { })
            Log.e("STARTUP", "STEP 2d: CoreContainer.sessionManager lazy init SUCCESS")
            manager
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2d FAILED: CoreContainer.sessionManager init", e)
            throw e
        }
    }

    val networkEngine by lazy {
        Log.e("STARTUP", "STEP 2e: CoreContainer.networkEngine lazy init START")
        try {
            val engine = NetworkModule.provideNetworkEngine(
                context = context,
                tokenProvider = sessionManager,
                enableLogging = true
            )
            Log.e("STARTUP", "STEP 2e: CoreContainer.networkEngine lazy init SUCCESS")
            engine
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2e FAILED: CoreContainer.networkEngine init", e)
            throw e
        }
    }

    val apiEndpoints by lazy {
        Log.e("STARTUP", "STEP 2f: CoreContainer.apiEndpoints lazy init START")
        try {
            val endpoints = NetworkModule.provideApiEndpoints(networkEngine)
            Log.e("STARTUP", "STEP 2f: CoreContainer.apiEndpoints lazy init SUCCESS")
            endpoints
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2f FAILED: CoreContainer.apiEndpoints init", e)
            throw e
        }
    }

    val syncEngine by lazy {
        Log.e("STARTUP", "STEP 2g: CoreContainer.syncEngine lazy init START")
        try {
            val engine = SyncEngine(context, NetworkModule.provideNetworkMonitor(context))
            Log.e("STARTUP", "STEP 2g: CoreContainer.syncEngine lazy init SUCCESS")
            engine
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2g FAILED: CoreContainer.syncEngine init", e)
            throw e
        }
    }

    val licenseEngine by lazy {
        Log.e("STARTUP", "STEP 2h: CoreContainer.licenseEngine lazy init START")
        try {
            val engine = LicenseEngine(context, networkEngine, apiEndpoints, deviceEngine, sessionManager)
            Log.e("STARTUP", "STEP 2h: CoreContainer.licenseEngine lazy init SUCCESS")
            engine
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2h FAILED: CoreContainer.licenseEngine init", e)
            throw e
        }
    }

    val backupEngine by lazy {
        Log.e("STARTUP", "STEP 2i: CoreContainer.backupEngine lazy init START")
        try {
            val engine = BackupEngine(context, cardRepository)
            Log.e("STARTUP", "STEP 2i: CoreContainer.backupEngine lazy init SUCCESS")
            engine
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2i FAILED: CoreContainer.backupEngine init", e)
            throw e
        }
    }

    // ─── Repository Implementations ───────────────────────────────────
    val authRepository by lazy {
        Log.e("STARTUP", "STEP 2j: CoreContainer.authRepository lazy init START")
        try {
            val repo = AuthRepositoryImpl(networkEngine, apiEndpoints, sessionManager, deviceEngine)
            Log.e("STARTUP", "STEP 2j: CoreContainer.authRepository lazy init SUCCESS")
            repo
        } catch (e: Throwable) {
            Log.e("STARTUP", "STEP 2j FAILED: CoreContainer.authRepository init", e)
            throw e
        }
    }
    val settingsRepository by lazy { SettingsRepositoryImpl(settingsEngine) }
    val syncRepository by lazy { SyncRepositoryImpl(syncEngine) }
    val inventoryRepository by lazy { InventoryRepositoryImpl(cardRepository) }
    val salesRepository by lazy { SalesRepositoryImpl(cardRepository) }
    val walletRepository by lazy { WalletRepositoryImpl(cardRepository) }
    val reportsRepository by lazy { ReportsRepositoryImpl(cardRepository) }
    val dashboardRepository by lazy { DashboardRepositoryImpl(cardRepository) }
    val approvalsRepository by lazy { ApprovalsRepositoryImpl(cardRepository) }
    val networkRepository by lazy { NetworkRepositoryImpl(cardRepository) }
    val distributorRepository by lazy { DistributorRepositoryImpl(cardRepository) }

    // ─── UseCases (ما يراه الـ ViewModel فقط) ────────────────────────
    val loginUseCase by lazy { LoginUseCase(authRepository) }
    val logoutUseCase by lazy { LogoutUseCase(authRepository) }

    val activateLicenseUseCase by lazy { ActivateLicenseUseCase(licenseEngine) }
    val validateLicenseUseCase by lazy { ValidateLicenseUseCase(licenseEngine) }
    val renewLicenseUseCase by lazy { RenewLicenseUseCase(licenseEngine) }

    val validateSmsAmountUseCase by lazy { ValidateSmsAmountUseCase() }

    val addCardsUseCase by lazy { AddCardsUseCase(inventoryRepository) }
    val getUnusedCardUseCase by lazy { GetUnusedCardUseCase(inventoryRepository) }
    val deleteCardUseCase by lazy { DeleteCardUseCase(inventoryRepository) }

    val sellCardUseCase by lazy {
        SellCardUseCase(inventoryRepository, salesRepository, validateSmsAmountUseCase)
    }

    val approvePendingUseCase by lazy {
        ApprovePendingUseCase(approvalsRepository, sellCardUseCase)
    }

    val rejectPendingUseCase by lazy {
        RejectPendingUseCase(approvalsRepository, salesRepository)
    }

    val generateReportUseCase by lazy { GenerateReportUseCase(reportsRepository) }

    val syncNowUseCase by lazy { SyncNowUseCase(syncRepository) }
    val uploadPendingUseCase by lazy { UploadPendingOperationsUseCase(syncRepository) }
    val syncTransactionsUseCase by lazy { SyncTransactionsUseCase(syncRepository) }

    val createBackupUseCase by lazy { CreateBackupUseCase(backupEngine) }
    val restoreBackupUseCase by lazy { RestoreBackupUseCase(backupEngine) }

    val createDistributorCustomerUseCase by lazy { CreateDistributorCustomerUseCase(distributorRepository) }
    val distributorSaleUseCase by lazy {
        DistributorSaleUseCase(distributorRepository, inventoryRepository, salesRepository)
    }

    // ─── Singleton ────────────────────────────────────────────────────
    companion object {
        @Volatile private var INSTANCE: CoreContainer? = null
        fun getInstance(context: Context): CoreContainer =
            INSTANCE ?: synchronized(this) {
                Log.e("STARTUP", "STEP 2: CoreContainer.getInstance() creating singleton START")
                try {
                    val instance = CoreContainer(context.applicationContext)
                    Log.e("STARTUP", "STEP 2: CoreContainer.getInstance() creating singleton SUCCESS")
                    INSTANCE = instance
                    instance
                } catch (e: Throwable) {
                    Log.e("STARTUP", "STEP 2 FAILED: CoreContainer.getInstance()", e)
                    throw e
                }
            }
    }
}
