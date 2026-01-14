package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.presentation.ui.components.RotaryKnob

/**
 * 페달 편집 바텀 시트
 * 페달 슬롯을 탭하면 노브 조절, 활성화 토글, 삭제 등을 할 수 있음
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PedalEditorBottomSheet(
    pedal: Pedal,
    slotIndex: Int,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onKnobChange: (knobIndex: Int, value: Float) -> Unit,
    onToggleEnabled: () -> Unit,
    onRemove: () -> Unit,
    onMoveToSlot: (toIndex: Int) -> Unit,
    availableSlots: List<Int>,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // 헤더: 페달 이름 + 닫기 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 전원 LED
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (pedal.isEnabled) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = pedal.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "닫기")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 슬롯 위치 표시
            Text(
                text = "슬롯 ${slotIndex + 1}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 노브 섹션
            Text(
                text = stringResource(R.string.knobs),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                pedal.knobs.forEachIndexed { index, knob ->
                    RotaryKnob(
                        value = knob.value,
                        onValueChange = { onKnobChange(index, it) },
                        label = knob.name,
                        size = 72.dp,
                        enabled = pedal.isEnabled
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 이동 가능한 슬롯이 있으면 표시
            if (availableSlots.isNotEmpty()) {
                Text(
                    text = "다른 슬롯으로 이동",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableSlots.forEach { targetSlot ->
                        OutlinedButton(
                            onClick = { onMoveToSlot(targetSlot) },
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("${targetSlot + 1}")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // 액션 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 전원 토글 버튼
                OutlinedButton(
                    onClick = onToggleEnabled,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Power,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (pedal.isEnabled) "끄기" else "켜기")
                }
                
                // 삭제 버튼
                Button(
                    onClick = onRemove,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.delete))
                }
            }
        }
    }
}
