package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.data.preset.PresetPedals
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalCategory
import com.haero.tonestore.presentation.ui.components.PedalColorUtils
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 프리셋 페달 선택 바텀시트
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

    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
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
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = navigationBarPadding.calculateBottomPadding())
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

            // Custom Pedal Card
            CustomPedalCard(onClick = onCustomPedalCreate)

            Spacer(modifier = Modifier.height(24.dp))

            // Category Filter
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
                        label = getCategoryDisplayName(category),
                        emoji = getCategoryEmoji(category),
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pedal Grid
            Text(
                text = "프리셋 이펙터",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredPedals, key = { it.name }) { pedal ->
                    SelectablePedalCard(
                        pedal = pedal,
                        onClick = { onPedalSelect(pedal) }
                    )
                }
            }
        }
    }
}

/**
 * 커스텀 페달 생성 카드
 */
@Composable
private fun CustomPedalCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.98f else 1f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
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
 * 선택 가능한 페달 카드 - 플랫 디자인, 세로로 길쭉한 형태
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectablePedalCard(
    pedal: Pedal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.95f else 1f

    val backgroundColor = pedal.color?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceVariant
    val isLightColor = PedalColorUtils.isLightColor(pedal.color)
    val contentColor = if (isLightColor) Color.Black else Color.White
    val borderColor = PedalColorUtils.calculateBorderColor(backgroundColor)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 페달 이름
        Text(
            text = pedal.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 노브들
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            val knobsToShow = minOf(pedal.knobs.size, 4)
            repeat(knobsToShow) {
                SelectableKnobIndicator(contentColor = contentColor)
            }
            if (pedal.knobs.size > 4) {
                Text(
                    text = "+${pedal.knobs.size - 4}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
            if (pedal.knobs.isEmpty()) {
                Text(
                    text = "튜너",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
        }

        // 풋스위치 (검은색, 가로 스트라이프 패턴)
        FootSwitch(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
        )
    }
}

/**
 * 선택 가능한 카드용 노브 인디케이터
 */
@Composable
private fun SelectableKnobIndicator(
    contentColor: Color,
    size: Dp = 20.dp
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

        // Track
        drawArc(
            color = trackColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
        )

        // Progress
        drawArc(
            color = knobColor,
            startAngle = startAngle,
            sweepAngle = normalizedValue * sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f)
        )

        // Knob center
        drawCircle(
            color = knobColor,
            radius = radius * 0.6f,
            center = center
        )

        // Pointer
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

/**
 * 풋스위치 - 검은색 직사각형 + 가로 스트라이프 패턴
 */
@Composable
private fun FootSwitch(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
    ) {
        val width = size.width
        val height = size.height

        // 베이스 검은색
        drawRoundRect(
            color = Color(0xFF1A1A1A),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )

        // 가로 스트라이프 패턴
        val stripeCount = 6
        val stripeSpacing = width / (stripeCount + 1)
        for (i in 1..stripeCount) {
            val x = stripeSpacing * i
            drawLine(
                color = Color(0xFF3A3A3A),
                start = Offset(x, height * 0.2f),
                end = Offset(x, height * 0.8f),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
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
 * 카테고리 영어 이름
 */
private fun getCategoryDisplayName(category: PedalCategory): String {
    return when (category) {
        PedalCategory.DRIVE -> "Drive"
        PedalCategory.MODULATION -> "Modulation"
        PedalCategory.TIME_BASED -> "Time"
        PedalCategory.DYNAMICS -> "Dynamics"
        PedalCategory.UTILITY -> "Utility"
        PedalCategory.PITCH -> "Pitch"
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
