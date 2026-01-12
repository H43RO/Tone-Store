package com.haero.tonestore.presentation.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * 실제 앰프/이펙터 노브를 모방한 회전 노브 컴포넌트
 *
 * @param value 현재 값 (0-10)
 * @param onValueChange 값 변경 콜백
 * @param label 노브 아래에 표시될 레이블
 * @param modifier Modifier
 * @param size 노브 크기
 * @param enabled 활성화 여부
 */
@Composable
fun RotaryKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    enabled: Boolean = true
) {
    val view = LocalView.current
    
    // 노브 회전 각도 범위: -135° ~ +135° (총 270°)
    val startAngle = -135f
    val sweepAngle = 270f
    
    // 현재 값에 따른 각도 계산
    val normalizedValue = (value / 10f).coerceIn(0f, 1f)
    val currentAngle = startAngle + (normalizedValue * sweepAngle)
    
    // 드래그 중 이전 각도 저장
    var previousAngle by remember { mutableFloatStateOf(0f) }
    
    val knobColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val trackColor = if (enabled) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val indicatorColor = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.outline
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    
                    detectDragGestures(
                        onDragStart = { offset ->
                            val centerX = this.size.width / 2f
                            val centerY = this.size.height / 2f
                            previousAngle = atan2(
                                offset.y - centerY,
                                offset.x - centerX
                            ) * (180f / PI.toFloat())
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            
                            val centerX = this.size.width / 2f
                            val centerY = this.size.height / 2f
                            val currentDragAngle = atan2(
                                change.position.y - centerY,
                                change.position.x - centerX
                            ) * (180f / PI.toFloat())
                            
                            var delta = currentDragAngle - previousAngle
                            
                            // 각도 점프 처리 (180° 경계)
                            if (delta > 180) delta -= 360
                            if (delta < -180) delta += 360
                            
                            // 민감도 조절
                            val sensitivity = 0.5f
                            val newValue = (value + delta * sensitivity / 27f).coerceIn(0f, 10f)
                            
                            // 정수 단위 변경 시 햅틱 피드백
                            if (newValue.toInt() != value.toInt()) {
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            }
                            
                            onValueChange(newValue)
                            previousAngle = currentDragAngle
                        }
                    )
                }
        ) {
            val strokeWidth = size.toPx() * 0.12f
            val radius = (size.toPx() - strokeWidth) / 2f
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            
            // 배경 트랙 (전체 범위)
            drawArc(
                color = trackColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
            )
            
            // 활성 트랙 (현재 값까지)
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
            
            // 포인터 (현재 위치 표시)
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
        
        // 현재 값 표시
        Text(
            text = String.format("%.1f", value),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
        )
        
        // 레이블
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
