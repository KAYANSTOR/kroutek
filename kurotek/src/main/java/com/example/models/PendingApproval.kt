package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_approvals")
data class PendingApproval(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val phone: String,
    val amount: Int,
    val walletType: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isAccountCode: Boolean = false,
    val depositId: String = ""
)
