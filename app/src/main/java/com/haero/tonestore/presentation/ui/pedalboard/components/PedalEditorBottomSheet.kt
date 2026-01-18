package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.presentation.ui.components.RotaryKnob

/**
 * 페달 편집 바텀 시트
 * 페달보드 템플릿에서는 노브 확인, 색상 변경, 삭제 가능
 * (노브 값/ON-OFF는 곡별 설정에서 조절)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PedalEditorBottomSheet(
    pedal: Pedal,
    slotIndex: Int,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onRemove: () -> Unit,
    onColorChange: (Long?) -> Unit,
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
                .verticalScroll(rememberScrollState())
        ) {
            // 헤더: 페달 이름 + 닫기 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pedal.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
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

            Spacer(modifier = Modifier.height(16.dp))

            // 노브 UI (읽기 전용 - 라벨 포함)
            if (pedal.knobs.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.knobs),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    pedal.knobs.forEach { knob ->
                        RotaryKnob(
                            value = 5f, // 기본값 중간으로 표시
                            onValueChange = { }, // 읽기 전용
                            label = knob.name,
                            size = 56.dp,
                            enabled = false // 비활성화하여 조작 불가
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // 색상 선택
            PedalColorPicker(
                selectedColor = pedal.color,
                onColorSelected = onColorChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 삭제 버튼
            Button(
                onClick = onRemove,
                modifier = Modifier.fillMaxWidth(),
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
