package com.haero.tonestore.presentation.ui.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
    val pagerState = rememberPagerState(pageCount = { 2 })
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
        stringResource(R.string.pedal_board),
        stringResource(R.string.amp_and_guitar_setting)
    )

    Box(modifier = Modifier.fillMaxSize().background(Obsidian.colors.bgPrimary)) {
        Column(modifier = Modifier.fillMaxSize()) {
            ObsidianDetailTopBar(
                title = state.toneSetting?.songName ?: stringResource(R.string.detail),
                onBack = onNavigateBack,
                modifier = Modifier.statusBarsPadding(),
                actions = {
                    ObsidianIconButton(
                        onClick = { onNavigateToShare(toneSettingId) },
                        icon = Icons.Default.Share,
                        tint = Obsidian.colors.primaryLight
                    )
                    ObsidianIconButton(
                        onClick = { viewModel.handleIntent(DetailIntent.DuplicateToneSetting) },
                        icon = Icons.Default.ContentCopy,
                        tint = Obsidian.colors.textSecondary
                    )
                    ObsidianIconButton(
                        onClick = { viewModel.handleIntent(DetailIntent.NavigateToEdit) },
                        icon = Icons.Default.Edit,
                        tint = Obsidian.colors.textSecondary
                    )
                    ObsidianIconButton(
                        onClick = { showDeleteDialog = true },
                        icon = Icons.Default.Delete,
                        tint = Obsidian.colors.error
                    )
                }
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
                            Spacer(Modifier.height(8.dp))
                            ObsidianTabBar(
                                tabs = tabs,
                                selectedIndex = pagerState.currentPage,
                                onTabSelected = { index ->
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                            Spacer(Modifier.height(16.dp))

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
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
            snackbar = { data ->
                ObsidianSnackbar(message = data.visuals.message)
            }
        )
    }

    if (showDeleteDialog) {
        ObsidianConfirmationDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = stringResource(R.string.delete_confirm_title),
            message = stringResource(
                R.string.delete_confirm_message,
                state.toneSetting?.songName ?: ""
            ),
            confirmText = stringResource(R.string.delete),
            onConfirm = {
                viewModel.handleIntent(DetailIntent.DeleteToneSetting)
            }
        )
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val chunkedPedals = pedals.chunked(2)
        chunkedPedals.forEach { rowPedals ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
            .padding(horizontal = Obsidian.spacing.screenPadding)
            .padding(bottom = 100.dp)
    ) {
        // Amp Section
        Text(
            text = stringResource(R.string.amp_setting),
            style = Obsidian.typography.titleLarge,
            color = Obsidian.colors.primaryLight,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (!ampSetting.ampModel.isNullOrBlank()) {
            Text(
                text = ampSetting.ampModel,
                style = Obsidian.typography.headlineMedium,
                color = Obsidian.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(Obsidian.spacing.lg))
        }

        ObsidianSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Obsidian.radius.lg)
        ) {
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(Obsidian.spacing.lg),
                maxItemsInEachRow = 4,
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(24.dp)
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
                        size = 60.dp,
                        enabled = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Obsidian.spacing.xxxl))

        ObsidianDivider()

        Spacer(modifier = Modifier.height(Obsidian.spacing.xxxl))

        // Guitar Section
        Text(
            text = stringResource(R.string.guitar_setting),
            style = Obsidian.typography.titleLarge,
            color = Obsidian.colors.primaryLight,
            modifier = Modifier.padding(bottom = 12.dp)
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
        Spacer(modifier = Modifier.height(12.dp))

        ObsidianSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Obsidian.radius.lg)
        ) {
            DetailPickupSelector(
                selectedPosition = guitarSetting.pickupSelector,
                modifier = Modifier.padding(vertical = 20.dp, horizontal = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(Obsidian.spacing.xl))

        ObsidianSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Obsidian.radius.lg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(Obsidian.spacing.lg),
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ObsidianSurface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(Obsidian.radius.button),
                    elevation = if (isSelected) Obsidian.elevation.sm else 0.dp,
                    border = if (isSelected) Obsidian.colors.primaryLight else Obsidian.colors.borderSubtle
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Obsidian.colors.primary, RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = label,
                    style = Obsidian.typography.labelLarge,
                    color = if (isSelected) Obsidian.colors.primaryLight else Obsidian.colors.textMuted
                )
            }
        }
    }
}
