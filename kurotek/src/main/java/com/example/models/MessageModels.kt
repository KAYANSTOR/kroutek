package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_templates")
data class MessageTemplate(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long
)

@Entity(tableName = "message_logs")
data class MessageLog(
    @PrimaryKey
    val id: String,
    val phoneNumber: String,
    val content: String,
    val status: String, // e.g. "READ", "REJECTED", "PENDING"
    val reason: String?,
    val createdAt: Long
)
