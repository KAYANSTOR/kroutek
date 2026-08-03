package com.example.core.usecase

import android.content.Context
import android.util.Log
import com.example.database.CardRepository
import com.example.models.Card
import com.example.utils.SmsSender
import com.example.utils.NotificationBus
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ProcessDepositUseCase — قلب أتمتة بيع الكروت.
 *
 * يجمع في مكان واحد المنطق الذي كان مبعثراً بين SmsReceiver وPendingApprovalReceiver:
 *   1. التحقق من تفعيل نوع المحفظة.
 *   2. حجز كرت بطريقة ذرّية (@Transaction) لمنع Race Condition.
 *   3. تنسيق رسالة الرد.
 *   4. إرسال SMS في الخلفية.
 *   5. تسجيل المعاملة والإيداع.
 *   6. إطلاق حدث الواجهة (NotificationBus).
 *
 * الفائدة المعمارية:
 *   - منطق الأعمال في مكان واحد قابل للاختبار بمعزل.
 *   - SmsReceiver و PendingApprovalReceiver يصبحان مجرد "بوابة دخول" نظيفة.
 */
@Singleton
class ProcessDepositUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: CardRepository
) {

    /** نتيجة معالجة الإيداع */
    sealed class Result {
        data class AutoSent(val card: Card, val cardDetails: String, val isSent: Boolean) : Result()
        data class PendingCreated(val pendingId: String, val depositId: String) : Result()
        data class OutOfStock(val amount: Int) : Result()
        data class WalletDisabled(val walletType: String) : Result()
        data class InvalidAmount(val amount: Int) : Result()
    }

    /**
     * نفّذ معالجة إيداع جديد (من SmsReceiver — وضع الإرسال التلقائي).
     */
    suspend fun processAutoSend(
        amount: Int,
        identifier: String,
        walletType: String,
        isAccountCode: Boolean
    ): Result {
        // 1) التحقق من المبلغ
        if (amount !in ALLOWED_AMOUNTS) return Result.InvalidAmount(amount)

        // 2) التحقق من تفعيل نوع المحفظة
        if (!isWalletEnabled(walletType)) return Result.WalletDisabled(walletType)

        // 3) تحديد رقم المستلم النهائي
        val recipientPhone = resolveRecipientPhone(identifier, isAccountCode)

        // 4) حجز كرت ذرّياً
        val card = repository.claimUnusedCardByCategory(amount)
            ?: return Result.OutOfStock(amount)

        // 5) تنسيق التفاصيل والرسالة
        val cardDetails = formatCardDetails(card)
        val replyMessage = buildReplyMessage(
            amount, identifier, cardDetails, walletType, isAccountCode
        )

        // 6) إرسال SMS
        val isSent = SmsSender.sendSmsInBackground(context, recipientPhone, replyMessage)
        val logDetails = if (isSent) "$cardDetails (تم الإرسال تلقائياً ✔)"
                         else "$cardDetails (فشل إرسال SMS ✖)"

        // 7) تسجيل المعاملة
        repository.insertTransaction(recipientPhone, amount, logDetails, walletType)
        repository.insertDeposit(recipientPhone, amount, walletType, isShared = isSent, cardDetails = cardDetails)

        // 8) إطلاق حدث الواجهة
        NotificationBus.emitEvent(
            amount = amount,
            walletType = walletType,
            recipientPhone = recipientPhone,
            cardDetails = cardDetails,
            isAutoSent = isSent
        )

        return Result.AutoSent(card, cardDetails, isSent)
    }

    /**
     * نفّذ قبول موافقة معلّقة (من PendingApprovalReceiver).
     */
    suspend fun processApproval(
        pendingPhone: String,
        amount: Int,
        walletType: String,
        isAccountCode: Boolean,
        depositId: String
    ): Result {
        // تحديد رقم المستلم
        val mappedCustomer = repository.getMappingByUniqueId(pendingPhone.trim())
        val recipientPhone  = mappedCustomer?.basicPhone?.trim() ?: pendingPhone
        val effectiveAccountCode = isAccountCode || (mappedCustomer != null)

        // حجز كرت ذرّياً
        val card = repository.claimUnusedCardByCategory(amount)
            ?: return Result.OutOfStock(amount)

        val cardDetails  = formatCardDetails(card)
        val replyMessage = buildReplyMessage(
            amount, pendingPhone, cardDetails, walletType, effectiveAccountCode
        )

        val isSent = SmsSender.sendSmsInBackground(context, recipientPhone, replyMessage)
        val logDetails = if (isSent) "$cardDetails (تم الإرسال بعد الموافقة ✔)"
                         else "$cardDetails (فشل إرسال SMS ✖)"

        repository.insertTransaction(recipientPhone, amount, logDetails, walletType)
        repository.updateDepositSharing(depositId, isShared = isSent, cardDetails = cardDetails)

        NotificationBus.emitEvent(
            amount = amount,
            walletType = walletType,
            recipientPhone = recipientPhone,
            cardDetails = cardDetails,
            isAutoSent = isSent
        )

        return Result.AutoSent(card, cardDetails, isSent)
    }

    /**
     * أنشئ إيداعاً معلّقاً (وضع الموافقة اليدوية في SmsReceiver).
     */
    suspend fun createPendingApproval(
        amount: Int,
        identifier: String,
        walletType: String,
        isAccountCode: Boolean
    ): Result {
        if (amount !in ALLOWED_AMOUNTS) return Result.InvalidAmount(amount)
        if (!isWalletEnabled(walletType)) return Result.WalletDisabled(walletType)

        val recipientPhone = resolveRecipientPhone(identifier, isAccountCode)
        val depositId = repository.insertDeposit(
            recipientPhone, amount, walletType, isShared = false, cardDetails = "معلق بانتظار الموافقة"
        )
        val pendingId = repository.insertPendingApproval(
            recipientPhone, amount, walletType, isAccountCode, depositId.toInt()
        )

        return Result.PendingCreated(
            pendingId = pendingId.toString(),
            depositId = depositId.toString()
        )
    }

    // ── مساعدات خاصة ──

    private suspend fun resolveRecipientPhone(identifier: String, isAccountCode: Boolean): String {
        val mapped = repository.getMappingByUniqueId(identifier.trim())?.basicPhone?.trim()
        return when {
            !mapped.isNullOrEmpty() -> mapped
            isAccountCode -> repository.accountCodeSmsPhone.value.trim().ifEmpty { identifier }
            else -> identifier
        }
    }

    private fun isWalletEnabled(walletType: String): Boolean = when (walletType) {
        "جيب"     -> repository.isJeebEnabled.value
        "جوالي"   -> repository.isJawaliEnabled.value
        "كريمي"   -> repository.isKuraimiEnabled.value
        "حاسب"    -> repository.isHasebEnabled.value
        "ون كاش"  -> repository.isOneCashEnabled.value
        "ام فلوس" -> repository.isMFloosEnabled.value
        else      -> true
    }

    private fun formatCardDetails(card: com.example.models.Card): String =
        if (card.password.isNotEmpty()) "اسم المستخدم :\n${card.username}\nكلمة السر :\n${card.password}"
        else card.code

    private fun buildReplyMessage(
        amount: Int,
        identifier: String,
        cardDetails: String,
        walletType: String,
        isAccountCode: Boolean
    ): String =
        if (isAccountCode) {
            repository.accountCodeSmsTemplate.value
                .replace("%amount", amount.toString())
                .replace("%account", identifier)
                .replace("%code", cardDetails)
                .replace("%wallet", walletType)
        } else {
            "تم استلام دفعتك بمبلغ $amount ر.ي بنجاح عبر $walletType.\nكود كرت الشحن الخاص بك هو:\n$cardDetails"
        }

    companion object {
        val ALLOWED_AMOUNTS = listOf(100, 200, 250, 300, 500, 1000, 3000)
    }
}
