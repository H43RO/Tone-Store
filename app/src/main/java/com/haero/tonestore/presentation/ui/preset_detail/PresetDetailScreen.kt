package com.haero.tonestore.presentation.ui.preset_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.SharedToneSetting
import com.haero.tonestore.presentation.ui.preset_detail.components.CommentInput
import com.haero.tonestore.presentation.ui.preset_detail.components.CommentItem
import com.haero.tonestore.presentation.viewmodel.PresetDetailViewModel
import com.haero.tonestore.ui.designsystem.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetDetailScreen(
    presetId: String,
    currentUserId: String?,
    onNavigateBack: () -> Unit,
    viewModel: PresetDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(presetId) {
        viewModel.handleIntent(PresetDetailIntent.LoadPreset(presetId))
    }

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            onNavigateBack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.handleIntent(PresetDetailIntent.ClearError)
        }
    }

    LaunchedEffect(state.downloadSuccess) {
        if (state.downloadSuccess) {
            snackbarHostState.showSnackbar("프리셋이 내 톤 목록에 저장되었습니다!")
            viewModel.handleIntent(PresetDetailIntent.ClearDownloadSuccess)
        }
    }

    ObsidianBackground {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = Obsidian.spacing.xs, vertical = Obsidian.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ObsidianIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onNavigateBack,
                        tint = Obsidian.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(Obsidian.spacing.sm))
                    Text(
                        text = "프리셋 상세",
                        style = Obsidian.typography.headlineMedium,
                        color = Obsidian.colors.textPrimary
                    )
                }
            },
            bottomBar = {
                CommentInput(
                    text = state.commentText,
                    onTextChange = { viewModel.handleIntent(PresetDetailIntent.UpdateCommentText(it)) },
                    onSendClick = { viewModel.handleIntent(PresetDetailIntent.SendComment) },
                    isLoading = state.isSendingComment,
                    isEditing = state.editingCommentId != null,
                    onCancelEdit = { viewModel.handleIntent(PresetDetailIntent.CancelEditComment) }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            if (state.isLoading && state.preset == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ObsidianLoadingIndicator()
                }
            } else {
                state.preset?.let { preset ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        // 프리셋 정보
                        item {
                            PresetInfoSection(
                                preset = preset,
                                isLiked = state.isLiked,
                                isDownloading = state.isDownloading,
                                onLikeClick = { viewModel.handleIntent(PresetDetailIntent.ToggleLike) },
                                onDownloadClick = { viewModel.handleIntent(PresetDetailIntent.DownloadPreset) }
                            )
                        }

                        // 구분선
                        item {
                            ObsidianDivider(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                            )
                        }

                        // 댓글 섹션 헤더
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubbleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = Obsidian.colors.textSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.comments_count, state.comments.size),
                                    style = Obsidian.typography.titleLarge,
                                    color = Obsidian.colors.textPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // 댓글 목록
                        if (state.comments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.no_comments_yet),
                                        style = Obsidian.typography.bodyMedium,
                                        color = Obsidian.colors.textMuted
                                    )
                                }
                            }
                        } else {
                            items(
                                items = state.comments,
                                key = { it.id }
                            ) { comment ->
                                CommentItem(
                                    comment = comment,
                                    isOwnComment = comment.authorId == currentUserId,
                                    onEditClick = { viewModel.handleIntent(PresetDetailIntent.EditComment(comment.id)) },
                                    onDeleteClick = { viewModel.handleIntent(PresetDetailIntent.DeleteComment(comment.id)) },
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetInfoSection(
    preset: SharedToneSetting,
    isLiked: Boolean,
    isDownloading: Boolean,
    onLikeClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp).padding(top = 8.dp)
    ) {
        // 작성자 정보
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Obsidian.colors.primary, Obsidian.colors.primaryLight)
                        )
                    )
                    .padding(2.dp)
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
                            style = Obsidian.typography.titleLarge,
                            color = Obsidian.colors.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = preset.authorName.ifEmpty { "익명" },
                    style = Obsidian.typography.titleMedium,
                    color = Obsidian.colors.textPrimary
                )
                Text(
                    text = formatDate(preset.createdAt),
                    style = Obsidian.typography.caption,
                    color = Obsidian.colors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 프리셋 제목
        Text(
            text = preset.title,
            style = Obsidian.typography.displaySmall,
            color = Obsidian.colors.textPrimary
        )

        // 원곡 이름
        Text(
            text = "🎸 ${preset.toneSetting.songName}",
            style = Obsidian.typography.headlineSmall,
            color = Obsidian.colors.primary
        )

        // 설명
        if (preset.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = preset.description,
                style = Obsidian.typography.bodyLarge,
                color = Obsidian.colors.textSecondary
            )
        }

        // 태그
        if (preset.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                preset.tags.forEach { tag ->
                    ObsidianTag(label = tag.displayNameKo)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 통계 및 액션 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 통계
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                StatItem(
                    icon = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    count = preset.likes,
                    tint = if (isLiked) Obsidian.colors.favorite else Obsidian.colors.textSecondary,
                    onClick = onLikeClick
                )
                StatItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    count = preset.commentCount,
                    tint = Obsidian.colors.textSecondary
                )
                StatItem(
                    icon = Icons.Default.Download,
                    count = preset.downloads,
                    tint = Obsidian.colors.textSecondary
                )
            }

            // 다운로드 버튼
            ObsidianButton(
                onClick = onDownloadClick,
                enabled = !isDownloading,
                isLoading = isDownloading,
                icon = Icons.Default.Download,
                modifier = Modifier.height(44.dp)
            ) {
                Text("내 톤에 저장", style = Obsidian.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    tint: androidx.compose.ui.graphics.Color,
    onClick: (() -> Unit)? = null
) {
    val modifier = if (onClick != null) {
        Modifier.clip(CircleShape).clickable { onClick() }
    } else {
        Modifier
    }

    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = tint
        )
        Text(
            text = "$count",
            style = Obsidian.typography.labelLarge,
            color = Obsidian.colors.textSecondary
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy년 MM월 dd일", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
