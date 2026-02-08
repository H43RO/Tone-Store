package com.haero.tonestore.presentation.ui.pedalboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.presentation.ui.pedalboard.components.ExpressionPedalSelectionDialog
import com.haero.tonestore.presentation.ui.pedalboard.components.ExpressionPedalZone
import com.haero.tonestore.presentation.ui.pedalboard.components.InlinePedalEditor
import com.haero.tonestore.presentation.ui.pedalboard.components.PedalBoardGrid
import com.haero.tonestore.presentation.ui.pedalboard.components.PedalboardInfoEditor
import com.haero.tonestore.presentation.ui.pedalboard.components.PresetPedalSelectionDialog
import com.haero.tonestore.presentation.viewmodel.PedalBoardViewModel
import com.haero.tonestore.ui.designsystem.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedalBoardScreen(
    onNavigateBack: () -> Unit,
    editingId: String? = null,
    viewModel: PedalBoardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var showAddPedalDialog by remember { mutableStateOf(false) }
    var addingToSlotIndex by remember { mutableStateOf<Int?>(null) }
    var showCustomPedalDialog by remember { mutableStateOf(false) }
    var showExpressionPedalDialog by remember { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }

    val slotPositions = remember { mutableStateMapOf<Int, Offset>() }

    val lastEditingPedal = remember { mutableStateOf<Pedal?>(null) }
    val lastEditingSlotIndex = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(state.editingPedal, state.editingSlotIndex) {
        if (state.editingPedal != null && state.editingSlotIndex != null) {
            lastEditingPedal.value = state.editingPedal
            lastEditingSlotIndex.value = state.editingSlotIndex
        }
    }

    LaunchedEffect(editingId) {
        editingId?.let {
            viewModel.handleIntent(PedalBoardIntent.LoadPedalBoard(it))
        }
    }

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            onNavigateBack()
            viewModel.handleIntent(PedalBoardIntent.NavigationHandled)
        }
    }

    val saveSuccessMessage = stringResource(R.string.pedalboard_save_success)
    LaunchedEffect(state.showSaveSuccess) {
        if (state.showSaveSuccess) {
            snackbarHostState.showSnackbar(saveSuccessMessage)
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.handleIntent(PedalBoardIntent.ClearError)
        }
    }

    ObsidianBackground {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                PedalBoardEditHeader(
                    title = if (state.isEditMode) {
                        stringResource(R.string.edit_pedalboard)
                    } else {
                        stringResource(R.string.create_pedalboard)
                    },
                    isSaving = state.isSaving,
                    isEditMode = state.isEditMode,
                    onCloseClick = {
                        if (state.hasUnsavedChanges) {
                            showUnsavedChangesDialog = true
                        } else {
                            onNavigateBack()
                        }
                    },
                    onSaveClick = { viewModel.handleIntent(PedalBoardIntent.SavePedalBoard) },
                    onDeleteClick = { viewModel.handleIntent(PedalBoardIntent.DeletePedalBoard) }
                )

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Obsidian.spacing.screenPadding)
                ) {
                    Spacer(modifier = Modifier.height(Obsidian.spacing.sm))

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PedalBoardGrid(
                                slots = state.slots,
                                columns = state.columns,
                                rows = state.rows,
                                editingSlotIndex = state.editingSlotIndex,
                                onSlotClick = { slotIndex ->
                                    viewModel.handleIntent(PedalBoardIntent.OpenPedalEditor(slotIndex))
                                },
                                onAddClick = { slotIndex ->
                                    addingToSlotIndex = slotIndex
                                    showAddPedalDialog = true
                                },
                                onSwapSlots = { fromIndex, toIndex ->
                                    viewModel.handleIntent(PedalBoardIntent.SwapSlots(fromIndex, toIndex))
                                },
                                onSlotPositioned = { index, offset ->
                                    slotPositions[index] = offset
                                },
                                onDeletePedal = { slotIndex ->
                                    viewModel.handleIntent(PedalBoardIntent.RemovePedalFromSlot(slotIndex))
                                },
                                isEditable = true,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(Obsidian.spacing.sm))
                            ExpressionPedalZone(
                                expressionPedal = state.expressionPedal,
                                onSelectPedal = { showExpressionPedalDialog = true },
                                onRemovePedal = { viewModel.handleIntent(PedalBoardIntent.RemoveExpressionPedal) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }

                val isEditingPedal = state.editingSlotIndex != null && state.editingPedal != null

                AnimatedVisibility(
                    visible = isEditingPedal,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    val pedal = lastEditingPedal.value ?: return@AnimatedVisibility
                    val slotIndex = lastEditingSlotIndex.value ?: return@AnimatedVisibility

                    InlinePedalEditor(
                        pedal = pedal,
                        slotIndex = slotIndex,
                        onDismiss = { viewModel.handleIntent(PedalBoardIntent.ClosePedalEditor) },
                        onColorChange = { color ->
                            viewModel.handleIntent(
                                PedalBoardIntent.UpdatePedalColor(slotIndex, color)
                            )
                        },
                        onKnobsChange = { knobs ->
                            viewModel.handleIntent(
                                PedalBoardIntent.UpdatePedalKnobs(slotIndex, knobs)
                            )
                        },
                        onPedalNameChange = { name ->
                            viewModel.handleIntent(
                                PedalBoardIntent.UpdatePedalName(slotIndex, name)
                            )
                        },
                        onKnobNameChange = { knobIndex, name ->
                            viewModel.handleIntent(PedalBoardIntent.UpdateKnobName(slotIndex, knobIndex, name))
                        }
                    )
                }

                AnimatedVisibility(
                    visible = isEditingPedal.not(),
                    enter = expandVertically(expandFrom = Alignment.Bottom) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()
                ) {
                    PedalboardInfoEditor(
                        name = state.name,
                        columns = state.columns,
                        rows = state.rows,
                        pedalCount = state.pedalCount,
                        totalSlots = state.totalSlots,
                        nameError = state.nameError,
                        onNameChange = { viewModel.handleIntent(PedalBoardIntent.UpdateName(it)) },
                        onColumnsChange = { newColumns ->
                            viewModel.handleIntent(PedalBoardIntent.UpdateLayout(newColumns, state.rows))
                        },
                        onRowsChange = { newRows ->
                            viewModel.handleIntent(PedalBoardIntent.UpdateLayout(state.columns, newRows))
                        }
                    )
                }
            }
        }
    }

    if (showAddPedalDialog && addingToSlotIndex != null) {
        val slotIndex = addingToSlotIndex ?: return@PedalBoardScreen
        PresetPedalSelectionDialog(
            customPedals = state.customPedals,
            onPedalSelect = { pedal ->
                viewModel.handleIntent(PedalBoardIntent.AddPedalToSlot(slotIndex, pedal))
                showAddPedalDialog = false
                addingToSlotIndex = null
            },
            onCustomPedalCreate = {
                showAddPedalDialog = false
                showCustomPedalDialog = true
            },
            onDismiss = {
                showAddPedalDialog = false
                addingToSlotIndex = null
            }
        )
    }

    if (showCustomPedalDialog && addingToSlotIndex != null) {
        val slotIndex = addingToSlotIndex ?: return@PedalBoardScreen
        CustomPedalDialog(
            onConfirm = { name, knobs ->
                viewModel.handleIntent(
                    PedalBoardIntent.AddCustomPedalToSlot(slotIndex, name, knobs)
                )
                showCustomPedalDialog = false
                addingToSlotIndex = null
            },
            onDismiss = {
                showCustomPedalDialog = false
                addingToSlotIndex = null
            }
        )
    }

    if (showExpressionPedalDialog) {
        ExpressionPedalSelectionDialog(
            onSelectWah = {
                val wahPedal = state.presetPedals.find { it.name == "Wah" }
                wahPedal?.let { viewModel.handleIntent(PedalBoardIntent.SelectExpressionPedal(it)) }
                showExpressionPedalDialog = false
            },
            onSelectWhammy = {
                val whammyPedal = state.presetPedals.find { it.name == "Whammy" }
                whammyPedal?.let { viewModel.handleIntent(PedalBoardIntent.SelectExpressionPedal(it)) }
                showExpressionPedalDialog = false
            },
            onDismiss = {
                showExpressionPedalDialog = false
            }
        )
    }

    // 뒤로가기 핸들링
    androidx.activity.compose.BackHandler {
        if (state.editingSlotIndex != null) {
            viewModel.handleIntent(PedalBoardIntent.ClosePedalEditor)
        } else if (state.hasUnsavedChanges) {
            showUnsavedChangesDialog = true
        } else {
            onNavigateBack()
        }
    }

    if (showUnsavedChangesDialog) {
        ObsidianAlertDialog(
            onDismissRequest = { showUnsavedChangesDialog = false },
            title = "저장되지 않은 변경사항",
            message = "페달보드가 저장되지 않았어요.\n나가기 전 저장할까요?",
            confirmText = "저장",
            onConfirm = {
                viewModel.handleIntent(PedalBoardIntent.SavePedalBoard)
                showUnsavedChangesDialog = false
            },
            dismissText = "나가기",
            onDismiss = {
                showUnsavedChangesDialog = false
                onNavigateBack()
            }
        )
    }
}

@Composable
private fun PedalBoardEditHeader(
    title: String,
    isSaving: Boolean,
    isEditMode: Boolean,
    onCloseClick: () -> Unit,
    onSaveClick: () -> Unit,
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
            onClick = onCloseClick,
            icon = Icons.Default.Close,
            tint = Obsidian.colors.textPrimary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = Obsidian.typography.headlineMedium,
            color = Obsidian.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        if (isEditMode) {
            ObsidianIconButton(
                onClick = onDeleteClick,
                icon = Icons.Default.Delete,
                tint = Obsidian.colors.textSecondary
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        ObsidianIconButton(
            onClick = onSaveClick,
            icon = Icons.Default.Save,
            tint = Obsidian.colors.primary,
            enabled = !isSaving
        )
    }
}

@Composable
private fun CustomPedalDialog(onConfirm: (name: String, knobs: List<String>) -> Unit, onDismiss: () -> Unit) {
    var pedalName by remember { mutableStateOf("") }
    val knobNames = remember { mutableStateListOf("Knob 1") }

    ObsidianDialog(
        onDismissRequest = onDismiss,
        title = stringResource(R.string.create_custom_pedal),
        confirmButton = {
            ObsidianButton(
                onClick = {
                    if (pedalName.isNotBlank()) {
                        onConfirm(pedalName, knobNames.filter { it.isNotBlank() })
                    }
                },
                enabled = pedalName.isNotBlank()
            ) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            ObsidianOutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Obsidian.spacing.md)
        ) {
            ObsidianTextField(
                value = pedalName,
                onValueChange = { pedalName = it },
                placeholder = stringResource(R.string.pedal_name),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.knobs),
                style = Obsidian.typography.labelLarge,
                color = Obsidian.colors.textSecondary
            )

            knobNames.forEachIndexed { index, name ->
                ObsidianTextField(
                    value = name,
                    onValueChange = { knobNames[index] = it },
                    placeholder = "노브 ${index + 1} 이름",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (knobNames.size < 6) {
                ObsidianTextButton(
                    onClick = { knobNames.add("Knob ${knobNames.size + 1}") },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.add_knob))
                }
            }
        }
    }
}
