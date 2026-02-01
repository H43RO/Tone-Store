package com.haero.tonestore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.haero.tonestore.ui.designsystem.*

/**
 * Legacy Glass Components - mapped to Obsidian Design System
 *
 * These components maintain backward compatibility while using the new Obsidian theme.
 */

// ============================================================
// GLASS BUTTON (Legacy)
// ============================================================

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    ObsidianButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        isLoading = isLoading,
        icon = icon
    ) {
        Text(text)
    }
}

@Composable
fun GlassOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    ObsidianOutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        icon = icon
    ) {
        Text(text)
    }
}

@Composable
fun GlassTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    ObsidianTextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        icon = icon
    ) {
        Text(text)
    }
}

@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    tint: Color = Obsidian.colors.textSecondary
) {
    ObsidianIconButton(
        onClick = onClick,
        icon = icon,
        modifier = modifier,
        enabled = enabled,
        tint = tint
    )
}

// ============================================================
// GLASS FAB (Legacy)
// ============================================================

@Composable
fun GlassFab(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(56.dp)
            .shadow(12.dp, CircleShape, spotColor = Obsidian.colors.primary.copy(alpha = 0.3f))
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Obsidian.colors.primary, Obsidian.colors.primaryDark)
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

// ============================================================
// GLASS TEXT FIELD (Legacy)
// ============================================================

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    isError: Boolean = false
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = Obsidian.typography.labelMedium,
                color = if (isError) Obsidian.colors.error else Obsidian.colors.textSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        ObsidianTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            enabled = enabled,
            singleLine = singleLine
        )
    }
}

// ============================================================
// GLASS CHIP (Legacy)
// ============================================================

@Composable
fun GlassChip(
    text: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    ObsidianChip(
        label = text,
        modifier = modifier,
        selected = selected,
        onClick = onClick
    )
}

// ============================================================
// GLASS SWITCH (Legacy)
// ============================================================

@Composable
fun GlassSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    ObsidianSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled
    )
}
