package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "points_of_sale")
data class PointOfSale(
    @PrimaryKey
    val id: String,
    val name: String,
    val location: String,
    val isActive: Boolean,
    val balance: Double,
    val createdAt: Long
)

@Entity(tableName = "wallets")
data class Wallet(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val connectionNumber: String,
    val conditions: String,
    val isActive: Boolean,
    val balance: Double,
    val createdAt: Long
)
