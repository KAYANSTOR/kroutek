package com.example.security

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * 🔥 مدير Firebase المركزي لـ كروتك
 *
 * المهام:
 * 1. تسجيل الجهاز تلقائياً في Firebase عند أول تشغيل.
 * 2. التحقق من حالة Kill Switch (هل التطبيق محظور أم لا).
 * 3. تحديث حالة الجهاز (Trial / Active / Blocked).
 *
 * هيكل قاعدة البيانات في Firebase:
 * kurotek_devices/
 *   └── {deviceId}/
 *         ├── deviceId: String
 *         ├── deviceName: String       ← اسم الهاتف
 *         ├── androidVersion: String   ← إصدار أندرويد
 *         ├── appVersion: String       ← إصدار التطبيق
 *         ├── installDate: Long        ← تاريخ التثبيت (timestamp)
 *         ├── lastSeen: Long           ← آخر تشغيل (timestamp)
 *         ├── status: String           ← "trial" | "active" | "blocked"
 *         └── isBlocked: Boolean       ← ← زر الإيقاف عن بُعد (Kill Switch)
 */
object FirebaseManager {

    private val database: FirebaseDatabase? by lazy {
        try {
            FirebaseDatabase.getInstance()
        } catch (e: Exception) {
            null
        }
    }
    private val devicesRef by lazy { database?.getReference("kurotek_devices") }

    /** حالات الجهاز */
    enum class DeviceStatus { TRIAL, ACTIVE, BLOCKED, LOADING, ERROR }

    /** نموذج بيانات الجهاز المسجّل */
    data class DeviceInfo(
        val deviceId: String = "",
        val deviceName: String = "",
        val androidVersion: String = "",
        val appVersion: String = "",
        val installDate: Long = 0L,
        val lastSeen: Long = 0L,
        val status: String = "trial",
        val isBlocked: Boolean = false
    )

    /**
     * الحصول على معرّف الجهاز الفريد (لا يتغير حتى بعد إعادة ضبط المصنع في أغلب الأحيان)
     */
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: "unknown_${System.currentTimeMillis()}"
    }

    /**
     * 📲 تسجيل الجهاز في Firebase عند أول تشغيل أو تحديث بيانات آخر تشغيل.
     * إذا كان الجهاز مسجلاً مسبقاً، يتم تحديث حقل `lastSeen` فقط.
     */
    fun registerOrUpdateDevice(context: Context, appVersion: String = "1.0") {
        val deviceId = getDeviceId(context)
        val deviceRef = devicesRef?.child(deviceId) ?: return

        deviceRef.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                // ✅ جهاز جديد — سجّله بالكامل
                val deviceInfo = mapOf(
                    "deviceId" to deviceId,
                    "deviceName" to "${Build.MANUFACTURER} ${Build.MODEL}",
                    "androidVersion" to "Android ${Build.VERSION.RELEASE}",
                    "appVersion" to appVersion,
                    "installDate" to System.currentTimeMillis(),
                    "lastSeen" to System.currentTimeMillis(),
                    "status" to "trial",
                    "isBlocked" to false
                )
                deviceRef.setValue(deviceInfo)
            } else {
                // 🔄 جهاز موجود — حدّث فقط آخر تشغيل وإصدار التطبيق
                deviceRef.updateChildren(
                    mapOf(
                        "lastSeen" to System.currentTimeMillis(),
                        "appVersion" to appVersion
                    )
                )
            }
        }.addOnFailureListener {
            // تجاهل الخطأ بصمت — سيحاول مرة أخرى في التشغيل القادم
        }
    }

    /**
     * 🔴 مراقب Kill Switch — Flow يبثّ حالة الحظر بشكل مستمر وفوري.
     * كلما غيّر الأدمن حالة `isBlocked` في Firebase، يستجيب التطبيق فوراً.
     *
     * الاستخدام في ViewModel:
     *   val status = FirebaseManager.observeKillSwitch(context).collectAsState()
     */
    fun observeKillSwitch(context: Context): Flow<DeviceStatus> = callbackFlow {
        val deviceId = getDeviceId(context)
        val deviceRef = devicesRef?.child(deviceId)
        
        var listener: ValueEventListener? = null

        if (deviceRef == null) {
            trySend(DeviceStatus.ERROR)
            close()
        } else {
            trySend(DeviceStatus.LOADING)

            listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        trySend(DeviceStatus.TRIAL)
                        return
                    }

                    val isBlocked = snapshot.child("isBlocked").getValue(Boolean::class.java) ?: false
                    val statusStr = snapshot.child("status").getValue(String::class.java) ?: "trial"

                    val status = when {
                        isBlocked -> DeviceStatus.BLOCKED
                        statusStr == "active" -> DeviceStatus.ACTIVE
                        else -> DeviceStatus.TRIAL
                    }
                    trySend(status)
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(DeviceStatus.ERROR)
                }
            }

            deviceRef.addValueEventListener(listener)
        }

        // تنظيف عند إلغاء الـ Flow (يجب أن يتم استدعاؤه دائماً لتجنب الانهيار)
        awaitClose {
            if (listener != null) {
                deviceRef?.removeEventListener(listener)
            }
        }
    }

    /**
     * ✅ تفعيل جهاز من الـ Admin (تغيير الحالة إلى "active")
     * يُستدعى من تطبيق الإدارة (Admin Panel)
     */
    fun activateDevice(deviceId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        devicesRef?.child(deviceId)?.updateChildren(
            mapOf("status" to "active", "isBlocked" to false)
        )?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { onError(it.message ?: "خطأ غير معروف") }
    }

    /**
     * 🚫 حظر جهاز عن بُعد (Kill Switch)
     * يُستدعى من تطبيق الإدارة (Admin Panel)
     */
    fun blockDevice(deviceId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        devicesRef?.child(deviceId)?.updateChildren(
            mapOf("isBlocked" to true, "status" to "blocked")
        )?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { onError(it.message ?: "خطأ غير معروف") }
    }
}
