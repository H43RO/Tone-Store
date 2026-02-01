package com.haero.tonestore.presentation.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.presentation.ui.home.components.GridToneSettingCard
import com.haero.tonestore.presentation.ui.home.components.SortFilterBar
import com.haero.tonestore.presentation.ui.home.components.ToneSettingCard
import com.haero.tonestore.presentation.viewmodel.HomeViewModel
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianAlertDialog
import com.haero.tonestore.ui.designsystem.ObsidianBackground
import com.haero.tonestore.ui.designsystem.ObsidianButton
import com.haero.tonestore.ui.designsystem.ObsidianIconButton
import com.haero.tonestore.ui.designsystem.ObsidianLoadingIndicator
import com.haero.tonestore.ui.designsystem.ObsidianOutlinedButton
import com.haero.tonestore.ui.designsystem.ObsidianSearchField
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToLogin: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(state.navigateToCreate) {
        if (state.navigateToCreate) {
            onNavigateToCreate()
            viewModel.handleIntent(HomeIntent.NavigationHandled)
        }
    }

    LaunchedEffect(state.navigateToDetail) {
        state.navigateToDetail?.let { id ->
            onNavigateToDetail(id)
            viewModel.handleIntent(HomeIntent.NavigationHandled)
        }
    }

    LaunchedEffect(state.navigateToLogin) {
        if (state.navigateToLogin) {
            onNavigateToLogin()
            viewModel.handleIntent(HomeIntent.NavigationHandled)
        }
    }

    LaunchedEffect(state.scrollToTop) {
        if (state.scrollToTop) {
            if (listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0) {
                listState.animateScrollToItem(0)
            }
            viewModel.handleIntent(HomeIntent.ScrollToTopHandled)
        }
    }

    // 스크롤하면 축소, 멈추거나 맨 위면 확장
    val isExpanded by remember {
        derivedStateOf {
            // 스크롤 위치가 거의 맨 위일 때만 expanded
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 50
        }
    }

    ObsidianBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // 메인 콘텐츠 Column
            Column(modifier = Modifier.fillMaxSize()) {
                ObsidianHomeHeader(
                    isSearchActive = state.isSearchActive,
                    searchQuery = state.searchQuery,
                    totalCount = state.toneSettings.size,
                    onSearchQueryChange = { viewModel.handleIntent(HomeIntent.UpdateSearchQuery(it)) },
                    onSearchActiveChange = { viewModel.handleIntent(HomeIntent.SetSearchActive(it)) }
                )

                if (state.toneSettings.isNotEmpty()) {
                    SortFilterBar(
                        viewMode = state.viewMode,
                        sortOption = state.sortOption,
                        onViewModeChange = { viewModel.handleIntent(HomeIntent.SetViewMode(it)) },
                        onSortOptionChange = { viewModel.handleIntent(HomeIntent.SetSortOption(it)) }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    when {
                        state.isLoading -> {
                            ObsidianLoadingIndicator()
                        }
                        !state.isLoggedIn -> {
                            ObsidianLoginRequiredState(
                                onLoginClick = { viewModel.handleIntent(HomeIntent.NavigateToLogin) },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        state.filteredToneSettings.isEmpty() && state.searchQuery.isNotBlank() -> {
                            ObsidianEmptySearchState(
                                onClearSearch = {
                                    viewModel.handleIntent(HomeIntent.UpdateSearchQuery(""))
                                },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        state.toneSettings.isEmpty() -> {
                            ObsidianEmptyToneState(
                                onCreateClick = {
                                    viewModel.handleIntent(HomeIntent.NavigateToCreate)
                                },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        else -> {
                            Crossfade(
                                targetState = state.viewMode,
                                label = "viewModeTransition",
                                animationSpec = tween(durationMillis = 300)
                            ) { viewMode ->
                                when (viewMode) {
                                    ViewMode.LIST -> {
                                        ToneSettingList(
                                            toneSettings = state.filteredToneSettings,
                                            listState = listState,
                                            onItemClick = { id ->
                                                viewModel.handleIntent(HomeIntent.SelectToneSetting(id))
                                            },
                                            onDelete = { id ->
                                                viewModel.handleIntent(HomeIntent.DeleteToneSetting(id))
                                            },
                                            onFavoriteClick = { id ->
                                                viewModel.handleIntent(HomeIntent.ToggleFavorite(id))
                                            }
                                        )
                                    }
                                    ViewMode.GRID -> {
                                        ToneSettingGrid(
                                            toneSettings = state.filteredToneSettings,
                                            onItemClick = { id ->
                                                viewModel.handleIntent(HomeIntent.SelectToneSetting(id))
                                            },
                                            onFavoriteClick = { id ->
                                                viewModel.handleIntent(HomeIntent.ToggleFavorite(id))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Expandable FAB - 스크롤 내리면 expanded, 올리면 mini
            if (state.isLoggedIn && state.toneSettings.isNotEmpty()) {
                ObsidianExpandableFab(
                    expanded = isExpanded,
                    onClick = { viewModel.handleIntent(HomeIntent.NavigateToCreate) },
                    label = stringResource(R.string.add_tone_setting),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 100.dp)
                        .navigationBarsPadding()
                )
            }
        }
    }
}

/**
 * Expandable FAB - 스크롤 방향에 따라 확장/축소
 * expanded = true: 라벨 표시 (Extended FAB)
 * expanded = false: 아이콘만 (Mini FAB)
 */
@Composable
private fun ObsidianExpandableFab(
    expanded: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val fabWidth by animateDpAsState(
        targetValue = if (expanded) 160.dp else 56.dp,
        animationSpec = spring(stiffness = 400f),
        label = "fabWidth"
    )

    Box(
        modifier = modifier
            .width(fabWidth)
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Obsidian.colors.primary.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Obsidian.colors.primary,
                        Obsidian.colors.primaryDark
                    )
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = label,
                        style = Obsidian.typography.labelLarge,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ObsidianHomeHeader(
    isSearchActive: Boolean,
    searchQuery: String,
    totalCount: Int,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.app_name),
                    style = Obsidian.typography.displaySmall,
                    color = Obsidian.colors.textPrimary
                )
                if (totalCount > 0 && !isSearchActive) {
                    Text(
                        text = stringResource(R.string.tones_saved_count, totalCount),
                        style = Obsidian.typography.bodySmall,
                        color = Obsidian.colors.textSecondary
                    )
                }
            }

            ObsidianIconButton(
                onClick = { onSearchActiveChange(!isSearchActive) },
                icon = if (isSearchActive) Icons.Rounded.Close else Icons.Rounded.Search,
                tint = if (isSearchActive) Obsidian.colors.primary else Obsidian.colors.textSecondary
            )
        }

        AnimatedVisibility(
            visible = isSearchActive,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(isSearchActive) {
                if (isSearchActive) {
                    focusRequester.requestFocus()
                }
            }

            Box(modifier = Modifier.padding(top = Obsidian.spacing.lg)) {
                ObsidianSearchField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = stringResource(R.string.search_hint),
                    onClear = { onSearchQueryChange("") }
                )
            }
        }
    }
}

@Composable
private fun ObsidianEmptyToneState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(durationMillis = 600)) +
            slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(durationMillis = 600)
            ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(Obsidian.spacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Obsidian.colors.primaryMuted)
                    .border(1.dp, Obsidian.colors.primary.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = Obsidian.colors.primary,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(Obsidian.spacing.xxl))

            Text(
                text = stringResource(R.string.empty_state_title_v2),
                style = Obsidian.typography.headlineMedium,
                color = Obsidian.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(Obsidian.spacing.sm))

            Text(
                text = stringResource(R.string.empty_state_subtitle_v2),
                style = Obsidian.typography.bodyMedium,
                color = Obsidian.colors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Obsidian.spacing.xxl))

            ObsidianButton(
                onClick = onCreateClick,
                icon = Icons.Rounded.Add,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(stringResource(R.string.add_first_tone))
            }

            Spacer(Modifier.height(64.dp))
        }
    }
}

@Composable
private fun ObsidianEmptySearchState(
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(durationMillis = 400)) +
            slideInVertically(
                initialOffsetY = { it / 4 },
                animationSpec = tween(durationMillis = 400)
            ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(Obsidian.spacing.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Obsidian.colors.surfaceHighlight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SearchOff,
                    contentDescription = null,
                    tint = Obsidian.colors.textMuted,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(Obsidian.spacing.xl))

            Text(
                text = stringResource(R.string.no_results_found),
                style = Obsidian.typography.headlineSmall,
                color = Obsidian.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(Obsidian.spacing.xs))

            Text(
                text = stringResource(R.string.empty_search_subtitle_v2),
                style = Obsidian.typography.bodyMedium,
                color = Obsidian.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(Obsidian.spacing.xl))

            ObsidianOutlinedButton(
                onClick = onClearSearch,
                icon = Icons.Rounded.Close
            ) {
                Text(stringResource(R.string.clear_search_button))
            }
        }
    }
}

@Composable
private fun ObsidianLoginRequiredState(
    onLoginClick: () -> Unit,
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
                .background(Obsidian.colors.primaryMuted)
                .border(1.dp, Obsidian.colors.primary.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Obsidian.colors.primary
            )
        }

        Spacer(modifier = Modifier.height(Obsidian.spacing.xxl))

        Text(
            text = stringResource(R.string.login_required),
            style = Obsidian.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = Obsidian.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(Obsidian.spacing.sm))

        Text(
            text = stringResource(R.string.login_required_tone_message),
            style = Obsidian.typography.bodyMedium,
            color = Obsidian.colors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Obsidian.spacing.xxl))

        ObsidianButton(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text(stringResource(R.string.login_button))
        }
    }
}

@Composable
private fun ToneSettingList(
    toneSettings: List<ToneSetting>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onItemClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(
            start = Obsidian.spacing.screenPadding,
            end = Obsidian.spacing.screenPadding,
            top = Obsidian.spacing.md,
            bottom = 120.dp // NavBar 영역까지 스크롤 가능
        ),
        verticalArrangement = Arrangement.spacedBy(Obsidian.spacing.itemGap)
    ) {
        items(
            items = toneSettings,
            key = { it.id }
        ) { toneSetting ->
            ToneSettingCard(
                modifier = Modifier.animateItem(),
                toneSetting = toneSetting,
                onClick = { onItemClick(toneSetting.id) },
                onFavoriteClick = { onFavoriteClick(toneSetting.id) },
                onDeleteClick = { showDeleteDialog = toneSetting.id },
                sharedElementKey = toneSetting.id
            )
        }
    }

    showDeleteDialog?.let { id ->
        val toneSetting = toneSettings.find { it.id == id }
        ObsidianAlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = stringResource(R.string.delete_confirm_title),
            message = stringResource(
                R.string.delete_confirm_message,
                toneSetting?.songName ?: ""
            ),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = { onDelete(id) },
            onDismiss = { showDeleteDialog = null },
            isDangerous = true
        )
    }
}

@Composable
private fun ToneSettingGrid(
    toneSettings: List<ToneSetting>,
    onItemClick: (String) -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        state = gridState,
        contentPadding = PaddingValues(
            start = Obsidian.spacing.screenPadding,
            end = Obsidian.spacing.screenPadding,
            top = Obsidian.spacing.md,
            bottom = 120.dp // NavBar 영역까지 스크롤 가능
        ),
        horizontalArrangement = Arrangement.spacedBy(Obsidian.spacing.itemGap),
        verticalArrangement = Arrangement.spacedBy(Obsidian.spacing.itemGap)
    ) {
        items(
            items = toneSettings,
            key = { it.id }
        ) { toneSetting ->
            GridToneSettingCard(
                toneSetting = toneSetting,
                onClick = { onItemClick(toneSetting.id) },
                onFavoriteClick = { onFavoriteClick(toneSetting.id) },
                sharedElementKey = toneSetting.id
            )
        }
    }
}
