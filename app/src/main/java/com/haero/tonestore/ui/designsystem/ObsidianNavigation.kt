package com.haero.tonestore.ui.designsystem

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Obsidian Design System - Navigation Components (Slate Studio)
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
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(999.dp), spotColor = Obsidian.colors.primary.copy(alpha = 0.2f))
                .clip(RoundedCornerShape(999.dp))
                .background(Obsidian.colors.surfaceElevated.copy(alpha = 0.9f))
                .border(1.dp, Obsidian.colors.borderSubtle, RoundedCornerShape(999.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.primaryLight else Obsidian.colors.textMuted,
        animationSpec = tween(200),
        label = "iconColor"
    )

    val indicatorOffset by animateDpAsState(
        targetValue = if (selected) 4.dp else 12.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "indicatorOffset"
    )
    val indicatorAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(200),
        label = "indicatorAlpha"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(Obsidian.radius.md))
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
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.height(indicatorOffset))

        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(Obsidian.colors.primary.copy(alpha = indicatorAlpha))
                .shadow(8.dp, CircleShape, spotColor = Obsidian.colors.primary, ambientColor = Obsidian.colors.primary)
        )
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Obsidian.colors.bgPrimary.copy(alpha = 0.95f),
                        Obsidian.colors.bgPrimary.copy(alpha = 0f)
                    )
                )
            )
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (navigationIcon != null) {
            navigationIcon()
            Spacer(Modifier.width(16.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = Obsidian.typography.displaySmall,
                color = Obsidian.colors.textPrimary
            )
            if (subtitle != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = Obsidian.typography.bodyMedium,
                    color = Obsidian.colors.primaryLight
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ObsidianIconButton(
            onClick = onBack,
            icon = Icons.Rounded.ArrowBack,
            size = 44.dp
        )

        if (title != null) {
            Spacer(Modifier.width(16.dp))
            Text(
                text = title,
                style = Obsidian.typography.headlineMedium,
                color = Obsidian.colors.textPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Obsidian.spacing.screenPadding)
            .clip(RoundedCornerShape(999.dp))
            .background(Obsidian.colors.surfaceElevated)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
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
}

@Composable
private fun ObsidianTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.surfaceHighlight else Color.Transparent,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
        label = "bgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Obsidian.colors.textPrimary else Obsidian.colors.textSecondary,
        animationSpec = tween(150),
        label = "textColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bgColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 12.dp),
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
        targetValue = if (selected) Obsidian.colors.primaryLight else Obsidian.colors.textMuted,
        animationSpec = tween(200),
        label = "textColor"
    )
    val indicatorWidth by animateDpAsState(
        targetValue = if (selected) 24.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "indicatorWidth"
    )

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = if (selected) Obsidian.typography.labelLarge else Obsidian.typography.bodyMedium,
            color = textColor
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(indicatorWidth)
                .height(3.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(Obsidian.colors.primary)
                .shadow(4.dp, spotColor = Obsidian.colors.primary)
        )
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(Obsidian.radius.md))
            .background(if (isPressed && onClick != null) Obsidian.colors.surfaceElevated else Color.Transparent)
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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Obsidian.colors.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Obsidian.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
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
                Spacer(Modifier.height(2.dp))
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
            Spacer(Modifier.width(16.dp))
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Obsidian.colors.primary.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
                .border(1.dp, Obsidian.colors.primary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Obsidian.colors.primaryLight,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

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
                color = Obsidian.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        if (action != null) {
            Spacer(Modifier.height(32.dp))
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
            trackColor = Obsidian.colors.primary.copy(alpha = 0.2f),
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
                    .background(Obsidian.colors.bgPrimary.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Obsidian.colors.primary,
                    trackColor = Obsidian.colors.primary.copy(alpha = 0.2f),
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
