package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_mappings")
data class CustomerMapping(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val customerUniqueId: String,
    val basicPhone: String,
    val customerName: String = "",
    val walletType: String = "جيب"
)
