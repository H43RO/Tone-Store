package com.haero.tonestore.presentation.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.presentation.ui.create.components.AmpSection
import com.haero.tonestore.presentation.ui.create.components.GuitarSection
import com.haero.tonestore.presentation.ui.create.components.PedalBoardSection
import com.haero.tonestore.presentation.viewmodel.DetailViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * 톤 세팅 상세 보기 화면
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    toneSettingId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: DetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 데이터 로드
    LaunchedEffect(toneSettingId) {
        viewModel.handleIntent(DetailIntent.LoadToneSetting(toneSettingId))
    }

    // 네비게이션 처리
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(state.toneSetting?.songName ?: stringResource(R.string.detail))
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
                    IconButton(
                        onClick = { viewModel.handleIntent(DetailIntent.NavigateToEdit) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit)
                        )
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.toneSetting != null -> {
                    val toneSetting = state.toneSetting!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // 페달보드 섹션 (읽기 전용)
                        PedalBoardSection(
                            pedalBoard = toneSetting.pedalBoard,
                            presetPedals = emptyList(),
                            savedPedalBoards = emptyList(),
                            onAddPresetPedal = {},
                            onAddCustomPedal = { _, _ -> },
                            onLoadSavedPedalBoard = {},
                            onRemovePedal = {},
                            onKnobChange = { _, _, _ -> },
                            onTogglePedalEnabled = {},
                            isEditable = false
                        )

                        // 앰프 섹션 (읽기 전용)
                        AmpSection(
                            ampSetting = toneSetting.ampSetting,
                            onAmpModelChange = {},
                            onKnobChange = { _, _ -> },
                            isEditable = false
                        )

                        // 기타 섹션 (읽기 전용)
                        GuitarSection(
                            guitarSetting = toneSetting.guitarSetting,
                            onGuitarModelChange = {},
                            onPickupPositionChange = {},
                            onToneChange = {},
                            onVolumeChange = {},
                            isEditable = false
                        )

                        Spacer(modifier = Modifier.height(32.dp))
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

    // 삭제 확인 다이얼로그
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
