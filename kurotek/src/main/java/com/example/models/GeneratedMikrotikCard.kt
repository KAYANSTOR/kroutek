package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "generated_mikrotik_cards")
data class GeneratedMikrotikCard(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val category: Int, // 100, 200, 250, 300, 500, 1000, 3000 (subscription)
    val pin: String,
    val username: String = "",
    val password: String = "",
    val printed: Boolean = false,
    val transferred: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
