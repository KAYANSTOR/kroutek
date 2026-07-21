package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.Index

@Entity(tableName = "transactions", indices = [Index(value = ["createdAt"])])
data class Transaction(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val phone: String,
    val amount: Int,
    val cardCode: String,
    val walletType: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

