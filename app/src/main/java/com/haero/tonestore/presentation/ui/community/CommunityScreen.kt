package com.haero.tonestore.presentation.ui.community

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.presentation.ui.community.components.SharedPresetCard
import com.haero.tonestore.presentation.viewmodel.CommunityViewModel
import com.haero.tonestore.ui.components.GlassBackground
import com.haero.tonestore.ui.components.GlassCard
import com.haero.tonestore.ui.components.LocalEmberGlassTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: CommunityViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = LocalEmberGlassTheme.current

    LaunchedEffect(state.navigateToDetail) {
        state.navigateToDetail?.let { id ->
            onNavigateToDetail(id)
            viewModel.handleIntent(CommunityIntent.NavigationHandled)
        }
    }

    GlassBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            GlassCommunityHeader()

            GlassCommunityTabs(
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
                            CircularProgressIndicator(color = theme.primary)
                        }
                    }
                    state.displayedPresets.isEmpty() -> {
                        GlassEmptyCommunityState(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.displayedPresets,
                                key = { it.id }
                            ) { preset ->
                                SharedPresetCard(
                                    preset = preset,
                                    isLiked = state.likedPresetIds.contains(preset.id),
                                    onClick = { viewModel.handleIntent(CommunityIntent.SelectPreset(preset.id)) },
                                    onLikeClick = { viewModel.handleIntent(CommunityIntent.ToggleLike(preset.id)) },
                                    modifier = Modifier.animateItem()
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassCommunityHeader(
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = theme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Text(
                text = stringResource(R.string.community),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = theme.textPrimary
            )
        }
        Text(
            text = stringResource(R.string.community_subtitle),
            fontSize = 14.sp,
            color = theme.textSecondary
        )
    }
}

@Composable
private fun GlassCommunityTabs(
    currentTab: CommunityTab,
    onTabSelected: (CommunityTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 16.dp,
            glassAlpha = 0.1f
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
                        targetValue = if (selected) theme.primary else theme.textSecondary,
                        label = "tabColor"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (selected) {
                                    Modifier
                                        .background(theme.primary.copy(alpha = 0.15f))
                                        .border(
                                            1.dp,
                                            theme.primary.copy(alpha = 0.3f),
                                            RoundedCornerShape(12.dp)
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
                                fontSize = 14.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassEmptyCommunityState(
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = theme.textSecondary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.empty_presets_title),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = theme.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_presets_subtitle),
            fontSize = 14.sp,
            color = theme.textSecondary,
            textAlign = TextAlign.Center
        )
    }
}
