package com.haero.tonestore.presentation.ui.pedalboard

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.presentation.ui.pedalboard.components.PedalBoardPreview
import com.haero.tonestore.presentation.viewmodel.PedalBoardListViewModel
import com.haero.tonestore.ui.components.GlassBackground
import com.haero.tonestore.ui.components.GlassButton
import com.haero.tonestore.ui.components.GlassCard
import com.haero.tonestore.ui.components.LocalEmberGlassTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PedalBoardListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: PedalBoardListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val theme = LocalEmberGlassTheme.current
    val listState = rememberLazyListState()

    val isScrolling by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 100 }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var pedalBoardToDelete by remember { mutableStateOf<SavedPedalBoard?>(null) }

    LaunchedEffect(state.navigateToLogin) {
        if (state.navigateToLogin) {
            onNavigateToLogin()
            viewModel.navigationHandled()
        }
    }

    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                GlassPedalBoardHeader(totalCount = state.pedalBoards.size)

                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        state.isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                color = theme.primary
                            )
                        }
                        !state.isLoggedIn -> {
                            GlassLoginRequiredState(
                                onLoginClick = { viewModel.navigateToLogin() },
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        state.pedalBoards.isEmpty() -> {
                            GlassEmptyPedalBoardState(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        else -> {
                            GlassPedalBoardList(
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

            // FAB
            if (state.isLoggedIn) {
                GlassExtendedFab(
                    expanded = isScrolling.not(),
                    onClick = onNavigateToCreate,
                    icon = Icons.Default.Add,
                    text = stringResource(R.string.create_pedalboard),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 100.dp, end = 20.dp)
                )
            }
        }
    }

    if (showDeleteDialog && pedalBoardToDelete != null) {
        val targetPedalBoard = pedalBoardToDelete ?: return@PedalBoardListScreen
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                pedalBoardToDelete = null
            },
            title = {
                Text(
                    stringResource(R.string.delete_pedalboard_confirm_title),
                    color = theme.textPrimary
                )
            },
            text = {
                Text(
                    stringResource(R.string.delete_pedalboard_confirm_message, targetPedalBoard.name),
                    color = theme.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pedalBoardToDelete?.let { viewModel.delete(it) }
                        showDeleteDialog = false
                        pedalBoardToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete), color = theme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        pedalBoardToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.cancel), color = theme.textSecondary)
                }
            },
            containerColor = theme.surfaceElevated,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun GlassPedalBoardHeader(
    totalCount: Int,
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
        Text(
            text = stringResource(R.string.pedalboard_list_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary
        )
        if (totalCount > 0) {
            Text(
                text = stringResource(R.string.pedalboards_saved_count, totalCount),
                fontSize = 14.sp,
                color = theme.textSecondary
            )
        }
    }
}

@Composable
private fun GlassEmptyPedalBoardState(modifier: Modifier = Modifier) {
    val theme = LocalEmberGlassTheme.current

    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            theme.secondary.copy(alpha = 0.2f),
                            theme.secondary.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(1.dp, theme.secondary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Dashboard,
                contentDescription = null,
                tint = theme.secondary,
                modifier = Modifier.size(56.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.empty_pedalboard_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = theme.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.empty_pedalboard_subtitle),
            fontSize = 14.sp,
            color = theme.textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(Modifier.height(64.dp))
    }
}

@Composable
private fun GlassPedalBoardList(
    pedalBoards: List<SavedPedalBoard>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onItemClick: (String) -> Unit,
    onDeleteRequest: (SavedPedalBoard) -> Unit
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = pedalBoards,
            key = { it.id }
        ) { pedalBoard ->
            GlassPedalBoardCard(
                pedalBoard = pedalBoard,
                onClick = { onItemClick(pedalBoard.id) },
                onDeleteClick = { onDeleteRequest(pedalBoard) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun GlassPedalBoardCard(
    pedalBoard: SavedPedalBoard,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current
    val pedalCount = pedalBoard.slots.count { it != null }
    val totalSlots = pedalBoard.columns * pedalBoard.rows
    val isKorean = LocalConfiguration.current.locales[0].language == "ko"

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
            PedalBoardPreview(
                slots = pedalBoard.slots,
                columns = pedalBoard.columns,
                rows = pedalBoard.rows,
                expressionPedal = pedalBoard.expressionPedal,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Glass icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(theme.secondary, theme.accent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.GridView,
                        contentDescription = null,
                        tint = theme.background,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pedalBoard.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = theme.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassMetaChip(text = "${pedalBoard.columns}×${pedalBoard.rows}")
                        GlassMetaChip(
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
                        .clickable { onDeleteClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = stringResource(R.string.delete),
                        tint = theme.textMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassMetaChip(
    text: String,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(theme.primary.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = theme.primary
        )
    }
}

@Composable
private fun GlassExtendedFab(
    expanded: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0f else 90f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(if (expanded) 24.dp else 20.dp),
                spotColor = theme.primary.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(if (expanded) 24.dp else 20.dp))
            .background(
                Brush.linearGradient(listOf(theme.primary, theme.accent))
            )
            .clickable(onClick = onClick)
            .animateContentSize(alignment = Alignment.CenterEnd)
            .padding(
                horizontal = if (expanded) 20.dp else 16.dp,
                vertical = 16.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = theme.background,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation)
            )
            if (expanded) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = theme.background
                )
            }
        }
    }
}

@Composable
private fun GlassLoginRequiredState(
    onLoginClick: () -> Unit,
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
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            theme.primary.copy(alpha = 0.25f),
                            theme.primary.copy(alpha = 0.05f)
                        )
                    )
                )
                .border(1.dp, theme.primary.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Dashboard,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = theme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.login_required),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = theme.textPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.login_required_pedalboard_message),
            fontSize = 14.sp,
            color = theme.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        GlassButton(
            text = "로그인하기",
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth(0.6f)
        )
    }
}
