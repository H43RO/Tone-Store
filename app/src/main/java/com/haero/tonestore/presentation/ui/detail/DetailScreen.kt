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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    toneSettingId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: DetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

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

    val tabs = listOf(
        TabItem(stringResource(R.string.pedal_board), Icons.Default.GraphicEq),
        TabItem(stringResource(R.string.amp_setting), Icons.Default.Speaker),
        TabItem(stringResource(R.string.guitar_setting), Icons.Default.MusicNote)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DetailHeader(
                title = state.toneSetting?.songName ?: stringResource(R.string.detail),
                onBackClick = onNavigateBack,
                onEditClick = { viewModel.handleIntent(DetailIntent.NavigateToEdit) },
                onDeleteClick = { showDeleteDialog = true }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    state.toneSetting != null -> {
                        val toneSetting = state.toneSetting!!

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
                                    1 -> DetailAmpContent(ampSetting = toneSetting.ampSetting)
                                    2 -> DetailGuitarContent(guitarSetting = toneSetting.guitarSetting)
                                }
                            }
                        }
                    }
                    state.error != null -> {
                        Text(
                            text = state.error ?: stringResource(R.string.error_occurred),
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.delete_confirm_message,
                        state.toneSetting?.songName ?: ""
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.handleIntent(DetailIntent.DeleteToneSetting)
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun DetailHeader(
    title: String,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
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
        Surface(
            onClick = onBackClick,
            shape = CircleShape,
            color = Color.Transparent,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Surface(
            onClick = onEditClick,
            shape = CircleShape,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            onClick = onDeleteClick,
            shape = CircleShape,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = index == selectedIndex
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    Color.Transparent
                },
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "tabBackground"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "tabContent"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
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
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = contentColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailPedalBoardContent(
    pedalBoard: PedalBoard
) {
    if (pedalBoard.pedals.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_pedals),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pedalBoard.pedals.chunked(2).forEach { rowPedals ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowPedals.forEach { pedal ->
                        PedalCard(
                            pedal = pedal,
                            onKnobChange = { _, _ -> },
                            onToggleEnabled = {},
                            onRemove = {},
                            isEditable = false,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowPedals.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailAmpContent(
    ampSetting: AmpSetting
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (ampSetting.ampModel.isNullOrBlank().not()) {
            Text(
                text = ampSetting.ampModel!!,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(20.dp)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                RotaryKnob(
                    value = ampSetting.gain,
                    onValueChange = {},
                    label = "Gain",
                    size = 68.dp,
                    enabled = false
                )
                RotaryKnob(
                    value = ampSetting.bass,
                    onValueChange = {},
                    label = "Bass",
                    size = 68.dp,
                    enabled = false
                )
                RotaryKnob(
                    value = ampSetting.middle,
                    onValueChange = {},
                    label = "Middle",
                    size = 68.dp,
                    enabled = false
                )
                RotaryKnob(
                    value = ampSetting.treble,
                    onValueChange = {},
                    label = "Treble",
                    size = 68.dp,
                    enabled = false
                )
                RotaryKnob(
                    value = ampSetting.presence,
                    onValueChange = {},
                    label = "Presence",
                    size = 68.dp,
                    enabled = false
                )
                RotaryKnob(
                    value = ampSetting.reverb,
                    onValueChange = {},
                    label = "Reverb",
                    size = 68.dp,
                    enabled = false
                )
                RotaryKnob(
                    value = ampSetting.masterVolume,
                    onValueChange = {},
                    label = "Master",
                    size = 68.dp,
                    enabled = false
                )
            }
        }
    }
}

@Composable
private fun DetailGuitarContent(
    guitarSetting: GuitarSetting
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        if (guitarSetting.guitarModel.isNullOrBlank().not()) {
            Text(
                text = guitarSetting.guitarModel!!,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = stringResource(R.string.pickup_selector),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        DetailPickupSelector(
            selectedPosition = guitarSetting.pickupSelector
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RotaryKnob(
                value = guitarSetting.volumeKnob,
                onValueChange = {},
                label = "Volume",
                size = 80.dp,
                enabled = false
            )
            RotaryKnob(
                value = guitarSetting.toneKnob,
                onValueChange = {},
                label = "Tone",
                size = 80.dp,
                enabled = false
            )
        }
    }
}

@Composable
private fun DetailPickupSelector(
    selectedPosition: PickupPosition
) {
    val positions = listOf(
        PickupPosition.NECK to "N",
        PickupPosition.NECK_MIDDLE to "N+M",
        PickupPosition.MIDDLE to "M",
        PickupPosition.MIDDLE_BRIDGE to "M+B",
        PickupPosition.BRIDGE to "B"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
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
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                        .border(
                            width = 2.dp,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun DetailHeaderPreview() {
    com.haero.tonestore.ui.theme.ToneStoreTheme {
        DetailHeader(
            title = "Sample Song",
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun DetailTabBarPreview() {
    com.haero.tonestore.ui.theme.ToneStoreTheme {
        DetailTabBar(
            tabs = listOf(
                TabItem("Pedal Board", Icons.Default.GraphicEq),
                TabItem("Amp", Icons.Default.Speaker),
                TabItem("Guitar", Icons.Default.MusicNote)
            ),
            selectedIndex = 0,
            onTabSelected = {}
        )
    }
}
