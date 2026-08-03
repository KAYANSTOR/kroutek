package com.example.core.backup

import android.content.Context
import com.example.core.model.BackupInfo
import com.example.core.model.Resource
import com.example.core.repository.BackupRepository
import com.example.core.security.SecurityEngine
import com.example.database.CardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.firstOrNull

/**
 * BackupEngine (BackupRepositoryImpl)
 * مسؤول عن:
 * - تصدير بيانات المعاملات والعملاء إلى JSON مشفر (Encrypted JSON)
 * - استعادة البيانات (Import)
 * - إدارة ملفات النسخ الاحتياطية (List / Delete)
 */
class BackupEngine(
    private val context: Context,
    private val cardRepository: CardRepository
) : BackupRepository {

    private val backupDir: File
        get() = File(context.filesDir, "backups").also { it.mkdirs() }

    private val aesKey = SecurityEngine.generateAesKey("KUROTEK_BACKUP_KEY_v1")
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    // ==========================================
    // 1. Create Backup
    // ==========================================
    override suspend fun createBackup(): Resource<BackupInfo> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            val fileName = "kurotek_backup_${dateFormat.format(Date(timestamp))}.kbk"
            val file = File(backupDir, fileName)

            // جمع البيانات
            val transactions = cardRepository.getAllTransactions().firstOrNull() ?: emptyList()
            val deposits = cardRepository.getAllDeposits().firstOrNull() ?: emptyList()

            val payload = JSONObject().apply {
                put("version", 1)
                put("createdAt", timestamp)
                put("transactions", JSONArray(transactions.map { tx ->
                    JSONObject().apply {
                        put("id", tx.id)
                        put("phone", tx.phone)
                        put("amount", tx.amount)
                        put("walletType", tx.walletType)
                        put("timestamp", tx.createdAt)
                    }
                }))
                put("deposits", JSONArray(deposits.map { dep ->
                    JSONObject().apply {
                        put("id", dep.id)
                        put("amount", dep.amount)
                        put("walletType", dep.walletType)
                        put("isShared", dep.isShared)
                    }
                }))
            }.toString()

            // تشفير
            val encrypted = SecurityEngine.encryptAES(payload, aesKey)
            file.writeText(encrypted)

            val info = BackupInfo(
                id = UUID.randomUUID().toString(),
                fileName = fileName,
                sizeBytes = file.length(),
                recordCount = transactions.size + deposits.size,
                createdAt = timestamp,
                isEncrypted = true
            )
            Resource.Success(info)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    // ==========================================
    // 2. Restore Backup
    // ==========================================
    override suspend fun restoreBackup(backupId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            val file = backupDir.listFiles()?.find { it.nameWithoutExtension.contains(backupId) }
                ?: return@withContext Resource.Error(Exception("Backup file not found: $backupId"))

            val encrypted = file.readText()
            val decrypted = SecurityEngine.decryptAES(encrypted, aesKey)

            val json = JSONObject(decrypted)
            // TODO: اعادة إدراج البيانات في Room Database
            // سيتم استكماله عند ربط Room مع الـ Repository الجديد

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    // ==========================================
    // 3. List Available Backups
    // ==========================================
    override suspend fun listAvailableBackups(): List<BackupInfo> = withContext(Dispatchers.IO) {
        backupDir.listFiles()
            ?.filter { it.extension == "kbk" }
            ?.map { file ->
                BackupInfo(
                    id = file.nameWithoutExtension,
                    fileName = file.name,
                    sizeBytes = file.length(),
                    recordCount = -1, // غير محسوب بدون فتح الملف
                    createdAt = file.lastModified(),
                    isEncrypted = true
                )
            }
            ?.sortedByDescending { it.createdAt }
            ?: emptyList()
    }

    // ==========================================
    // 4. Delete Backup
    // ==========================================
    override suspend fun deleteBackup(backupId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        val file = backupDir.listFiles()?.find { it.nameWithoutExtension.contains(backupId) }
        return@withContext if (file?.delete() == true) {
            Resource.Success(Unit)
        } else {
            Resource.Error(Exception("Failed to delete backup: $backupId"))
        }
    }
}
