package com.haero.tonestore.ui.theme

import androidx.compose.ui.graphics.Color
import com.haero.tonestore.ui.designsystem.ObsidianColors

/**
 * ToneStore Color System - Slate Studio
 *
 * Use ObsidianColors from designsystem package for new components.
 * This file is kept for legacy compatibility.
 */

// ═══════════════════════════════════════════════════════════════════════════════
// TONE STORE PRO AUDIO THEME COLORS (Slate Studio Trend)
// ═══════════════════════════════════════════════════════════════════════════════

// Background
val ObsidianBgPrimary = ObsidianColors.bgPrimary
val ObsidianBgSecondary = ObsidianColors.bgSecondary
val ObsidianBgTertiary = ObsidianColors.bgTertiary

// Surface
val ObsidianSurface = ObsidianColors.surface
val ObsidianSurfaceElevated = ObsidianColors.surfaceElevated
val ObsidianSurfaceHighlight = ObsidianColors.surfaceHighlight

// Border
val ObsidianBorder = ObsidianColors.border
val ObsidianBorderSubtle = ObsidianColors.borderSubtle
val ObsidianBorderFocus = ObsidianColors.borderFocus

// Primary
val ObsidianPrimary = ObsidianColors.primary
val ObsidianPrimaryLight = ObsidianColors.primaryLight
val ObsidianPrimaryLighter = ObsidianColors.primaryLighter
val ObsidianPrimaryDark = ObsidianColors.primaryDark
val ObsidianPrimaryMuted = ObsidianColors.primaryMuted

// Secondary
val ObsidianSecondary = ObsidianColors.secondary
val ObsidianSecondaryLight = ObsidianColors.secondaryLight

// Text
val ObsidianTextPrimary = ObsidianColors.textPrimary
val ObsidianTextSecondary = ObsidianColors.textSecondary
val ObsidianTextMuted = ObsidianColors.textMuted
val ObsidianTextDisabled = ObsidianColors.textDisabled

// Semantic
val ObsidianSuccess = ObsidianColors.success
val ObsidianSuccessMuted = ObsidianColors.successMuted
val ObsidianError = ObsidianColors.error
val ObsidianErrorMuted = ObsidianColors.errorMuted
val ObsidianWarning = ObsidianColors.warning
val ObsidianWarningMuted = ObsidianColors.warningMuted
val ObsidianInfo = ObsidianColors.info
val ObsidianInfoMuted = ObsidianColors.infoMuted

// Special
val ObsidianFavorite = ObsidianColors.favorite
val ObsidianFavoriteMuted = ObsidianColors.favoriteMuted

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

// Light Mode Colors (not used/updated)
val PrimaryLight = Color(0xFF5E6AD2)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE0E7FF)
val OnPrimaryContainerLight = Color(0xFF312E81)
val SecondaryLight = Color(0xFFF97316)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFFFEDD5)
val OnSecondaryContainerLight = Color(0xFF7C2D12)
val BackgroundLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF0F172A)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF0F172A)
val SurfaceVariantLight = Color(0xFFF1F5F9)
val OnSurfaceVariantLight = Color(0xFF334155)
val ErrorLight = Color(0xFFDC2626)
val OnErrorLight = Color(0xFFFFFFFF)
val OutlineLight = Color(0xFFCBD5E1)
