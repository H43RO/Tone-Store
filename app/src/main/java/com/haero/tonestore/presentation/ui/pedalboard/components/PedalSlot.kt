package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType

/**
 * 페달보드 그리드의 슬롯 컴포넌트
 * 실제 이펙터 페달 모양을 모방 (세로로 긴 직사각형)
 */
@Composable
fun PedalSlot(
    index: Int,
    pedal: Pedal?,
    showAddButton: Boolean,
    onAddClick: () -> Unit,
    onPedalClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (pedal != null) {
                    Color.Transparent
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                }
            )
            .then(
                if (pedal != null) {
                    Modifier.clickable(enabled = isEditable) { onPedalClick() }
                } else if (showAddButton && isEditable) {
                    Modifier
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onAddClick() }
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            pedal != null -> {
                MiniPedalCard(
                    pedal = pedal,
                    modifier = Modifier.fillMaxSize()
                )
            }
            showAddButton && isEditable -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "페달 추가",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "추가",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * 미니 페달 카드 - 실제 PedalCard 스타일을 따르되 노브 이름 생략
 */
@Composable
private fun MiniPedalCard(
    pedal: Pedal,
    modifier: Modifier = Modifier
) {
    val cardColor = if (pedal.isEnabled) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    }
    
    val borderColor = when {
        !pedal.isEnabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        pedal.type == PedalType.PRESET -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
    }
    
    Column(
        modifier = modifier
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .background(cardColor, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 페달 이름
        Text(
            text = pedal.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (pedal.isEnabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // 미니 노브들 (값만 표시, 이름 생략)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            pedal.knobs.take(4).forEach { knob ->
                MiniKnob(
                    value = knob.value,
                    enabled = pedal.isEnabled,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
        
        // 노브가 4개 초과면 두 번째 줄
        if (pedal.knobs.size > 4) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                pedal.knobs.drop(4).take(4).forEach { knob ->
                    MiniKnob(
                        value = knob.value,
                        enabled = pedal.isEnabled,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 전원 LED
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(
                    if (pedal.isEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
        )
    }
}

/**
 * 미니 노브 - 값을 시각적으로 표현 (노브 이름 생략)
 */
@Composable
private fun MiniKnob(
    value: Float,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val knobColor = if (enabled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }
    
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(knobColor.copy(alpha = 0.2f))
            .border(1.5.dp, knobColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // 값 텍스트 (소수점 없이)
        Text(
            text = value.toInt().toString(),
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
            fontWeight = FontWeight.Bold,
            color = knobColor
        )
    }
}
