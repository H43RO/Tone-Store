package com.haero.tonestore.presentation.ui.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.AmpSetting
import com.haero.tonestore.domain.model.GuitarSetting
import com.haero.tonestore.domain.model.PedalBoard
import com.haero.tonestore.domain.model.PickupPosition
import com.haero.tonestore.presentation.ui.components.RotaryKnob
import com.haero.tonestore.presentation.ui.create.components.PedalCard
import com.haero.tonestore.presentation.viewmodel.DetailViewModel
import com.haero.tonestore.ui.designsystem.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    toneSettingId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToShare: (String) -> Unit = {},
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: DetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toneSettingId) {
        viewModel.handleIntent(DetailIntent.LoadToneSetting(toneSettingId))
    }

    LaunchedEffect(state.navigateToEdit) {
        if (state.navigateToEdit) {
            onNavigateToEdit(toneSettingId)
            viewModel.handleIntent(DetailIntent.NavigationHandled)
        }
    }

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            onNavigateBack()
            viewModel.handleIntent(DetailIntent.NavigationHandled)
        }
    }

    val duplicateSuccessMessage = stringResource(R.string.duplicate_success)
    LaunchedEffect(state.showDuplicateSuccess) {
        if (state.showDuplicateSuccess) {
            snackbarHostState.showSnackbar(duplicateSuccessMessage)
            viewModel.handleIntent(DetailIntent.ClearDuplicateSuccess)
        }
    }

    val tabs = listOf(
        TabItem(stringResource(R.string.pedal_board), Icons.Default.GraphicEq),
        TabItem(stringResource(R.string.amp_and_guitar_setting), Icons.Default.Speaker)
    )

    ObsidianBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                DetailHeader(
                    title = state.toneSetting?.songName ?: stringResource(R.string.detail),
                    onBackClick = onNavigateBack,
                    onEditClick = { viewModel.handleIntent(DetailIntent.NavigateToEdit) },
                    onDuplicateClick = { viewModel.handleIntent(DetailIntent.DuplicateToneSetting) },
                    onShareClick = { onNavigateToShare(toneSettingId) },
                    onDeleteClick = { showDeleteDialog = true }
                )

                Box(modifier = Modifier.weight(1f)) {
                    when {
                        state.isLoading -> {
                            Box(modifier = Modifier.align(Alignment.Center)) {
                                ObsidianLoadingIndicator()
                            }
                        }
                        state.toneSetting != null -> {
                            val toneSetting = state.toneSetting ?: return@Box

                            Column(modifier = Modifier.fillMaxSize()) {
                                DetailTabBar(
                                    tabs = tabs,
                                    selectedIndex = pagerState.currentPage,
                                    onTabSelected = { index ->
                                        scope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                                )

                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier.fillMaxSize()
                                ) { page ->
                                    when (page) {
                                        0 -> DetailPedalBoardContent(pedalBoard = toneSetting.pedalBoard)
                                        1 -> DetailAmpAndGuitarContent(
                                            ampSetting = toneSetting.ampSetting,
                                            guitarSetting = toneSetting.guitarSetting
                                        )
                                    }
                                }
                            }
                        }
                        state.error != null -> {
                            Text(
                                text = state.error ?: stringResource(R.string.error_occurred),
                                style = Obsidian.typography.bodyLarge,
                                color = Obsidian.colors.error,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
            )
        }
    }

    if (showDeleteDialog) {
        ObsidianAlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = stringResource(R.string.delete_confirm_title),
            message = stringResource(
                R.string.delete_confirm_message,
                state.toneSetting?.songName ?: ""
            ),
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel),
            onConfirm = {
                viewModel.handleIntent(DetailIntent.DeleteToneSetting)
            },
            isDangerous = true
        )
    }
}

@Composable
private fun DetailHeader(
    title: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDuplicateClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ObsidianIconButton(
            onClick = onBackClick,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            tint = Obsidian.colors.textPrimary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = Obsidian.typography.headlineMedium,
            color = Obsidian.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            ObsidianIconButton(
                onClick = onShareClick,
                icon = Icons.Default.Share,
                tint = Obsidian.colors.primary
            )

            ObsidianIconButton(
                onClick = onDuplicateClick,
                icon = Icons.Default.ContentCopy,
                tint = Obsidian.colors.textSecondary
            )

            ObsidianIconButton(
                onClick = onEditClick,
                icon = Icons.Default.Edit,
                tint = Obsidian.colors.textSecondary
            )

            ObsidianIconButton(
                onClick = onDeleteClick,
                icon = Icons.Default.Delete,
                tint = Obsidian.colors.error
            )
        }
    }
}

private data class TabItem(
    val title: String,
    val icon: ImageVector
)

@Composable
private fun DetailTabBar(
    tabs: List<TabItem>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ObsidianSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Obsidian.radius.lg),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedIndex
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) Obsidian.colors.primaryMuted else Color.Transparent,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "tabBackground"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) Obsidian.colors.primary else Obsidian.colors.textSecondary,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "tabContent"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(Obsidian.radius.md))
                        .background(backgroundColor)
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    1.dp,
                                    Obsidian.colors.primary.copy(alpha = 0.3f),
                                    RoundedCornerShape(Obsidian.radius.md)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onTabSelected(index) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = tab.title,
                            style = if (isSelected) Obsidian.typography.labelLarge else Obsidian.typography.bodyMedium,
                            color = contentColor,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailPedalBoardContent(
    pedalBoard: PedalBoard
) {
    if (pedalBoard.pedals.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_pedals),
                style = Obsidian.typography.bodyLarge,
                color = Obsidian.colors.textMuted
            )
        }
    } else {
        GridPedalView(pedals = pedalBoard.pedals)
    }
}

@Composable
private fun GridPedalView(
    pedals: List<com.haero.tonestore.domain.model.Pedal>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Obsidian.spacing.screenPadding, vertical = Obsidian.spacing.md),
        verticalArrangement = Arrangement.spacedBy(Obsidian.spacing.itemGap)
    ) {
        val chunkedPedals = pedals.chunked(2)
        chunkedPedals.forEach { rowPedals ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(androidx.compose.foundation.layout.IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(Obsidian.spacing.itemGap)
            ) {
                rowPedals.forEach { pedal ->
                    PedalCard(
                        pedal = pedal,
                        onKnobChange = { _, _ -> },
                        onToggleEnabled = {},
                        onRemove = {},
                        isEditable = false,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }

                if (rowPedals.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun DetailAmpAndGuitarContent(
    ampSetting: AmpSetting,
    guitarSetting: GuitarSetting
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Obsidian.spacing.screenPadding)
    ) {
        // Amp Section
        Text(
            text = stringResource(R.string.amp_setting),
            style = Obsidian.typography.titleLarge,
            color = Obsidian.colors.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (!ampSetting.ampModel.isNullOrBlank()) {
            Text(
                text = ampSetting.ampModel,
                style = Obsidian.typography.headlineMedium,
                color = Obsidian.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(Obsidian.spacing.lg))
        }

        ObsidianCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                listOf(
                    "Gain" to ampSetting.gain,
                    "Bass" to ampSetting.bass,
                    "Middle" to ampSetting.middle,
                    "Treble" to ampSetting.treble,
                    "Presence" to ampSetting.presence,
                    "Reverb" to ampSetting.reverb,
                    "Master" to ampSetting.masterVolume
                ).forEach { (label, value) ->
                    RotaryKnob(
                        value = value,
                        onValueChange = {},
                        label = label,
                        size = 64.dp,
                        enabled = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Obsidian.spacing.xxl))

        ObsidianDivider()

        Spacer(modifier = Modifier.height(Obsidian.spacing.xxl))

        // Guitar Section
        Text(
            text = stringResource(R.string.guitar_setting),
            style = Obsidian.typography.titleLarge,
            color = Obsidian.colors.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (!guitarSetting.guitarModel.isNullOrBlank()) {
            Text(
                text = guitarSetting.guitarModel,
                style = Obsidian.typography.headlineMedium,
                color = Obsidian.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(Obsidian.spacing.lg))
        }

        Text(
            text = stringResource(R.string.pickup_selector),
            style = Obsidian.typography.titleMedium,
            color = Obsidian.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(Obsidian.spacing.md))

        ObsidianSurface(
            modifier = Modifier.fillMaxWidth(),
            elevation = 0.dp
        ) {
            DetailPickupSelector(
                selectedPosition = guitarSetting.pickupSelector,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(Obsidian.spacing.xl))

        ObsidianCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RotaryKnob(
                    value = guitarSetting.volumeKnob,
                    onValueChange = {},
                    label = "Volume",
                    size = 72.dp,
                    enabled = false
                )
                RotaryKnob(
                    value = guitarSetting.toneKnob,
                    onValueChange = {},
                    label = "Tone",
                    size = 72.dp,
                    enabled = false
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun DetailPickupSelector(
    selectedPosition: PickupPosition,
    modifier: Modifier = Modifier
) {
    val positions = listOf(
        PickupPosition.NECK to "N",
        PickupPosition.NECK_MIDDLE to "N+M",
        PickupPosition.MIDDLE to "M",
        PickupPosition.MIDDLE_BRIDGE to "M+B",
        PickupPosition.BRIDGE to "B"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        positions.forEach { (position, label) ->
            val isSelected = selectedPosition == position

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Obsidian.colors.primary else Obsidian.colors.bgSecondary
                        )
                        .border(
                            width = 2.dp,
                            color = if (isSelected) Obsidian.colors.primary else Obsidian.colors.border,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Obsidian.colors.bgPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = label,
                    style = if (isSelected) Obsidian.typography.labelLarge else Obsidian.typography.labelSmall,
                    color = if (isSelected) Obsidian.colors.primary else Obsidian.colors.textMuted,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
