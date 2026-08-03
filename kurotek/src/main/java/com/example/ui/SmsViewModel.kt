package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.CoreContainer
import com.example.database.CardRepository
import com.example.models.Card
import com.example.models.CustomerMapping
import com.example.models.Deposit
import com.example.models.GeneratedMikrotikCard
import com.example.models.PendingApproval
import com.example.models.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * SmsViewModel — النسخة المحدّثة (Clean Architecture)
 *
 * القاعدة الصارمة (ADR-006):
 * أي منطق تحقق يستدعي UseCase → فقط.
 * عمليات الـ DB المباشرة (كروت، موافقات) تبقى عبر Repository مؤقتاً
 * حتى تكتمل UseCases الخاصة بها في مرحلة قادمة.
 *
 * ┌─────────────────┐
 * │   SmsViewModel  │
 * └────────┬────────┘
 *          ├─ ValidateSmsAmountUseCase  (Core)
 *          ├─ SyncTransactionsUseCase  (Core)
 *          └─ CardRepository           (Data, مؤقت)
 */
class SmsViewModel(
    private val repository: CardRepository,
    private val coreContainer: CoreContainer
) : ViewModel() {

    // ─────────────────────────────────────────────
    // إعدادات الخدمة والمحافظ
    // ─────────────────────────────────────────────
    val isServiceEnabled: StateFlow<Boolean> = repository.isServiceEnabled
    fun toggleService(enabled: Boolean) = repository.setServiceEnabled(enabled)

    val isJeebEnabled: StateFlow<Boolean> = repository.isJeebEnabled
    val isJawaliEnabled: StateFlow<Boolean> = repository.isJawaliEnabled
    val isKuraimiEnabled: StateFlow<Boolean> = repository.isKuraimiEnabled
    val isHasebEnabled: StateFlow<Boolean> = repository.isHasebEnabled
    val isOneCashEnabled: StateFlow<Boolean> = repository.isOneCashEnabled
    val isMFloosEnabled: StateFlow<Boolean> = repository.isMFloosEnabled

    fun toggleJeeb(enabled: Boolean) = repository.setJeebEnabled(enabled)
    fun toggleJawali(enabled: Boolean) = repository.setJawaliEnabled(enabled)
    fun toggleKuraimi(enabled: Boolean) = repository.setKuraimiEnabled(enabled)
    fun toggleHaseb(enabled: Boolean) = repository.setHasebEnabled(enabled)
    fun toggleOneCash(enabled: Boolean) = repository.setOneCashEnabled(enabled)
    fun toggleMFloos(enabled: Boolean) = repository.setMFloosEnabled(enabled)

    // ─────────────────────────────────────────────
    // إعدادات الرد التلقائي
    // ─────────────────────────────────────────────
    val isAutoSendSmsEnabled: StateFlow<Boolean> = repository.isAutoSendSmsEnabled
    val isNotificationClickComposeEnabled: StateFlow<Boolean> = repository.isNotificationClickComposeEnabled
    val approvedSmsTemplates: StateFlow<List<String>> = repository.approvedSmsTemplates
    val accountCodeSmsTemplate: StateFlow<String> = repository.accountCodeSmsTemplate
    val accountCodeSmsPhone: StateFlow<String> = repository.accountCodeSmsPhone
    val generalSmsTemplate: StateFlow<String> = repository.generalSmsTemplate
    val networkName: StateFlow<String> = repository.networkName
    val customGeminiApiKey: StateFlow<String> = repository.customGeminiApiKey
    val cardFormatMode: StateFlow<String> = repository.cardFormatMode

    fun toggleAutoSendSms(enabled: Boolean) = repository.setAutoSendSmsEnabled(enabled)
    fun toggleNotificationClickCompose(enabled: Boolean) = repository.setNotificationClickComposeEnabled(enabled)
    fun addApprovedSmsTemplate(template: String) = repository.addApprovedSmsTemplate(template)
    fun removeApprovedSmsTemplate(template: String) = repository.removeApprovedSmsTemplate(template)
    fun updateAccountCodeSmsTemplate(template: String) = repository.setAccountCodeSmsTemplate(template)
    fun updateAccountCodeSmsPhone(phone: String) = repository.setAccountCodeSmsPhone(phone)
    fun updateGeneralSmsTemplate(template: String) = repository.setGeneralSmsTemplate(template)
    fun updateNetworkName(name: String) = repository.setNetworkName(name)
    fun updateCustomGeminiApiKey(apiKey: String) = repository.setCustomGeminiApiKey(apiKey)
    fun getActiveGeminiApiKey(): String = repository.getActiveGeminiApiKey()
    fun updateCardFormatMode(mode: String) = repository.setCardFormatMode(mode)

    // ─────────────────────────────────────────────
    // [UseCase] التحقق من مبالغ الـ SMS
    // ─────────────────────────────────────────────
    fun isAmountAllowed(amount: Int): Boolean =
        coreContainer.validateSmsAmountUseCase(amount)

    // ─────────────────────────────────────────────
    // إدارة الكروت والفئات
    // ─────────────────────────────────────────────
    val categories: StateFlow<List<Int>> = repository.categories
    fun addCategory(cat: Int) = repository.addCategory(cat)
    fun removeCategory(cat: Int) { viewModelScope.launch { repository.removeCategory(cat) } }

    val totalUnusedCount: StateFlow<Int> = repository.getUnusedCardsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val count100: StateFlow<Int> = repository.getUnusedCountByCategory(100)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val count200: StateFlow<Int> = repository.getUnusedCountByCategory(200)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val count250: StateFlow<Int> = repository.getUnusedCountByCategory(250)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val count300: StateFlow<Int> = repository.getUnusedCountByCategory(300)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val count500: StateFlow<Int> = repository.getUnusedCountByCategory(500)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allCards: StateFlow<List<Card>> = repository.getAllCards()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addSingleCard(category: Int, card: Card, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch { onComplete(repository.insertCardsList(listOf(card)) > 0) }
    }
    fun addCards(category: Int, codesBlock: String, onComplete: (Int) -> Unit) {
        viewModelScope.launch { onComplete(repository.insertCardsBulk(category, codesBlock)) }
    }
    fun addCardsList(cards: List<Card>, onComplete: (Int) -> Unit) {
        viewModelScope.launch { onComplete(repository.insertCardsList(cards)) }
    }
    fun deleteCard(cardId: String) { viewModelScope.launch { repository.deleteCard(cardId) } }
    fun markCardAsUsed(cardId: String) { viewModelScope.launch { repository.markCardAsUsed(cardId) } }
    fun clearAllCards() { viewModelScope.launch { repository.clearAllCards() } }

    // ─────────────────────────────────────────────
    // المعاملات والإيداعات
    // ─────────────────────────────────────────────
    val allTransactions: StateFlow<List<Transaction>> = repository.getAllTransactions()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDeposits: StateFlow<List<Deposit>> = repository.getAllDeposits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearAllDeposits() { viewModelScope.launch { repository.clearAllDeposits() } }
    fun clearTransactions() { viewModelScope.launch { repository.clearAllTransactions() } }

    /**
     * [UseCase + DB] إدراج معاملة يدوية مع مزامنة تلقائية
     */
    fun insertManualTransaction(phone: String, amount: Int, cardCode: String, walletType: String) {
        viewModelScope.launch {
            repository.insertTransaction(phone, amount, cardCode, walletType)
            // Sync Engine — ندفع للمزامنة تلقائياً
            val payload = """{"phone":"$phone","amount":$amount,"walletType":"$walletType"}"""
            coreContainer.syncTransactionsUseCase(payload)
        }
    }

    // ─────────────────────────────────────────────
    // الموافقات المعلقة
    // ─────────────────────────────────────────────
    val allPendingApprovals: StateFlow<List<PendingApproval>> = repository.getAllPendingApprovals()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertPendingApproval(phone: String, amount: Int, walletType: String, isAccountCode: Boolean, depositId: String) {
        viewModelScope.launch { repository.insertPendingApproval(phone, amount, walletType, isAccountCode, depositId) }
    }
    fun deletePendingApproval(id: String) { viewModelScope.launch { repository.deletePendingApproval(id) } }
    fun updatePendingApprovalPhone(pendingId: String, newPhone: String) {
        viewModelScope.launch { repository.updatePendingApprovalPhone(pendingId, newPhone) }
    }

    fun rejectPendingApproval(pendingId: String) {
        viewModelScope.launch {
            try {
                val pending = repository.getPendingApproval(pendingId) ?: return@launch
                repository.insertTransaction(pending.phone, pending.amount, "تم رفض وإلغاء إرسال الكرت يدوياً", pending.walletType)
                repository.deletePendingApproval(pendingId)
            } catch (e: Exception) { Log.e("SmsViewModel", "rejectPending failed", e) }
        }
    }

    fun approvePendingApproval(
        pendingId: String,
        onComplete: (success: Boolean, isSent: Boolean, replyMsg: String, phone: String) -> Unit = { _, _, _, _ -> }
    ) {
        viewModelScope.launch {
            try {
                val pending = repository.getPendingApproval(pendingId)
                if (pending == null) { onComplete(false, false, "", ""); return@launch }

                // [UseCase] التحقق من المبلغ
                if (!isAmountAllowed(pending.amount)) {
                    Log.w("SmsViewModel", "Amount ${pending.amount} blocked by ValidateSmsAmountUseCase")
                    onComplete(false, false, "المبلغ غير مسموح به", pending.phone)
                    return@launch
                }

                val mappedCustomer = repository.getMappingByUniqueId(pending.phone.trim())
                val recipientPhone = mappedCustomer?.basicPhone?.trim() ?: pending.phone
                val isAccountCode = pending.isAccountCode || (mappedCustomer != null)
                val card = repository.getUnusedCardByCategory(pending.amount)

                if (card != null) {
                    repository.markCardAsUsed(card.id)
                    val cardDetails = if (card.password.isNotEmpty())
                        "اسم المستخدم :\n${card.username}\nكلمة السر :\n${card.password}"
                    else card.code

                    val replyMsg = if (isAccountCode) {
                        repository.accountCodeSmsTemplate.value
                            .replace("%amount", pending.amount.toString())
                            .replace("%account", pending.phone)
                            .replace("%code", cardDetails)
                            .replace("%wallet", pending.walletType)
                    } else {
                        "تم استلام دفعتك بمبلغ ${pending.amount} ر.ي بنجاح عبر ${pending.walletType}.\nكود كرت الشحن الخاص بك هو:\n$cardDetails"
                    }

                    val isSent = com.example.utils.SmsSender.sendSmsInBackground(repository.context, recipientPhone, replyMsg)
                    val logDetails = if (isSent) "$cardDetails (تم الإرسال بعد الموافقة ✔)" else "$cardDetails (فشل SMS ✖)"
                    repository.insertTransaction(recipientPhone, pending.amount, logDetails, pending.walletType)
                    repository.updateDepositSharing(pending.depositId, isShared = isSent, cardDetails = cardDetails)
                    com.example.utils.NotificationBus.emitEvent(pending.amount, pending.walletType, recipientPhone, cardDetails, isSent)

                    // [UseCase] مزامنة المعاملة
                    val payload = """{"phone":"$recipientPhone","amount":${pending.amount},"walletType":"${pending.walletType}"}"""
                    coreContainer.syncTransactionsUseCase(payload)

                    repository.deletePendingApproval(pendingId)
                    onComplete(true, isSent, replyMsg, recipientPhone)
                } else {
                    val replyMsg = "نعتذر، لا يوجد كروت متوفرة حالياً لهذه الفئة (${pending.amount} ر.ي)."
                    com.example.utils.SmsSender.sendSmsInBackground(repository.context, recipientPhone, replyMsg)
                    repository.insertTransaction(recipientPhone, pending.amount, "كرت غير متوفر - نفذ المخزن", pending.walletType)
                    repository.updateDepositSharing(pending.depositId, isShared = false, cardDetails = "نفذ المخزن")
                    repository.deletePendingApproval(pendingId)
                    onComplete(false, false, replyMsg, recipientPhone)
                }
            } catch (e: Exception) {
                Log.e("SmsViewModel", "approvePending failed", e)
                onComplete(false, false, "", "")
            }
        }
    }

    // ─────────────────────────────────────────────
    // مطابقة العملاء (Mappings)
    // ─────────────────────────────────────────────
    val allMappings: StateFlow<List<CustomerMapping>> = repository.getAllMappings()
        .flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertMapping(customerUniqueId: String, basicPhone: String, customerName: String, walletType: String) {
        viewModelScope.launch { repository.insertMapping(customerUniqueId, basicPhone, customerName, walletType) }
    }
    fun deleteMapping(id: String) { viewModelScope.launch { repository.deleteMapping(id) } }

    // ─────────────────────────────────────────────
    // كروت شبكة (Mikrotik)
    // ─────────────────────────────────────────────
    val allGeneratedCards: StateFlow<List<GeneratedMikrotikCard>> = repository.getAllGeneratedCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertGeneratedCard(category: Int, pin: String, username: String, password: String) {
        viewModelScope.launch { repository.insertGeneratedCard(GeneratedMikrotikCard(category = category, pin = pin, username = username, password = password)) }
    }
    fun insertGeneratedCards(cards: List<GeneratedMikrotikCard>) {
        viewModelScope.launch { repository.insertGeneratedCards(cards) }
    }
    fun markGeneratedCardAsPrinted(id: String, printed: Boolean) {
        viewModelScope.launch { repository.markGeneratedCardAsPrinted(id, printed) }
    }
    fun transferGeneratedCardToAutoSales(id: String, category: Int, pin: String, username: String, password: String) {
        viewModelScope.launch { repository.transferGeneratedCardToAutoSales(id, category, pin, username, password) }
    }
    fun deleteGeneratedCard(id: String) { viewModelScope.launch { repository.deleteGeneratedCard(id) } }
    fun clearAllGeneratedCards() { viewModelScope.launch { repository.clearAllGeneratedCards() } }
}

class SmsViewModelFactory(
    private val repository: CardRepository,
    private val coreContainer: CoreContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SmsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SmsViewModel(repository, coreContainer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
