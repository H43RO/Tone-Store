package com.haero.tonestore.presentation.ui.create.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.presentation.ui.components.RotaryKnob

/**
 * 이펙터 페달 카드 컴포넌트
 * 실제 이펙터 페달의 디자인을 모방
 *
 * @param pedal 페달 데이터
 * @param onKnobChange 노브 값 변경 콜백
 * @param onToggleEnabled 페달 활성화 토글 콜백
 * @param onRemove 페달 삭제 콜백
 * @param isEditable 편집 가능 여부 (상세 보기에서는 false)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PedalCard(
    pedal: Pedal,
    onKnobChange: (knobIndex: Int, value: Float) -> Unit,
    onToggleEnabled: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    val cardColor = if (pedal.isEnabled) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    }

    val borderColor = when {
        pedal.isEnabled.not() -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        pedal.type == PedalType.PRESET -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단: 페달 이름 + 삭제 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pedal.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (pedal.isEnabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    }
                )

                if (isEditable) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "삭제",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 노브들
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pedal.knobs.forEachIndexed { index, knob ->
                    RotaryKnob(
                        value = knob.value,
                        onValueChange = { newValue ->
                            if (isEditable) onKnobChange(index, newValue)
                        },
                        label = knob.name,
                        size = 56.dp,
                        enabled = pedal.isEnabled && isEditable,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // 전원 버튼 (Footswitch)
            if (isEditable) {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (pedal.isEnabled) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .clickable { onToggleEnabled() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Power,
                        contentDescription = if (pedal.isEnabled) "끄기" else "켜기",
                        tint = if (pedal.isEnabled) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
