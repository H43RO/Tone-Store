package com.haero.tonestore.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Obsidian Design System - Core Components (Slate Studio)
 */

// ============================================================
// SURFACE / CARD
// ============================================================

@Composable
fun ObsidianSurface(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(Obsidian.radius.card),
    elevation: Dp = Obsidian.elevation.card,
    hasBorder: Boolean = true,
    border: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val actualBorderColor = border ?: Obsidian.colors.primary.copy(alpha = 0.1f)

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = shape,
                spotColor = Obsidian.colors.primary.copy(alpha = 0.3f),
                ambientColor = Obsidian.colors.primary.copy(alpha = 0.05f)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Obsidian.colors.surfaceHighlight.copy(alpha = 0.3f),
                        Obsidian.colors.surface
                    )
                )
            )
            .then(
                if (hasBorder) {
                    Modifier.border(1.dp, actualBorderColor, shape)
                } else {
                    Modifier
                }
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            ),
        content = content
    )
}

@Composable
fun ObsidianConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    isDestructive: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Obsidian.colors.bgSecondary,
        shape = RoundedCornerShape(Obsidian.radius.lg),
        title = {
            Text(
                text = title,
                style = Obsidian.typography.titleLarge,
                color = Obsidian.colors.textPrimary
            )
        },
        text = {
            Text(
                text = message,
                style = Obsidian.typography.bodyMedium,
                color = Obsidian.colors.textSecondary
            )
        },
        confirmButton = {
            ObsidianTextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = confirmText,
                    color = if (isDestructive) Obsidian.colors.error else Obsidian.colors.primaryLight
                )
            }
        },
        dismissButton = {
            ObsidianTextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = dismissText,
                    color = Obsidian.colors.textSecondary
                )
            }
        }
    )
}

@Composable
fun ObsidianCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ObsidianSurface(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(Obsidian.spacing.cardPadding),
            content = content
        )
    }
}

@Composable
fun ObsidianCardSmall(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ObsidianSurface(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(Obsidian.spacing.cardPaddingSmall),
            content = content
        )
    }
}

// ============================================================
// BACKGROUND
// ============================================================

@Composable
fun ObsidianBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Obsidian.colors.bgPrimary)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Obsidian.colors.primary.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    radius = 1200f
                )
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Obsidian.colors.bgSecondary.copy(alpha = 0.8f),
                        Obsidian.colors.bgTertiary
                    )
                )
            ),
        content = content
    )
}

// ============================================================
// BUTTONS
// ============================================================

@Composable
fun ObsidianButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val backgroundModifier = if (enabled) {
        Modifier.background(
            Brush.horizontalGradient(
                colors = listOf(Obsidian.colors.primaryDark, Obsidian.colors.primaryLight)
            )
        )
    } else {
        Modifier.background(Obsidian.colors.surfaceElevated)
    }

    Box(
        modifier = modifier
            .scale(scale)
            .height(52.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(Obsidian.radius.button),
                spotColor = Obsidian.colors.primary.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(Obsidian.radius.button))
            .then(backgroundModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Obsidian.colors.textPrimary,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) Obsidian.colors.textPrimary else Obsidian.colors.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                ProvideTextStyle(
                    value = Obsidian.typography.labelLarge.copy(
                        color = if (enabled) Obsidian.colors.textPrimary else Obsidian.colors.textMuted
                    )
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ObsidianOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val borderColor = when {
        !enabled -> Obsidian.colors.border
        isPressed -> Obsidian.colors.primaryLight
        else -> Obsidian.colors.primary.copy(alpha = 0.5f)
    }
    val textColor = when {
        !enabled -> Obsidian.colors.textMuted
        else -> Obsidian.colors.primaryLight
    }

    Box(
        modifier = modifier
            .scale(scale)
            .height(52.dp)
            .clip(RoundedCornerShape(Obsidian.radius.button))
            .background(if (isPressed) Obsidian.colors.primary.copy(alpha = 0.1f) else Color.Transparent)
            .border(1.5.dp, borderColor, RoundedCornerShape(Obsidian.radius.button))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            ProvideTextStyle(
                value = Obsidian.typography.labelLarge.copy(color = textColor)
            ) {
                content()
            }
        }
    }
}

@Composable
fun ObsidianTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val textColor = when {
        !enabled -> Obsidian.colors.textMuted
        else -> Obsidian.colors.primaryLight
    }

    Row(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(Obsidian.radius.sm))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
        }
        ProvideTextStyle(
            value = Obsidian.typography.labelLarge.copy(color = textColor)
        ) {
            content()
        }
    }
}

@Composable
fun ObsidianIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Obsidian.colors.textSecondary,
    size: Dp = 48.dp,
    iconSize: Dp = 24.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(size)
            .clip(CircleShape)
            .background(if (isPressed) Obsidian.colors.surfaceHighlight else Obsidian.colors.surface)
            .border(1.dp, Obsidian.colors.borderSubtle, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) tint else Obsidian.colors.textDisabled,
            modifier = Modifier.size(iconSize)
        )
    }
}

// ============================================================
// TEXT FIELDS
// ============================================================

@Composable
fun ObsidianTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Obsidian.colors.primary else Obsidian.colors.border,
        animationSpec = tween(200),
        label = "borderColor"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isFocused) 8f else 0f,
        animationSpec = tween(200),
        label = "elevation"
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(56.dp)
            .shadow(elevation.dp, RoundedCornerShape(Obsidian.radius.input), spotColor = Obsidian.colors.primary.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(Obsidian.radius.input))
            .background(Obsidian.colors.surface)
            .border(
                width = if (isFocused) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(Obsidian.radius.input)
            )
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused },
        enabled = enabled,
        singleLine = singleLine,
        textStyle = Obsidian.typography.bodyLarge.copy(color = Obsidian.colors.textPrimary),
        cursorBrush = SolidColor(Obsidian.colors.primary),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isFocused) Obsidian.colors.primary else Obsidian.colors.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                }

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = Obsidian.typography.bodyLarge,
                            color = Obsidian.colors.textMuted
                        )
                    }
                    innerTextField()
                }

                trailingIcon?.invoke()
            }
        }
    )
}

@Composable
fun ObsidianSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search tones...",
    onClear: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Obsidian.colors.primary.copy(alpha = 0.5f) else Obsidian.colors.border,
        label = "borderColor"
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(Obsidian.radius.full))
            .background(Obsidian.colors.surfaceElevated)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(Obsidian.radius.full)
            )
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused },
        singleLine = true,
        textStyle = Obsidian.typography.bodyLarge.copy(color = Obsidian.colors.textPrimary),
        cursorBrush = SolidColor(Obsidian.colors.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null,
                    tint = if (isFocused) Obsidian.colors.primary else Obsidian.colors.textMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = Obsidian.typography.bodyLarge,
                            color = Obsidian.colors.textMuted
                        )
                    }
                    innerTextField()
                }

                if (value.isNotEmpty() && onClear != null) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Clear",
                            tint = Obsidian.colors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ObsidianPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Password"
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    ObsidianTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { isPasswordVisible = !isPasswordVisible },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                    contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                    tint = Obsidian.colors.textMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}

// ============================================================
// CHIPS / TAGS
// ============================================================

@Composable
fun ObsidianChip(
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    leadingIcon: ImageVector? = null
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.primary.copy(alpha = 0.15f) else Obsidian.colors.surfaceHighlight,
        label = "bgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.primaryLight else Obsidian.colors.textSecondary,
        label = "textColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.primary.copy(alpha = 0.4f) else Color.Transparent,
        label = "borderColor"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Obsidian.radius.chip))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(Obsidian.radius.chip))
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = Obsidian.typography.labelMedium,
            color = textColor
        )
    }
}

@Composable
fun ObsidianTag(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = Obsidian.colors.primary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Obsidian.radius.chip))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(Obsidian.radius.chip))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = Obsidian.typography.labelSmall,
            color = color
        )
    }
}

// ============================================================
// TOGGLE / SWITCH
// ============================================================

@Composable
fun ObsidianSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val trackColor = if (checked) Obsidian.colors.primary else Obsidian.colors.surfaceHighlight
    val thumbColor = if (checked) Obsidian.colors.textPrimary else Obsidian.colors.textMuted

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = thumbColor,
            checkedTrackColor = trackColor,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = thumbColor,
            uncheckedTrackColor = trackColor,
            uncheckedBorderColor = Obsidian.colors.border
        )
    )
}

@Composable
fun ObsidianCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val bgColor = if (checked) Obsidian.colors.primary else Color.Transparent
    val borderColor = if (checked) Obsidian.colors.primary else Obsidian.colors.border

    Box(
        modifier = modifier
            .size(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Obsidian.colors.bgPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ============================================================
// DIVIDER
// ============================================================

@Composable
fun ObsidianDivider(
    modifier: Modifier = Modifier,
    color: Color = Obsidian.colors.border
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(color)
    )
}

// ============================================================
// SECTION HEADER
// ============================================================

@Composable
fun ObsidianSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Obsidian.spacing.screenPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = Obsidian.typography.titleLarge,
                color = Obsidian.colors.textPrimary
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = Obsidian.typography.bodySmall,
                    color = Obsidian.colors.textSecondary
                )
            }
        }
        action?.invoke()
    }
}
