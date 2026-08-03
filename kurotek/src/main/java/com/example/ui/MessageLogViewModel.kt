package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.models.MessageLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MessageLogViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    val readMessages = database.messageLogDao().getLogsByStatus("READ")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val rejectedMessages = database.messageLogDao().getLogsByStatus("REJECTED")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Mock data insertion for testing
    fun insertMockData() {
        viewModelScope.launch(Dispatchers.IO) {
            val logs = listOf(
                MessageLog(UUID.randomUUID().toString(), "77XXXXXXX", "تم تسديد باقة انترنت بنجاح", "READ", null, System.currentTimeMillis()),
                MessageLog(UUID.randomUUID().toString(), "73XXXXXXX", "تم تحويل الرصيد بنجاح", "READ", null, System.currentTimeMillis() - 100000),
                MessageLog(UUID.randomUUID().toString(), "71XXXXXXX", "الرصيد غير كافي لإتمام العملية", "REJECTED", "خطأ في الرصيد", System.currentTimeMillis() - 200000),
                MessageLog(UUID.randomUUID().toString(), "77XXXXXXX", "الرقم غير صحيح أو غير مفعل", "REJECTED", "رقم غير صالح", System.currentTimeMillis() - 300000)
            )
            logs.forEach { database.messageLogDao().insertLog(it) }
        }
    }
}
