package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.R
import com.haero.tonestore.data.preset.PresetPedals
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalCategory
import com.haero.tonestore.domain.model.SavedCustomPedal
import com.haero.tonestore.presentation.ui.components.PedalColorUtils
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 프리셋/커스텀 페달 선택 바텀시트
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetPedalSelectionDialog(
    customPedals: List<SavedCustomPedal>,
    onDismiss: () -> Unit,
    onPedalSelect: (Pedal) -> Unit,
    onCustomPedalCreate: () -> Unit
) {
    val presetPedals = remember { PresetPedals.getPresetPedals() }
    var selectedTab by remember { mutableStateOf(0) } // 0: 프리셋, 1: 커스텀
    var selectedCategory by remember { mutableStateOf<PedalCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filteredPedals = remember(selectedCategory, searchQuery) {
        presetPedals.filter { pedal ->
            val matchesCategory = if (searchQuery.isNotBlank()) {
                true
            } else {
                selectedCategory == null || getCategoryForPedal(pedal.name) == selectedCategory
            }
            val matchesSearch = searchQuery.isBlank() || pedal.name.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
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
                .fillMaxHeight(0.85f)
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
                    text = stringResource(R.string.add_effect),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tabs (프리셋 / 커스텀)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf("프리셋 페달", "나의 커스텀 페달")
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    val backgroundColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "tabBackground"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "tabContent"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(backgroundColor)
                            .clickable { selectedTab = index }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = contentColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Pedal Card (프리셋 탭에서만 표시)
            if (selectedTab == 0) {
                CustomPedalCard(onClick = onCustomPedalCreate)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 프리셋 탭: Search Bar & Category Filter
            if (selectedTab == 0) {
                // Search Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "페달 검색",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { searchQuery = "" }
                        )
                    }
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = searchQuery.isEmpty(),
                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
                    exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        val categoryAllLabel = stringResource(R.string.category_all)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                CategoryChip(
                                    label = categoryAllLabel,
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
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                if (searchQuery.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // 페달 리스트
            if (selectedTab == 0) {
                // 프리셋 페달 그리드
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
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
                            onClick = { onPedalSelect(pedal) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            } else {
                // 커스텀 페달 그리드
                if (customPedals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "아직 커스텀 페달이 없어요",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "프리셋 탭에서 커스텀 페달을 만들어보세요!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(customPedals, key = { it.id }) { savedPedal ->
                            SelectablePedalCard(
                                pedal = savedPedal.toPedal(),
                                onClick = { onPedalSelect(savedPedal.toPedal()) }
                            )
                        }
                    }
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

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(primaryColor.copy(alpha = 0.1f), CircleShape)
                    .border(1.dp, primaryColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.create_custom_effect),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.custom_knob_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
    val gradient = Brush.verticalGradient(
        colors = listOf(
            PedalColorUtils.darken(backgroundColor, 0.9f),
            PedalColorUtils.darken(backgroundColor, 0.7f)
        )
    )
    val isLightColor = PedalColorUtils.isLightColor(pedal.color)
    val contentColor = if (isLightColor) Color.Black else Color.White
    val borderColor = backgroundColor.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .scale(scale)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = backgroundColor.copy(alpha = 0.4f)
            )
            .clip(RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // 페달 이름
            Text(
                text = pedal.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 노브들
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                val knobsToShow = minOf(pedal.knobs.size, 6)
                repeat(knobsToShow) {
                    SelectableKnobIndicator(contentColor = contentColor)
                }
            }

            // 풋스위치
            FootSwitch(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
                    .height(12.dp)
            )
        }
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
        "Chorus", "Flanger", "Phaser", "Tremolo", "Ring Modulator" -> PedalCategory.MODULATION
        "Delay", "Reverb", "Looper" -> PedalCategory.TIME_BASED
        "Compressor", "Noise Gate" -> PedalCategory.DYNAMICS
        "Tuner", "EQ", "Wah", "Bass Preamp", "Auto-Wah", "Envelope Filter" -> PedalCategory.UTILITY
        "Octave", "Whammy", "Harmonizer", "Pitch Shifter" -> PedalCategory.PITCH
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
