package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.haero.tonestore.domain.model.Pedal

@Composable
fun CableOverlay(
    slots: List<Pedal?>,
    slotPositions: Map<Int, Offset>,
    columns: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cableColor = Color(0xFF424242)

            slotPositions.forEach { (index, position) ->
                val row = index / columns
                val col = index % columns

                if (col < columns - 1) {
                    val nextIndex = index + 1
                    val nextPos = slotPositions[nextIndex]
                    if (nextPos != null) {
                        drawLine(
                            color = cableColor,
                            start = position,
                            end = nextPos,
                            strokeWidth = 2.dp.toPx()
                        )

                        drawCircle(
                            color = cableColor,
                            radius = 4.dp.toPx(),
                            center = position
                        )

                        if (col == columns - 2) {
                            drawCircle(
                                color = cableColor,
                                radius = 4.dp.toPx(),
                                center = nextPos
                            )
                        }
                    }
                }
            }
        }
    }
}
