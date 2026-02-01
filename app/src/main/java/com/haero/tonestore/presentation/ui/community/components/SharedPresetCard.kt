package com.haero.tonestore.presentation.ui.community.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.haero.tonestore.domain.model.GenreTag
import com.haero.tonestore.domain.model.SharedToneSetting
import com.haero.tonestore.ui.components.GlassCard
import com.haero.tonestore.ui.components.LocalEmberGlassTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SharedPresetCard(
    preset: SharedToneSetting,
    isLiked: Boolean,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 20.dp,
        glassAlpha = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 작성자 정보
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 프로필 이미지 with gradient border
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(theme.primary, theme.accent))
                        )
                        .padding(2.dp)
                ) {
                    if (preset.authorPhotoUrl != null) {
                        AsyncImage(
                            model = preset.authorPhotoUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape)
                                .background(theme.surfaceElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset.authorName.firstOrNull()?.uppercase() ?: "?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = theme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = preset.authorName.ifEmpty { "익명" },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = theme.textPrimary
                    )
                    Text(
                        text = formatRelativeTime(preset.createdAt),
                        fontSize = 12.sp,
                        color = theme.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 프리셋 제목
            Text(
                text = preset.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = theme.textPrimary
            )

            // 원곡 이름
            Text(
                text = "🎸 ${preset.toneSetting.songName}",
                fontSize = 14.sp,
                color = theme.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 설명
            if (preset.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = preset.description,
                    fontSize = 14.sp,
                    color = theme.textSecondary,
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
                        GlassTagChip(tag = tag)
                    }
                    if (preset.tags.size > 3) {
                        Text(
                            text = "+${preset.tags.size - 3}",
                            fontSize = 12.sp,
                            color = theme.textSecondary,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 좋아요, 댓글, 다운로드 수
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 좋아요
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onLikeClick() }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) theme.secondary else theme.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${preset.likes}",
                            fontSize = 13.sp,
                            color = if (isLiked) theme.secondary else theme.textSecondary
                        )
                    }

                    // 댓글
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = theme.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${preset.commentCount}",
                            fontSize = 13.sp,
                            color = theme.textSecondary
                        )
                    }

                    // 다운로드
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Downloads",
                            tint = theme.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${preset.downloads}",
                            fontSize = 13.sp,
                            color = theme.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassTagChip(
    tag: GenreTag,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(theme.primary.copy(alpha = 0.15f))
            .border(1.dp, theme.primary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = tag.displayNameKo,
            fontSize = 11.sp,
            color = theme.primary
        )
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
