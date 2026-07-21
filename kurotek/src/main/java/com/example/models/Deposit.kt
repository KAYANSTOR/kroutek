package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deposits")
data class Deposit(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val phone: String,
    val amount: Int,
    val walletType: String,      // "جيب" or "جوالي"
    val isShared: Boolean,       // true: Green (شاركت كرت), false: Red (لم يتشارك كرت)
    val cardDetails: String = "", 
    val createdAt: Long = System.currentTimeMillis()
)
