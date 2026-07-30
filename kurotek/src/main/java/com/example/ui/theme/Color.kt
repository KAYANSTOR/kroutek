package com.example.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val isDarkThemeState = mutableStateOf(true)

// Unified Design System - Source of Truth
// Based on docs/design/reference-screens/وثيقة_تخطيط___.md

// Primary palette
val BrandPrimaryRed = Color(0xFFE11D48)
val BrandPrimaryDark = Color(0xFFBE123C)
val BrandSecondaryBlue = Color(0xFF3B82F6)

val BrandPrimary = Color(0xFF03DAC5)
val BrandOnPrimary = Color(0xFF000000)
val BrandSecondary = Color(0xFFBB86FC)
val BrandOnSecondary = Color(0xFF000000)
val BrandTertiary = Color(0xFF018786)
val BrandOnTertiary = Color(0xFFFFFFFF)
val BrandError = Color(0xFFB00020)

// Surfaces
val BrandSurface = Color(0xFFFFFFFF)
val BrandSurfaceVariant = Color(0xFFF5F5F5)
val BrandBackground = Color(0xFFFFFFFF)
val BrandOnBackground = Color(0xFF000000)
val BrandOnSurface = Color(0xFF000000)
val BrandOnSurfaceVariant = Color(0xFF666666)

// Legacy aliases - replace usages over time
val DeepBlack: Color get() = BrandBackground
val SurfaceDark: Color get() = BrandSurface
val SurfaceLight: Color get() = if (isDarkThemeState.value) Color(0xFF27272A) else Color(0xFFF1F5F9)
val PureWhite: Color get() = BrandOnBackground
val TextSecondary: Color get() = BrandOnSurfaceVariant
val TextDisabled = Color(0xFF71717A)

val StatusGreen = Color(0xFF018786)
val StatusRed = Color(0xFFEF4444)
val StatusWarning = Color(0xFFF59E0B)

val PrimaryRedGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFF43F5E), Color(0xFFE11D48))
)
val SuccessGreenGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF34D399), Color(0xFF10B981))
)
val BrandPrimaryGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF03DAC5), Color(0xFFBB86FC))
)

// Card categories
val Category100Cardboard = Color(0xFFD97706)
val Category200Blue = Color(0xFF2563EB)
val Category250Purple = Color(0xFF9333EA)
val Category300Green = Color(0xFF059669)
val Category500Turmeric = Color(0xFFEA580C)

