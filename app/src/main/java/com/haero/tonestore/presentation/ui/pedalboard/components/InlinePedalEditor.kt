package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.Knob
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.presentation.ui.components.RotaryKnob

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
    val knobsList = remember {
        mutableStateListOf(*pedal.knobs.toTypedArray())
    }

    var pedalNameEditState = remember {
        mutableStateListOf(pedal.name)
    }

    var knobNamesEditState = remember {
        mutableStateListOf(*pedal.knobs.map { it.name }.toTypedArray())
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
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
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "닫기")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "슬롯 ${slotIndex + 1}",
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                                label = knobNamesEditState[index],
                                size = 56.dp,
                                enabled = false
                            )
                            OutlinedTextField(
                                value = knobNamesEditState[index],
                                onValueChange = { newName ->
                                    knobNamesEditState[index] = newName
                                    onKnobNameChange(index, newName)
                                },
                                label = { Text("노브 ${index + 1}") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .width(56.dp)
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
                                    Icons.Default.Delete,
                                    contentDescription = "노브 삭제",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            PedalColorPicker(
                selectedColor = pedal.color,
                onColorSelected = onColorChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
