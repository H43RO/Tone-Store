package com.haero.tonestore.presentation.ui.pedalboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.presentation.ui.pedalboard.components.CableOverlay
import com.haero.tonestore.presentation.ui.pedalboard.components.ExpressionPedalSelectionDialog
import com.haero.tonestore.presentation.ui.pedalboard.components.ExpressionPedalZone
import com.haero.tonestore.presentation.ui.pedalboard.components.InlinePedalEditor
import com.haero.tonestore.presentation.ui.pedalboard.components.PedalBoardGrid
import com.haero.tonestore.presentation.ui.pedalboard.components.PedalboardInfoEditor
import com.haero.tonestore.presentation.ui.pedalboard.components.PresetPedalSelectionDialog
import com.haero.tonestore.presentation.viewmodel.PedalBoardViewModel
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
    val scope = rememberCoroutineScope()

    var showAddPedalDialog by remember { mutableStateOf(false) }
    var addingToSlotIndex by remember { mutableStateOf<Int?>(null) }
    var showCustomPedalDialog by remember { mutableStateOf(false) }
    var showExpressionPedalDialog by remember { mutableStateOf(false) }

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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
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
                onCloseClick = onNavigateBack,
                onSaveClick = { viewModel.handleIntent(PedalBoardIntent.SavePedalBoard) },
                onDeleteClick = { viewModel.handleIntent(PedalBoardIntent.DeletePedalBoard) }
            )

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CableOverlay(
                        slots = state.slots,
                        slotPositions = slotPositions,
                        columns = state.columns,
                        modifier = Modifier.matchParentSize()
                    )

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
                        Spacer(Modifier.width(8.dp))
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

    if (showAddPedalDialog && addingToSlotIndex != null) {
        val slotIndex = addingToSlotIndex ?: return@PedalBoardScreen
        PresetPedalSelectionDialog(
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
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        if (isEditMode) {
            Surface(
                onClick = onDeleteClick,
                shape = CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            onClick = onSaveClick,
            shape = CircleShape,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = stringResource(R.string.save),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddPedalDialog(
    presetPedals: List<Pedal>,
    onSelectPreset: (Pedal) -> Unit,
    onCreateCustom: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_preset_pedal)) },
        text = {
            Column {
                presetPedals.forEach { pedal ->
                    AssistChip(
                        onClick = { onSelectPreset(pedal) },
                        label = { Text(pedal.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                AssistChip(
                    onClick = onCreateCustom,
                    label = { Text(stringResource(R.string.add_custom_pedal)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun CustomPedalDialog(onConfirm: (name: String, knobs: List<String>) -> Unit, onDismiss: () -> Unit) {
    var pedalName by remember { mutableStateOf("") }
    val knobNames = remember { mutableStateListOf("Knob 1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.create_custom_pedal)) },
        text = {
            Column {
                OutlinedTextField(
                    value = pedalName,
                    onValueChange = { pedalName = it },
                    label = { Text(stringResource(R.string.pedal_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.knobs),
                    style = MaterialTheme.typography.labelLarge
                )

                knobNames.forEachIndexed { index, name ->
                    OutlinedTextField(
                        value = name,
                        onValueChange = { knobNames[index] = it },
                        label = { Text("Knob ${index + 1}") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }

                if (knobNames.size < 6) {
                    TextButton(onClick = { knobNames.add("Knob ${knobNames.size + 1}") }) {
                        Text(stringResource(R.string.add_knob))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
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
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun PedalBoardEditHeaderPreview() {
    com.haero.tonestore.ui.theme.ToneStoreTheme {
        PedalBoardEditHeader(
            title = "Create Pedal Board",
            isSaving = false,
            isEditMode = false,
            onCloseClick = {},
            onSaveClick = {},
            onDeleteClick = {}
        )
    }
}
