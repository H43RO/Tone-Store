package com.haero.tonestore.presentation.ui.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.presentation.ui.create.components.AmpSection
import com.haero.tonestore.presentation.ui.create.components.GuitarSection
import com.haero.tonestore.presentation.ui.create.components.PedalBoardSection
import com.haero.tonestore.presentation.ui.create.components.TagSection
import com.haero.tonestore.presentation.viewmodel.CreateToneViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * 톤 세팅 생성/편집 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateToneScreen(
    onNavigateBack: () -> Unit,
    editingId: String? = null,
    viewModel: CreateToneViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // 편집 모드일 경우 데이터 로드
    LaunchedEffect(editingId) {
        editingId?.let {
            viewModel.handleIntent(CreateToneIntent.LoadToneSetting(it))
        }
    }
    
    // 네비게이션 처리
    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            onNavigateBack()
            viewModel.handleIntent(CreateToneIntent.NavigationHandled)
        }
    }
    
    // 성공 메시지
    val saveSuccessMessage = stringResource(R.string.save_success)
    LaunchedEffect(state.showSaveSuccess) {
        if (state.showSaveSuccess) {
            snackbarHostState.showSnackbar(saveSuccessMessage)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditMode) stringResource(R.string.edit_tone_setting)
                        else stringResource(R.string.create_tone_setting)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = { viewModel.handleIntent(CreateToneIntent.SaveToneSetting) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = stringResource(R.string.save)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 곡 이름 입력
            OutlinedTextField(
                value = state.songName,
                onValueChange = { viewModel.handleIntent(CreateToneIntent.UpdateSongName(it)) },
                label = { Text(stringResource(R.string.song_name)) },
                placeholder = { Text(stringResource(R.string.song_name_hint)) },
                singleLine = true,
                isError = state.songNameError != null,
                supportingText = state.songNameError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 태그 섹션
            TagSection(
                selectedTags = state.selectedTags,
                onTagToggle = { tag ->
                    viewModel.handleIntent(CreateToneIntent.ToggleTag(tag))
                }
            )
            
            // 페달보드 섹션
            PedalBoardSection(
                pedalBoard = state.pedalBoard,
                presetPedals = state.presetPedals,
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
            
            // 앰프 섹션
            AmpSection(
                ampSetting = state.ampSetting,
                onAmpModelChange = { viewModel.handleIntent(CreateToneIntent.UpdateAmpModel(it)) },
                onKnobChange = { knobName, value ->
                    viewModel.handleIntent(CreateToneIntent.UpdateAmpKnob(knobName, value))
                }
            )
            
            // 기타 섹션
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
}
