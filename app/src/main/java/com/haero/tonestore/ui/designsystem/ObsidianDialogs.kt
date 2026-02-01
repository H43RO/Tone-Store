package com.haero.tonestore.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Obsidian Design System - Dialog & Modal Components
 */

// ============================================================
// DIALOG
// ============================================================

@Composable
fun ObsidianDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    confirmButton: @Composable (() -> Unit)? = null,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .shadow(Obsidian.elevation.dialog, RoundedCornerShape(Obsidian.radius.dialog))
                .clip(RoundedCornerShape(Obsidian.radius.dialog))
                .background(Obsidian.colors.surface)
                .border(1.dp, Obsidian.colors.border, RoundedCornerShape(Obsidian.radius.dialog))
        ) {
            // Header
            if (title != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Obsidian.spacing.xl,
                            end = Obsidian.spacing.md,
                            top = Obsidian.spacing.lg,
                            bottom = Obsidian.spacing.sm
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = Obsidian.typography.headlineMedium,
                        color = Obsidian.colors.textPrimary
                    )

                    ObsidianIconButton(
                        onClick = onDismissRequest,
                        icon = Icons.Rounded.Close,
                        size = 36.dp,
                        iconSize = 20.dp
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Obsidian.spacing.xl),
                content = content
            )

            // Buttons
            if (confirmButton != null || dismissButton != null) {
                Spacer(Modifier.height(Obsidian.spacing.lg))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    dismissButton?.invoke()
                    Spacer(Modifier.width(12.dp))
                    confirmButton?.invoke()
                }
            } else {
                Spacer(Modifier.height(Obsidian.spacing.xl))
            }
        }
    }
}

@Composable
fun ObsidianAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "확인",
    dismissText: String? = "취소",
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    confirmColor: Color = Obsidian.colors.primary,
    isDangerous: Boolean = false
) {
    ObsidianDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        confirmButton = {
            ObsidianButton(
                onClick = {
                    onConfirm()
                    onDismissRequest()
                },
                modifier = Modifier.height(44.dp)
            ) {
                Text(
                    text = confirmText,
                    color = if (isDangerous) Obsidian.colors.bgPrimary else Obsidian.colors.bgPrimary
                )
            }
        },
        dismissButton = if (dismissText != null) {
            {
                ObsidianOutlinedButton(
                    onClick = {
                        onDismiss?.invoke()
                        onDismissRequest()
                    },
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(dismissText)
                }
            }
        } else {
            null
        }
    ) {
        Text(
            text = message,
            style = Obsidian.typography.bodyMedium,
            color = Obsidian.colors.textSecondary
        )
    }
}

// ============================================================
// BOTTOM SHEET
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObsidianBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    showDragHandle: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(),
        shape = RoundedCornerShape(
            topStart = Obsidian.radius.xxl,
            topEnd = Obsidian.radius.xxl
        ),
        containerColor = Obsidian.colors.surface,
        contentColor = Obsidian.colors.textPrimary,
        dragHandle = if (showDragHandle) {
            {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Obsidian.colors.border)
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Obsidian.spacing.xl)
        ) {
            if (title != null) {
                Text(
                    text = title,
                    style = Obsidian.typography.headlineMedium,
                    color = Obsidian.colors.textPrimary
                )
                Spacer(Modifier.height(Obsidian.spacing.lg))
            }

            content()

            Spacer(Modifier.height(Obsidian.spacing.xxxl))
        }
    }
}

// ============================================================
// SNACKBAR
// ============================================================

@Composable
fun ObsidianSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    isError: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Obsidian.spacing.screenPadding)
            .shadow(Obsidian.elevation.lg, RoundedCornerShape(Obsidian.radius.md))
            .clip(RoundedCornerShape(Obsidian.radius.md))
            .background(if (isError) Obsidian.colors.error else Obsidian.colors.surfaceElevated)
            .border(
                1.dp,
                if (isError) Obsidian.colors.error.copy(alpha = 0.5f) else Obsidian.colors.border,
                RoundedCornerShape(Obsidian.radius.md)
            )
            .padding(Obsidian.spacing.cardPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            style = Obsidian.typography.bodyMedium,
            color = if (isError) Color.White else Obsidian.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        if (actionLabel != null && onAction != null) {
            Spacer(Modifier.width(Obsidian.spacing.md))
            Text(
                text = actionLabel,
                style = Obsidian.typography.labelLarge,
                color = if (isError) Color.White else Obsidian.colors.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(Obsidian.radius.sm))
                    .clickable(onClick = onAction)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// ============================================================
// TOOLTIP
// ============================================================

@Composable
fun ObsidianTooltip(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(Obsidian.elevation.sm, RoundedCornerShape(Obsidian.radius.sm))
            .clip(RoundedCornerShape(Obsidian.radius.sm))
            .background(Obsidian.colors.surfaceElevated)
            .border(1.dp, Obsidian.colors.border, RoundedCornerShape(Obsidian.radius.sm))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = Obsidian.typography.bodySmall,
            color = Obsidian.colors.textPrimary
        )
    }
}

// ============================================================
// SELECTION DIALOG
// ============================================================

@Composable
fun <T> ObsidianSelectionDialog(
    onDismissRequest: () -> Unit,
    title: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemContent: @Composable (T, Boolean) -> Unit
) {
    ObsidianDialog(
        onDismissRequest = onDismissRequest,
        title = title
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Obsidian.radius.md))
                        .background(
                            if (isSelected) {
                                Obsidian.colors.primaryMuted
                            } else {
                                Color.Transparent
                            }
                        )
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) Obsidian.colors.primary.copy(alpha = 0.5f) else Color.Transparent,
                            shape = RoundedCornerShape(Obsidian.radius.md)
                        )
                        .clickable { onItemSelected(item) }
                        .padding(Obsidian.spacing.cardPaddingSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemContent(item, isSelected)
                }
            }
        }

        Spacer(Modifier.height(Obsidian.spacing.md))
    }
}

// ============================================================
// CONFIRMATION POPUP (for delete, etc.)
// ============================================================

@Composable
fun ObsidianConfirmationDialog(
    onDismissRequest: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "삭제",
    onConfirm: () -> Unit
) {
    ObsidianAlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        message = message,
        confirmText = confirmText,
        onConfirm = onConfirm,
        isDangerous = true
    )
}
