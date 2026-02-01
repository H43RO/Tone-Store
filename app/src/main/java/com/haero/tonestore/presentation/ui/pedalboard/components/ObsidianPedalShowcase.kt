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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianBackground
import com.haero.tonestore.ui.designsystem.ObsidianTheme

/**
 * 모던 페달 디자인 스타일 5가지
 */

data class ShowcasePedal(
    val name: String,
    val category: String,
    val primaryColor: Color,
    val knobCount: Int = 3
)

// 샘플 페달 (시그니처 색상)
val samplePedals = listOf(
    ShowcasePedal("DS-1", "Distortion", Color(0xFFFF6B00), 3),
    ShowcasePedal("Tube Screamer", "Overdrive", Color(0xFF4CAF50), 3),
    ShowcasePedal("Klon", "Overdrive", Color(0xFFD4AF37), 3),
)

// =====================================================
// STYLE 1: Pill Badge - 알약형 뱃지 스타일
// 특징: 초미니멀, 색상 포인트, 텍스트 중심
// =====================================================
@Composable
fun PedalStyle1_PillBadge(
    name: String,
    category: String,
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(100.dp)
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Obsidian.colors.surface)
            .border(
                width = 2.dp,
                color = primaryColor,
                shape = RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = category,
                fontSize = 8.sp,
                color = Obsidian.colors.textMuted
            )
        }
    }
}

// =====================================================
// STYLE 2: Color Block - 색상 블록 스타일
// 특징: 페달 색상 배경, 심플 사각형, Figma 느낌
// =====================================================
@Composable
fun PedalStyle2_ColorBlock(
    name: String,
    category: String,
    primaryColor: Color,
    knobCount: Int,
    modifier: Modifier = Modifier
) {
    val luminance = 0.299f * primaryColor.red + 0.587f * primaryColor.green + 0.114f * primaryColor.blue
    val textColor = if (luminance > 0.5f) Color.Black.copy(alpha = 0.85f) else Color.White

    Box(
        modifier = modifier
            .width(90.dp)
            .height(110.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(primaryColor)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 카테고리
            Text(
                text = category.uppercase(),
                fontSize = 7.sp,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp
            )

            // 노브 (단순 원)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(knobCount.coerceAtMost(3)) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(textColor.copy(alpha = 0.3f))
                            .border(1.5.dp, textColor.copy(alpha = 0.5f), CircleShape)
                    )
                }
            }

            // 이름
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// =====================================================
// STYLE 3: Outline Card - 아웃라인 카드 스타일
// 특징: 다크 배경, 컬러 테두리, 깔끔한 구조
// =====================================================
@Composable
fun PedalStyle3_OutlineCard(
    name: String,
    category: String,
    primaryColor: Color,
    knobCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Obsidian.colors.bgSecondary)
            .border(
                width = 1.5.dp,
                color = primaryColor.copy(alpha = 0.7f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 상단 컬러 바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(primaryColor)
            )

            // 노브
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(knobCount.coerceAtMost(3)) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Obsidian.colors.bgTertiary)
                            .border(1.dp, primaryColor.copy(alpha = 0.5f), CircleShape)
                    )
                }
            }

            // 이름
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Obsidian.colors.textPrimary,
                    maxLines = 1
                )
                Text(
                    text = category,
                    fontSize = 8.sp,
                    color = primaryColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// =====================================================
// STYLE 4: Glow Accent - 글로우 포인트 스타일
// 특징: 어두운 카드, 컬러 LED 글로우, 프리미엄 느낌
// =====================================================
@Composable
fun PedalStyle4_GlowAccent(
    name: String,
    category: String,
    primaryColor: Color,
    knobCount: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(90.dp)
            .height(110.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = primaryColor.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Obsidian.colors.bgSecondary,
                        Obsidian.colors.bgPrimary
                    )
                )
            )
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // LED 글로우
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .shadow(4.dp, CircleShape, spotColor = primaryColor)
                    .clip(CircleShape)
                    .background(primaryColor)
            )

            // 노브
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(knobCount.coerceAtMost(3)) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Obsidian.colors.bgTertiary)
                            .border(1.dp, Obsidian.colors.border, CircleShape)
                    )
                }
            }

            // 이름
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name,
                    fontSize = 10.sp,
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

// =====================================================
// STYLE 5: Gradient Edge - 그라데이션 엣지 스타일
// 특징: 다크 카드, 컬러 그라데이션 테두리, 모던
// =====================================================
@Composable
fun PedalStyle5_GradientEdge(
    name: String,
    category: String,
    primaryColor: Color,
    knobCount: Int,
    modifier: Modifier = Modifier
) {
    val gradientBorder = Brush.verticalGradient(
        colors = listOf(
            primaryColor,
            primaryColor.copy(alpha = 0.3f)
        )
    )

    Box(
        modifier = modifier
            .width(90.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(gradientBorder)
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(Obsidian.colors.surface)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 카테고리 태그
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(primaryColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = category,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryColor
                    )
                }

                // 노브
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    repeat(knobCount.coerceAtMost(3)) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Obsidian.colors.bgSecondary)
                                .border(1.dp, Obsidian.colors.border, CircleShape)
                        )
                    }
                }

                // 이름
                Text(
                    text = name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Obsidian.colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// =====================================================
// PREVIEW - 5가지 스타일 비교
// =====================================================
@Preview(showBackground = true, backgroundColor = 0xFF0C0C0E)
@Composable
private fun PedalStylesComparisonPreview() {
    ObsidianTheme {
        ObsidianBackground {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Style 1: Pill Badge
                item {
                    StyleSection(
                        number = 1,
                        name = "Pill Badge",
                        description = "초미니멀, 텍스트 중심"
                    ) {
                        samplePedals.forEach { pedal ->
                            PedalStyle1_PillBadge(
                                name = pedal.name,
                                category = pedal.category,
                                primaryColor = pedal.primaryColor
                            )
                        }
                    }
                }

                // Style 2: Color Block
                item {
                    StyleSection(
                        number = 2,
                        name = "Color Block",
                        description = "페달 색상 배경, Figma 스타일"
                    ) {
                        samplePedals.forEach { pedal ->
                            PedalStyle2_ColorBlock(
                                name = pedal.name,
                                category = pedal.category,
                                primaryColor = pedal.primaryColor,
                                knobCount = pedal.knobCount
                            )
                        }
                    }
                }

                // Style 3: Outline Card
                item {
                    StyleSection(
                        number = 3,
                        name = "Outline Card",
                        description = "다크 배경, 컬러 테두리"
                    ) {
                        samplePedals.forEach { pedal ->
                            PedalStyle3_OutlineCard(
                                name = pedal.name,
                                category = pedal.category,
                                primaryColor = pedal.primaryColor,
                                knobCount = pedal.knobCount
                            )
                        }
                    }
                }

                // Style 4: Glow Accent
                item {
                    StyleSection(
                        number = 4,
                        name = "Glow Accent",
                        description = "LED 글로우, 프리미엄"
                    ) {
                        samplePedals.forEach { pedal ->
                            PedalStyle4_GlowAccent(
                                name = pedal.name,
                                category = pedal.category,
                                primaryColor = pedal.primaryColor,
                                knobCount = pedal.knobCount
                            )
                        }
    }
                }

                // Style 5: Gradient Edge
                item {
                    StyleSection(
                        number = 5,
                        name = "Gradient Edge",
                        description = "그라데이션 테두리, 모던"
                    ) {
                        samplePedals.forEach { pedal ->
                            PedalStyle5_GradientEdge(
                                name = pedal.name,
                                category = pedal.category,
                                primaryColor = pedal.primaryColor,
                                knobCount = pedal.knobCount
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleSection(
    number: Int,
    name: String,
    description: String,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Obsidian.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$number",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = name,
                    style = Obsidian.typography.titleMedium,
                    color = Obsidian.colors.textPrimary
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = Obsidian.colors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}
