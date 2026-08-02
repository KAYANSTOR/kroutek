package com.example.core.api.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ValidateSerialRequestDto(
    val serial: String,
    val deviceId: String
)

@JsonClass(generateAdapter = true)
data class ValidateSerialResponseDto(
    val success: Boolean,
    val status: String,
    val message: String,
    val features: List<String>?,
    val duration_months: Int?,
    val expiry: String?
)
