package com.example.sync

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * جدول طابور المزامنة (Outbox Queue).
 *
 * كل صف يمثّل تغييراً محلياً واحداً (إنشاء/تحديث/حذف) لم يُرفع بعد
 * إلى السيرفر. بمجرد رفعه بنجاح، يُحذف الصف من الجدول.
 *
 * نمط العمل (Offline-First):
 *   1. يكتب التطبيق التغيير في Room أولاً (محلياً فوراً).
 *   2. يُضيف صفاً هنا في نفس الـ @Transaction الذرية.
 *   3. عند توفر الإنترنت، يقرأ SyncWorker هذا الجدول ويرفع التغييرات بالترتيب.
 *   4. عند نجاح الرفع، يحذف الصف (أو يعلّمه syncedAt).
 */
@Entity(tableName = "sync_outbox")
data class SyncOutboxEntry(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    /** اسم الكيان المتأثر (cards, transactions, pending_approvals, etc.) */
    val entityType: String,

    /** معرّف السجل المتأثر في جدوله الأصلي */
    val entityId: String,

    /** نوع العملية: CREATE | UPDATE | DELETE */
    val operation: String,

    /**
     * البيانات المُسلسَلة (JSON) للسجل — تُحفظ هنا لكي يعمل الرفع
     * حتى لو تغيّر/حُذف السجل الأصلي لاحقاً قبل اكتمال المزامنة.
     * قيمة null تعني عملية DELETE.
     */
    val payload: String?,

    /** وقت إنشاء الإدخال (للترتيب والتشخيص) */
    val createdAt: Long = System.currentTimeMillis(),

    /** عدد محاولات الرفع الفاشلة (للـ Retry backoff) */
    val retryCount: Int = 0,

    /** وقت آخر محاولة فاشلة */
    val lastAttemptAt: Long? = null,

    /** رسالة آخر خطأ (للتشخيص) */
    val lastError: String? = null
)

/** ثوابت أنواع العمليات */
object SyncOperation {
    const val CREATE = "CREATE"
    const val UPDATE = "UPDATE"
    const val DELETE = "DELETE"
}

/** ثوابت أسماء الكيانات — تطابق ما يتوقعه السيرفر في sync.registry.ts */
object SyncEntityType {
    const val CARD               = "cards"
    const val TRANSACTION        = "transactions"
    const val PENDING_APPROVAL   = "pending_approvals"
    const val DEPOSIT            = "deposits"
    const val CUSTOMER_MAPPING   = "customer_mappings"
    const val DISTRIBUTOR_CUSTOMER    = "distributor_customers"
    const val DISTRIBUTOR_TRANSACTION = "distributor_transactions"
    const val DISTRIBUTOR_EXPENSE     = "distributor_expenses"
    const val DISTRIBUTOR_CAPITAL     = "distributor_capitals"
}
