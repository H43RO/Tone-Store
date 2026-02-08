package com.haero.tonestore.presentation.ui.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.ui.designsystem.Obsidian

@Composable
fun ToneSettingCard(
    toneSetting: ToneSetting,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Obsidian.colors.surfaceElevated,
            Obsidian.colors.surface
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(Obsidian.radius.lg))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .background(gradientBrush)
                .border(
                    1.dp,
                    Obsidian.colors.border.copy(alpha = 0.5f),
                    RoundedCornerShape(Obsidian.radius.lg)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual Indicator
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Obsidian.colors.primary.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.dp, Obsidian.colors.primary.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.GraphicEq,
                    contentDescription = null,
                    tint = Obsidian.colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = toneSetting.songName,
                    style = Obsidian.typography.titleMedium,
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
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Obsidian.colors.surface,
                                border = BorderStroke(1.dp, Obsidian.colors.border)
                            ) {
                                Text(
                                    text = tag.displayName,
                                    style = Obsidian.typography.labelSmall,
                                    color = Obsidian.colors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (toneSetting.tags.size > 2) {
                            Text(
                                text = "+${toneSetting.tags.size - 2}",
                                style = Obsidian.typography.labelSmall,
                                color = Obsidian.colors.textMuted
                            )
                        }
                    } else {
                        // 태그가 없을 때 표시할 내용 (선택 사항)
                        Text(
                            text = "No Tags",
                            style = Obsidian.typography.caption,
                            color = Obsidian.colors.textMuted
                        )
                    }
                }
            }

            // Actions
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (toneSetting.isFavorite) Icons.Default.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite),
                        tint = if (toneSetting.isFavorite) Obsidian.colors.error else Obsidian.colors.textMuted
                    )
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = Obsidian.colors.textMuted
                    )
                }
            }
        }
    }
}
