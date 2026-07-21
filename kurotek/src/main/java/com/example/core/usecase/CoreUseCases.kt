package com.example.core.usecase

import com.example.core.model.Resource
import com.example.core.repository.*
import com.example.core.repository.impl.*
import com.example.models.*
import java.util.UUID

// ══════════════════════════════════════════════════════════
// Authentication UseCases
// ══════════════════════════════════════════════════════════

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(userId: String, passwordHash: String) =
        authRepository.login(userId, passwordHash)
}

class LogoutUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke() = authRepository.logout()
}

// ══════════════════════════════════════════════════════════
// Inventory UseCases
// ══════════════════════════════════════════════════════════

class AddCardsUseCase(private val inventoryRepository: InventoryRepository) {
    suspend operator fun invoke(category: Int, codesBlock: String): Resource<Int> =
        inventoryRepository.insertCardsBulk(category, codesBlock)
}

class GetUnusedCardUseCase(private val inventoryRepository: InventoryRepository) {
    suspend operator fun invoke(category: Int): Card? =
        inventoryRepository.getUnusedCardByCategory(category)
}

class DeleteCardUseCase(private val inventoryRepository: InventoryRepository) {
    suspend operator fun invoke(cardId: String) = inventoryRepository.deleteCard(cardId)
}

// ══════════════════════════════════════════════════════════
// Sales / Card Selling UseCases
// ══════════════════════════════════════════════════════════

class SellCardUseCase(
    private val inventoryRepository: InventoryRepository,
    private val salesRepository: SalesRepository,
    private val validateSmsAmountUseCase: ValidateSmsAmountUseCase
) {
    suspend operator fun invoke(
        phone: String,
        amount: Int,
        walletType: String,
        onSmsSend: suspend (recipient: String, message: String) -> Boolean
    ): Resource<String> {
        if (!validateSmsAmountUseCase(amount)) {
            return Resource.Error(Exception("المبلغ $amount غير مدرج في قائمة المبالغ المسموحة"))
        }
        val card = inventoryRepository.claimUnusedCardByCategory(amount)
            ?: return Resource.Error(Exception("لا يوجد كروت متوفرة لهذه الفئة ($amount ر.ي)"))

        val cardDetails = if (card.password.isNotEmpty())
            "اسم المستخدم :\n${card.username}\nكلمة السر :\n${card.password}"
        else card.code

        val replyMsg = "تم استلام دفعتك بمبلغ $amount ر.ي بنجاح عبر $walletType.\nكود كرت الشحن:\n$cardDetails"
        val isSent = onSmsSend(phone, replyMsg)
        val logCode = if (isSent) "$cardDetails ✔" else "$cardDetails ✖"

        salesRepository.insertTransaction(phone, amount, logCode, walletType)
        salesRepository.insertDeposit(phone, amount, walletType, isShared = isSent, cardDetails = cardDetails)

        return Resource.Success(replyMsg)
    }
}

/**
 * ApprovePendingUseCase — يُركّز منطق الموافقة على عملية معلقة
 * يُستدعى من DashboardViewModel ويُزيل Business Logic من الـ ViewModel.
 */
class ApprovePendingUseCase(
    private val approvalsRepository: com.example.core.repository.ApprovalsRepository,
    private val sellCardUseCase: SellCardUseCase
) {
    suspend operator fun invoke(
        pendingId: String,
        onSmsSend: suspend (recipient: String, message: String) -> Boolean
    ): Resource<String> {
        val pending = approvalsRepository.getPendingApproval(pendingId)
            ?: return Resource.Error(Exception("الطلب رقم $pendingId غير موجود"))

        val result = sellCardUseCase(
            phone = pending.phone,
            amount = pending.amount,
            walletType = pending.walletType,
            onSmsSend = onSmsSend
        )

        return if (result is Resource.Success) {
            approvalsRepository.deletePendingApproval(pendingId)
            Resource.Success(result.data)
        } else {
            result
        }
    }
}

/**
 * RejectPendingUseCase — يُسجّل رفض الطلب كمعاملة ويحذفه من قائمة الانتظار.
 */
class RejectPendingUseCase(
    private val approvalsRepository: com.example.core.repository.ApprovalsRepository,
    private val salesRepository: com.example.core.repository.SalesRepository
) {
    suspend operator fun invoke(pendingId: String): Resource<Unit> {
        val pending = approvalsRepository.getPendingApproval(pendingId)
            ?: return Resource.Error(Exception("الطلب غير موجود"))
        salesRepository.insertTransaction(
            phone = pending.phone,
            amount = pending.amount,
            cardCode = "مرفوض يدوياً",
            walletType = pending.walletType
        )
        return approvalsRepository.deletePendingApproval(pendingId)
    }
}

// ══════════════════════════════════════════════════════════
// Reports UseCases
// ══════════════════════════════════════════════════════════

class GenerateReportUseCase(private val reportsRepository: ReportsRepository) {
    suspend operator fun invoke(from: Long, to: Long): Map<String, Any> {
        val transactions = reportsRepository.getTransactionsByDateRange(from, to)
        val deposits = reportsRepository.getDepositsByDateRange(from, to)
        return mapOf(
            "transactions" to transactions,
            "deposits" to deposits,
            "totalRevenue" to transactions.sumOf { it.amount.toDouble() },
            "transactionCount" to transactions.size
        )
    }
}

// ══════════════════════════════════════════════════════════
// Sync UseCases
// ══════════════════════════════════════════════════════════

class SyncNowUseCase(private val syncRepository: SyncRepositoryImpl) {
    suspend operator fun invoke(handler: com.example.core.sync.SyncTaskHandler) =
        syncRepository.runSync(handler)
}

class UploadPendingOperationsUseCase(private val syncRepository: SyncRepositoryImpl) {
    suspend operator fun invoke(): Int {
        val pending = syncRepository.getPendingTasks()
        return pending.size
    }
}

// ══════════════════════════════════════════════════════════
// Distributor UseCases
// ══════════════════════════════════════════════════════════

class CreateDistributorCustomerUseCase(private val distributorRepository: DistributorRepository) {
    suspend operator fun invoke(name: String, customId: String? = null): Resource<Unit> {
        val customer = DistributorCustomer(
            id = customId ?: UUID.randomUUID().toString(),
            name = name
        )
        return distributorRepository.insertCustomer(customer)
    }
}

class DistributorSaleUseCase(
    private val distributorRepository: DistributorRepository,
    private val inventoryRepository: InventoryRepository,
    private val salesRepository: SalesRepository
) {
    suspend operator fun invoke(
        customerId: String,
        quantities: Map<Int, Int>,
        totalAmount: Double,
        buyingCost: Double,
        profit: Double,
        receivedAmount: Double
    ): Resource<String> {
        return try {
            // التحقق من المخزون
            val insufficient = quantities.filter { (cat, qty) ->
                qty > 0 && inventoryRepository.getUnusedCountByCategory(cat) < qty
            }.keys.toList()

            // خصم الكروت
            quantities.forEach { (cat, qty) ->
                if (qty > 0) repeat(qty) {
                    inventoryRepository.claimUnusedCardByCategory(cat)
                }
            }

            val notes = "تكلفة: $buyingCost | أرباح: $profit | ${
                quantities.filter { it.value > 0 }.entries.joinToString { "${it.value}×${it.key}" }
            }"
            distributorRepository.insertTransaction(
                DistributorTransaction(
                    id = UUID.randomUUID().toString(),
                    customerId = customerId,
                    type = "sale",
                    amount = totalAmount,
                    notes = notes
                )
            )

            if (receivedAmount > 0) {
                distributorRepository.insertTransaction(
                    DistributorTransaction(
                        id = UUID.randomUUID().toString(),
                        customerId = customerId,
                        type = "payment",
                        amount = receivedAmount,
                        notes = "دفعة نقدية"
                    )
                )
            }

            val custName = distributorRepository.getCustomerById(customerId)?.name ?: "عميل موزع"
            salesRepository.insertTransaction(custName, totalAmount.toInt(),
                "فاتورة موزع - مسدد: $receivedAmount | أرباح: $profit", "حاسبة الموزع")

            val msg = if (insufficient.isEmpty()) "🟢 تم تسجيل المبيعات بنجاح"
            else "⚠️ تم البيع، مخزون غير كافٍ للفئات: $insufficient"
            Resource.Success(msg)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}

// ══════════════════════════════════════════════════════════
// License Renewal UseCase
// ══════════════════════════════════════════════════════════

class RenewLicenseUseCase(private val licenseEngine: com.example.core.license.LicenseEngine) {
    suspend operator fun invoke(newKey: String) = licenseEngine.activateLicense(newKey)
}

class ActivateLicenseUseCase(private val licenseEngine: com.example.core.license.LicenseEngine) {
    suspend operator fun invoke(key: String): com.example.core.model.Resource<com.example.core.model.LicenseStatus> = licenseEngine.activateLicense(key)
}

class ValidateLicenseUseCase(private val licenseEngine: com.example.core.license.LicenseEngine) {
    suspend operator fun invoke(): com.example.core.model.LicenseStatus = licenseEngine.getLicenseStatus()
}

class CreateBackupUseCase(private val backupEngine: com.example.core.backup.BackupEngine) {
    suspend operator fun invoke(): com.example.core.model.Resource<com.example.core.model.BackupInfo> = backupEngine.createBackup()
}

class RestoreBackupUseCase(private val backupEngine: com.example.core.backup.BackupEngine) {
    suspend operator fun invoke(backupId: String): com.example.core.model.Resource<Unit> = backupEngine.restoreBackup(backupId)
}

class SyncTransactionsUseCase(private val syncRepository: SyncRepositoryImpl) {
    suspend operator fun invoke(payload: String) {
        syncRepository.enqueueSyncTask(com.example.core.model.SyncTask(
            id = java.util.UUID.randomUUID().toString(),
            type = "transaction",
            payload = payload,
            status = com.example.core.model.SyncStatus.PENDING,
            retryCount = 0,
            createdAt = System.currentTimeMillis(),
            lastAttemptAt = null
        ))
    }
}

class ValidateSmsAmountUseCase() {
    operator fun invoke(amount: Int): Boolean {
        return amount in listOf(100, 200, 250, 300, 500, 1000)
    }
}
