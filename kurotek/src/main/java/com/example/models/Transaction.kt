package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.Index

@Entity(tableName = "transactions", indices = [Index(value = ["createdAt"])])
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phone: String,
    val amount: Int,
    val cardCode: String,
    val walletType: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

