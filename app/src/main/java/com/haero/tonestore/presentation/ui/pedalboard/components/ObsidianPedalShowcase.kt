package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianBackground
import com.haero.tonestore.ui.designsystem.ObsidianTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 유명 페달들의 시그니처 색상 레퍼런스
 *
 * Distortion/Overdrive:
 * - Boss DS-1 Distortion: Orange (#FF6B00)
 * - Ibanez Tube Screamer: Green (#4CAF50)
 * - ProCo RAT: Black with yellow text
 * - Boss BD-2 Blues Driver: Blue (#2196F3)
 * - Fulltone OCD: Cream/Tan (#D4C4A8)
 * - Klon Centaur: Gold (#FFD700)
 *
 * Delay/Reverb:
 * - Boss DD-3 Digital Delay: Blue (#1976D2)
 * - MXR Carbon Copy: Green (#2E7D32)
 * - TC Electronic Flashback: Orange-Red (#FF5722)
 * - Strymon BigSky: Blue-Gray (#607D8B)
 *
 * Modulation:
 * - Boss CE-2 Chorus: Light Blue (#03A9F4)
 * - MXR Phase 90: Orange (#FF9800)
 * - Boss BF-3 Flanger: Purple (#9C27B0)
 * - EHX Small Clone: Teal (#009688)
 *
 * Compressor/EQ:
 * - Boss CS-3: Blue (#1565C0)
 * - MXR Dyna Comp: Red (#F44336)
 * - Boss GE-7 EQ: White/Silver (#E0E0E0)
 *
 * Fuzz:
 * - Big Muff Pi: Silver/Black (#424242)
 * - Fuzz Face: Red/Blue variants
 *
 * Wah:
 * - Dunlop Cry Baby: Black (#212121)
 * - Vox Wah: Chrome/Black
 */

data class ShowcasePedal(
    val name: String,
    val category: String,
    val primaryColor: Color,
    val knobCount: Int = 3,
    val knobValues: List<Float> = listOf(0.5f, 0.7f, 0.3f)
)

val showcasePedals = listOf(
    // Distortion/Overdrive
    ShowcasePedal("DS-1", "Distortion", Color(0xFFFF6B00), 3, listOf(0.6f, 0.7f, 0.5f)),
    ShowcasePedal("Tube Screamer", "Overdrive", Color(0xFF4CAF50), 3, listOf(0.4f, 0.8f, 0.6f)),
    ShowcasePedal("Blues Driver", "Overdrive", Color(0xFF2196F3), 2, listOf(0.5f, 0.6f)),
    ShowcasePedal("Klon", "Overdrive", Color(0xFFD4AF37), 3, listOf(0.3f, 0.5f, 0.7f)),

    // Delay/Reverb
    ShowcasePedal("DD-3", "Delay", Color(0xFF1976D2), 3, listOf(0.4f, 0.6f, 0.5f)),
    ShowcasePedal("Carbon Copy", "Delay", Color(0xFF2E7D32), 3, listOf(0.5f, 0.5f, 0.4f)),

    // Modulation
    ShowcasePedal("CE-2", "Chorus", Color(0xFF03A9F4), 2, listOf(0.6f, 0.5f)),
    ShowcasePedal("Phase 90", "Phaser", Color(0xFFFF9800), 1, listOf(0.5f)),

    // Compressor
    ShowcasePedal("Dyna Comp", "Compressor", Color(0xFFF44336), 2, listOf(0.7f, 0.4f)),

    // Fuzz
    ShowcasePedal("Big Muff", "Fuzz", Color(0xFF424242), 3, listOf(0.8f, 0.5f, 0.6f))
)

/**
 * Obsidian 테마에 맞는 새 페달 디자인
 * - 페달 고유 색상 유지
 * - 다크 테마와 조화로운 그라데이션
 * - 미니멀한 노브 디자인
 * - 세련된 그림자 효과
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ObsidianPedalCard(
    name: String,
    category: String,
    primaryColor: Color,
    knobValues: List<Float>,
    modifier: Modifier = Modifier
) {
    // 페달 색상 기반으로 그라데이션 생성
    val darkerColor = primaryColor.copy(
        red = (primaryColor.red * 0.7f).coerceIn(0f, 1f),
        green = (primaryColor.green * 0.7f).coerceIn(0f, 1f),
        blue = (primaryColor.blue * 0.7f).coerceIn(0f, 1f)
    )

    // 텍스트/노브 색상 결정 (밝은 배경 vs 어두운 배경)
    val luminance = 0.299f * primaryColor.red + 0.587f * primaryColor.green + 0.114f * primaryColor.blue
    val contentColor = if (luminance > 0.5f) Color.Black.copy(alpha = 0.9f) else Color.White
    val secondaryContentColor = if (luminance > 0.5f) Color.Black.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .width(100.dp)
            .height(130.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = primaryColor.copy(alpha = 0.4f),
                ambientColor = Color.Black.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        primaryColor,
                        darkerColor
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color.Black.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 카테고리 라벨
            Text(
                text = category.uppercase(),
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = secondaryContentColor,
                letterSpacing = 1.sp
            )

            // 노브 영역
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                knobValues.forEach { value ->
                    ObsidianMiniKnob(
                        value = value,
                        knobColor = contentColor,
                        indicatorColor = if (luminance > 0.5f) primaryColor else Color.White,
                        size = 22.dp
                    )
                }
            }

            // 페달 이름
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 풋스위치 (LED 포함)
            ObsidianFootswitch(
                isOn = true,
                ledColor = if (luminance > 0.5f) Color.Red else primaryColor.copy(
                    red = (primaryColor.red * 1.3f).coerceIn(0f, 1f),
                    green = (primaryColor.green * 1.3f).coerceIn(0f, 1f),
                    blue = (primaryColor.blue * 1.3f).coerceIn(0f, 1f)
                )
            )
        }
    }
}

@Composable
private fun ObsidianMiniKnob(
    value: Float,
    knobColor: Color,
    indicatorColor: Color,
    size: Dp = 20.dp
) {
    val startAngle = 135f  // 7시 방향
    val sweepAngle = 270f  // 5시 방향까지
    val currentAngle = startAngle + (value * sweepAngle)

    Canvas(modifier = Modifier.size(size)) {
        val strokeWidth = size.toPx() * 0.1f
        val radius = (size.toPx() - strokeWidth) / 2f
        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)

        // 노브 베이스 (원형)
        drawCircle(
            color = knobColor,
            radius = radius * 0.85f,
            center = center
        )

        // 노브 하이라이트 (상단)
        drawCircle(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.3f),
                    Color.Transparent
                ),
                startY = 0f,
                endY = size.toPx() * 0.5f
            ),
            radius = radius * 0.75f,
            center = center
        )

        // 포인터 (값 표시)
        val pointerAngle = currentAngle * (PI.toFloat() / 180f)
        val pointerLength = radius * 0.6f
        val pointerStart = Offset(
            center.x + cos(pointerAngle) * (radius * 0.15f),
            center.y + sin(pointerAngle) * (radius * 0.15f)
        )
        val pointerEnd = Offset(
            center.x + cos(pointerAngle) * pointerLength,
            center.y + sin(pointerAngle) * pointerLength
        )
        drawLine(
            color = indicatorColor,
            start = pointerStart,
            end = pointerEnd,
            strokeWidth = strokeWidth * 0.8f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ObsidianFootswitch(
    isOn: Boolean,
    ledColor: Color = Color.Red,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // LED
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(
                    if (isOn) {
                        Brush.radialGradient(
                            colors = listOf(
                                ledColor,
                                ledColor.copy(alpha = 0.6f)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.5f),
                                Color.Gray.copy(alpha = 0.3f)
                            )
                        )
                    }
                )
        )

        Spacer(modifier = Modifier.width(6.dp))

        // 풋스위치 버튼
        Box(
            modifier = Modifier
                .size(width = 28.dp, height = 10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4A4A4A),
                            Color(0xFF2A2A2A)
                        )
                    )
                )
                .border(
                    width = 0.5.dp,
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(3.dp)
                )
        )
    }
}

// ===== Preview =====

@Preview(showBackground = true, backgroundColor = 0xFF0C0C0E)
@Composable
private fun ObsidianPedalShowcasePreview() {
    ObsidianTheme {
        ObsidianBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Obsidian Pedal Design",
                    style = Obsidian.typography.headlineMedium,
                    color = Obsidian.colors.textPrimary
                )

                Text(
                    text = "각 페달의 시그니처 색상 유지",
                    style = Obsidian.typography.bodySmall,
                    color = Obsidian.colors.textSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(110.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(showcasePedals) { pedal ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ObsidianPedalCard(
                                name = pedal.name,
                                category = pedal.category,
                                primaryColor = pedal.primaryColor,
                                knobValues = pedal.knobValues
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = pedal.category,
                                fontSize = 10.sp,
                                color = Obsidian.colors.textMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0C0C0E)
@Composable
private fun SinglePedalPreview() {
    ObsidianTheme {
        Box(
            modifier = Modifier
                .background(Obsidian.colors.bgPrimary)
                .padding(24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // DS-1 (Orange)
                ObsidianPedalCard(
                    name = "DS-1",
                    category = "Distortion",
                    primaryColor = Color(0xFFFF6B00),
                    knobValues = listOf(0.6f, 0.7f, 0.5f)
                )

                // Tube Screamer (Green)
                ObsidianPedalCard(
                    name = "Tube Screamer",
                    category = "Overdrive",
                    primaryColor = Color(0xFF4CAF50),
                    knobValues = listOf(0.4f, 0.8f, 0.6f)
                )

                // Klon (Gold)
                ObsidianPedalCard(
                    name = "Klon",
                    category = "Overdrive",
                    primaryColor = Color(0xFFD4AF37),
                    knobValues = listOf(0.3f, 0.5f, 0.7f)
                )
            }
        }
    }
}
