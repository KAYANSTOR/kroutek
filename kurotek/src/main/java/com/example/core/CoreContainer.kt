package com.example.core

import android.content.Context
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

/**
 * CoreContainer (v2)
 * النسخة المحدّثة — تحتوي على جميع الـ Repositories والـ UseCases
 * حسب الخطة المعمارية الكاملة (ADR-006).
 *
 * التسلسل الإلزامي:
 * UI → ViewModel → UseCase → Repository → Engine → API/DB
 */
class CoreContainer private constructor(private val context: Context) {

    // ─── Local DB (Bridge مؤقت حتى اكتمال Room Migrations) ───────────
    val cardRepository by lazy { CardRepository(context) }

    // ─── Core Engines ─────────────────────────────────────────────────
    val deviceEngine by lazy { DeviceEngine(context) }
    val settingsEngine by lazy { SettingsEngine(context) }

    val sessionManager by lazy {
        SessionManager(context, onLogout = { /* يتم ربطه بـ AuthViewModel لاحقاً */ })
    }

    val networkEngine by lazy {
        NetworkModule.provideNetworkEngine(
            context = context,
            tokenProvider = sessionManager,
            enableLogging = true
        )
    }

    val apiEndpoints by lazy { NetworkModule.provideApiEndpoints(networkEngine) }

    val syncEngine by lazy {
        SyncEngine(context, NetworkModule.provideNetworkMonitor(context))
    }

    val licenseEngine by lazy {
        LicenseEngine(context, networkEngine, apiEndpoints, deviceEngine, sessionManager)
    }

    val backupEngine by lazy {
        BackupEngine(context, cardRepository)
    }

    // ─── Repository Implementations ───────────────────────────────────
    val authRepository by lazy {
        AuthRepositoryImpl(networkEngine, apiEndpoints, sessionManager, deviceEngine)
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
                INSTANCE ?: CoreContainer(context.applicationContext).also { INSTANCE = it }
            }
    }
}
