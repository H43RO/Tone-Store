package com.haero.tonestore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.haero.tonestore.ui.designsystem.*

/**
 * Legacy Glass Components - mapped to Obsidian Design System
 */

// Backward compatibility - map to Obsidian theme
val LocalEmberGlassTheme: androidx.compose.runtime.ProvidableCompositionLocal<EmberGlassThemeCompat>
    get() = _localEmberGlassTheme

private val _localEmberGlassTheme = androidx.compose.runtime.staticCompositionLocalOf {
    EmberGlassThemeCompat()
}

data class EmberGlassThemeCompat(
    val background: Color = ObsidianColors.bgPrimary,
    val backgroundGradientEnd: Color = ObsidianColors.bgTertiary,
    val glassSurface: Color = ObsidianColors.surface,
    val glassBorder: Color = ObsidianColors.border,
    val glassBorderLight: Color = ObsidianColors.borderSubtle,
    val primary: Color = ObsidianColors.primary, // Now Tangerine #E88A3C
    val secondary: Color = ObsidianColors.secondary,
    val accent: Color = ObsidianColors.primaryLight,
    val tertiary: Color = ObsidianColors.primaryLighter,
    val textPrimary: Color = ObsidianColors.textPrimary,
    val textSecondary: Color = ObsidianColors.textSecondary,
    val textMuted: Color = ObsidianColors.textMuted,
    val success: Color = ObsidianColors.success,
    val error: Color = ObsidianColors.error,
    val warning: Color = ObsidianColors.warning,
    val surfaceElevated: Color = ObsidianColors.surfaceElevated,
    val surfaceVariant: Color = ObsidianColors.surfaceHighlight
)

/**
 * Legacy GlassBackground - uses Obsidian background
 */
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    ObsidianBackground(modifier = modifier, content = content)
}

/**
 * Legacy GlassCard - uses Obsidian surface
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    glassAlpha: Float = 0.15f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(cornerRadius), spotColor = Color.Black)
            .clip(RoundedCornerShape(cornerRadius))
            .background(ObsidianColors.surface)
            .border(1.dp, ObsidianColors.border, RoundedCornerShape(cornerRadius)),
        content = content
    )
}

/**
 * Legacy GlassSurface
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(ObsidianColors.surfaceHighlight)
            .border(1.dp, ObsidianColors.border, RoundedCornerShape(cornerRadius)),
        content = content
    )
}

/**
 * Legacy GlassCardAccent
 */
@Composable
fun GlassCardAccent(
    modifier: Modifier = Modifier,
    accentColor: Color = ObsidianColors.primary,
    cornerRadius: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(accentColor.copy(alpha = 0.15f))
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(cornerRadius)),
        content = content
    )
}

/**
 * Legacy GlassPedalSurface - preserves pedal color identity
 */
@Composable
fun GlassPedalSurface(
    modifier: Modifier = Modifier,
    pedalColor: Color,
    cornerRadius: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        pedalColor.copy(alpha = 0.35f),
                        pedalColor.copy(alpha = 0.15f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.3f),
                        pedalColor.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            ),
        content = content
    )
}

/**
 * Legacy GlassChipSurface
 */
@Composable
fun GlassChipSurface(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    accentColor: Color = ObsidianColors.primary,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) {
                    accentColor.copy(alpha = 0.15f)
                } else {
                    ObsidianColors.surfaceHighlight
                }
            )
            .border(
                width = 1.dp,
                color = if (isSelected) accentColor.copy(alpha = 0.5f) else ObsidianColors.border,
                shape = RoundedCornerShape(20.dp)
            ),
        content = content
    )
}
