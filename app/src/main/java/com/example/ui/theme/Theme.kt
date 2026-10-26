package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class FinancyColorTokens(
    val isDark: Boolean,
    val background: Color,
    val card: Color,
    val cardSubtle: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val emerald: Color,
    val emeraldDark: Color,
    val emeraldSoft: Color,
    val cyan: Color,
    val cyanSoft: Color,
    val amber: Color,
    val amberSoft: Color,
    val rose: Color,
    val roseSoft: Color,
    val navBackground: Color,
    val navBorder: Color
)

val LightFinancyTokens = FinancyColorTokens(
    isDark = false,
    background = FinancyBgLight,
    card = FinancyCardLight,
    cardSubtle = FinancyCardSubtleLight,
    border = FinancyBorderLight,
    textPrimary = FinancyTextPrimaryLight,
    textSecondary = FinancyTextSecondaryLight,
    textMuted = FinancyTextMutedLight,
    emerald = FinancyEmerald,
    emeraldDark = FinancyEmeraldDark,
    emeraldSoft = FinancyEmeraldSoft,
    cyan = FinancyCyan,
    cyanSoft = FinancyCyanSoft,
    amber = FinancyAmber,
    amberSoft = FinancyAmberSoft,
    rose = FinancyRose,
    roseSoft = FinancyRoseSoft,
    navBackground = Color(0xF5FFFFFF),
    navBorder = Color(0x33CBD5E1)
)

val DarkFinancyTokens = FinancyColorTokens(
    isDark = true,
    background = FinancyBgDark,
    card = FinancyCardDark,
    cardSubtle = FinancyCardSubtleDark,
    border = FinancyBorderDark,
    textPrimary = FinancyTextPrimaryDark,
    textSecondary = FinancyTextSecondaryDark,
    textMuted = FinancyTextMutedDark,
    emerald = FinancyEmeraldNeon,
    emeraldDark = FinancyEmerald,
    emeraldSoft = Color(0x2610B981),
    cyan = FinancyCyan,
    cyanSoft = Color(0x260EA5E9),
    amber = FinancyAmber,
    amberSoft = Color(0x26F59E0B),
    rose = FinancyRose,
    roseSoft = Color(0x26F43F5E),
    navBackground = Color(0xF2151C2C),
    navBorder = Color(0x40334155)
)

val LocalFinancy = staticCompositionLocalOf { LightFinancyTokens }

object FinancyTheme {
    val colors: FinancyColorTokens
        @Composable
        get() = LocalFinancy.current
}

private val LightColorScheme = lightColorScheme(
    primary = FinancyEmerald,
    onPrimary = Color.White,
    primaryContainer = FinancyEmeraldSoft,
    onPrimaryContainer = FinancyEmeraldDark,
    secondary = FinancyCyan,
    onSecondary = Color.White,
    secondaryContainer = FinancyCyanSoft,
    onSecondaryContainer = Cyan700,
    tertiary = FinancyAmber,
    onTertiary = Color.White,
    background = FinancyBgLight,
    onBackground = FinancyTextPrimaryLight,
    surface = FinancyCardLight,
    onSurface = FinancyTextPrimaryLight,
    surfaceVariant = FinancyCardSubtleLight,
    onSurfaceVariant = FinancyTextSecondaryLight,
    surfaceContainerHigh = FinancyCardLight,
    surfaceContainerHighest = FinancyCardLight,
    outline = FinancyBorderLight
)

private val DarkColorScheme = darkColorScheme(
    primary = FinancyEmeraldNeon,
    onPrimary = Color(0xFF064E3B),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = FinancyEmeraldLight,
    secondary = FinancyCyan,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0C4A6E),
    onSecondaryContainer = Cyan200,
    tertiary = FinancyAmber,
    onTertiary = Color.White,
    background = FinancyBgDark,
    onBackground = FinancyTextPrimaryDark,
    surface = FinancyCardDark,
    onSurface = FinancyTextPrimaryDark,
    surfaceVariant = FinancyCardSubtleDark,
    onSurfaceVariant = FinancyTextSecondaryDark,
    surfaceContainerHigh = FinancyCardDark,
    surfaceContainerHighest = FinancyCardDark,
    outline = FinancyBorderDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val financyTokens = if (darkTheme) DarkFinancyTokens else LightFinancyTokens

    CompositionLocalProvider(LocalFinancy provides financyTokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
