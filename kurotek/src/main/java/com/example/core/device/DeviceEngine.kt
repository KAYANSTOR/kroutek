package com.example.core.device

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.example.core.model.DeviceInfo
import java.io.File
import java.security.MessageDigest

/**
 * Device Engine
 * المحرك المسؤول عن استخراج تفاصيل الجهاز، الـ Device ID، التوقيع، والتحقق من سلامة البيئة.
 */
class DeviceEngine(private val context: Context) {

    /**
     * يستخرج معرّف الجهاز (Device ID) الموثوق والثابت لتأمين التراخيص
     */
    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "UNKNOWN_DEVICE"
    }

    /**
     * يجمع معلومات الجهاز شاملة للاستخدام في الترخيص والمزامنة
     */
    fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            deviceId = getDeviceId(),
            androidVersion = Build.VERSION.RELEASE,
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
            appVersion = getAppVersion(),
            signatureHash = getAppSignatureHash(),
            isRooted = checkIsDeviceRooted()
        )
    }

    /**
     * استخراج إصدار التطبيق (Version Name)
     */
    private fun getAppVersion(): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    /**
     * استخراج وتشفير بصمة التطبيق (Signature) لحمايته من التعديل العكسي
     */
    @SuppressLint("PackageManagerGetSignatures")
    private fun getAppSignatureHash(): String {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            if (signatures != null && signatures.isNotEmpty()) {
                val md = MessageDigest.getInstance("SHA-256")
                md.update(signatures[0].toByteArray())
                val digest = md.digest()
                digest.joinToString("") { "%02x".format(it) }
            } else {
                "NO_SIGNATURE"
            }
        } catch (e: Exception) {
            "ERROR_GETTING_SIGNATURE"
        }
    }

    /**
     * التحقق من سلامة البيئة (Environment Integrity)
     * يفحص إذا كان الجهاز معمول له Root لتجنب الاختراقات.
     */
    private fun checkIsDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        for (path in paths) {
            if (File(path).exists()) return true
        }
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }
}
