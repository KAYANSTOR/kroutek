package com.example.core.engine.security

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * SecurityEngine
 * مسؤولة عن تشفير وفك تشفير البيانات الحساسة مثل كلمات المرور ومعلومات التراخيص والكروت.
 */
object SecurityEngine {
    private const val AES_MODE = "AES/ECB/PKCS5Padding"
    private const val SECRET = "KUROTEK_SECURE_V1_2026_MASTER_KEY" // In production, use Android Keystore

    private val secretKeySpec: SecretKeySpec
        get() {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = SECRET.toByteArray(Charsets.UTF_8)
            digest.update(bytes, 0, bytes.size)
            val key = digest.digest()
            return SecretKeySpec(key, "AES")
        }

    fun encrypt(value: String): String {
        return try {
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            val encryptedBytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            value // Fallback to plain text if error occurs
        }
    }

    fun decrypt(encryptedValue: String): String {
        return try {
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            val decryptedBytes = cipher.doFinal(Base64.decode(encryptedValue, Base64.NO_WRAP))
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            encryptedValue // Fallback
        }
    }
}
