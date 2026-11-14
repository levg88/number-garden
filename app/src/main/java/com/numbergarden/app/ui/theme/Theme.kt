package com.numbergarden.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalHighContrast = staticCompositionLocalOf { false }

private val GardenColorScheme = lightColorScheme(
    primary = GardenGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE8DD),
    onPrimaryContainer = Color(0xFF1B3828),
    secondary = GardenLeaf,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE6E9DA),
    onSecondaryContainer = Color(0xFF26302A),
    tertiary = GardenAmber,
    onTertiary = Color.White,
    tertiaryContainer = GardenAmberSoft,
    onTertiaryContainer = Color(0xFF4A2E0A),
    background = GardenCream,
    onBackground = GardenInk,
    surface = GardenSurface,
    onSurface = GardenInk,
    surfaceVariant = Color(0xFFF0E9DA),
    onSurfaceVariant = GardenMuted,
    outline = GardenOutline,
    outlineVariant = GardenOutlineSoft,
    error = GardenError,
    onError = Color.White,
    errorContainer = GardenErrorSoft,
    onErrorContainer = Color(0xFF3F0F0A),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F1E5),
    surfaceContainer = Color(0xFFF2EBDD),
    surfaceContainerHigh = Color(0xFFECE4D4),
    surfaceContainerHighest = Color(0xFFE5DCCB),
)

private val HighContrastColorScheme = lightColorScheme(
    primary = HcGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDE5D7),
    onPrimaryContainer = HcInk,
    secondary = Color(0xFF1F3D2D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE4E4E4),
    onSecondaryContainer = HcInk,
    tertiary = Color(0xFF5C2E00),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE2B8),
    onTertiaryContainer = HcInk,
    background = HcBackground,
    onBackground = HcInk,
    surface = HcBackground,
    onSurface = HcInk,
    surfaceVariant = Color(0xFFF2F2F2),
    onSurfaceVariant = HcMuted,
    outline = HcOutline,
    outlineVariant = Color(0xFF4A4A4A),
    error = HcError,
    onError = Color.White,
    errorContainer = Color(0xFFF7D9D5),
    onErrorContainer = HcInk,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F7F7),
    surfaceContainer = Color(0xFFF2F2F2),
    surfaceContainerHigh = Color(0xFFEAEAEA),
    surfaceContainerHighest = Color(0xFFE2E2E2),
)

@Composable
fun NumberGardenTheme(
    highContrast: Boolean = false,
    content: @Composable () -> Unit,
) {
    // The app deliberately stays in the calm light theme even if the device is dark.
    CompositionLocalProvider(LocalHighContrast provides highContrast) {
        MaterialTheme(
            colorScheme = if (highContrast) HighContrastColorScheme else GardenColorScheme,
            typography = GardenTypography,
            shapes = GardenShapes,
            content = content,
        )
    }
}
