package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.data.preset.PresetPedals
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalCategory
import com.haero.tonestore.presentation.ui.components.PedalColorUtils

/**
 * 프리셋 페달 선택 바텀시트 - 2025/2026 디자인 트렌드
 * 실제 페달 느낌의 3D 스큐어모픽 디자인
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetPedalSelectionDialog(
    onDismiss: () -> Unit,
    onPedalSelect: (Pedal) -> Unit,
    onCustomPedalCreate: () -> Unit
) {
    val presetPedals = remember { PresetPedals.getPresetPedals() }
    var selectedCategory by remember { mutableStateOf<PedalCategory?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filteredPedals = remember(selectedCategory) {
        if (selectedCategory == null) {
            presetPedals
        } else {
            presetPedals.filter { pedal ->
                getCategoryForPedal(pedal.name) == selectedCategory
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.85f),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "이펙터 추가",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Custom Pedal Card - 프리미엄 스타일
            CustomPedalCard(onClick = onCustomPedalCreate)

            Spacer(modifier = Modifier.height(24.dp))

            // Category Filter - 스크롤 가능한 칩
            Text(
                text = "카테고리",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                item {
                    CategoryChip(
                        label = "전체",
                        emoji = "🎸",
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                items(PedalCategory.entries) { category ->
                    CategoryChip(
                        label = getCategoryDisplayNameKo(category),
                        emoji = getCategoryEmoji(category),
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pedal Grid - 3D 스큐어모픽 카드
            Text(
                text = "프리셋 이펙터",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPedals, key = { it.name }) { pedal ->
                    SkeuomorphicPedalCard(
                        pedal = pedal,
                        onClick = { onPedalSelect(pedal) }
                    )
                }
            }
        }
    }
}

/**
 * 커스텀 페달 생성 카드 - 그라디언트 프리미엄 스타일
 */
@Composable
private fun CustomPedalCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "scale"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "나만의 이펙터 만들기",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "커스텀 노브와 설정으로 구성",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

/**
 * 카테고리 필터 칩
 */
@Composable
private fun CategoryChip(
    label: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            selectedBorderColor = Color.Transparent,
            enabled = true,
            selected = selected
        )
    )
}

/**
 * 스큐어모픽 페달 카드 - 실제 페달처럼 보이는 3D 디자인
 */
@Composable
private fun SkeuomorphicPedalCard(
    pedal: Pedal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pedalColor = pedal.color?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceVariant
    val isLightColor = PedalColorUtils.isLightColor(pedal.color)
    val contentColor = if (isLightColor) Color.Black else Color.White
    val borderColor = PedalColorUtils.calculateBorderColor(pedalColor)

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "scale"
    )
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 8f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "elevation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .scale(scale)
            .graphicsLayer {
                shadowElevation = elevation.dp.toPx()
                shape = RoundedCornerShape(16.dp)
                clip = true
            }
            .shadow(
                elevation = elevation.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = pedalColor.copy(alpha = 0.4f),
                spotColor = pedalColor.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(pedalColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 LED 표시등
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = Color.Red.copy(alpha = 0.8f),
                            shape = CircleShape
                        )
                        .border(1.dp, Color.Red.copy(alpha = 0.5f), CircleShape)
                )
            }

            // 페달 이름
            Text(
                text = pedal.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // 노브 미리보기
            KnobPreviewRow(
                knobCount = pedal.knobs.size,
                contentColor = contentColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 풋스위치
            Box(
                modifier = Modifier
                    .size(width = 48.dp, height = 16.dp)
                    .background(
                        color = contentColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = contentColor.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

/**
 * 노브 미리보기 Row
 */
@Composable
private fun KnobPreviewRow(
    knobCount: Int,
    contentColor: Color
) {
    if (knobCount == 0) {
        Text(
            text = "튜너",
            style = MaterialTheme.typography.labelSmall,
            color = contentColor.copy(alpha = 0.7f)
        )
        return
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(minOf(knobCount, 4)) {
            MiniKnob(color = contentColor)
        }
        if (knobCount > 4) {
            Text(
                text = "+${knobCount - 4}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * 미니 노브 (3D 느낌)
 */
@Composable
private fun MiniKnob(color: Color) {
    Box(
        modifier = Modifier
            .size(18.dp)
            .shadow(2.dp, CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.3f),
                        color.copy(alpha = 0.1f)
                    )
                ),
                shape = CircleShape
            )
            .border(1.5.dp, color.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // 노브 포인터
        Box(
            modifier = Modifier
                .size(4.dp)
                .background(color.copy(alpha = 0.8f), CircleShape)
        )
    }
}

/**
 * 페달 이름 → 카테고리 매핑
 */
private fun getCategoryForPedal(pedalName: String): PedalCategory {
    return when (pedalName) {
        "Overdrive", "Distortion", "Fuzz", "Boost" -> PedalCategory.DRIVE
        "Chorus", "Flanger", "Phaser", "Tremolo" -> PedalCategory.MODULATION
        "Delay", "Reverb" -> PedalCategory.TIME_BASED
        "Compressor", "Noise Gate" -> PedalCategory.DYNAMICS
        "Tuner", "EQ", "Wah", "Bass Preamp" -> PedalCategory.UTILITY
        "Octave", "Whammy" -> PedalCategory.PITCH
        else -> PedalCategory.UTILITY
    }
}

/**
 * 카테고리 한글 이름
 */
private fun getCategoryDisplayNameKo(category: PedalCategory): String {
    return when (category) {
        PedalCategory.DRIVE -> "드라이브"
        PedalCategory.MODULATION -> "모듈레이션"
        PedalCategory.TIME_BASED -> "타임"
        PedalCategory.DYNAMICS -> "다이나믹"
        PedalCategory.UTILITY -> "유틸리티"
        PedalCategory.PITCH -> "피치"
    }
}

/**
 * 카테고리 이모지
 */
private fun getCategoryEmoji(category: PedalCategory): String {
    return when (category) {
        PedalCategory.DRIVE -> "🔥"
        PedalCategory.MODULATION -> "🌊"
        PedalCategory.TIME_BASED -> "⏱️"
        PedalCategory.DYNAMICS -> "📊"
        PedalCategory.UTILITY -> "🔧"
        PedalCategory.PITCH -> "🎵"
    }
}
