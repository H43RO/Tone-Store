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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.StrokeCap
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
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianBackground
import com.haero.tonestore.ui.designsystem.ObsidianButton
import com.haero.tonestore.ui.designsystem.ObsidianIconButton
import com.haero.tonestore.ui.designsystem.ObsidianOutlinedButton
import com.haero.tonestore.ui.designsystem.ObsidianSurface
import com.haero.tonestore.ui.designsystem.ObsidianTextField
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

    ObsidianBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp) // BottomBar space
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

            StepperBottomBar(
                currentStep = currentStep,
                totalSteps = steps.size,
                isLastStep = isLastStep,
                isFirstStep = isFirstStep,
                isSaving = state.isSaving,
                onPrevious = { currentStep-- },
                onNext = { currentStep++ },
                onSave = { viewModel.handleIntent(CreateToneIntent.SaveToneSetting) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp)
            )
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
        ObsidianIconButton(
            onClick = onCloseClick,
            icon = Icons.Default.Close,
            tint = Obsidian.colors.textPrimary
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = Obsidian.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Obsidian.colors.textPrimary
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
                style = Obsidian.typography.titleLarge, // Bolder step title
                color = Obsidian.colors.textPrimary
            )
            Text(
                text = stringResource(R.string.step_indicator, currentStep + 1, totalSteps),
                style = Obsidian.typography.labelMedium,
                color = Obsidian.colors.textSecondary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp) // Thicker indicator
                .clip(RoundedCornerShape(4.dp)),
            color = Obsidian.colors.primary,
            trackColor = Obsidian.colors.surfaceElevated,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(12.dp))

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
        modifier = modifier.padding(horizontal = 4.dp), // add slight padding
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(28.dp) // larger hit area and visual balance
                .background(
                    color = when {
                        isCompleted -> Obsidian.colors.secondary
                        isCurrent -> Obsidian.colors.primary
                        else -> Obsidian.colors.surfaceElevated
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Obsidian.colors.bgPrimary,
                    modifier = Modifier.size(16.dp)
                )
            } else if (isCurrent) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Obsidian.colors.bgPrimary, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = Obsidian.typography.labelSmall,
            color = if (isCurrent || isCompleted) {
                Obsidian.colors.textPrimary
            } else {
                Obsidian.colors.textMuted
            },
            textAlign = TextAlign.Center,
            fontSize = 11.sp, // improved legibility for Pretendard
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
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
    ObsidianSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = Obsidian.radius.xl, topEnd = Obsidian.radius.xl), // slightly rounder top
        elevation = 0.dp // Flatter design
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = !isFirstStep,
                modifier = Modifier.weight(1f)
            ) {
                ObsidianOutlinedButton(
                    onClick = onPrevious,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.previous))
                }
            }

            if (!isFirstStep) {
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Box(modifier = Modifier.weight(1f)) {
                if (isLastStep) {
                    ObsidianButton(
                        onClick = onSave,
                        icon = Icons.Default.Save,
                        isLoading = isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                } else {
                    ObsidianButton(
                        onClick = onNext,
                        icon = null, // 아이콘은 텍스트 뒤에 넣기 위해 null 처리 후 content에서 처리
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.next))
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
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

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(R.string.song_name),
                style = Obsidian.typography.labelLarge,
                color = Obsidian.colors.textSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ObsidianTextField(
                value = songName,
                onValueChange = onSongNameChange,
                placeholder = stringResource(R.string.song_name_hint),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (songNameError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = songNameError,
                    style = Obsidian.typography.bodySmall,
                    color = Obsidian.colors.error
                )
            }
        }

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
