package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.data.preset.PresetPedals
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalCategory

/**
 * 프리셋 페달 선택 다이얼로그 - 카드 그리드 스타일
 * 18개 프리셋 페달을 카테고리별로 필터링하여 표시
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
        modifier = Modifier.fillMaxHeight(0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Title
            Text(
                text = stringResource(R.string.select_preset_pedal),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Category FilterChips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All") }
                )

                PedalCategory.values().forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        },
                        label = { Text(getCategoryDisplayName(category)) }
                    )
                }
            }

            // Pedal Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPedals) { pedal ->
                    PedalCard(
                        pedal = pedal,
                        onClick = { onPedalSelect(pedal) }
                    )
                }
            }

            // Custom Pedal Button
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCustomPedalCreate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.add_custom_pedal))
            }
        }
    }
}

@Composable
private fun PedalCard(
    pedal: Pedal,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = pedal.color?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor.copy(alpha = 0.15f))
                .border(
                    width = 3.dp,
                    color = backgroundColor,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Color indicator stripe
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(backgroundColor)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Pedal name
                Text(
                    text = pedal.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Knob count
                Text(
                    text = "${pedal.knobs.size} knobs",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * 페달 이름으로 카테고리 매핑
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
 * 카테고리 표시 이름
 */
private fun getCategoryDisplayName(category: PedalCategory): String {
    return when (category) {
        PedalCategory.DRIVE -> "Drive"
        PedalCategory.MODULATION -> "Mod"
        PedalCategory.TIME_BASED -> "Time"
        PedalCategory.DYNAMICS -> "Dyn"
        PedalCategory.UTILITY -> "Util"
        PedalCategory.PITCH -> "Pitch"
    }
}
