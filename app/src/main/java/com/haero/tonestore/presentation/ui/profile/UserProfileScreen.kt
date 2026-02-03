package com.haero.tonestore.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haero.tonestore.presentation.ui.community.components.SharedPresetCard
import com.haero.tonestore.presentation.viewmodel.UserProfileViewModel
import com.haero.tonestore.ui.designsystem.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserProfileScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: UserProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.handleIntent(UserProfileIntent.LoadProfile(userId))
    }

    LaunchedEffect(state.navigateToDetail) {
        state.navigateToDetail?.let { id ->
            onNavigateToDetail(id)
            viewModel.handleIntent(UserProfileIntent.NavigationHandled)
        }
    }

    ObsidianBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Obsidian.spacing.xs, vertical = Obsidian.spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ObsidianIconButton(
                    onClick = onNavigateBack,
                    icon = Icons.Default.ArrowBack,
                    tint = Obsidian.colors.textPrimary
                )

                Spacer(modifier = Modifier.width(Obsidian.spacing.sm))

                Text(
                    text = "프로필",
                    style = Obsidian.typography.headlineMedium,
                    color = Obsidian.colors.textPrimary
                )
            }

            if (state.isLoading && state.profile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ObsidianLoadingIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(Obsidian.spacing.md)
                ) {
                    item {
                        UserProfileHeader(state)
                    }

                    item {
                        Spacer(modifier = Modifier.height(Obsidian.spacing.lg))
                        Text(
                            text = "공유한 톤 세팅",
                            style = Obsidian.typography.headlineSmall,
                            color = Obsidian.colors.textPrimary,
                            modifier = Modifier.padding(horizontal = Obsidian.spacing.xxl)
                        )
                    }

                    items(state.presets) { preset ->
                        SharedPresetCard(
                            preset = preset,
                            isLiked = state.likedPresetIds.contains(preset.id),
                            isBookmarked = state.bookmarkedPresetIds.contains(preset.id),
                            onClick = { viewModel.handleIntent(UserProfileIntent.SelectPreset(preset.id)) },
                            onLikeClick = { viewModel.handleIntent(UserProfileIntent.ToggleLike(preset.id)) },
                            onBookmarkClick = { viewModel.handleIntent(UserProfileIntent.ToggleBookmark(preset.id)) },
                            onAuthorClick = { }, // Already on profile
                            modifier = Modifier.padding(horizontal = Obsidian.spacing.xxl)
                        )
                    }

                    if (state.presets.isEmpty() && !state.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "공유한 프리셋이 없습니다.",
                                    style = Obsidian.typography.bodyMedium,
                                    color = Obsidian.colors.textSecondary
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
fun UserProfileHeader(state: UserProfileState) {
    val profile = state.profile ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Obsidian.spacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(Obsidian.colors.primary, Obsidian.colors.primaryLight)
                    )
                )
                .padding(2.dp)
        ) {
            if (profile.photoUrl != null) {
                AsyncImage(
                    model = profile.photoUrl,
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
                        text = profile.displayName.firstOrNull()?.uppercase() ?: "?",
                        style = Obsidian.typography.displaySmall,
                        color = Obsidian.colors.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Obsidian.spacing.lg))

        Text(
            text = profile.displayName,
            style = Obsidian.typography.displaySmall,
            color = Obsidian.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(Obsidian.spacing.xxl))

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Obsidian.spacing.xl, Alignment.CenterHorizontally)
        ) {
            StatItem(
                label = "좋아요",
                value = profile.totalLikes.toString(),
                icon = Icons.Default.Favorite
            )
            StatItem(
                label = "공유",
                value = profile.sharedPresetsCount.toString(),
                icon = Icons.Default.GridView
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ObsidianSurface(
            modifier = Modifier.size(64.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(Obsidian.radius.lg),
            elevation = Obsidian.elevation.sm
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Obsidian.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(Obsidian.spacing.sm))
        Text(
            text = value,
            style = Obsidian.typography.titleLarge,
            color = Obsidian.colors.textPrimary
        )
        Text(
            text = label,
            style = Obsidian.typography.caption,
            color = Obsidian.colors.textSecondary
        )
    }
}
