package com.example.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Single source of truth for theme mode. Persist this in Settings/DataStore.
val isDarkThemeState = mutableStateOf(true)

// Design Tokens - Light Mode
// Based on docs/design/reference-screens/وثيقة_تخطيط___.md
// Primary palette
val LightBrandPrimary = Color(0xFF03DAC5)
val LightBrandOnPrimary = Color(0xFF000000)
val LightBrandSecondary = Color(0xFFBB86FC)
val LightBrandOnSecondary = Color(0xFF000000)
val LightBrandTertiary = Color(0xFF018786)
val LightBrandOnTertiary = Color(0xFFFFFFFF)

// Surfaces & background
val LightBrandBackground = Color(0xFFFFFFFF)
val LightBrandSurface = Color(0xFFFFFFFF)
val LightBrandSurfaceVariant = Color(0xFFF5F5F5)
val LightBrandOnBackground = Color(0xFF000000)
val LightBrandOnSurface = Color(0xFF000000)
val LightBrandOnSurfaceVariant = Color(0xFF666666)
val LightBrandOutline = Color(0xFFE5E7EB)

// Semantic
val LightBrandError = Color(0xFFB00020)
val LightBrandErrorContainer = Color(0xFFFFDAD6)
val LightBrandSuccess = Color(0xFF10B981)
val LightBrandWarning = Color(0xFFF59E0B)

// Gradients
val LightBrandPrimaryGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF03DAC5), Color(0xFFBB86FC))
)

// Status
val LightStatusActive = Color(0xFF10B981)
val LightStatusInactive = Color(0xFFEF4444)
val LightStatusWarning = Color(0xFFF59E0B)

// Card categories
val LightCategory100Cardboard = Color(0xFFD97706)
val LightCategory200Blue = Color(0xFF2563EB)
val LightCategory250Purple = Color(0xFF9333EA)
val LightCategory300Green = Color(0xFF059669)
val LightCategory500Turmeric = Color(0xFFEA580C)

// Design Tokens - Dark Mode (kept consistent with dark brand)
val DarkBrandPrimary = Color(0xFF03DAC5)
val DarkBrandOnPrimary = Color(0xFF000000)
val DarkBrandSecondary = Color(0xFFBB86FC)
val DarkBrandOnSecondary = Color(0xFF000000)
val DarkBrandTertiary = Color(0xFF018786)
val DarkBrandOnTertiary = Color(0xFFFFFFFF)

// Surfaces & background
val DarkBrandBackground = Color(0xFF0F0F0F)
val DarkBrandSurface = Color(0xFF18181B)
val DarkBrandSurfaceVariant = Color(0xFF1E1E21)
val DarkBrandOnBackground = Color(0xFFFAFAFA)
val DarkBrandOnSurface = Color(0xFFFAFAFA)
val DarkBrandOnSurfaceVariant = Color(0xFFA1A1AA)
val DarkBrandOutline = Color(0xFF2E2E33)

// Semantic
val DarkBrandError = Color(0xFFEF4444)
val DarkBrandErrorContainer = Color(0xFF450A0A)
val DarkBrandSuccess = Color(0xFF10B981)
val DarkBrandWarning = Color(0xFFF59E0B)

// Gradients
val DarkBrandPrimaryGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF03DAC5), Color(0xFFBB86FC))
)

// Status
val DarkStatusActive = Color(0xFF10B981)
val DarkStatusInactive = Color(0xFFEF4444)
val DarkStatusWarning = Color(0xFFF59E0B)

// Card categories
val DarkCategory100Cardboard = Color(0xFFD97706)
val DarkCategory200Blue = Color(0xFF2563EB)
val DarkCategory250Purple = Color(0xFF9333EA)
val DarkCategory300Green = Color(0xFF059669)
val DarkCategory500Turmeric = Color(0xFFEA580C)

// Light aliases for gradual migration
val BrandPrimary = LightBrandPrimary
val BrandOnPrimary = LightBrandOnPrimary
val BrandSecondary = LightBrandSecondary
val BrandOnSecondary = LightBrandOnSecondary
val BrandSurface = LightBrandSurface
val BrandSurfaceVariant = LightBrandSurfaceVariant
val BrandBackground = LightBrandBackground
val BrandOnBackground = LightBrandOnBackground
val BrandOnSurface = LightBrandOnSurface
val BrandOnSurfaceVariant = LightBrandOnSurfaceVariant
val BrandOutline = LightBrandOutline
val BrandPrimaryRed = LightBrandPrimary
val BrandPrimaryDark = LightBrandTertiary
val BrandSecondaryBlue = LightBrandSecondary
val GlowPurplePink = LightBrandSecondary
val GlowOrangeGold = LightBrandWarning
val GlowEmeraldGreen = LightBrandSuccess
val PrimaryRedGradient = LightBrandPrimaryGradient
val BlueInfoGradient = LightBrandPrimaryGradient
val SuccessGreenGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF34D399), Color(0xFF10B981))
)
val PurplePinkGradient = PrimaryRedGradient
val OrangeGoldGradient = PrimaryRedGradient
val EmeraldGreenGradient = SuccessGreenGradient
val GoldPrimary = LightBrandPrimary
val GoldAccent = LightBrandTertiary
val GoldDark = Color(0xFF9F1239)
val DeepBlack: Color get() = if (isDarkThemeState.value) DarkBrandBackground else LightBrandBackground
val SurfaceDark: Color get() = if (isDarkThemeState.value) DarkBrandSurface else LightBrandSurface
val SurfaceLight: Color get() = if (isDarkThemeState.value) DarkBrandSurfaceVariant else LightBrandSurfaceVariant
val PureWhite: Color get() = if (isDarkThemeState.value) DarkBrandOnBackground else LightBrandOnBackground
val TextSecondary: Color get() = if (isDarkThemeState.value) DarkBrandOnSurfaceVariant else LightBrandOnSurfaceVariant
val TextDisabled = if (isDarkThemeState.value) Color(0xFF71717A) else Color(0xFF9CA3AF)
val StatusGreen = if (isDarkThemeState.value) DarkBrandSuccess else LightBrandSuccess
val StatusRed = if (isDarkThemeState.value) DarkStatusInactive else Color(0xFFEF4444)
val StatusWarning = if (isDarkThemeState.value) DarkBrandWarning else LightBrandWarning

// Special card categories unified
val Category100Cardboard = if (isDarkThemeState.value) DarkCategory100Cardboard else LightCategory100Cardboard
val Category200Blue = if (isDarkThemeState.value) DarkCategory200Blue else LightCategory200Blue
val Category250Purple = if (isDarkThemeState.value) DarkCategory250Purple else LightCategory250Purple
val Category300Green = if (isDarkThemeState.value) DarkCategory300Green else LightCategory300Green
val Category500Turmeric = if (isDarkThemeState.value) DarkCategory500Turmeric else LightCategory500Turmeric
