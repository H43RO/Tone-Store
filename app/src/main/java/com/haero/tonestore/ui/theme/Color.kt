package com.haero.tonestore.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ToneStore Color System - Obsidian Copper
 *
 * Use ObsidianColors from designsystem package for new components.
 * This file is kept for legacy compatibility.
 */

// ═══════════════════════════════════════════════════════════════════════════════
// OBSIDIAN COPPER THEME COLORS
// ═══════════════════════════════════════════════════════════════════════════════

// Background (Deep dark)
val ObsidianBgPrimary = Color(0xFF0C0C0E)
val ObsidianBgSecondary = Color(0xFF141416)
val ObsidianBgTertiary = Color(0xFF1A1A1F)

// Surface
val ObsidianSurface = Color(0xFF1C1C21)
val ObsidianSurfaceElevated = Color(0xFF232328)
val ObsidianSurfaceHighlight = Color(0xFF2A2A30)

// Border
val ObsidianBorder = Color(0xFF2A2A30)
val ObsidianBorderSubtle = Color(0xFF222226)
val ObsidianBorderFocus = Color(0xFFBF5A36)

// Primary (Copper)
val ObsidianPrimary = Color(0xFFBF5A36)
val ObsidianPrimaryLight = Color(0xFFD4714A)
val ObsidianPrimaryLighter = Color(0xFFE89B77)
val ObsidianPrimaryDark = Color(0xFF9A4829)
val ObsidianPrimaryMuted = Color(0x26BF5A36)

// Secondary
val ObsidianSecondary = Color(0xFF8B7355)
val ObsidianSecondaryLight = Color(0xFFA68B6A)

// Text
val ObsidianTextPrimary = Color(0xFFF4F4F5)
val ObsidianTextSecondary = Color(0xFFA1A1AA)
val ObsidianTextMuted = Color(0xFF52525B)
val ObsidianTextDisabled = Color(0xFF3F3F46)

// Semantic
val ObsidianSuccess = Color(0xFF10B981)
val ObsidianSuccessMuted = Color(0x2610B981)
val ObsidianError = Color(0xFFEF4444)
val ObsidianErrorMuted = Color(0x26EF4444)
val ObsidianWarning = Color(0xFFF59E0B)
val ObsidianWarningMuted = Color(0x26F59E0B)
val ObsidianInfo = Color(0xFF3B82F6)
val ObsidianInfoMuted = Color(0x263B82F6)

// Special
val ObsidianFavorite = Color(0xFFB91C1C)
val ObsidianFavoriteMuted = Color(0x26B91C1C)

// ═══════════════════════════════════════════════════════════════════════════════
// Legacy Material3 colors (for compatibility)
// ═══════════════════════════════════════════════════════════════════════════════

val PrimaryDark = ObsidianPrimary
val OnPrimaryDark = ObsidianBgPrimary
val PrimaryContainerDark = ObsidianPrimaryMuted
val OnPrimaryContainerDark = ObsidianPrimaryLight

val SecondaryDark = ObsidianSecondary
val OnSecondaryDark = ObsidianBgPrimary
val SecondaryContainerDark = ObsidianSurfaceHighlight
val OnSecondaryContainerDark = ObsidianSecondaryLight

val BackgroundDark = ObsidianBgPrimary
val OnBackgroundDark = ObsidianTextPrimary
val SurfaceDark = ObsidianSurface
val OnSurfaceDark = ObsidianTextPrimary
val SurfaceVariantDark = ObsidianSurfaceElevated
val OnSurfaceVariantDark = ObsidianTextSecondary

val ErrorDark = ObsidianError
val OnErrorDark = ObsidianBgPrimary
val OutlineDark = ObsidianBorder

// Legacy Ember colors (mapped to Obsidian for compatibility)
val EmberBackground = ObsidianBgPrimary
val EmberBackgroundGradientEnd = ObsidianBgTertiary
val EmberGlassSurface = ObsidianSurface
val EmberGlassBorder = ObsidianBorder
val EmberGlassBorderLight = ObsidianBorderSubtle
val EmberPrimary = ObsidianPrimary
val EmberSecondary = ObsidianSecondary
val EmberAccent = ObsidianPrimaryLight
val EmberTertiary = ObsidianPrimaryLighter
val EmberTextPrimary = ObsidianTextPrimary
val EmberTextSecondary = ObsidianTextSecondary
val EmberTextMuted = ObsidianTextMuted
val EmberSuccess = ObsidianSuccess
val EmberError = ObsidianError
val EmberWarning = ObsidianWarning
val EmberSurfaceElevated = ObsidianSurfaceElevated
val EmberSurfaceVariant = ObsidianSurfaceHighlight

// Light Mode Colors (not used)
val PrimaryLight = Color(0xFFBF5A36)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFFFDBD1)
val OnPrimaryContainerLight = Color(0xFF3A0A00)
val SecondaryLight = Color(0xFF8B7355)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFFFDBCE)
val OnSecondaryContainerLight = Color(0xFF3A0A00)
val BackgroundLight = Color(0xFFFFFBFF)
val OnBackgroundLight = Color(0xFF201A18)
val SurfaceLight = Color(0xFFFFFBFF)
val OnSurfaceLight = Color(0xFF201A18)
val SurfaceVariantLight = Color(0xFFF5DED6)
val OnSurfaceVariantLight = Color(0xFF53433E)
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val OutlineLight = Color(0xFF85736D)
