package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

data class DemoPedal(
    val name: String,
    val category: String,
    val color: Color,
    val knobs: Int = 3
)

val demoPedals = listOf(
    DemoPedal("DS-1", "Distortion", Color(0xFFFF6B00)),
    DemoPedal("Tube Screamer", "Overdrive", Color(0xFF4CAF50)),
    DemoPedal("Klon", "Overdrive", Color(0xFFD4AF37)),
    DemoPedal("DD-7", "Delay", Color(0xFF1976D2)),
)

// 밝기 계산 유틸
private fun Color.isLight(): Boolean {
    val luminance = 0.299f * red + 0.587f * green + 0.114f * blue
    return luminance > 0.5f
}

private fun Color.contentColor(): Color = if (isLight()) Color.Black.copy(alpha = 0.85f) else Color.White
private fun Color.mutedContentColor(): Color = if (isLight()) Color.Black.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.6f)

// ═══════════════════════════════════════════════════════════════
// 공통 노브 컴포넌트
// ═══════════════════════════════════════════════════════════════

@Composable
private fun SimpleKnob(
    color: Color,
    size: Dp = 16.dp,
    style: KnobStyle = KnobStyle.Solid
) {
    val knobColor = color.contentColor()
    
    when (style) {
        KnobStyle.Solid -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(knobColor.copy(alpha = 0.25f))
                    .border(1.5.dp, knobColor.copy(alpha = 0.5f), CircleShape)
            )
        }
        KnobStyle.Ring -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(2.dp, knobColor.copy(alpha = 0.6f), CircleShape)
            )
        }
        KnobStyle.Dot -> {
            Box(
                modifier = Modifier
                    .size(size * 0.6f)
                    .clip(CircleShape)
                    .background(knobColor.copy(alpha = 0.7f))
            )
        }
        KnobStyle.Filled -> {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(knobColor.copy(alpha = 0.4f))
            )
        }
    }
}

enum class KnobStyle { Solid, Ring, Dot, Filled }

@Composable
private fun KnobRow(
    count: Int,
    color: Color,
    knobSize: Dp = 14.dp,
    spacing: Dp = 6.dp,
    style: KnobStyle = KnobStyle.Solid
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count.coerceAtMost(4)) {
            SimpleKnob(color = color, size = knobSize, style = style)
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 스타일 1: 클래식 미니멀
// 단순한 사각형, 페달색 배경, 이름 + 노브
// ═══════════════════════════════════════════════════════════════

@Composable
fun PedalStyle1_ClassicMinimal(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val textColor = color.contentColor()
    
    Box(
        modifier = modifier
            .width(88.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            
            KnobRow(knobs, color, knobSize = 16.dp, style = KnobStyle.Solid)
            
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 스타일 2: 라운드 소프트
// 더 둥근 모서리, 부드러운 느낌
// ═══════════════════════════════════════════════════════════════

@Composable
fun PedalStyle2_RoundSoft(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val textColor = color.contentColor()
    
    Box(
        modifier = modifier
            .width(88.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            KnobRow(knobs, color, knobSize = 18.dp, style = KnobStyle.Ring)
            
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 스타일 3: 그라데이션 페이드
// 위→아래 그라데이션
// ═══════════════════════════════════════════════════════════════

@Composable
fun PedalStyle3_GradientFade(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val textColor = color.contentColor()
    val darkerColor = color.copy(
        red = (color.red * 0.75f).coerceIn(0f, 1f),
        green = (color.green * 0.75f).coerceIn(0f, 1f),
        blue = (color.blue * 0.75f).coerceIn(0f, 1f)
    )
    
    Box(
        modifier = modifier
            .width(88.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(color, darkerColor)))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            
            KnobRow(knobs, color, knobSize = 15.dp, style = KnobStyle.Filled)
            
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 스타일 4: 인너 보더
// 안쪽 테두리 효과
// ═══════════════════════════════════════════════════════════════

@Composable
fun PedalStyle4_InnerBorder(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val textColor = color.contentColor()
    
    Box(
        modifier = modifier
            .width(88.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(9.dp))
                .border(1.5.dp, textColor.copy(alpha = 0.2f), RoundedCornerShape(9.dp))
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                KnobRow(knobs, color, knobSize = 14.dp, style = KnobStyle.Ring)
                
                Text(
                    text = name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 스타일 5: 쉐도우 리프트
// 컬러 섀도우로 띄운 느낌
// ═══════════════════════════════════════════════════════════════

@Composable
fun PedalStyle5_ShadowLift(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val textColor = color.contentColor()
    
    Box(
        modifier = modifier
            .width(88.dp)
            .height(110.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = color.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(color)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            
            KnobRow(knobs, color, knobSize = 16.dp, style = KnobStyle.Solid)
            
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 스타일 6: 탑 LED
// 상단에 LED 인디케이터
// ═══════════════════════════════════════════════════════════════

@Composable
fun PedalStyle6_TopLED(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val textColor = color.contentColor()
    val ledColor = if (color.isLight()) Color.Red else Color(0xFFFF4444)
    
    Box(
        modifier = modifier
            .width(88.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // LED
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .shadow(4.dp, CircleShape, spotColor = ledColor)
                    .clip(CircleShape)
                    .background(ledColor)
            )
            
            KnobRow(knobs, color, knobSize = 15.dp, style = KnobStyle.Solid)
            
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 스타일 7: 풋스위치
// 하단에 풋스위치 표시
// ═══════════════════════════════════════════════════════════════

@Composable
fun PedalStyle7_Footswitch(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val textColor = color.contentColor()
    
    Box(
        modifier = modifier
            .width(88.dp)
            .height(115.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            
            KnobRow(knobs, color, knobSize = 14.dp, style = KnobStyle.Solid)
            
            // 풋스위치
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(textColor.copy(alpha = 0.3f))
                    .border(1.dp, textColor.copy(alpha = 0.4f), RoundedCornerShape(3.dp))
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 스타일 8: 글로우 에지
// 가장자리 글로우 효과
// ═══════════════════════════════════════════════════════════════

@Composable
fun PedalStyle8_GlowEdge(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val textColor = color.contentColor()
    
    Box(
        modifier = modifier
            .width(92.dp)
            .height(114.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color,
                        color.copy(alpha = 0.7f)
                    )
                )
            )
            .border(
                2.dp,
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.3f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                RoundedCornerShape(14.dp)
            )
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            
            KnobRow(knobs, color, knobSize = 16.dp, style = KnobStyle.Ring)
            
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 스타일 9: 컴팩트 스퀘어
// 정사각형에 가까운 컴팩트
// ═══════════════════════════════════════════════════════════════

@Composable
fun PedalStyle9_CompactSquare(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val textColor = color.contentColor()
    
    Box(
        modifier = modifier
            .size(85.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            KnobRow(knobs.coerceAtMost(3), color, knobSize = 12.dp, spacing = 4.dp, style = KnobStyle.Dot)
            
            Text(
                text = name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 11.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 스타일 10: 플랫 모던
// 완전 플랫, 미니멀
// ═══════════════════════════════════════════════════════════════

@Composable
fun PedalStyle10_FlatModern(
    name: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val textColor = color.contentColor()
    
    Box(
        modifier = modifier
            .width(88.dp)
            .height(105.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            KnobRow(knobs, color, knobSize = 14.dp, spacing = 8.dp, style = KnobStyle.Ring)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 📱 PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFF0C0C0E, heightDp = 1400)
@Composable
private fun AllPedalStylesPreview() {
    ObsidianTheme {
        ObsidianBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Text(
                        "페달 디자인 10가지",
                        style = Obsidian.typography.headlineMedium,
                        color = Obsidian.colors.textPrimary
                    )
                    Text(
                        "페달 색상 배경 + 이름 + 노브",
                        fontSize = 12.sp,
                        color = Obsidian.colors.textMuted
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                item { StylePreviewRow("1. 클래식 미니멀") { p -> PedalStyle1_ClassicMinimal(p.name, p.color, p.knobs) } }
                item { StylePreviewRow("2. 라운드 소프트") { p -> PedalStyle2_RoundSoft(p.name, p.color, p.knobs) } }
                item { StylePreviewRow("3. 그라데이션") { p -> PedalStyle3_GradientFade(p.name, p.color, p.knobs) } }
                item { StylePreviewRow("4. 인너 보더") { p -> PedalStyle4_InnerBorder(p.name, p.color, p.knobs) } }
                item { StylePreviewRow("5. 쉐도우 리프트") { p -> PedalStyle5_ShadowLift(p.name, p.color, p.knobs) } }
                item { StylePreviewRow("6. 탑 LED") { p -> PedalStyle6_TopLED(p.name, p.color, p.knobs) } }
                item { StylePreviewRow("7. 풋스위치") { p -> PedalStyle7_Footswitch(p.name, p.color, p.knobs) } }
                item { StylePreviewRow("8. 글로우 에지") { p -> PedalStyle8_GlowEdge(p.name, p.color, p.knobs) } }
                item { StylePreviewRow("9. 컴팩트 스퀘어") { p -> PedalStyle9_CompactSquare(p.name, p.color, p.knobs) } }
                item { StylePreviewRow("10. 플랫 모던") { p -> PedalStyle10_FlatModern(p.name, p.color, p.knobs) } }
                
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun StylePreviewRow(
    label: String,
    content: @Composable (DemoPedal) -> Unit
) {
    Column {
        Text(label, fontSize = 11.sp, color = Obsidian.colors.textSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            demoPedals.forEach { pedal ->
                content(pedal)
            }
        }
    }
}
