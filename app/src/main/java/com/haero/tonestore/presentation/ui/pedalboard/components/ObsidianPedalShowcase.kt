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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianBackground
import com.haero.tonestore.ui.designsystem.ObsidianTheme

data class SamplePedal(
    val name: String,
    val category: String,
    val color: Color,
    val knobs: Int = 3
)

val demoPedals = listOf(
    SamplePedal("DS-1", "Distortion", Color(0xFFFF6B00)),
    SamplePedal("Tube Screamer", "Overdrive", Color(0xFF4CAF50)),
    SamplePedal("Klon", "Overdrive", Color(0xFFD4AF37)),
    SamplePedal("DD-7", "Delay", Color(0xFF1976D2)),
)

// ═══════════════════════════════════════════════════════════════
// 🎨 CATEGORY A: ToneSettingCard 통일 스타일 (앱 내 카드와 동일)
// ═══════════════════════════════════════════════════════════════

/** A1: 기본 통일 카드 - surface 배경, 컬러 아이콘 박스 */
@Composable
fun PedalA1_UnifiedCard(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Obsidian.colors.surface)
            .border(1.dp, Obsidian.colors.border, RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 컬러 아이콘 박스 (ToneSettingCard 스타일)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 14.sp)
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Obsidian.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = category,
                    fontSize = 9.sp,
                    color = Obsidian.colors.textMuted
                )
            }
        }
    }
}

/** A2: 컬러 탑바 - 상단에 페달 색상 바 */
@Composable
fun PedalA2_ColorTopBar(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Obsidian.colors.surface)
            .border(1.dp, Obsidian.colors.border, RoundedCornerShape(14.dp))
    ) {
        Column {
            // 상단 컬러 바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(color)
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Obsidian.colors.textPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category,
                    fontSize = 9.sp,
                    color = color.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/** A3: 컬러 사이드 라인 */
@Composable
fun PedalA3_SideLine(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Obsidian.colors.surface)
            .border(1.dp, Obsidian.colors.border, RoundedCornerShape(12.dp))
    ) {
        // 좌측 컬러 라인
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(100.dp)
                .background(color)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Obsidian.colors.textPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category,
                fontSize = 9.sp,
                color = Obsidian.colors.textMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 노브 도트
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(knobs.coerceAtMost(4)) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}

/** A4: 컬러 코너 뱃지 */
@Composable
fun PedalA4_CornerBadge(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Obsidian.colors.surface)
            .border(1.dp, Obsidian.colors.border, RoundedCornerShape(14.dp))
    ) {
        // 우상단 컬러 뱃지
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Obsidian.colors.textPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category,
                fontSize = 9.sp,
                color = Obsidian.colors.textMuted
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 🎨 CATEGORY B: 초미니멀 스타일 (텍스트 + 최소 색상)
// ═══════════════════════════════════════════════════════════════

/** B1: 텍스트 + 언더라인 */
@Composable
fun PedalB1_Underline(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Obsidian.colors.textPrimary,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = category,
            fontSize = 8.sp,
            color = Obsidian.colors.textMuted
        )
    }
}

/** B2: 도트 + 텍스트 */
@Composable
fun PedalB2_DotText(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Obsidian.colors.textPrimary
            )
            Text(
                text = category,
                fontSize = 8.sp,
                color = Obsidian.colors.textMuted
            )
        }
    }
}

/** B3: 컬러 텍스트 온리 */
@Composable
fun PedalB3_ColorText(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = category,
            fontSize = 8.sp,
            color = Obsidian.colors.textMuted
        )
    }
}

/** B4: 심플 칩 */
@Composable
fun PedalB4_SimpleChip(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

/** B5: 링 뱃지 */
@Composable
fun PedalB5_RingBadge(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .border(2.dp, color, CircleShape)
                .background(Obsidian.colors.bgSecondary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(2),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 9.sp,
            color = Obsidian.colors.textSecondary,
            maxLines = 1
        )
    }
}

/** B6: 버티컬 바 */
@Composable
fun PedalB6_VerticalBar(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(32.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Obsidian.colors.textPrimary
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// 🎨 CATEGORY C: 글래스모피즘 스타일 (반투명 + 블러)
// ═══════════════════════════════════════════════════════════════

/** C1: 프로스트 글래스 */
@Composable
fun PedalC1_FrostGlass(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(100.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = color.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(16.dp))
            .background(Obsidian.colors.bgSecondary.copy(alpha = 0.7f))
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.05f))
                ),
                RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 글로우 LED
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .shadow(6.dp, CircleShape, spotColor = color)
                    .clip(CircleShape)
                    .background(color)
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Obsidian.colors.textPrimary
                )
                Text(
                    text = category,
                    fontSize = 8.sp,
                    color = Obsidian.colors.textSecondary
                )
            }
        }
    }
}

/** C2: 컬러 글로우 배경 */
@Composable
fun PedalC2_ColorGlow(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(100.dp)
    ) {
        // 블러 글로우 배경
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .blur(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.3f))
        )
        
        // 실제 카드
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(14.dp))
                .background(Obsidian.colors.bgSecondary.copy(alpha = 0.85f))
                .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Obsidian.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category,
                    fontSize = 9.sp,
                    color = color
                )
            }
        }
    }
}

/** C3: 그라데이션 보더 글래스 */
@Composable
fun PedalC3_GradientBorderGlass(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    val gradientBorder = Brush.linearGradient(
        colors = listOf(
            color.copy(alpha = 0.6f),
            color.copy(alpha = 0.1f),
            color.copy(alpha = 0.4f)
        )
    )
    
    Box(
        modifier = modifier
            .width(90.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(gradientBorder)
            .padding(1.5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.5.dp))
                .background(Obsidian.colors.bgPrimary.copy(alpha = 0.9f))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Obsidian.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category,
                    fontSize = 9.sp,
                    color = Obsidian.colors.textMuted
                )
            }
        }
    }
}

/** C4: 네온 엣지 */
@Composable
fun PedalC4_NeonEdge(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(100.dp)
            .shadow(12.dp, RoundedCornerShape(12.dp), spotColor = color.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(12.dp))
            .background(Obsidian.colors.bgPrimary)
            .border(1.5.dp, color.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category,
                fontSize = 9.sp,
                color = Obsidian.colors.textMuted
            )
        }
    }
}

/** C5: 소프트 섀도우 글래스 */
@Composable
fun PedalC5_SoftShadowGlass(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(100.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = color.copy(alpha = 0.25f),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Obsidian.colors.surface,
                        Obsidian.colors.bgSecondary
                    )
                )
            )
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 탑 인디케이터
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Obsidian.colors.textPrimary
                )
                Text(
                    text = category,
                    fontSize = 8.sp,
                    color = Obsidian.colors.textMuted
                )
            }
        }
    }
}

/** C6: 아우라 글래스 */
@Composable
fun PedalC6_AuraGlass(
    name: String, category: String, color: Color, knobs: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.15f),
                        Obsidian.colors.bgSecondary
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(color.copy(alpha = 0.4f), color.copy(alpha = 0.1f))
                ),
                RoundedCornerShape(14.dp)
            )
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Obsidian.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = category,
                fontSize = 9.sp,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// 📱 PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true, backgroundColor = 0xFF0C0C0E, heightDp = 1800)
@Composable
private fun AllPedalStylesPreview() {
    ObsidianTheme {
        ObsidianBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // ═══ Category A: 통일 스타일 ═══
                item {
                    CategoryHeader("A", "ToneSettingCard 통일", "앱 내 카드와 동일한 느낌")
                }
                
                item {
                    StyleRow("A1: 기본 통일") {
                        demoPedals.forEach { p ->
                            PedalA1_UnifiedCard(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("A2: 컬러 탑바") {
                        demoPedals.forEach { p ->
                            PedalA2_ColorTopBar(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("A3: 사이드 라인") {
                        demoPedals.forEach { p ->
                            PedalA3_SideLine(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("A4: 코너 뱃지") {
                        demoPedals.forEach { p ->
                            PedalA4_CornerBadge(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                // ═══ Category B: 미니멀 스타일 ═══
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryHeader("B", "초미니멀", "텍스트 중심, 최소 색상")
                }
                
                item {
                    StyleRow("B1: 언더라인") {
                        demoPedals.forEach { p ->
                            PedalB1_Underline(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("B2: 도트 + 텍스트") {
                        demoPedals.forEach { p ->
                            PedalB2_DotText(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("B3: 컬러 텍스트") {
                        demoPedals.forEach { p ->
                            PedalB3_ColorText(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("B4: 심플 칩") {
                        demoPedals.forEach { p ->
                            PedalB4_SimpleChip(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("B5: 링 뱃지") {
                        demoPedals.forEach { p ->
                            PedalB5_RingBadge(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("B6: 버티컬 바") {
                        demoPedals.forEach { p ->
                            PedalB6_VerticalBar(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                // ═══ Category C: 글래스모피즘 ═══
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryHeader("C", "글래스모피즘", "반투명 + 컬러 글로우")
                }
                
                item {
                    StyleRow("C1: 프로스트 글래스") {
                        demoPedals.forEach { p ->
                            PedalC1_FrostGlass(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("C2: 컬러 글로우") {
                        demoPedals.forEach { p ->
                            PedalC2_ColorGlow(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("C3: 그라데이션 보더") {
                        demoPedals.forEach { p ->
                            PedalC3_GradientBorderGlass(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("C4: 네온 엣지") {
                        demoPedals.forEach { p ->
                            PedalC4_NeonEdge(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("C5: 소프트 섀도우") {
                        demoPedals.forEach { p ->
                            PedalC5_SoftShadowGlass(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    StyleRow("C6: 아우라 글래스") {
                        demoPedals.forEach { p ->
                            PedalC6_AuraGlass(p.name, p.category, p.color, p.knobs)
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(code: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Obsidian.colors.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(code, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, style = Obsidian.typography.titleMedium, color = Obsidian.colors.textPrimary)
            Text(desc, fontSize = 11.sp, color = Obsidian.colors.textMuted)
        }
    }
}

@Composable
private fun StyleRow(label: String, content: @Composable () -> Unit) {
    Column {
        Text(label, fontSize = 10.sp, color = Obsidian.colors.textSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            content()
        }
    }
}
