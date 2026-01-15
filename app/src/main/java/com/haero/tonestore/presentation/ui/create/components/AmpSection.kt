package com.haero.tonestore.presentation.ui.create.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.AmpSetting
import com.haero.tonestore.presentation.ui.components.RotaryKnob
import com.haero.tonestore.presentation.ui.components.SectionHeader

/**
 * 앰프 세팅 섹션 컴포넌트
 * 실제 앰프 헤드의 노브 배치를 모방
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AmpSection(
    ampSetting: AmpSetting,
    onAmpModelChange: (String) -> Unit,
    onKnobChange: (knobName: String, value: Float) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    var isExpanded by remember { mutableStateOf(true) }

    SectionHeader(
        title = stringResource(R.string.amp_setting),
        isExpanded = isExpanded,
        onToggle = { isExpanded = isExpanded.not() },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // 앰프 모델명 입력
            if (isEditable) {
                OutlinedTextField(
                    value = ampSetting.ampModel ?: "",
                    onValueChange = onAmpModelChange,
                    label = { Text(stringResource(R.string.amp_model_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else if (ampSetting.ampModel.isNullOrBlank().not()) {
                Text(
                    text = ampSetting.ampModel,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 앰프 노브 패널 (앰프 헤드 스타일)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Gain
                    RotaryKnob(
                        value = ampSetting.gain,
                        onValueChange = { if (isEditable) onKnobChange("gain", it) },
                        label = "Gain",
                        size = 60.dp,
                        enabled = isEditable
                    )

                    // Bass
                    RotaryKnob(
                        value = ampSetting.bass,
                        onValueChange = { if (isEditable) onKnobChange("bass", it) },
                        label = "Bass",
                        size = 60.dp,
                        enabled = isEditable
                    )

                    // Middle
                    RotaryKnob(
                        value = ampSetting.middle,
                        onValueChange = { if (isEditable) onKnobChange("middle", it) },
                        label = "Middle",
                        size = 60.dp,
                        enabled = isEditable
                    )

                    // Treble
                    RotaryKnob(
                        value = ampSetting.treble,
                        onValueChange = { if (isEditable) onKnobChange("treble", it) },
                        label = "Treble",
                        size = 60.dp,
                        enabled = isEditable
                    )

                    // Presence
                    RotaryKnob(
                        value = ampSetting.presence,
                        onValueChange = { if (isEditable) onKnobChange("presence", it) },
                        label = "Presence",
                        size = 60.dp,
                        enabled = isEditable
                    )

                    // Reverb
                    RotaryKnob(
                        value = ampSetting.reverb,
                        onValueChange = { if (isEditable) onKnobChange("reverb", it) },
                        label = "Reverb",
                        size = 60.dp,
                        enabled = isEditable
                    )

                    // Master Volume
                    RotaryKnob(
                        value = ampSetting.masterVolume,
                        onValueChange = { if (isEditable) onKnobChange("masterVolume", it) },
                        label = "Master",
                        size = 60.dp,
                        enabled = isEditable
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
