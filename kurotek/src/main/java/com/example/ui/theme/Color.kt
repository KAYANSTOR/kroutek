package com.example.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val isDarkThemeState = mutableStateOf(true)

// Premium Sleek Accents
val BrandPrimaryRed = Color(0xFFE11D48)      // Rose/Crimson (Premium Red)
val BrandPrimaryDark = Color(0xFFBE123C)     // Rose 700
val BrandSecondaryBlue = Color(0xFF3B82F6)   // Blue 500

val GlowPurplePink = Color(0xFFF43F5E)       // Rose 500 (Vibrant)
val GlowOrangeGold = Color(0xFFF59E0B)       // Amber 500
val GlowEmeraldGreen = Color(0xFF10B981)     // Emerald 500

// Modern Gradients
val PrimaryRedGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFF43F5E), Color(0xFFE11D48))
)
val BlueInfoGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF60A5FA), Color(0xFF3B82F6))
)
val SuccessGreenGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF34D399), Color(0xFF10B981))
)
val PurplePinkGradient = PrimaryRedGradient
val OrangeGoldGradient = BlueInfoGradient
val EmeraldGreenGradient = SuccessGreenGradient

// Aliases for compatibility
val GoldPrimary = BrandPrimaryRed
val GoldAccent = BrandPrimaryDark
val GoldDark = Color(0xFF9F1239)

// Sleek Dark Mode / Light Mode Backgrounds
// Using Slate/Zinc premium grays instead of basic black
val DeepBlack: Color get() = if (isDarkThemeState.value) Color(0xFF09090B) else Color(0xFFF8FAFC) // Zinc 950
val SurfaceDark: Color get() = if (isDarkThemeState.value) Color(0xFF18181B) else Color(0xFFFFFFFF) // Zinc 900
val SurfaceLight: Color get() = if (isDarkThemeState.value) Color(0xFF27272A) else Color(0xFFF1F5F9) // Zinc 800

// Typography
val PureWhite: Color get() = if (isDarkThemeState.value) Color(0xFFFAFAFA) else Color(0xFF09090B)
val TextSecondary: Color get() = if (isDarkThemeState.value) Color(0xFFA1A1AA) else Color(0xFF52525B) // Zinc 400/600
val TextDisabled = Color(0xFF71717A) // Zinc 500

val StatusGreen = Color(0xFF10B981)
val StatusRed = Color(0xFFEF4444)
val StatusWarning = Color(0xFFF59E0B)

// Special card categories
val Category100Cardboard = Color(0xFFD97706) // Amber 600
val Category200Blue = Color(0xFF2563EB)      // Blue 600
val Category250Purple = Color(0xFF9333EA)    // Purple 600
val Category300Green = Color(0xFF059669)     // Emerald 600
val Category500Turmeric = Color(0xFFEA580C)  // Orange 600

