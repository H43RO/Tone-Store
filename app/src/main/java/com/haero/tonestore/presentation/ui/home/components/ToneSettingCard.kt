package com.haero.tonestore.presentation.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianSurface

@Composable
fun ToneSettingCard(
    toneSetting: ToneSetting,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ObsidianSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Obsidian.spacing.sm),
        onClick = onClick,
        shape = RoundedCornerShape(Obsidian.radius.card)
    ) {
        Row(
            modifier = Modifier
                .padding(Obsidian.spacing.lg)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Indicator (Neon / Amplifier look)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(Obsidian.radius.sm))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Obsidian.colors.primary.copy(alpha = 0.2f),
                                Obsidian.colors.surfaceHighlight
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    tint = Obsidian.colors.primaryLight,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(Obsidian.spacing.lg))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = toneSetting.songName,
                    style = Obsidian.typography.titleLarge,
                    color = Obsidian.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (toneSetting.tags.isNotEmpty()) {
                        toneSetting.tags.take(2).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Obsidian.colors.surfaceHighlight)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tag.displayName,
                                    style = Obsidian.typography.labelMedium,
                                    color = Obsidian.colors.textSecondary
                                )
                            }
                        }
                        if (toneSetting.tags.size > 2) {
                            Text(
                                text = "+${toneSetting.tags.size - 2}",
                                style = Obsidian.typography.labelMedium,
                                color = Obsidian.colors.textMuted
                            )
                        }
                    } else {
                        Text(
                            text = "No Tags",
                            style = Obsidian.typography.bodySmall,
                            color = Obsidian.colors.textMuted
                        )
                    }
                }
            }

            // Actions - More separated and sleek
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.height(60.dp)
            ) {
                AnimatedFavoriteIcon(
                    isFavorite = toneSetting.isFavorite,
                    onClick = onFavoriteClick
                )

                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = Obsidian.colors.textMuted,
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDeleteClick)
                )
            }
        }
    }
}

@Composable
private fun AnimatedFavoriteIcon(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else if (isFavorite) 1.1f else 1f,
        animationSpec = tween(150),
        label = "favoriteScale"
    )

    Icon(
        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
        contentDescription = stringResource(R.string.favorite),
        tint = if (isFavorite) Obsidian.colors.secondary else Obsidian.colors.textMuted,
        modifier = Modifier
            .size(24.dp)
            .scale(scale)
            .clip(CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    )
}
