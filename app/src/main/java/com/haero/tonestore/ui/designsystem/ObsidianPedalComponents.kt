package com.haero.tonestore.ui.designsystem

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Obsidian Design System - Pedal & Tone Specific Components
 */

// ============================================================
// ROTARY KNOB
// ============================================================

@Composable
fun ObsidianRotaryKnob(
    value: Float,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    enabled: Boolean = true,
    onValueChange: ((Float) -> Unit)? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .drawBehind {
                    // Track (background arc)
                    drawArc(
                        color = ObsidianColors.border,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Value arc
                    drawArc(
                        color = if (enabled) ObsidianColors.primary else ObsidianColors.textMuted,
                        startAngle = 135f,
                        sweepAngle = value * 270f,
                        useCenter = false,
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Knob body
                    drawCircle(
                        brush = Brush.verticalGradient(
                            listOf(ObsidianColors.surfaceElevated, ObsidianColors.surface)
                        ),
                        radius = this.size.minDimension / 2 - 6.dp.toPx()
                    )

                    // Outer ring
                    drawCircle(
                        color = ObsidianColors.border,
                        radius = this.size.minDimension / 2 - 6.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Indicator line
                    val angle = 135 + (value * 270)
                    val rad = angle * PI.toFloat() / 180f
                    val innerRadius = 8.dp.toPx()
                    val outerRadius = this.size.minDimension / 2 - 12.dp.toPx()
                    val startX = center.x + cos(rad) * innerRadius
                    val startY = center.y + sin(rad) * innerRadius
                    val endX = center.x + cos(rad) * outerRadius
                    val endY = center.y + sin(rad) * outerRadius
                    drawLine(
                        color = if (enabled) ObsidianColors.primary else ObsidianColors.textMuted,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 2.5.dp.toPx()
                    )

                    // Center dot
                    drawCircle(
                        color = if (enabled) ObsidianColors.primary else ObsidianColors.textMuted,
                        radius = 3.dp.toPx()
                    )
                }
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = label,
            style = Obsidian.typography.labelSmall,
            color = if (enabled) Obsidian.colors.textSecondary else Obsidian.colors.textMuted
        )
    }
}

@Composable
fun ObsidianKnobWithValue(
    value: Float,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    showValue: Boolean = true,
    valueFormatter: (Float) -> String = { "${(it * 10).toInt()}" },
    enabled: Boolean = true,
    onValueChange: ((Float) -> Unit)? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ObsidianRotaryKnob(
            value = value,
            label = "",
            size = size,
            enabled = enabled,
            onValueChange = onValueChange
        )

        if (showValue) {
            Text(
                text = valueFormatter(value),
                style = Obsidian.typography.labelMedium,
                color = if (enabled) Obsidian.colors.primary else Obsidian.colors.textMuted,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = label,
            style = Obsidian.typography.caption,
            color = Obsidian.colors.textMuted
        )
    }
}

// ============================================================
// PEDAL CARD
// ============================================================

@Composable
fun ObsidianPedalCard(
    name: String,
    abbreviation: String,
    pedalColor: Color,
    isOn: Boolean,
    modifier: Modifier = Modifier,
    knobs: List<Pair<String, Float>> = emptyList(),
    onClick: (() -> Unit)? = null,
    onToggle: ((Boolean) -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.98f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    // 페달 색상에 약간의 어두운 톤을 더해 Obsidian 테마와 조화
    val adjustedColor = pedalColor.copy(alpha = 0.9f)
    val darkerColor = Color(
        red = (pedalColor.red * 0.7f),
        green = (pedalColor.green * 0.7f),
        blue = (pedalColor.blue * 0.7f)
    )

    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(Obsidian.radius.lg))
            .background(
                Brush.verticalGradient(
                    listOf(adjustedColor, darkerColor)
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.Black.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(Obsidian.radius.lg)
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
            )
            .padding(12.dp)
    ) {
        // Top row: LED + Name
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LED indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (isOn) {
                            Obsidian.colors.success
                        } else {
                            Obsidian.colors.error.copy(alpha = 0.4f)
                        }
                    )
                    .then(
                        if (isOn) {
                            Modifier.drawBehind {
                                drawCircle(
                                    color = ObsidianColors.success.copy(alpha = 0.4f),
                                    radius = size.minDimension
                                )
                            }
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onToggle?.invoke(!isOn) }
            )

            // ON/OFF text
            Text(
                text = if (isOn) "ON" else "OFF",
                style = Obsidian.typography.labelSmall,
                color = if (isOn) Obsidian.colors.success else Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        // Pedal name
        Text(
            text = name,
            style = Obsidian.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Knobs
        if (knobs.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                knobs.take(4).forEach { (label, value) ->
                    ObsidianPedalKnob(
                        value = value,
                        label = label,
                        size = 36.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun ObsidianPedalKnob(
    value: Float,
    label: String,
    size: Dp = 36.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .drawBehind {
                    // Knob body
                    drawCircle(
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.3f), Color.White.copy(alpha = 0.1f))
                        ),
                        radius = this.size.minDimension / 2 - 2.dp.toPx()
                    )

                    // Outer ring
                    drawCircle(
                        color = Color.White.copy(alpha = 0.4f),
                        radius = this.size.minDimension / 2 - 2.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Indicator
                    val angle = 135 + (value * 270)
                    val rad = angle * PI.toFloat() / 180f
                    val indicatorLength = this.size.minDimension / 2 - 8.dp.toPx()
                    val startX = center.x + cos(rad) * 4.dp.toPx()
                    val startY = center.y + sin(rad) * 4.dp.toPx()
                    val endX = center.x + cos(rad) * indicatorLength
                    val endY = center.y + sin(rad) * indicatorLength
                    drawLine(
                        color = Color.White,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 2.dp.toPx()
                    )
                }
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = label,
            style = Obsidian.typography.caption,
            color = Color.White.copy(alpha = 0.8f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ============================================================
// MINI PEDAL (for lists)
// ============================================================

@Composable
fun ObsidianMiniPedal(
    name: String,
    abbreviation: String,
    pedalColor: Color,
    modifier: Modifier = Modifier,
    isOn: Boolean = true
) {
    val adjustedColor = pedalColor.copy(alpha = if (isOn) 0.9f else 0.5f)

    Box(
        modifier = modifier
            .size(width = 28.dp, height = 32.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(adjustedColor)
            .border(
                0.5.dp,
                Color.White.copy(alpha = 0.2f),
                RoundedCornerShape(4.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = abbreviation.take(2).uppercase(),
            style = Obsidian.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ObsidianPedalIconsRow(
    pedals: List<Triple<String, String, Color>>, // name, abbreviation, color
    modifier: Modifier = Modifier,
    maxDisplay: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        pedals.take(maxDisplay).forEach { (name, abbr, color) ->
            ObsidianMiniPedal(
                name = name,
                abbreviation = abbr,
                pedalColor = color
            )
        }

        if (pedals.size > maxDisplay) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Obsidian.colors.surfaceHighlight)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "+${pedals.size - maxDisplay}",
                    style = Obsidian.typography.labelSmall,
                    color = Obsidian.colors.textMuted
                )
            }
        }
    }
}

// ============================================================
// TONE CARD
// ============================================================

@Composable
fun ObsidianToneCard(
    songName: String,
    artistName: String,
    modifier: Modifier = Modifier,
    pedals: List<Triple<String, String, Color>> = emptyList(),
    tags: List<String> = emptyList(),
    isFavorite: Boolean = false,
    onClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {}
) {
    ObsidianCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(Obsidian.radius.md))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Obsidian.colors.primary,
                                Obsidian.colors.primaryDark
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = songName,
                    style = Obsidian.typography.titleMedium,
                    color = Obsidian.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = artistName,
                    style = Obsidian.typography.bodySmall,
                    color = Obsidian.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (pedals.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    ObsidianPedalIconsRow(pedals = pedals)
                }

                if (tags.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        tags.take(3).forEach { tag ->
                            ObsidianTag(label = tag)
                        }
                    }
                }
            }

            // Favorite button
            ObsidianFavoriteButton(
                isFavorite = isFavorite,
                onClick = onFavoriteClick
            )
        }
    }
}

// ============================================================
// FAVORITE BUTTON
// ============================================================

@Composable
fun ObsidianFavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(100),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(size)
            .clip(CircleShape)
            .background(
                if (isFavorite) {
                    Obsidian.colors.favoriteMuted
                } else {
                    Obsidian.colors.surfaceHighlight.copy(alpha = 0.5f)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorite) Obsidian.colors.favorite else Obsidian.colors.textMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

// ============================================================
// STAT ITEM
// ============================================================

@Composable
fun ObsidianStatItem(
    icon: ImageVector,
    count: Int,
    modifier: Modifier = Modifier,
    tint: Color = Obsidian.colors.textMuted,
    isActive: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive) tint else Obsidian.colors.textMuted,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = count.toString(),
            style = Obsidian.typography.labelMedium,
            color = Obsidian.colors.textSecondary
        )
    }
}

// ============================================================
// PROFILE AVATAR
// ============================================================

@Composable
fun ObsidianAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    imageUrl: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Obsidian.colors.primary, Obsidian.colors.primaryDark)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.take(1).uppercase(),
            style = Obsidian.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ObsidianAvatarWithBorder(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    borderWidth: Dp = 2.dp,
    imageUrl: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(Obsidian.colors.primary, Obsidian.colors.primaryLight)
                )
            )
            .padding(borderWidth)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Obsidian.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = Obsidian.typography.titleMedium,
                color = Obsidian.colors.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
