package com.haero.tonestore.presentation.ui.community.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.haero.tonestore.domain.model.SharedToneSetting
import com.haero.tonestore.ui.designsystem.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SharedPresetCard(
    preset: SharedToneSetting,
    isLiked: Boolean,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ObsidianCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 작성자 정보
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAuthorClick(preset.authorId) }
            ) {
                // 프로필 이미지 with gradient border
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Obsidian.colors.primary, Obsidian.colors.primaryLight)
                            )
                        )
                        .padding(1.5.dp)
                ) {
                    if (preset.authorPhotoUrl != null) {
                        AsyncImage(
                            model = preset.authorPhotoUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Obsidian.colors.bgSecondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset.authorName.firstOrNull()?.uppercase() ?: "?",
                                style = Obsidian.typography.labelLarge,
                                color = Obsidian.colors.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = preset.authorName.ifEmpty { "익명" },
                        style = Obsidian.typography.titleMedium,
                        color = Obsidian.colors.textPrimary
                    )
                    Text(
                        text = formatRelativeTime(preset.createdAt),
                        style = Obsidian.typography.caption,
                        color = Obsidian.colors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 프리셋 제목
            Text(
                text = preset.title,
                style = Obsidian.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Obsidian.colors.textPrimary
            )

            // 원곡 이름
            Text(
                text = "🎸 ${preset.toneSetting.songName}",
                style = Obsidian.typography.bodySmall,
                color = Obsidian.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 설명
            if (preset.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = preset.description,
                    style = Obsidian.typography.bodyMedium,
                    color = Obsidian.colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 태그
            if (preset.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    preset.tags.take(3).forEach { tag ->
                        ObsidianTag(label = tag.displayNameKo)
                    }
                    if (preset.tags.size > 3) {
                        Text(
                            text = "+${preset.tags.size - 3}",
                            style = Obsidian.typography.caption,
                            color = Obsidian.colors.textMuted,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 액션 영역 (좋아요, 북마크, 댓글, 다운로드)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 좋아요
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onLikeClick() }
                            .padding(4.dp)
                    ) {
                        val scale = animateFloatAsState(
                            targetValue = if (isLiked) 1.2f else 1f,
                            animationSpec = androidx.compose.animation.core.spring(
                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                            ),
                            label = "LikeScale"
                        )

                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Obsidian.colors.favorite else Obsidian.colors.textMuted,
                            modifier = Modifier
                                .size(18.dp)
                                .scale(scale.value)
                        )
                        Text(
                            text = "${preset.likes}",
                            style = Obsidian.typography.labelMedium,
                            color = if (isLiked) Obsidian.colors.favorite else Obsidian.colors.textMuted
                        )
                    }

                    // 북마크
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onBookmarkClick() }
                            .padding(4.dp)
                    ) {
                        val scale = animateFloatAsState(
                            targetValue = if (isBookmarked) 1.2f else 1f,
                            animationSpec = androidx.compose.animation.core.spring(
                                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                                stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                            ),
                            label = "BookmarkScale"
                        )

                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) Obsidian.colors.primary else Obsidian.colors.textMuted,
                            modifier = Modifier
                                .size(18.dp)
                                .scale(scale.value)
                        )
                    }

                    // 댓글
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = Obsidian.colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${preset.commentCount}",
                            style = Obsidian.typography.labelMedium,
                            color = Obsidian.colors.textMuted
                        )
                    }

                    // 다운로드
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Downloads",
                            tint = Obsidian.colors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${preset.downloads}",
                            style = Obsidian.typography.labelMedium,
                            color = Obsidian.colors.textMuted
                        )
                    }
                }
            }
        }
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "방금 전"
        diff < 3600_000 -> "${diff / 60_000}분 전"
        diff < 86400_000 -> "${diff / 3600_000}시간 전"
        diff < 604800_000 -> "${diff / 86400_000}일 전"
        else -> {
            val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
