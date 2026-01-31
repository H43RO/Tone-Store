package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import com.haero.tonestore.domain.model.Pedal

@Composable
fun CableOverlay(
    slots: List<Pedal?>,
    slotPositions: Map<Int, Offset>,
    expressionPedal: Pedal?,
    expressionPedalPosition: Offset?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val enabledPedalsWithPositions = slots
                .mapIndexedNotNull { index, pedal ->
                    if (pedal != null && pedal.isEnabled && slotPositions.containsKey(index)) {
                        Triple(index, pedal, slotPositions[index]!!)
                    } else {
                        null
                    }
                }
                .sortedBy { it.second.order }

            for (i in 0 until enabledPedalsWithPositions.size - 1) {
                val (_, currentPedal, currentPos) = enabledPedalsWithPositions[i]
                val (_, nextPedal, nextPos) = enabledPedalsWithPositions[i + 1]

                drawLine(
                    color = Color.White,
                    start = currentPos,
                    end = nextPos,
                    strokeWidth = 3.dp.toPx()
                )

                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = currentPos
                )

                if (i == enabledPedalsWithPositions.size - 2) {
                    drawCircle(
                        color = Color.White,
                        radius = 6.dp.toPx(),
                        center = nextPos
                    )
                }
            }

            if (expressionPedal != null &&
                expressionPedal.isEnabled &&
                expressionPedalPosition != null &&
                enabledPedalsWithPositions.isNotEmpty()
            ) {
                val lastPedalPos = enabledPedalsWithPositions.last().third

                drawLine(
                    color = Color.White,
                    start = lastPedalPos,
                    end = expressionPedalPosition,
                    strokeWidth = 3.dp.toPx()
                )

                drawCircle(
                    color = Color.White,
                    radius = 6.dp.toPx(),
                    center = expressionPedalPosition
                )
            }

            slots.forEachIndexed { index, pedal ->
                if (pedal != null && !pedal.isEnabled && slotPositions.containsKey(index)) {
                    val pos = slotPositions[index]!!
                    val nextIndex = slots.indexOfFirst { it != null && it.order == pedal.order + 1 }
                    if (nextIndex >= 0 && slotPositions.containsKey(nextIndex)) {
                        val nextPos = slotPositions[nextIndex]!!

                        drawLine(
                            color = Color.Gray,
                            start = pos,
                            end = nextPos,
                            strokeWidth = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                        )
                    }
                }
            }
        }
    }
}
