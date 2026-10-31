package fr.parentsapariscentre.badgecreche.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AppcCoralDark,
    onPrimary = Color.Black,
    primaryContainer = AppcNavy,
    onPrimaryContainer = Color.White,
    secondary = AppcLabel,
    background = AppcBackgroundDark,
    surface = AppcSurfaceDark,
    surfaceVariant = Color(0xFF282D3C),
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFB5BAC9)
)

private val LightColorScheme = lightColorScheme(
    primary = AppcNavy,
    onPrimary = Color.White,
    primaryContainer = AppcCoral,
    onPrimaryContainer = Color.White,
    secondary = AppcCoral,
    background = AppcBackgroundLight,
    surface = AppcSurfaceLight,
    surfaceVariant = Color(0xFFEBEFF5),
    onSurface = Color(0xFF1D2331),
    onSurfaceVariant = AppcSecondaryText
)

@Composable
fun BadgeCrecheTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
