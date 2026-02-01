package com.haero.tonestore.presentation.ui.pedalboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.presentation.ui.pedalboard.components.PedalBoardPreview
import com.haero.tonestore.presentation.viewmodel.PedalBoardListViewModel
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianAlertDialog
import com.haero.tonestore.ui.designsystem.ObsidianBackground
import com.haero.tonestore.ui.designsystem.ObsidianButton
import com.haero.tonestore.ui.designsystem.ObsidianLoadingIndicator
import org.koin.androidx.compose.koinViewModel

@Composable
fun PedalBoardListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: PedalBoardListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var pedalBoardToDelete by remember { mutableStateOf<SavedPedalBoard?>(null) }

    LaunchedEffect(state.navigateToLogin) {
        if (state.navigateToLogin) {
            onNavigateToLogin()
            viewModel.navigationHandled()
        }
    }

    // 스크롤 방향 감지 - 스크롤 내리면 expanded, 올리면 collapsed
    var previousScrollOffset by remember { mutableIntStateOf(0) }
    var previousFirstVisibleIndex by remember { mutableIntStateOf(0) }
    val isExpanded by remember {
        derivedStateOf {
            val currentIndex = listState.firstVisibleItemIndex
            val currentOffset = listState.firstVisibleItemScrollOffset

            val isScrollingDown = when {
                currentIndex > previousFirstVisibleIndex -> true
                currentIndex < previousFirstVisibleIndex -> false
                else -> currentOffset > previousScrollOffset
            }

            previousFirstVisibleIndex = currentIndex
            previousScrollOffset = currentOffset

            isScrollingDown || currentIndex == 0
        }
    }

    ObsidianBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            // 메인 콘텐츠 Column
            Column(modifier = Modifier.fillMaxSize()) {
                ObsidianPedalBoardHeader(totalCount = state.pedalBoards.size)

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
                                onLoginClick = { viewModel.navigateToLogin() },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        state.pedalBoards.isEmpty() -> {
                            ObsidianEmptyPedalBoardState(
                                onCreateClick = onNavigateToCreate,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        else -> {
                            ObsidianPedalBoardList(
                                pedalBoards = state.pedalBoards,
                                listState = listState,
                                onItemClick = onNavigateToEdit,
                                onDeleteRequest = { pedalBoard ->
                                    pedalBoardToDelete = pedalBoard
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }

            // Expandable FAB - 스크롤 내리면 expanded, 올리면 mini
            if (state.isLoggedIn && state.pedalBoards.isNotEmpty()) {
                ObsidianExpandableFab(
                    expanded = isExpanded,
                    onClick = onNavigateToCreate,
                    label = stringResource(R.string.create_pedalboard),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 100.dp)
                        .navigationBarsPadding()
                )
            }
        }
    }

    if (showDeleteDialog && pedalBoardToDelete != null) {
        val targetPedalBoard = pedalBoardToDelete ?: return
        ObsidianAlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                pedalBoardToDelete = null
            },
            title = stringResource(R.string.delete_pedalboard_confirm_title),
            message = stringResource(R.string.delete_pedalboard_confirm_message, targetPedalBoard.name),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                pedalBoardToDelete?.let { viewModel.delete(it) }
                showDeleteDialog = false
                pedalBoardToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                pedalBoardToDelete = null
            },
            isDangerous = true
        )
    }
}

/**
 * Expandable FAB - 스크롤 방향에 따라 확장/축소
 */
@Composable
private fun ObsidianExpandableFab(
    expanded: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val fabWidth by animateDpAsState(
        targetValue = if (expanded) 170.dp else 56.dp,
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
private fun ObsidianPedalBoardHeader(
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = Obsidian.spacing.screenPadding)
            .padding(top = Obsidian.spacing.lg, bottom = Obsidian.spacing.sm)
    ) {
        Text(
            text = stringResource(R.string.pedalboard_list_title),
            style = Obsidian.typography.displaySmall,
            color = Obsidian.colors.textPrimary
        )
        if (totalCount > 0) {
            Text(
                text = stringResource(R.string.pedalboards_saved_count, totalCount),
                style = Obsidian.typography.bodySmall,
                color = Obsidian.colors.textSecondary
            )
        }
    }
}

@Composable
private fun ObsidianEmptyPedalBoardState(
    onCreateClick: () -> Unit,
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
                .border(1.dp, Obsidian.colors.primary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Dashboard,
                contentDescription = null,
                tint = Obsidian.colors.primary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(Obsidian.spacing.xxl))

        Text(
            text = stringResource(R.string.empty_pedalboard_title),
            style = Obsidian.typography.headlineMedium,
            color = Obsidian.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(Obsidian.spacing.sm))

        Text(
            text = stringResource(R.string.empty_pedalboard_subtitle),
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
            Text(stringResource(R.string.create_first_pedalboard))
        }

        Spacer(Modifier.height(64.dp))
    }
}

@Composable
private fun ObsidianPedalBoardList(
    pedalBoards: List<SavedPedalBoard>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onItemClick: (String) -> Unit,
    onDeleteRequest: (SavedPedalBoard) -> Unit
) {
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
            items = pedalBoards,
            key = { it.id }
        ) { pedalBoard ->
            ObsidianPedalBoardCard(
                pedalBoard = pedalBoard,
                onClick = { onItemClick(pedalBoard.id) },
                onDeleteClick = { onDeleteRequest(pedalBoard) }
            )
        }
    }
}

@Composable
private fun ObsidianPedalBoardCard(
    pedalBoard: SavedPedalBoard,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pedalCount = pedalBoard.slots.count { it != null }
    val totalSlots = pedalBoard.columns * pedalBoard.rows
    val isKorean = LocalConfiguration.current.locales[0].language == "ko"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(Obsidian.radius.card), spotColor = Color.Black)
            .clip(RoundedCornerShape(Obsidian.radius.card))
            .background(Obsidian.colors.surface)
            .border(1.dp, Obsidian.colors.border, RoundedCornerShape(Obsidian.radius.card))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Obsidian.spacing.cardPadding)
        ) {
            PedalBoardPreview(
                slots = pedalBoard.slots,
                columns = pedalBoard.columns,
                rows = pedalBoard.rows,
                expressionPedal = pedalBoard.expressionPedal,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Obsidian.spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(Obsidian.radius.md))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Obsidian.colors.primary, Obsidian.colors.primaryDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GridView,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(Obsidian.spacing.md))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pedalBoard.name,
                        style = Obsidian.typography.titleMedium,
                        color = Obsidian.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ObsidianMetaChip(text = "${pedalBoard.columns}×${pedalBoard.rows}")
                        ObsidianMetaChip(
                            text = if (isKorean) {
                                "$pedalCount/$totalSlots 페달"
                            } else {
                                "$pedalCount/$totalSlots pedals"
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Obsidian.colors.surfaceHighlight.copy(alpha = 0.5f))
                        .clickable { onDeleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = stringResource(R.string.delete),
                        tint = Obsidian.colors.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ObsidianMetaChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Obsidian.radius.xs))
            .background(Obsidian.colors.primaryMuted)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = Obsidian.typography.labelSmall,
            color = Obsidian.colors.primary
        )
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
            text = stringResource(R.string.login_required_pedalboard_message),
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
