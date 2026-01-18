package com.haero.tonestore.presentation.ui.create.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalBoard
import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.presentation.ui.components.SectionHeader

@Composable
fun PedalBoardSection(
    pedalBoard: PedalBoard,
    presetPedals: List<Pedal>,
    savedPedalBoards: List<SavedPedalBoard>,
    onAddPresetPedal: (Pedal) -> Unit,
    onAddCustomPedal: (name: String, knobNames: List<String>) -> Unit,
    onLoadSavedPedalBoard: (SavedPedalBoard) -> Unit,
    onRemovePedal: (String) -> Unit,
    onKnobChange: (pedalId: String, knobIndex: Int, value: Float) -> Unit,
    onTogglePedalEnabled: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(true) }
    var showPresetDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showSavedPedalBoardDialog by remember { mutableStateOf(false) }

    SectionHeader(
        title = stringResource(R.string.pedal_board),
        isExpanded = isExpanded,
        onToggle = { isExpanded = isExpanded.not() },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (pedalBoard.pedals.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pedalBoard.pedals.forEach { pedal ->
                        PedalCard(
                            pedal = pedal,
                            onKnobChange = { knobIndex, value ->
                                onKnobChange(pedal.id, knobIndex, value)
                            },
                            onToggleEnabled = { onTogglePedalEnabled(pedal.id) },
                            onRemove = { onRemovePedal(pedal.id) },
                            isEditable = isEditable,
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.no_pedals),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            if (isEditable) {
                Spacer(modifier = Modifier.height(12.dp))

                if (savedPedalBoards.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { showSavedPedalBoardDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.Dashboard, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.load_saved_pedalboard))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showPresetDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.add_preset_pedal))
                    }

                    OutlinedButton(
                        onClick = { showCustomDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.add_custom_pedal))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showPresetDialog) {
        PresetPedalDialog(
            presetPedals = presetPedals,
            onSelect = { pedal ->
                onAddPresetPedal(pedal)
                showPresetDialog = false
            },
            onDismiss = { showPresetDialog = false }
        )
    }

    if (showCustomDialog) {
        CustomPedalDialog(
            onConfirm = { name, knobs ->
                onAddCustomPedal(name, knobs)
                showCustomDialog = false
            },
            onDismiss = { showCustomDialog = false }
        )
    }

    if (showSavedPedalBoardDialog) {
        SavedPedalBoardDialog(
            savedPedalBoards = savedPedalBoards,
            onSelect = { pedalBoard ->
                onLoadSavedPedalBoard(pedalBoard)
                showSavedPedalBoardDialog = false
            },
            onDismiss = { showSavedPedalBoardDialog = false }
        )
    }
}

@Composable
private fun PresetPedalDialog(presetPedals: List<Pedal>, onSelect: (Pedal) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_preset_pedal)) },
        text = {
            Column {
                presetPedals.forEach { pedal ->
                    AssistChip(
                        onClick = { onSelect(pedal) },
                        label = { Text(pedal.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
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
                    TextButton(
                        onClick = { knobNames.add("Knob ${knobNames.size + 1}") },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
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

@Composable
private fun SavedPedalBoardDialog(
    savedPedalBoards: List<SavedPedalBoard>,
    onSelect: (SavedPedalBoard) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.load_saved_pedalboard)) },
        text = {
            Column {
                savedPedalBoards.forEach { pedalBoard ->
                    AssistChip(
                        onClick = { onSelect(pedalBoard) },
                        label = {
                            Column {
                                Text(pedalBoard.name)
                                Text(
                                    text = "${pedalBoard.pedalCount} pedals",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
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
