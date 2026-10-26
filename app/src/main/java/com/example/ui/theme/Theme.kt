package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- Material 3 Official Color Schemes ---
val DarkColorScheme = darkColorScheme(
    primary = MintPrimaryDark,
    onPrimary = EmeraldContainerDark,
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = MintPrimaryDark,
    secondary = AmberAccentDark,
    onSecondary = Color(0xFF1E293B),
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF253347),
    onSurfaceVariant = TextSecondaryDark,
    outline = OutlineDark
)

val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = MintContainer,
    onPrimaryContainer = EmeraldPrimary,
    secondary = AmberAccent,
    onSecondary = Color.White,
    background = LightBackground,
    surface = LightSurface,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = OutlineLight
)

// --- Financy Design Tokens for Unified Theme Harmony ---
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
) {
    val surfaceCard: Color get() = card
    val surfaceSubtle: Color get() = cardSubtle
    val borderSubtle: Color get() = border
    val neonGreen: Color get() = emerald
    val emeraldPrimary: Color get() = emeraldDark
}

val LightFinancyTokens = FinancyColorTokens(
    isDark = false,
    background = LightBackground,
    card = LightSurface,
    cardSubtle = Color(0xFFF1F5F9),
    border = OutlineLight,
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    textMuted = Color(0xFF94A3B8),
    emerald = EmeraldPrimary,
    emeraldDark = EmeraldContainerDark,
    emeraldSoft = MintContainer,
    cyan = CyanAccent,
    cyanSoft = CyanAccentSoft,
    amber = AmberAccent,
    amberSoft = Color(0xFFFFFBEB),
    rose = RoseAccent,
    roseSoft = RoseAccentSoft,
    navBackground = Color(0xF5FFFFFF),
    navBorder = OutlineLight
)

val DarkFinancyTokens = FinancyColorTokens(
    isDark = true,
    background = DarkBackground,
    card = DarkSurface,
    cardSubtle = Color(0xFF151C2C),
    border = OutlineDark,
    textPrimary = TextPrimaryDark,
    textSecondary = TextSecondaryDark,
    textMuted = Color(0xFF64748B),
    emerald = MintPrimaryDark,
    emeraldDark = EmeraldContainerDark,
    emeraldSoft = Color(0x2610B981),
    cyan = Color(0xFF38BDF8),
    cyanSoft = Color(0x260EA5E9),
    amber = AmberAccentDark,
    amberSoft = Color(0x26FBBF24),
    rose = Color(0xFFFB7185),
    roseSoft = Color(0x26F43F5E),
    navBackground = Color(0xF2151C2C),
    navBorder = OutlineDark
)

val LocalFinancy = staticCompositionLocalOf { LightFinancyTokens }

object FinancyTheme {
    val colors: FinancyColorTokens
        @Composable
        get() = LocalFinancy.current
}

@Composable
fun NamazTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    val financyTokens = if (darkTheme) DarkFinancyTokens else LightFinancyTokens

    CompositionLocalProvider(LocalFinancy provides financyTokens) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Alias for seamless backward compatibility across all call sites
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    NamazTrackerTheme(darkTheme = darkTheme, content = content)
}
