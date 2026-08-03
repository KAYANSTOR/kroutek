package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.AppDatabase
import com.example.models.MessageTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class MessageTemplateViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    
    val templates = database.messageTemplateDao().getAllTemplates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTemplate(title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val template = MessageTemplate(
                id = UUID.randomUUID().toString(),
                title = title,
                content = content,
                createdAt = System.currentTimeMillis()
            )
            database.messageTemplateDao().insertTemplate(template)
        }
    }

    fun deleteTemplate(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            database.messageTemplateDao().deleteTemplate(id)
        }
    }
}
