package com.haero.tonestore.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.haero.tonestore.ui.designsystem.LocalObsidianTheme
import com.haero.tonestore.ui.designsystem.ObsidianColors
import com.haero.tonestore.ui.designsystem.ObsidianThemeData

/**
 * ToneStore Theme - Obsidian Copper
 */

private val DarkColorScheme = darkColorScheme(
    primary = ObsidianColors.primary,
    onPrimary = ObsidianColors.bgPrimary,
    primaryContainer = ObsidianColors.primaryMuted,
    onPrimaryContainer = ObsidianColors.primaryLight,
    secondary = ObsidianColors.secondary,
    onSecondary = ObsidianColors.bgPrimary,
    secondaryContainer = ObsidianColors.surfaceHighlight,
    onSecondaryContainer = ObsidianColors.secondaryLight,
    background = ObsidianColors.bgPrimary,
    onBackground = ObsidianColors.textPrimary,
    surface = ObsidianColors.surface,
    onSurface = ObsidianColors.textPrimary,
    surfaceVariant = ObsidianColors.surfaceElevated,
    onSurfaceVariant = ObsidianColors.textSecondary,
    error = ObsidianColors.error,
    onError = ObsidianColors.bgPrimary,
    outline = ObsidianColors.border
)

@Composable
fun ToneStoreTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val obsidianTheme = ObsidianThemeData()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = ObsidianColors.bgPrimary.toArgb()
            window.navigationBarColor = ObsidianColors.bgPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(LocalObsidianTheme provides obsidianTheme) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
