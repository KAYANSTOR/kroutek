package com.example.core.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Security Engine
 * المحرك الأساسي المسؤول عن التشفير، الهاش، والتحقق.
 * غير مرتبط بتطبيق معين، يمكن استخدامه بشكل مستقل (Generic).
 */
object SecurityEngine {
    private const val AES_MODE = "AES/ECB/PKCS5Padding"
    private const val HMAC_ALGO = "HmacSHA256"
    private const val HASH_ALGO = "SHA-256"

    /**
     * Helper to generate keys from string (In production, use Keystore).
     */
    fun generateAesKey(seed: String): SecretKeySpec {
        val digest = MessageDigest.getInstance(HASH_ALGO)
        val bytes = seed.toByteArray(Charsets.UTF_8)
        digest.update(bytes, 0, bytes.size)
        return SecretKeySpec(digest.digest(), "AES")
    }

    // ==========================================
    // 1. AES-256 Encryption & Decryption
    // ==========================================
    fun encryptAES(value: String, secretKey: SecretKeySpec): String {
        return try {
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val encryptedBytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun decryptAES(encryptedValue: String, secretKey: SecretKeySpec): String {
        return try {
            val cipher = Cipher.getInstance(AES_MODE)
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            val decryptedBytes = cipher.doFinal(Base64.decode(encryptedValue, Base64.NO_WRAP))
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    // ==========================================
    // 2. SHA-256 Hashing & Verification
    // ==========================================
    fun hashSHA256(data: String): String {
        return try {
            val digest = MessageDigest.getInstance(HASH_ALGO)
            val hashBytes = digest.digest(data.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun verifyHash(data: String, hash: String): Boolean {
        return hashSHA256(data).equals(hash, ignoreCase = true)
    }

    // ==========================================
    // 3. HMAC Generation
    // ==========================================
    fun generateHMAC(data: String, secret: String): String {
        return try {
            val mac = Mac.getInstance(HMAC_ALGO)
            val secretKeySpec = SecretKeySpec(secret.toByteArray(Charsets.UTF_8), HMAC_ALGO)
            mac.init(secretKeySpec)
            val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
            hmacBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    // ==========================================
    // 4. Secure Random Generation
    // ==========================================
    fun generateSecureRandomString(length: Int = 32): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(length)
        secureRandom.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP).substring(0, length)
    }

    // ==========================================
    // 5. JWT Helpers (Basic encoding, no 3rd party libs)
    // ==========================================
    fun createBasicJWT(payloadJson: String, secret: String): String {
        val header = """{"alg":"HS256","typ":"JWT"}"""
        val headerB64 = Base64.encodeToString(header.toByteArray(Charsets.UTF_8), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val payloadB64 = Base64.encodeToString(payloadJson.toByteArray(Charsets.UTF_8), Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        
        val signatureData = "$headerB64.$payloadB64"
        val signatureB64 = Base64.encodeToString(
            generateHMAC(signatureData, secret).toByteArray(Charsets.UTF_8), 
            Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
        )
        
        return "$signatureData.$signatureB64"
    }
}
