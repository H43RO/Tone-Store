package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.presentation.ui.components.PedalColorUtils
import com.haero.tonestore.ui.designsystem.Obsidian

@Composable
fun ExpressionPedalZone(
    expressionPedal: Pedal?,
    onSelectPedal: () -> Unit,
    onRemovePedal: () -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true,
    width: Dp = 80.dp,
    height: Dp = 200.dp
) {
    val pedalShape = RoundedCornerShape(12.dp)
    val treadleShape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = 4.dp,
        bottomEnd = 4.dp
    )

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .shadow(
                elevation = if (expressionPedal != null) 8.dp else 0.dp,
                shape = pedalShape,
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .clip(pedalShape)
            .background(
                if (expressionPedal != null) Obsidian.colors.bgSecondary else Obsidian.colors.surfaceHighlight
            )
            .then(
                if (expressionPedal == null) {
                    Modifier.border(
                        width = 2.dp,
                        color = Obsidian.colors.border,
                        shape = pedalShape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onSelectPedal),
        contentAlignment = Alignment.Center
    ) {
        if (expressionPedal != null) {
            val baseColor = expressionPedal.color?.let { Color(it) } ?: Obsidian.colors.primary
            val isLight = PedalColorUtils.isLightColor(baseColor)
            val textColor = if (isLight) Color.Black else Color.White

            // Pedal Body & Treadle Area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                // Treadle (Movement Part)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .shadow(4.dp, treadleShape)
                        .clip(treadleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    PedalColorUtils.darken(baseColor, 0.9f),
                                    PedalColorUtils.darken(baseColor, 0.7f)
                                )
                            )
                        )
                ) {
                    // Rubber Grip Texture
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gripColor = Color.Black.copy(alpha = 0.3f)
                        val stripeHeight = 4.dp.toPx()
                        val stripeGap = 6.dp.toPx()

                        var y = 0f
                        while (y < size.height) {
                            drawRect(
                                color = gripColor,
                                topLeft = Offset(0f, y),
                                size = Size(size.width, stripeHeight)
                            )
                            y += stripeHeight + stripeGap
                        }
                    }

                    // Pedal Name on Treadle (Top portion of treadle usually)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = expressionPedal.name,
                            style = Obsidian.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = textColor.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Hinge Area (Visual separation)
                Spacer(modifier = Modifier.height(4.dp))

                // Heel/Base Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    PedalColorUtils.darken(baseColor, 0.6f),
                                    PedalColorUtils.darken(baseColor, 0.5f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Chrome Accent or Logo could go here
                }
            }

            // Controls (Remove Button)
            if (isEditable) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    IconButton(
                        onClick = onRemovePedal,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .size(24.dp)
                            .background(Color.Black.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        } else {
            // Empty State
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Wah\n/\nWhammy",
                    style = Obsidian.typography.bodyMedium,
                    color = Obsidian.colors.textMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = Obsidian.typography.bodyMedium.lineHeight
                )
            }
        }
    }
}
