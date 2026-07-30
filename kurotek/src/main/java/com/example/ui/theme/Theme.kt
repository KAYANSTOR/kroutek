package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isDarkThemeState.value,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = DarkBrandPrimary,
            onPrimary = DarkBrandOnPrimary,
            secondary = DarkBrandSecondary,
            onSecondary = DarkBrandOnSecondary,
            tertiary = DarkBrandTertiary,
            onTertiary = DarkBrandOnTertiary,
            background = DarkBrandBackground,
            onBackground = DarkBrandOnBackground,
            surface = DarkBrandSurface,
            onSurface = DarkBrandOnSurface,
            surfaceVariant = DarkBrandSurfaceVariant,
            onSurfaceVariant = DarkBrandOnSurfaceVariant,
            outline = DarkBrandOutline,
            error = DarkBrandError,
            errorContainer = DarkBrandErrorContainer
        )
    } else {
        lightColorScheme(
            primary = LightBrandPrimary,
            onPrimary = LightBrandOnPrimary,
            secondary = LightBrandSecondary,
            onSecondary = LightBrandOnSecondary,
            tertiary = LightBrandTertiary,
            onTertiary = LightBrandOnTertiary,
            background = LightBrandBackground,
            onBackground = LightBrandOnBackground,
            surface = LightBrandSurface,
            onSurface = LightBrandOnSurface,
            surfaceVariant = LightBrandSurfaceVariant,
            onSurfaceVariant = LightBrandOnSurfaceVariant,
            outline = LightBrandOutline,
            error = LightBrandError,
            errorContainer = LightBrandErrorContainer
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
