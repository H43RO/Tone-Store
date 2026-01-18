package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R

/**
 * 페달 색상을 나타내는 프리셋
 */
data class PedalColorPreset(
    val name: String,
    val color: Long
)

/**
 * 프리셋 색상 목록
 */
val presetColors = listOf(
    PedalColorPreset("Red", 0xFFE53935),
    PedalColorPreset("Orange", 0xFFFF9800),
    PedalColorPreset("Yellow", 0xFFFFEB3B),
    PedalColorPreset("Lime", 0xFFCDDC39),
    PedalColorPreset("Green", 0xFF4CAF50),
    PedalColorPreset("Teal", 0xFF009688),
    PedalColorPreset("Cyan", 0xFF00BCD4),
    PedalColorPreset("Blue", 0xFF2196F3),
    PedalColorPreset("Indigo", 0xFF3F51B5),
    PedalColorPreset("Purple", 0xFF9C27B0),
    PedalColorPreset("Pink", 0xFFE91E63),
    PedalColorPreset("Brown", 0xFF795548),
    PedalColorPreset("Gray", 0xFF9E9E9E),
    PedalColorPreset("Black", 0xFF212121),
    PedalColorPreset("White", 0xFFFAFAFA)
)

/**
 * 페달 색상 선택 UI
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PedalColorPicker(
    selectedColor: Long?,
    onColorSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomColorDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.pedal_color),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 프리셋 색상 그리드
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 기본 색상 (색상 없음) 옵션
            ColorCircle(
                color = null,
                isSelected = selectedColor == null,
                onClick = { onColorSelected(null) }
            )

            // 프리셋 색상들
            presetColors.forEach { preset ->
                ColorCircle(
                    color = preset.color,
                    isSelected = selectedColor == preset.color,
                    onClick = { onColorSelected(preset.color) }
                )
            }

            // 커스텀 색상 버튼
            CustomColorButton(
                currentColor = selectedColor,
                onClick = { showCustomColorDialog = true }
            )
        }
    }

    // 커스텀 색상 다이얼로그
    if (showCustomColorDialog) {
        CustomColorDialog(
            initialColor = selectedColor,
            onConfirm = { color ->
                onColorSelected(color)
                showCustomColorDialog = false
            },
            onDismiss = { showCustomColorDialog = false }
        )
    }
}

/**
 * 색상 원 컴포넌트
 */
@Composable
private fun ColorCircle(
    color: Long?,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayColor = color?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(displayColor)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (isLightColor(color)) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        // 기본 색상 표시 (색상 없음)
        if (color == null && !isSelected) {
            Text(
                text = "–",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 커스텀 색상 버튼
 */
@Composable
private fun CustomColorButton(
    currentColor: Long?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCustomColor = currentColor != null && presetColors.none { it.color == currentColor }
    val displayColor = if (isCustomColor) Color(currentColor!!) else null

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .then(
                if (displayColor != null) {
                    Modifier.background(displayColor)
                } else {
                    Modifier.background(
                        brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                            colors = listOf(
                                Color.Red,
                                Color.Yellow,
                                Color.Green,
                                Color.Cyan,
                                Color.Blue,
                                Color.Magenta,
                                Color.Red
                            )
                        )
                    )
                }
            )
            .border(
                width = if (isCustomColor) 3.dp else 1.dp,
                color = if (isCustomColor) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                },
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isCustomColor) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (isLightColor(currentColor)) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * 커스텀 색상 선택 다이얼로그 - 컬러 팔레트 형태
 */
@Composable
private fun CustomColorDialog(
    initialColor: Long?,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    // 초기 색상에서 HSV 추출
    val initial = initialColor ?: 0xFF2196F3
    val initialHsv = remember {
        val r = ((initial shr 16) and 0xFF).toFloat() / 255f
        val g = ((initial shr 8) and 0xFF).toFloat() / 255f
        val b = (initial and 0xFF).toFloat() / 255f
        rgbToHsv(r, g, b)
    }

    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }

    val currentColor = remember(hue, saturation, value) {
        hsvToColor(hue, saturation, value)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.custom_color)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 색상 미리보기
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(currentColor))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 컬러 팔레트 (Saturation x Value)
                ColorPalette(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onSaturationValueChange = { s, v ->
                        saturation = s
                        value = v
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Hue 슬라이더 (색상환)
                HueSlider(
                    hue = hue,
                    onHueChange = { hue = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(currentColor) }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 컬러 팔레트 (Saturation x Value 2D 선택)
 */
@Composable
private fun ColorPalette(
    hue: Float,
    saturation: Float,
    value: Float,
    onSaturationValueChange: (saturation: Float, value: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val s = (offset.x / size.width).coerceIn(0f, 1f)
                    val v = 1f - (offset.y / size.height).coerceIn(0f, 1f)
                    onSaturationValueChange(s, v)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val s = (change.position.x / size.width).coerceIn(0f, 1f)
                    val v = 1f - (change.position.y / size.height).coerceIn(0f, 1f)
                    onSaturationValueChange(s, v)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 배경: 흰색 -> 색상 (가로 그라데이션)
            val pureColor = Color.hsv(hue, 1f, 1f)
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.White, pureColor)
                )
            )
            // 오버레이: 투명 -> 검정 (세로 그라데이션)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black)
                )
            )

            // 선택 포인터
            val pointerX = saturation * this.size.width
            val pointerY = (1f - value) * this.size.height
            val pointerRadius = 10.dp.toPx()

            // 외곽선 (흰색)
            drawCircle(
                color = Color.White,
                radius = pointerRadius + 2.dp.toPx(),
                center = Offset(pointerX, pointerY),
                style = Stroke(width = 3.dp.toPx())
            )
            // 내부 원
            drawCircle(
                color = Color.hsv(hue, saturation, value),
                radius = pointerRadius,
                center = Offset(pointerX, pointerY)
            )
        }
    }
}

/**
 * Hue 슬라이더 (색상환)
 */
@Composable
private fun HueSlider(
    hue: Float,
    onHueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var width by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .onSizeChanged { width = it.width }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newHue = (offset.x / width * 360f).coerceIn(0f, 360f)
                    onHueChange(newHue)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val newHue = (change.position.x / width * 360f).coerceIn(0f, 360f)
                    onHueChange(newHue)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 색상환 그라데이션
            val hueColors = (0..360 step 30).map { Color.hsv(it.toFloat(), 1f, 1f) }
            drawRect(
                brush = Brush.horizontalGradient(colors = hueColors)
            )

            // 선택 포인터
            val pointerX = (hue / 360f) * this.size.width
            val pointerHeight = this.size.height

            // 포인터 (세로 막대)
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(pointerX - 6.dp.toPx(), 0f),
                size = Size(12.dp.toPx(), pointerHeight),
                cornerRadius = CornerRadius(4.dp.toPx()),
                style = Stroke(width = 3.dp.toPx())
            )
        }
    }
}

/**
 * RGB to HSV 변환
 */
private fun rgbToHsv(r: Float, g: Float, b: Float): FloatArray {
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val h = when {
        delta == 0f -> 0f
        max == r -> 60f * (((g - b) / delta) % 6)
        max == g -> 60f * (((b - r) / delta) + 2)
        else -> 60f * (((r - g) / delta) + 4)
    }.let { if (it < 0) it + 360f else it }

    val s = if (max == 0f) 0f else delta / max
    val v = max

    return floatArrayOf(h, s, v)
}

/**
 * HSV to Color (Long) 변환
 */
private fun hsvToColor(h: Float, s: Float, v: Float): Long {
    val c = v * s
    val x = c * (1 - kotlin.math.abs((h / 60f) % 2 - 1))
    val m = v - c

    val (r, g, b) = when {
        h < 60 -> Triple(c, x, 0f)
        h < 120 -> Triple(x, c, 0f)
        h < 180 -> Triple(0f, c, x)
        h < 240 -> Triple(0f, x, c)
        h < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    val red = ((r + m) * 255).toInt()
    val green = ((g + m) * 255).toInt()
    val blue = ((b + m) * 255).toInt()

    return (0xFFL shl 24) or (red.toLong() shl 16) or (green.toLong() shl 8) or blue.toLong()
}

/**
 * 밝은 색상인지 판단 (텍스트 색상 결정용)
 */
private fun isLightColor(color: Long?): Boolean {
    if (color == null) return true
    val r = ((color shr 16) and 0xFF).toFloat()
    val g = ((color shr 8) and 0xFF).toFloat()
    val b = (color and 0xFF).toFloat()
    // 상대 휘도 계산
    val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255
    return luminance > 0.5
}
