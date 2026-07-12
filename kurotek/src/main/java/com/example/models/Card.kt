package com.example.models

import androidx.room.Entity
import androidx.room.PrimaryKey

import androidx.room.Index

@Entity(tableName = "cards", indices = [Index(value = ["category", "used"])])
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: Int,
    val code: String,
    val username: String = "",
    val password: String = "",
    val used: Boolean = false
)

