package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.Knob
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.presentation.ui.components.RotaryKnob
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianIconButton
import com.haero.tonestore.ui.designsystem.ObsidianTextField
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
            .background(Obsidian.colors.bgSecondary)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ObsidianTextField(
                value = pedalNameEditState[0],
                onValueChange = { newName ->
                    pedalNameEditState[0] = newName
                    onPedalNameChange(newName)
                },
                placeholder = stringResource(R.string.pedal_name),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            ObsidianIconButton(
                onClick = onDismiss,
                icon = Icons.Default.Close,
                tint = Obsidian.colors.textSecondary
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (knobsList.isNotEmpty()) {
            Text(
                text = stringResource(R.string.knobs),
                style = Obsidian.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Obsidian.colors.textPrimary,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 24.dp)
            ) {
                if (knobsList.size < 6) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(64.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Obsidian.colors.surfaceHighlight)
                                    .clickable {
                                        val newKnob = Knob(name = "Knob ${knobsList.size + 1}", value = 5f)
                                        knobsList.add(newKnob)
                                        knobNamesEditState.add(newKnob.name)
                                        onKnobsChange(knobsList.toList())
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = stringResource(R.string.add_knob),
                                    modifier = Modifier.size(24.dp),
                                    tint = Obsidian.colors.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.add_knob),
                                style = Obsidian.typography.labelSmall,
                                color = Obsidian.colors.textSecondary
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
                            size = 64.dp,
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ObsidianTextField(
                            value = knobNamesEditState[index],
                            onValueChange = { newName ->
                                knobNamesEditState[index] = newName
                                onKnobNameChange(index, newName)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .width(100.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        ObsidianIconButton(
                            onClick = {
                                if (knobsList.size > 1) {
                                    knobsList.removeAt(index)
                                    knobNamesEditState.removeAt(index)
                                    onKnobsChange(knobsList.toList())
                                }
                            },
                            icon = Icons.Default.Close,
                            modifier = Modifier.size(24.dp),
                            tint = Obsidian.colors.textMuted,
                            enabled = knobsList.size > 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.pedal_color),
            style = Obsidian.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Obsidian.colors.textPrimary,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PedalColorPicker(
            selectedColor = pedal.color,
            onColorSelected = onColorChange,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
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
