package com.haero.tonestore.presentation.ui.create.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.GuitarSetting
import com.haero.tonestore.domain.model.PickupPosition
import com.haero.tonestore.presentation.ui.components.RotaryKnob
import com.haero.tonestore.presentation.ui.components.SectionHeader
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianSurface
import com.haero.tonestore.ui.designsystem.ObsidianTextField

@Composable
fun GuitarSection(
    guitarSetting: GuitarSetting,
    onGuitarModelChange: (String) -> Unit,
    onPickupPositionChange: (PickupPosition) -> Unit,
    onToneChange: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(true) }

    SectionHeader(
        title = stringResource(R.string.guitar_setting),
        isExpanded = isExpanded,
        onToggle = { isExpanded = isExpanded.not() },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Obsidian.spacing.screenPadding)
        ) {
            if (isEditable) {
                Text(
                    text = stringResource(R.string.guitar_model_hint),
                    style = Obsidian.typography.labelMedium,
                    color = Obsidian.colors.textSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ObsidianTextField(
                    value = guitarSetting.guitarModel ?: "",
                    onValueChange = onGuitarModelChange,
                    placeholder = stringResource(R.string.guitar_model_hint),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
            } else if (!guitarSetting.guitarModel.isNullOrBlank()) {
                Text(
                    text = guitarSetting.guitarModel,
                    style = Obsidian.typography.headlineMedium,
                    color = Obsidian.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = stringResource(R.string.pickup_selector),
                style = Obsidian.typography.titleMedium,
                color = Obsidian.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            ObsidianSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Obsidian.radius.lg)
            ) {
                PickupSelector(
                    selectedPosition = guitarSetting.pickupSelector,
                    onPositionChange = onPickupPositionChange,
                    enabled = isEditable,
                    modifier = Modifier.padding(vertical = 20.dp, horizontal = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            ObsidianSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Obsidian.radius.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(Obsidian.spacing.lg),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RotaryKnob(
                        value = guitarSetting.volumeKnob,
                        onValueChange = { if (isEditable) onVolumeChange(it) },
                        label = "Volume",
                        size = 72.dp,
                        enabled = isEditable
                    )

                    RotaryKnob(
                        value = guitarSetting.toneKnob,
                        onValueChange = { if (isEditable) onToneChange(it) },
                        label = "Tone",
                        size = 72.dp,
                        enabled = isEditable
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PickupSelector(
    selectedPosition: PickupPosition,
    onPositionChange: (PickupPosition) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val positions = listOf(
        PickupPosition.NECK to "N",
        PickupPosition.NECK_MIDDLE to "N+M",
        PickupPosition.MIDDLE to "M",
        PickupPosition.MIDDLE_BRIDGE to "M+B",
        PickupPosition.BRIDGE to "B"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        positions.forEach { (position, label) ->
            val isSelected = selectedPosition == position

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(Obsidian.radius.button))
                    .clickable(enabled = enabled) { onPositionChange(position) }
                    .padding(4.dp)
            ) {
                ObsidianSurface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(Obsidian.radius.button),
                    elevation = if (isSelected) Obsidian.elevation.sm else 0.dp,
                    border = if (isSelected) Obsidian.colors.primaryLight else Obsidian.colors.borderSubtle
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Obsidian.colors.primary, RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = label,
                    style = Obsidian.typography.labelLarge,
                    color = if (isSelected) Obsidian.colors.primaryLight else Obsidian.colors.textMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
