package com.haero.tonestore.presentation.ui.create

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.presentation.ui.create.components.AmpSection
import com.haero.tonestore.presentation.ui.create.components.GuitarSection
import com.haero.tonestore.presentation.ui.create.components.PedalBoardSection
import com.haero.tonestore.presentation.ui.create.components.TagSection
import com.haero.tonestore.presentation.viewmodel.CreateToneViewModel
import org.koin.androidx.compose.koinViewModel

private enum class CreateStep(val titleResId: Int) {
    SONG_INFO(R.string.step_song_info),
    PEDAL_BOARD(R.string.step_pedal_board),
    AMP(R.string.step_amp),
    GUITAR(R.string.step_guitar)
}

private val steps = CreateStep.entries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateToneScreen(
    onNavigateBack: () -> Unit,
    editingId: String? = null,
    viewModel: CreateToneViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var currentStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(editingId) {
        editingId?.let {
            viewModel.handleIntent(CreateToneIntent.LoadToneSetting(it))
        }
    }

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            onNavigateBack()
            viewModel.handleIntent(CreateToneIntent.NavigationHandled)
        }
    }

    val saveSuccessMessage = stringResource(R.string.save_success)
    LaunchedEffect(state.showSaveSuccess) {
        if (state.showSaveSuccess) {
            snackbarHostState.showSnackbar(saveSuccessMessage)
        }
    }

    // 에러 메시지 표시
    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.handleIntent(CreateToneIntent.ClearError)
        }
    }

    val isLastStep = currentStep == steps.lastIndex
    val isFirstStep = currentStep == 0

    Scaffold(
        bottomBar = {
            StepperBottomBar(
                currentStep = currentStep,
                totalSteps = steps.size,
                isLastStep = isLastStep,
                isFirstStep = isFirstStep,
                isSaving = state.isSaving,
                onPrevious = { currentStep-- },
                onNext = { currentStep++ },
                onSave = { viewModel.handleIntent(CreateToneIntent.SaveToneSetting) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CreateToneHeader(
                title = if (state.isEditMode) {
                    stringResource(R.string.edit_tone_setting)
                } else {
                    stringResource(R.string.create_tone_setting)
                },
                onCloseClick = onNavigateBack
            )

            StepProgressIndicator(
                currentStep = currentStep,
                totalSteps = steps.size,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "step_content"
            ) { step ->
                when (steps[step]) {
                    CreateStep.SONG_INFO -> SongInfoStepContent(
                        songName = state.songName,
                        songNameError = state.songNameError,
                        selectedTags = state.selectedTags,
                        onSongNameChange = { viewModel.handleIntent(CreateToneIntent.UpdateSongName(it)) },
                        onTagToggle = { viewModel.handleIntent(CreateToneIntent.ToggleTag(it)) }
                    )
                    CreateStep.PEDAL_BOARD -> PedalBoardStepContent(
                        state = state,
                        viewModel = viewModel
                    )
                    CreateStep.AMP -> AmpStepContent(
                        state = state,
                        viewModel = viewModel
                    )
                    CreateStep.GUITAR -> GuitarStepContent(
                        state = state,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateToneHeader(
    title: String,
    onCloseClick: () -> Unit,
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
            onClick = onCloseClick,
            shape = CircleShape,
            color = Color.Transparent,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Close,
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
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun StepProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = (currentStep + 1) / totalSteps.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progress"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(steps[currentStep].titleResId),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.step_indicator, currentStep + 1, totalSteps),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, step ->
                StepDot(
                    label = stringResource(step.titleResId),
                    isCompleted = index < currentStep,
                    isCurrent = index == currentStep
                )
            }
        }
    }
}

@Composable
private fun StepDot(
    label: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = when {
                        isCompleted -> MaterialTheme.colorScheme.primary
                        isCurrent -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp)
                )
            } else if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCurrent || isCompleted) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun StepperBottomBar(
    currentStep: Int,
    totalSteps: Int,
    isLastStep: Boolean,
    isFirstStep: Boolean,
    isSaving: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = !isFirstStep) {
                StepperButton(
                    text = stringResource(R.string.previous),
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    iconAtStart = true,
                    isPrimary = false,
                    onClick = onPrevious
                )
            }
            if (isFirstStep) {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (isLastStep) {
                StepperButton(
                    text = stringResource(R.string.save),
                    icon = Icons.Default.Save,
                    iconAtStart = false,
                    isPrimary = true,
                    isLoading = isSaving,
                    onClick = onSave
                )
            } else {
                StepperButton(
                    text = stringResource(R.string.next),
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    iconAtStart = false,
                    isPrimary = true,
                    onClick = onNext
                )
            }
        }
    }
}

@Composable
private fun StepperButton(
    text: String,
    icon: ImageVector,
    iconAtStart: Boolean,
    isPrimary: Boolean,
    isLoading: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .shadow(
                elevation = if (isPrimary) 8.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = if (isPrimary) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (isPrimary) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        contentColor = if (isPrimary) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                if (iconAtStart) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelLarge
                )
                if (!iconAtStart) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SongInfoStepContent(
    songName: String,
    songNameError: String?,
    selectedTags: List<com.haero.tonestore.domain.model.GenreTag>,
    onSongNameChange: (String) -> Unit,
    onTagToggle: (com.haero.tonestore.domain.model.GenreTag) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = songName,
            onValueChange = onSongNameChange,
            label = { Text(stringResource(R.string.song_name)) },
            placeholder = { Text(stringResource(R.string.song_name_hint)) },
            singleLine = true,
            isError = songNameError != null,
            supportingText = songNameError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        TagSection(
            selectedTags = selectedTags,
            onTagToggle = onTagToggle
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PedalBoardStepContent(
    state: CreateToneState,
    viewModel: CreateToneViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PedalBoardSection(
            pedalBoard = state.pedalBoard,
            presetPedals = state.presetPedals,
            customPedals = state.customPedals,
            savedPedalBoards = state.savedPedalBoards,
            onAddPresetPedal = { pedal ->
                viewModel.handleIntent(CreateToneIntent.AddPresetPedal(pedal))
            },
            onAddCustomPedal = { name, knobs ->
                viewModel.handleIntent(CreateToneIntent.AddCustomPedal(name, knobs))
            },
            onLoadSavedPedalBoard = { pedalBoard ->
                viewModel.handleIntent(CreateToneIntent.LoadSavedPedalBoard(pedalBoard))
            },
            onRemovePedal = { pedalId ->
                viewModel.handleIntent(CreateToneIntent.RemovePedal(pedalId))
            },
            onKnobChange = { pedalId, knobIndex, value ->
                viewModel.handleIntent(CreateToneIntent.UpdatePedalKnob(pedalId, knobIndex, value))
            },
            onTogglePedalEnabled = { pedalId ->
                viewModel.handleIntent(CreateToneIntent.TogglePedalEnabled(pedalId))
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AmpStepContent(
    state: CreateToneState,
    viewModel: CreateToneViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AmpSection(
            ampSetting = state.ampSetting,
            onAmpModelChange = { viewModel.handleIntent(CreateToneIntent.UpdateAmpModel(it)) },
            onKnobChange = { knobName, value ->
                viewModel.handleIntent(CreateToneIntent.UpdateAmpKnob(knobName, value))
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun GuitarStepContent(
    state: CreateToneState,
    viewModel: CreateToneViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        GuitarSection(
            guitarSetting = state.guitarSetting,
            onGuitarModelChange = { viewModel.handleIntent(CreateToneIntent.UpdateGuitarModel(it)) },
            onPickupPositionChange = { viewModel.handleIntent(CreateToneIntent.UpdatePickupPosition(it)) },
            onToneChange = { viewModel.handleIntent(CreateToneIntent.UpdateGuitarTone(it)) },
            onVolumeChange = { viewModel.handleIntent(CreateToneIntent.UpdateGuitarVolume(it)) }
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun CreateToneHeaderPreview() {
    com.haero.tonestore.ui.theme.ToneStoreTheme {
        CreateToneHeader(
            title = "Create Tone Setting",
            onCloseClick = {}
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun StepProgressIndicatorPreview() {
    com.haero.tonestore.ui.theme.ToneStoreTheme {
        StepProgressIndicator(
            currentStep = 1,
            totalSteps = 4
        )
    }
}
