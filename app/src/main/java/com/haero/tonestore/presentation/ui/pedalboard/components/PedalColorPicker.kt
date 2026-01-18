package com.haero.tonestore.presentation.ui.pedalboard.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
 * 커스텀 색상 선택 다이얼로그
 */
@Composable
private fun CustomColorDialog(
    initialColor: Long?,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    // 초기 색상에서 RGB 추출
    val initial = initialColor ?: 0xFF2196F3
    var red by remember { mutableFloatStateOf(((initial shr 16) and 0xFF).toFloat()) }
    var green by remember { mutableFloatStateOf(((initial shr 8) and 0xFF).toFloat()) }
    var blue by remember { mutableFloatStateOf((initial and 0xFF).toFloat()) }

    val currentColor = (0xFF shl 24) or (red.toInt() shl 16) or (green.toInt() shl 8) or blue.toInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.custom_color)) },
        text = {
            Column {
                // 색상 미리보기
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(currentColor.toLong()))
                )

                Spacer(modifier = Modifier.height(16.dp))

                // HEX 입력
                var hexText by remember(currentColor) {
                    mutableStateOf(String.format("%06X", currentColor and 0xFFFFFF))
                }
                OutlinedTextField(
                    value = hexText,
                    onValueChange = { value ->
                        val filtered = value.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }
                            .take(6)
                            .uppercase()
                        hexText = filtered
                        if (filtered.length == 6) {
                            runCatching {
                                val parsed = filtered.toLong(16)
                                red = ((parsed shr 16) and 0xFF).toFloat()
                                green = ((parsed shr 8) and 0xFF).toFloat()
                                blue = (parsed and 0xFF).toFloat()
                            }
                        }
                    },
                    label = { Text("HEX") },
                    prefix = { Text("#") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // RGB 슬라이더
                ColorSlider(
                    label = "R",
                    value = red,
                    onValueChange = { red = it },
                    color = Color.Red
                )
                ColorSlider(
                    label = "G",
                    value = green,
                    onValueChange = { green = it },
                    color = Color.Green
                )
                ColorSlider(
                    label = "B",
                    value = blue,
                    onValueChange = { blue = it },
                    color = Color.Blue
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(currentColor.toLong() or (0xFFL shl 24)) }) {
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
 * RGB 슬라이더 컴포넌트
 */
@Composable
private fun ColorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.width(24.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..255f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            )
        )
        Text(
            text = value.toInt().toString(),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(36.dp)
        )
    }
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
