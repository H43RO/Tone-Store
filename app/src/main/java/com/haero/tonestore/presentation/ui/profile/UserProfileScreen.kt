package com.haero.tonestore.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.haero.tonestore.presentation.ui.community.components.SharedPresetCard
import com.haero.tonestore.presentation.viewmodel.UserProfileViewModel
import com.haero.tonestore.ui.components.GlassBackground
import com.haero.tonestore.ui.components.GlassCard
import com.haero.tonestore.ui.components.LocalEmberGlassTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserProfileScreen(
    userId: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: UserProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = LocalEmberGlassTheme.current

    LaunchedEffect(userId) {
        viewModel.handleIntent(UserProfileIntent.LoadProfile(userId))
    }

    LaunchedEffect(state.navigateToDetail) {
        state.navigateToDetail?.let { id ->
            onNavigateToDetail(id)
            viewModel.handleIntent(UserProfileIntent.NavigationHandled)
        }
    }

    GlassBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = theme.textPrimary
                    )
                }
            }

            if (state.isLoading && state.profile == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = theme.primary)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    item {
                        UserProfileHeader(state)
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "공유한 톤 세팅",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = theme.textPrimary,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }

                    if (state.presets.isEmpty() && !state.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "공유한 프리셋이 없습니다.",
                                    color = theme.textSecondary
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
    val theme = LocalEmberGlassTheme.current
    val profile = state.profile ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(theme.primary, theme.accent))
                )
                .padding(3.dp)
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
                        .background(theme.surfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.displayName.firstOrNull()?.uppercase() ?: "?",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = theme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = profile.displayName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            StatItem(
                label = "좋아요",
                value = profile.totalLikes.toString(),
                icon = Icons.Default.Favorite
            )
            Spacer(modifier = Modifier.size(40.dp))
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
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val theme = LocalEmberGlassTheme.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        GlassCard(
            modifier = Modifier.size(60.dp),
            cornerRadius = 16.dp,
            glassAlpha = 0.1f
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = theme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = theme.textSecondary
        )
    }
}
