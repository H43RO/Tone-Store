package com.haero.tonestore.presentation.ui.share

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.GenreTag
import com.haero.tonestore.presentation.viewmodel.ShareToneViewModel
import com.haero.tonestore.ui.designsystem.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShareToneScreen(
    toneSettingId: String,
    onNavigateBack: () -> Unit,
    onShareSuccess: (presetId: String) -> Unit,
    viewModel: ShareToneViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toneSettingId) {
        viewModel.handleIntent(ShareToneIntent.LoadToneSetting(toneSettingId))
    }

    LaunchedEffect(state.isSuccess, state.sharedPresetId) {
        if (state.isSuccess && state.sharedPresetId != null) {
            onShareSuccess(state.sharedPresetId!!)
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.handleIntent(ShareToneIntent.ClearError)
        }
    }

    ObsidianBackground {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = Obsidian.spacing.xs, vertical = Obsidian.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ObsidianIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onNavigateBack,
                        tint = Obsidian.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(Obsidian.spacing.sm))
                    Text(
                        text = "톤 공유하기",
                        style = Obsidian.typography.headlineMedium,
                        color = Obsidian.colors.textPrimary
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            if (state.isLoading && state.toneSetting == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ObsidianLoadingIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(Obsidian.spacing.screenPadding)
                ) {
                    // 원곡 정보 카드
                    state.toneSetting?.let { toneSetting ->
                        ObsidianCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "공유할 톤 정보",
                                style = Obsidian.typography.labelMedium,
                                color = Obsidian.colors.textMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "🎸 ${toneSetting.songName}",
                                style = Obsidian.typography.headlineSmall,
                                color = Obsidian.colors.primary
                            )
                            Text(
                                text = stringResource(
                                    R.string.pedals_count_format,
                                    toneSetting.pedalBoard.pedals.size
                                ) + " · " + (toneSetting.ampSetting.ampModel ?: stringResource(R.string.amp_setting_exists)),
                                style = Obsidian.typography.bodyMedium,
                                color = Obsidian.colors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Obsidian.spacing.sectionGap))

                    // 제목 입력
                    Text(
                        text = stringResource(R.string.title_required),
                        style = Obsidian.typography.titleMedium,
                        color = Obsidian.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(Obsidian.spacing.sm))
                    ObsidianTextField(
                        value = state.title,
                        onValueChange = { viewModel.handleIntent(ShareToneIntent.UpdateTitle(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = stringResource(R.string.title_placeholder)
                    )

                    Spacer(modifier = Modifier.height(Obsidian.spacing.xl))

                    // 설명 입력
                    Text(
                        text = stringResource(R.string.description),
                        style = Obsidian.typography.titleMedium,
                        color = Obsidian.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(Obsidian.spacing.sm))
                    ObsidianTextField(
                        value = state.description,
                        onValueChange = { viewModel.handleIntent(ShareToneIntent.UpdateDescription(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = stringResource(R.string.description_placeholder),
                        singleLine = false
                    )

                    Spacer(modifier = Modifier.height(Obsidian.spacing.xl))

                    // 태그 선택
                    Text(
                        text = stringResource(R.string.tags),
                        style = Obsidian.typography.titleMedium,
                        color = Obsidian.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(Obsidian.spacing.sm))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GenreTag.entries.forEach { tag ->
                            val isSelected = state.selectedTags.contains(tag)
                            ObsidianChip(
                                label = tag.displayNameKo,
                                selected = isSelected,
                                onClick = { viewModel.handleIntent(ShareToneIntent.ToggleTag(tag)) },
                                leadingIcon = if (isSelected) Icons.Default.Check else null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // 공유 버튼
                    ObsidianButton(
                        onClick = { viewModel.handleIntent(ShareToneIntent.Share) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = state.title.isNotBlank() && !state.isLoading,
                        isLoading = state.isLoading,
                        icon = Icons.Default.Share
                    ) {
                        Text(stringResource(R.string.share_to_community))
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
