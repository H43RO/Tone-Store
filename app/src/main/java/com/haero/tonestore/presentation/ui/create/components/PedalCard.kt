package com.haero.tonestore.presentation.ui.create.components

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.presentation.ui.components.PedalColorUtils
import com.haero.tonestore.presentation.ui.components.RotaryKnob

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PedalCard(
    pedal: Pedal,
    onKnobChange: (knobIndex: Int, value: Float) -> Unit,
    onToggleEnabled: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    val backgroundColor = if (pedal.color != null) {
        Color(pedal.color)
    } else {
        when (pedal.type) {
            PedalType.PRESET -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            PedalColorUtils.darken(backgroundColor, 0.9f),
            PedalColorUtils.darken(backgroundColor, 0.7f)
        )
    )

    val adjustedGradient = if (pedal.isEnabled) {
        gradient
    } else {
        Brush.verticalGradient(
            colors = listOf(
                PedalColorUtils.darken(backgroundColor, 0.45f),
                PedalColorUtils.darken(backgroundColor, 0.35f)
            )
        )
    }

    val isLightBackground = PedalColorUtils.isLightColor(pedal.color)
    val contentColor = if (pedal.color != null) {
        if (isLightBackground) Color.Black else Color.White
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val adjustedContentColor = if (pedal.isEnabled) {
        contentColor
    } else {
        contentColor.copy(alpha = 0.5f)
    }

    val borderColor = if (pedal.isEnabled) {
        backgroundColor.copy(alpha = 0.3f)
    } else {
        backgroundColor.copy(alpha = 0.15f)
    }

    val shadowElevation = if (pedal.isEnabled) 12.dp else 6.dp
    val spotColor = if (pedal.isEnabled) {
        backgroundColor.copy(alpha = 0.5f)
    } else {
        backgroundColor.copy(alpha = 0.25f)
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(12.dp),
                spotColor = spotColor
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(adjustedGradient)
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pedal.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = adjustedContentColor
                    )

                    if (isEditable) {
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "삭제",
                                tint = if (isLightBackground) {
                                    Color(0xFFB71C1C)
                                } else {
                                    Color(0xFFFF8A80)
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pedal.knobs.forEachIndexed { index, knob ->
                        RotaryKnob(
                            value = knob.value,
                            onValueChange = { newValue ->
                                if (isEditable) onKnobChange(index, newValue)
                            },
                            label = knob.name,
                            size = 56.dp,
                            enabled = pedal.isEnabled && isEditable,
                            modifier = Modifier.padding(horizontal = 4.dp),
                            labelColor = adjustedContentColor,
                            isPedalKnob = true
                        )
                    }
                }

                if (isEditable) {
                    val powerButtonColor = if (pedal.isEnabled) {
                        PedalColorUtils.calculateBorderColor(backgroundColor)
                    } else {
                        contentColor.copy(alpha = 0.2f)
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(powerButtonColor)
                            .clickable { onToggleEnabled() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Power,
                            contentDescription = if (pedal.isEnabled) "끄기" else "켜기",
                            tint = if (pedal.isEnabled) {
                                if (PedalColorUtils.isLightColor(powerButtonColor)) {
                                    Color.Black
                                } else {
                                    Color.White
                                }
                            } else {
                                contentColor.copy(alpha = 0.5f)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
