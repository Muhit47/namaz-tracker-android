package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// --- Light Theme Colors ---
val EmeraldPrimary = Color(0xFF059669)
val MintContainer = Color(0xFFD1FAE5)
val AmberAccent = Color(0xFFD97706)
val LightBackground = Color(0xFFF1F5F9)
val LightSurface = Color(0xFFFFFFFF)
val TextPrimaryLight = Color(0xFF1E293B)
val TextSecondaryLight = Color(0xFF64748B)
val OutlineLight = Color(0xFFE2E8F0)

// --- Dark Theme Colors (Obsidian & Glowing Islamic Emerald) ---
val MintPrimaryDark = Color(0xFF10B981) // Glowing emerald 500 for dark mode
val EmeraldContainerDark = Color(0xFF064E3B)
val AmberAccentDark = Color(0xFFFBBF24)
val DarkBackground = Color(0xFF0B120F) // Soothing obsidian midnight canvas with organic emerald undertone
val DarkSurface = Color(0xFF131D18)    // Elevated dark charcoal card
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)
val OutlineDark = Color(0xFF22352C)    // Harmonious emerald-tinted dark border

// --- Secondary / Brand Accent Tokens ---
val CyanAccent = Color(0xFF0284C7)
val CyanAccentSoft = Color(0xFFF0F9FF)
val RoseAccent = Color(0xFFE11D48)
val RoseAccentSoft = Color(0xFFFFF1F2)

// --- Compatibility Aliases for Design Tokens ---
val Slate950 = Color(0xFF020617)
val Slate900 = TextPrimaryLight
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate600 = TextSecondaryLight
val Slate500 = Color(0xFF64748B)
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate200 = OutlineLight
val Slate100 = Color(0xFFF1F5F9)
val Slate50 = LightBackground

val Emerald900 = EmeraldContainerDark
val Emerald800 = Color(0xFF065F46)
val Emerald700 = EmeraldPrimary
val Emerald600 = Color(0xFF059669)
val Emerald500 = Color(0xFF10B981)
val Emerald300 = Color(0xFF6EE7B7)
val Emerald200 = MintPrimaryDark
val Emerald100 = MintContainer
val Emerald50 = Color(0xFFECFDF5)

val Cyan700 = Color(0xFF0369A1)
val Cyan600 = CyanAccent
val Cyan200 = Color(0xFFBAE6FD)
val Cyan100 = Color(0xFFCFFAFE)
val Cyan50 = CyanAccentSoft

val Amber500 = Color(0xFFF59E0B)
val Amber600 = AmberAccent
val Amber700 = Color(0xFFB45309)
val Amber50 = Color(0xFFFFFBEB)
val Amber200 = Color(0xFFFDE68A)

val Rose600 = RoseAccent
val Rose700 = Color(0xFFBE123C)
val Rose50 = RoseAccentSoft
val Rose200 = Color(0xFFFECDD3)

// Card & Surface Colors
val GlassCardWhite = LightSurface
val GlassCardSubtle = LightBackground
val GlassBorder = OutlineLight

val StatusJamatBg = Emerald50
val StatusJamatBorder = Emerald500
val StatusJamatText = Emerald700

val StatusAloneBg = Cyan50
val StatusAloneBorder = Cyan600
val StatusAloneText = Cyan700

val StatusQazaBg = Amber50
val StatusQazaBorder = Amber600
val StatusQazaText = Amber700

val StatusMissedBg = Rose50
val StatusMissedBorder = Rose600
val StatusMissedText = Rose700

val StatusDefaultBg = LightBackground
val StatusDefaultBorder = OutlineLight
val StatusDefaultText = TextSecondaryLight

// Financy Fintech Theme Tokens
val FinancyBgLight = LightBackground
val FinancyCardLight = LightSurface
val FinancyCardSubtleLight = Color(0xFFF1F5F9)
val FinancyBorderLight = OutlineLight
val FinancyTextPrimaryLight = TextPrimaryLight
val FinancyTextSecondaryLight = TextSecondaryLight
val FinancyTextMutedLight = Color(0xFF94A3B8)

val FinancyBgDark = DarkBackground
val FinancyCardDark = DarkSurface
val FinancyCardSubtleDark = Color(0xFF151C2C)
val FinancyBorderDark = OutlineDark
val FinancyTextPrimaryDark = TextPrimaryDark
val FinancyTextSecondaryDark = TextSecondaryDark
val FinancyTextMutedDark = Color(0xFF64748B)

val FinancyEmerald = EmeraldPrimary
val FinancyEmeraldDark = EmeraldContainerDark
val FinancyEmeraldLight = MintContainer
val FinancyEmeraldSoft = Color(0xFFECFDF5)
val FinancyEmeraldNeon = MintPrimaryDark

val FinancyCyan = CyanAccent
val FinancyCyanSoft = CyanAccentSoft
val FinancyAmber = AmberAccent
val FinancyAmberSoft = Color(0xFFFFFBEB)
val FinancyRose = RoseAccent
val FinancyRoseSoft = RoseAccentSoft
