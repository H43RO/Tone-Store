package com.haero.tonestore.presentation.ui.preset_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.Comment
import com.haero.tonestore.ui.designsystem.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CommentItem(
    comment: Comment,
    isOwnComment: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 프로필 이미지
        if (comment.authorPhotoUrl != null) {
            AsyncImage(
                model = comment.authorPhotoUrl,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Obsidian.colors.surfaceHighlight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = comment.authorName.firstOrNull()?.uppercase() ?: "?",
                    style = Obsidian.typography.labelLarge,
                    color = Obsidian.colors.primary
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = comment.authorName.ifEmpty { "익명" },
                    style = Obsidian.typography.titleMedium,
                    color = Obsidian.colors.textPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatCommentTime(comment.createdAt),
                    style = Obsidian.typography.caption,
                    color = Obsidian.colors.textMuted
                )
                if (comment.updatedAt > comment.createdAt) {
                    Text(
                        text = stringResource(R.string.edited_suffix),
                        style = Obsidian.typography.caption,
                        color = Obsidian.colors.textMuted
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // 본인 댓글에만 메뉴 표시
                if (isOwnComment) {
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                modifier = Modifier.size(18.dp),
                                tint = Obsidian.colors.textSecondary
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            containerColor = Obsidian.colors.surfaceElevated
                        ) {
                            DropdownMenuItem(
                                text = { Text("수정", color = Obsidian.colors.textPrimary) },
                                onClick = {
                                    showMenu = false
                                    onEditClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = Obsidian.colors.textSecondary)
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "삭제",
                                        color = Obsidian.colors.error
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Obsidian.colors.error
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comment.content,
                style = Obsidian.typography.bodyMedium,
                color = Obsidian.colors.textSecondary
            )
        }
    }
}

private fun formatCommentTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "방금"
        diff < 3600_000 -> "${diff / 60_000}분 전"
        diff < 86400_000 -> "${diff / 3600_000}시간 전"
        diff < 604800_000 -> "${diff / 86400_000}일 전"
        else -> {
            val sdf = SimpleDateFormat("MM.dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
