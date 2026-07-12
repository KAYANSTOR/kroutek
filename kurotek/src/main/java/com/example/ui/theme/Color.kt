package com.example.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val isDarkThemeState = mutableStateOf(true)

// Vibrant Brand Gradients
val PrimaryRedGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFE53935), Color(0xFFC62828))
)
val BlueInfoGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF2196F3), Color(0xFF1976D2))
)
val SuccessGreenGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF4CAF50), Color(0xFF388E3C))
)

// Brand Core Colors
val BrandPrimaryRed = Color(0xFFE53935)      // Primary Red CTA and highlight
val BrandPrimaryDark = Color(0xFFC62828)     // Primary Red Dark
val BrandSecondaryBlue = Color(0xFF2196F3)   // Info Blue

// Aliases for layout compatibility & compilation with Jaib/Kurotek visual styles
val GoldPrimary = BrandPrimaryRed
val GoldAccent = BrandPrimaryDark
val GoldDark = Color(0xFFB71C1C)

val GlowPurplePink = BrandPrimaryRed
val GlowOrangeGold = Color(0xFFFF9800)       // Warm brand accents
val GlowEmeraldGreen = Color(0xFF4CAF50)     // Brand success highlight

val PurplePinkGradient = PrimaryRedGradient
val OrangeGoldGradient = BlueInfoGradient
val EmeraldGreenGradient = SuccessGreenGradient

// Dynamic Backgrounds and Cards matching Jaib Design System specs
val DeepBlack: Color get() = if (isDarkThemeState.value) Color(0xFF0F172A) else Color(0xFFF9FAFB) // Main page background
val SurfaceDark: Color get() = if (isDarkThemeState.value) Color(0xFF1E293B) else Color(0xFFFFFFFF) // Card backgrounds
val SurfaceLight: Color get() = if (isDarkThemeState.value) Color(0xFF334155) else Color(0xFFF3F4F6) // Elevated overlay surfaces

val PureWhite: Color get() = if (isDarkThemeState.value) Color(0xFFF1F5F9) else Color(0xFF1F2937) // Text Primary
val TextSecondary: Color get() = if (isDarkThemeState.value) Color(0xFFCBD5E1) else Color(0xFF6B7280) // Text Secondary
val TextDisabled = Color(0xFF9CA3AF)

val StatusGreen = Color(0xFF4CAF50)      // Success Green
val StatusRed = Color(0xFFF44336)        // Error Red
val StatusWarning = Color(0xFFFF9800)    // Warning Orange

// Special card categories (keep user definitions colored with brand style fallback)
val Category100Cardboard = Color(0xFFC5A059) // كرتوني (Paper/Cardboard Gold Color)
val Category200Blue = Color(0xFF2196F3)      // أزرق (Blue)
val Category250Purple = Color(0xFF9C27B0)    // بنفسجي (Purple)
val Category300Green = Color(0xFF4CAF50)     // أخضر (Green)
val Category500Turmeric = Color(0xFFFF9800)  // كركمي (Turmeric/Curcuma Yellow)

