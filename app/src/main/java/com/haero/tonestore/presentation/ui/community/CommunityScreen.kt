package com.haero.tonestore.presentation.ui.community

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.presentation.ui.community.components.SharedPresetCard
import com.haero.tonestore.presentation.viewmodel.CommunityViewModel
import com.haero.tonestore.ui.designsystem.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: CommunityViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateToDetail) {
        state.navigateToDetail?.let { id ->
            onNavigateToDetail(id)
            viewModel.handleIntent(CommunityIntent.NavigationHandled)
        }
    }

    ObsidianBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            ObsidianCommunityHeader()

            ObsidianCommunityTabs(
                currentTab = state.currentTab,
                onTabSelected = { viewModel.handleIntent(CommunityIntent.SetTab(it)) }
            )

            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.handleIntent(CommunityIntent.RefreshPresets) },
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ObsidianLoadingIndicator()
                        }
                    }
                    state.displayedPresets.isEmpty() -> {
                        ObsidianEmptyCommunityState(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = Obsidian.spacing.screenPadding,
                                end = Obsidian.spacing.screenPadding,
                                top = Obsidian.spacing.md,
                                bottom = 120.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(Obsidian.spacing.itemGap)
                        ) {
                            items(
                                items = state.displayedPresets,
                                key = { it.id }
                            ) { preset ->
                                SharedPresetCard(
                                    preset = preset,
                                    isLiked = state.likedPresetIds.contains(preset.id),
                                    isBookmarked = state.bookmarkedPresetIds.contains(preset.id),
                                    onClick = { viewModel.handleIntent(CommunityIntent.SelectPreset(preset.id)) },
                                    onLikeClick = { viewModel.handleIntent(CommunityIntent.ToggleLike(preset.id)) },
                                    onBookmarkClick = { viewModel.handleIntent(CommunityIntent.ToggleBookmark(preset.id)) },
                                    onAuthorClick = { onNavigateToProfile(it) },
                                    modifier = Modifier.animateItem()
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
private fun ObsidianCommunityHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = Obsidian.spacing.screenPadding)
            .padding(top = Obsidian.spacing.lg, bottom = Obsidian.spacing.sm)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = Obsidian.colors.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(Obsidian.spacing.sm))
            Text(
                text = stringResource(R.string.community),
                style = Obsidian.typography.displaySmall,
                color = Obsidian.colors.textPrimary
            )
        }
        Text(
            text = stringResource(R.string.community_subtitle),
            style = Obsidian.typography.bodySmall,
            color = Obsidian.colors.textSecondary
        )
    }
}

@Composable
private fun ObsidianCommunityTabs(
    currentTab: CommunityTab,
    onTabSelected: (CommunityTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Obsidian.spacing.screenPadding, vertical = Obsidian.spacing.sm)
    ) {
        ObsidianSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Obsidian.radius.lg),
            elevation = 0.dp // 탭 영역은 깊이감보다 일체감
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CommunityTab.entries.forEach { tab ->
                    val selected = currentTab == tab
                    val color by animateColorAsState(
                        targetValue = if (selected) Obsidian.colors.primary else Obsidian.colors.textSecondary,
                        label = "tabColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Obsidian.radius.md))
                            .then(
                                if (selected) {
                                    Modifier
                                        .background(Obsidian.colors.primaryMuted)
                                        .border(
                                            1.dp,
                                            Obsidian.colors.primary.copy(alpha = 0.3f),
                                            RoundedCornerShape(Obsidian.radius.md)
                                        )
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = when (tab) {
                                    CommunityTab.LATEST -> Icons.Outlined.NewReleases
                                    CommunityTab.POPULAR -> Icons.Default.TrendingUp
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = color
                            )
                            Text(
                                text = when (tab) {
                                    CommunityTab.LATEST -> "최신"
                                    CommunityTab.POPULAR -> "인기"
                                },
                                color = color,
                                style = if (selected) Obsidian.typography.labelLarge else Obsidian.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ObsidianEmptyCommunityState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(Obsidian.spacing.xxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Obsidian.colors.surfaceHighlight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = Obsidian.colors.textMuted,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(Obsidian.spacing.xl))
        Text(
            text = stringResource(R.string.empty_presets_title),
            style = Obsidian.typography.headlineMedium,
            color = Obsidian.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(Obsidian.spacing.sm))
        Text(
            text = stringResource(R.string.empty_presets_subtitle),
            style = Obsidian.typography.bodyMedium,
            color = Obsidian.colors.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}
