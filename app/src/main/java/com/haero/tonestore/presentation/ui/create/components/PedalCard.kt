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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.presentation.ui.components.PedalColorUtils
import com.haero.tonestore.presentation.ui.components.RotaryKnob
import com.haero.tonestore.ui.designsystem.Obsidian

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
            PedalType.PRESET -> Obsidian.colors.primary.copy(alpha = 0.6f)
            else -> Obsidian.colors.pedalDistortion
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
        Color.White
    }

    val adjustedContentColor = if (pedal.isEnabled) {
        contentColor
    } else {
        contentColor.copy(alpha = 0.5f)
    }

    val borderColor = if (pedal.isEnabled) {
        backgroundColor.copy(alpha = 0.5f)
    } else {
        backgroundColor.copy(alpha = 0.2f)
    }

    Box(
        modifier = modifier
            .padding(4.dp)
            .shadow(
                elevation = if (pedal.isEnabled) 16.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (pedal.isEnabled) backgroundColor.copy(alpha = 0.6f) else Color.Transparent
            )
            .clip(RoundedCornerShape(16.dp))
            .background(adjustedGradient)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pedal.name,
                    style = Obsidian.typography.titleMedium,
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
                            }.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(12.dp)
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

            Spacer(Modifier.weight(1f))

            if (isEditable) {
                // Footswitch (Metallic Power Button)
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.8f),
                                    Color.Gray
                                )
                            )
                        )
                        .border(2.dp, Color.DarkGray, CircleShape)
                        .clickable { onToggleEnabled() },
                    contentAlignment = Alignment.Center
                ) {
                    // Indicator LED above it (simulated)
                    val ledColor = if (pedal.isEnabled) Color.Red else Color.Black
                    val ledGlow = if (pedal.isEnabled) Color.Red.copy(alpha = 0.6f) else Color.Transparent

                    Box(
                        modifier = Modifier
                            .offset(y = (-32).dp)
                            .size(8.dp)
                            .shadow(8.dp, CircleShape, spotColor = ledGlow)
                            .clip(CircleShape)
                            .background(ledColor)
                    )

                    Icon(
                        imageVector = Icons.Default.Power,
                        contentDescription = if (pedal.isEnabled) "끄기" else "켜기",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
