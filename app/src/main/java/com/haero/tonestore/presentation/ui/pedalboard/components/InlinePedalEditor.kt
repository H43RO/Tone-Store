package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.Knob
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.presentation.ui.components.RotaryKnob
import com.haero.tonestore.ui.theme.ToneStoreTheme

@Composable
fun InlinePedalEditor(
    pedal: Pedal,
    slotIndex: Int,
    onDismiss: () -> Unit,
    onColorChange: (Long?) -> Unit,
    onKnobsChange: (List<Knob>) -> Unit,
    onPedalNameChange: (String) -> Unit,
    onKnobNameChange: (knobIndex: Int, name: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val knobsList = remember(pedal.id) {
        mutableStateListOf(*pedal.knobs.toTypedArray())
    }

    val pedalNameEditState = remember(pedal.id) {
        mutableStateListOf(pedal.name)
    }

    val knobNamesEditState = remember(pedal.id) {
        mutableStateListOf(*pedal.knobs.map { it.name }.toTypedArray())
    }

    LaunchedEffect(pedal.id) {
        knobsList.clear()
        knobsList.addAll(pedal.knobs)

        pedalNameEditState.clear()
        pedalNameEditState.add(pedal.name)

        knobNamesEditState.clear()
        knobNamesEditState.addAll(pedal.knobs.map { it.name })
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = pedalNameEditState[0],
                onValueChange = { newName ->
                    pedalNameEditState[0] = newName
                    onPedalNameChange(newName)
                },
                label = { Text("페달 이름") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.slot_number, slotIndex + 1),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (knobsList.isNotEmpty()) {
            Text(
                text = stringResource(R.string.knobs),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (knobsList.size < 6) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(56.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    val newKnob = Knob(name = "Knob ${knobsList.size + 1}", value = 5f)
                                    knobsList.add(newKnob)
                                    knobNamesEditState.add(newKnob.name)
                                    onKnobsChange(knobsList.toList())
                                },
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = stringResource(R.string.add_knob),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.add_knob),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                itemsIndexed(knobsList) { index, knob ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RotaryKnob(
                            value = 5f,
                            onValueChange = { },
                            label = "",
                            size = 56.dp,
                            enabled = false
                        )
                        OutlinedTextField(
                            value = knobNamesEditState[index],
                            onValueChange = { newName ->
                                knobNamesEditState[index] = newName
                                onKnobNameChange(index, newName)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .width(100.dp)
                                .height(56.dp)
                        )
                        IconButton(
                            onClick = {
                                if (knobsList.size > 1) {
                                    knobsList.removeAt(index)
                                    knobNamesEditState.removeAt(index)
                                    onKnobsChange(knobsList.toList())
                                }
                            },
                            modifier = Modifier.size(32.dp),
                            enabled = knobsList.size > 1
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "노브 삭제",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        PedalColorPicker(
            selectedColor = pedal.color,
            onColorSelected = onColorChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InlinePedalEditorPreview() {
    ToneStoreTheme {
        InlinePedalEditor(
            pedal = Pedal(
                id = "1",
                name = "Overdrive",
                type = PedalType.PRESET,
                knobs = listOf(
                    Knob("Gain", 5f),
                    Knob("Tone", 5f),
                    Knob("Level", 5f)
                ),
                order = 0,
                isEnabled = true,
                color = null
            ),
            slotIndex = 0,
            onDismiss = {},
            onColorChange = {},
            onKnobsChange = {},
            onPedalNameChange = {},
            onKnobNameChange = { _, _ -> }
        )
    }
}
