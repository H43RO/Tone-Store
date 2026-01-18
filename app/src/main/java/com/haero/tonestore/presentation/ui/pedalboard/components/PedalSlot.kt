package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.presentation.ui.components.PedalColorUtils
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 페달보드 그리드의 슬롯 컴포넌트
 */
@Composable
fun PedalSlot(
    index: Int,
    pedal: Pedal?,
    showAddButton: Boolean,
    onAddClick: () -> Unit,
    onPedalClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true,
    isDragging: Boolean = false,
    isDropTarget: Boolean = false
) {
    val borderColor = when {
        isDropTarget -> MaterialTheme.colorScheme.primary
        isDragging -> MaterialTheme.colorScheme.tertiary
        else -> Color.Transparent
    }
    val borderWidth = if (isDropTarget || isDragging) 3.dp else 0.dp
    val backgroundColor = when {
        isDropTarget -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        pedal != null -> Color.Transparent
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(borderWidth, borderColor, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .then(
                if (pedal != null && isDragging.not()) {
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
 * 미니 페달 카드
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MiniPedalCard(pedal: Pedal, modifier: Modifier = Modifier) {
    // 사용자 지정 색상이 있으면 사용, 없으면 타입에 따른 기본 색상
    val backgroundColor = if (pedal.color != null) {
        Color(pedal.color)
    } else {
        when (pedal.type) {
            PedalType.PRESET -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        }
    }

    // 배경색 밝기에 따라 텍스트/노브 색상 결정
    val isLightBackground = PedalColorUtils.isLightColor(pedal.color)
    val contentColor = if (pedal.color != null) {
        if (isLightBackground) Color.Black else Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    // 입체감을 위한 Border 색상 계산
    val borderColor = PedalColorUtils.calculateBorderColor(backgroundColor)

    Column(
        modifier = modifier
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 페달 이름 (최대 2줄)
        Text(
            text = pedal.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        // 노브 UI
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            pedal.knobs.forEach { _ ->
                MiniKnobIndicator(contentColor = contentColor)
            }
        }
    }
}

/**
 * 노브 표시용 UI
 */
@Composable
private fun MiniKnobIndicator(
    size: Dp = 18.dp,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    val knobColor = contentColor
    val trackColor = contentColor.copy(alpha = 0.3f)
    val indicatorColor = if (PedalColorUtils.isLightColor(contentColor)) {
        Color.White
    } else {
        Color.Black
    }

    // 기본값 5.0 (중간)으로 표시
    val normalizedValue = 0.5f
    val startAngle = -90f
    val sweepAngle = 300f
    val currentAngle = startAngle + (normalizedValue * sweepAngle)

    Canvas(modifier = Modifier.size(size)) {
        val strokeWidth = size.toPx() * 0.12f
        val radius = (size.toPx() - strokeWidth) / 2f
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)

        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
        )

        drawArc(
            color = knobColor,
            startAngle = startAngle,
            sweepAngle = normalizedValue * sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
        )

        // 중앙 원 (노브 몸체)
        drawCircle(
            color = knobColor,
            radius = radius * 0.6f,
            center = center
        )

        // 포인터
        val pointerAngle = currentAngle * (PI.toFloat() / 180f)
        val pointerLength = radius * 0.4f
        val pointerStart = Offset(
            center.x + cos(pointerAngle) * (radius * 0.2f),
            center.y + sin(pointerAngle) * (radius * 0.2f)
        )
        val pointerEnd = Offset(
            center.x + cos(pointerAngle) * pointerLength,
            center.y + sin(pointerAngle) * pointerLength
        )
        drawLine(
            color = indicatorColor,
            start = pointerStart,
            end = pointerEnd,
            strokeWidth = strokeWidth * 0.4f,
            cap = StrokeCap.Round
        )
    }
}
