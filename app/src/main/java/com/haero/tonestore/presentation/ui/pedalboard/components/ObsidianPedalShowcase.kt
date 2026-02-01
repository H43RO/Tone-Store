package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
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

// ═══════════════════════════════════════════════════════════════
// 기존 MiniPedalCard + 음영 버전 쇼케이스
// ═══════════════════════════════════════════════════════════════

data class DemoPedal(
    val name: String,
    val color: Color,
    val knobs: Int = 3
)

// 유명 페달 색상들
val allDemoPedals = listOf(
    DemoPedal("DS-1", Color(0xFFFF6B00), 3),           // Orange - Boss DS-1
    DemoPedal("Tube Screamer", Color(0xFF4CAF50), 3), // Green - Ibanez TS
    DemoPedal("Klon", Color(0xFFD4AF37), 3),          // Gold - Klon Centaur
    DemoPedal("DD-7", Color(0xFF1976D2), 3),          // Blue - Boss Delay
    DemoPedal("Big Muff", Color(0xFF424242), 3),      // Dark Gray - EHX
    DemoPedal("Phase 90", Color(0xFFFF9800), 1),      // Orange - MXR
    DemoPedal("CE-2", Color(0xFF03A9F4), 2),          // Light Blue - Boss Chorus
    DemoPedal("Dyna Comp", Color(0xFFF44336), 2),     // Red - MXR
)

private fun Color.isLight(): Boolean {
    val luminance = 0.299f * red + 0.587f * green + 0.114f * blue
    return luminance > 0.5f
}

private fun Color.darken(factor: Float = 0.8f): Color = copy(
    red = (red * factor).coerceIn(0f, 1f),
    green = (green * factor).coerceIn(0f, 1f),
    blue = (blue * factor).coerceIn(0f, 1f)
)

private fun Color.lighten(factor: Float = 1.2f): Color = copy(
    red = (red * factor).coerceIn(0f, 1f),
    green = (green * factor).coerceIn(0f, 1f),
    blue = (blue * factor).coerceIn(0f, 1f)
)

// ═══════════════════════════════════════════════════════════════
// 기존 스타일 (음영 없음) - 비교용
// ═══════════════════════════════════════════════════════════════

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PedalOriginal(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val isLight = color.isLight()
    val contentColor = if (isLight) Color.Black else Color.White
    val borderColor = if (isLight) color.darken(0.7f) else color.lighten(1.3f)

    Column(
        modifier = modifier
            .width(90.dp)
            .height(120.dp)
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .background(color, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(knobs) {
                MiniKnob(isLightBg = isLight)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 음영 버전 - 위→아래 그라데이션
// ═══════════════════════════════════════════════════════════════

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PedalWithShading(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val isLight = color.isLight()
    val contentColor = if (isLight) Color.Black else Color.White
    val borderColor = if (isLight) color.darken(0.7f) else color.lighten(1.3f)
    
    // 음영: 위는 밝게, 아래는 어둡게
    val topColor = color.lighten(1.1f)
    val bottomColor = color.darken(0.85f)

    Column(
        modifier = modifier
            .width(90.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(listOf(topColor, bottomColor))
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(knobs) {
                MiniKnob(isLightBg = isLight)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 음영 + 그림자 버전
// ═══════════════════════════════════════════════════════════════

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PedalWithShadingAndShadow(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val isLight = color.isLight()
    val contentColor = if (isLight) Color.Black else Color.White
    val borderColor = if (isLight) color.darken(0.7f) else color.lighten(1.3f)
    
    val topColor = color.lighten(1.1f)
    val bottomColor = color.darken(0.85f)

    Column(
        modifier = modifier
            .width(90.dp)
            .height(120.dp)
            .shadow(6.dp, RoundedCornerShape(8.dp), spotColor = color.darken(0.5f))
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(listOf(topColor, bottomColor))
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(knobs) {
                MiniKnob(isLightBg = isLight)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 기존 MiniKnob 그대로
// ═══════════════════════════════════════════════════════════════

@Composable
private fun MiniKnob(
    size: Dp = 18.dp,
    isLightBg: Boolean = false
) {
    val knobColor = Color.Black
    val trackColor = Color.Black.copy(alpha = 0.3f)
    val indicatorColor = Color.White

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

        drawCircle(
            color = knobColor,
            radius = radius * 0.6f,
            center = center
        )

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

// ═══════════════════════════════════════════════════════════════
// 📱 PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFF0C0C0E, heightDp = 900)
@Composable
private fun PedalShadingComparisonPreview() {
    ObsidianTheme {
        ObsidianBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text(
                        "기존 디자인 vs 음영 적용",
                        style = Obsidian.typography.headlineMedium,
                        color = Obsidian.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // 기존 (Original)
                item {
                    SectionTitle("기존 (플랫)")
                    PedalRow { p -> PedalOriginal(p.name, p.color, p.knobs) }
                }
                
                // 음영 적용
                item {
                    SectionTitle("음영 적용 (위 밝음 → 아래 어두움)")
                    PedalRow { p -> PedalWithShading(p.name, p.color, p.knobs) }
                }
                
                // 음영 + 그림자
                item {
                    SectionTitle("음영 + 그림자")
                    PedalRow { p -> PedalWithShadingAndShadow(p.name, p.color, p.knobs) }
                }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Obsidian.colors.textSecondary
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun PedalRow(content: @Composable (DemoPedal) -> Unit) {
    // 4개씩 두 줄로
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allDemoPedals.take(4).forEach { content(it) }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allDemoPedals.drop(4).forEach { content(it) }
        }
    }
}
