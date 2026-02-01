package com.haero.tonestore.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Obsidian Design System - Navigation Components
 */

// ============================================================
// BOTTOM NAVIGATION
// ============================================================

data class ObsidianNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@Composable
fun ObsidianBottomNavigation(
    items: List<ObsidianNavItem>,
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        ObsidianSurface(
            shape = RoundedCornerShape(Obsidian.radius.bottomNav),
            elevation = Obsidian.elevation.bottomNav
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    ObsidianNavItemView(
                        item = item,
                        selected = item.route == selectedRoute,
                        onClick = { onItemSelected(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ObsidianNavItemView(
    item: ObsidianNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.primaryMuted else Color.Transparent,
        animationSpec = tween(200),
        label = "bgColor"
    )
    val iconColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.primary else Obsidian.colors.textMuted,
        animationSpec = tween(200),
        label = "iconColor"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(Obsidian.radius.md))
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
        )
        if (selected) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.label,
                style = Obsidian.typography.labelSmall,
                color = Obsidian.colors.primary
            )
        }
    }
}

// ============================================================
// TOP APP BAR
// ============================================================

@Composable
fun ObsidianTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Obsidian.spacing.screenPadding,
                vertical = Obsidian.spacing.md
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (navigationIcon != null) {
            navigationIcon()
            Spacer(Modifier.width(12.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Obsidian.typography.displaySmall,
                color = Obsidian.colors.textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = Obsidian.typography.bodySmall,
                    color = Obsidian.colors.textSecondary
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
}

@Composable
fun ObsidianDetailTopBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = Obsidian.spacing.screenPadding,
                vertical = Obsidian.spacing.md
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ObsidianIconButton(
            onClick = onBack,
            icon = Icons.Rounded.ArrowBack,
            size = 40.dp
        )

        if (title != null) {
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = Obsidian.typography.headlineMedium,
                color = Obsidian.colors.textPrimary,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(Modifier.weight(1f))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
}

// ============================================================
// TAB BAR
// ============================================================

@Composable
fun ObsidianTabBar(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Obsidian.spacing.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            ObsidianTab(
                label = tab,
                selected = index == selectedIndex,
                onClick = { onTabSelected(index) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ObsidianTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.primary else Obsidian.colors.surface,
        animationSpec = tween(200),
        label = "bgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.bgPrimary else Obsidian.colors.textSecondary,
        animationSpec = tween(200),
        label = "textColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Obsidian.radius.full))
            .background(bgColor)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) Color.Transparent else Obsidian.colors.border,
                shape = RoundedCornerShape(Obsidian.radius.full)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = Obsidian.typography.labelMedium,
            color = textColor
        )
    }
}

@Composable
fun ObsidianUnderlineTabBar(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Obsidian.spacing.screenPadding),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        tabs.forEachIndexed { index, tab ->
            ObsidianUnderlineTab(
                label = tab,
                selected = index == selectedIndex,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
private fun ObsidianUnderlineTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val textColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.primary else Obsidian.colors.textMuted,
        animationSpec = tween(200),
        label = "textColor"
    )

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = if (selected) Obsidian.typography.labelLarge else Obsidian.typography.bodyMedium,
            color = textColor
        )
        Spacer(Modifier.height(6.dp))
        if (selected) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Obsidian.colors.primary)
            )
        } else {
            Spacer(Modifier.height(2.dp))
        }
    }
}

// ============================================================
// LIST ITEM
// ============================================================

@Composable
fun ObsidianListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = Obsidian.spacing.screenPadding,
                vertical = Obsidian.spacing.md
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingContent != null) {
            leadingContent()
            Spacer(Modifier.width(16.dp))
        } else if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Obsidian.colors.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Obsidian.typography.titleMedium,
                color = Obsidian.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = Obsidian.typography.bodySmall,
                    color = Obsidian.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (trailingContent != null) {
            Spacer(Modifier.width(12.dp))
            trailingContent()
        }
    }
}

// ============================================================
// EMPTY STATE
// ============================================================

@Composable
fun ObsidianEmptyState(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    action: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Obsidian.spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Obsidian.colors.surfaceHighlight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Obsidian.colors.textMuted,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = title,
            style = Obsidian.typography.headlineSmall,
            color = Obsidian.colors.textPrimary
        )

        if (description != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = description,
                style = Obsidian.typography.bodyMedium,
                color = Obsidian.colors.textSecondary
            )
        }

        if (action != null) {
            Spacer(Modifier.height(24.dp))
            action()
        }
    }
}

// ============================================================
// LOADING
// ============================================================

@Composable
fun ObsidianLoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Obsidian.colors.primary,
            strokeWidth = 3.dp,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun ObsidianLoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Obsidian.colors.bgPrimary.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Obsidian.colors.primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
