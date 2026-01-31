package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.ui.theme.ToneStoreTheme

@Composable
fun MiniPedalBoardPreview(
    slots: List<Pedal?>,
    columns: Int,
    rows: Int,
    expressionPedal: Pedal?,
    modifier: Modifier = Modifier,
    slotHeight: Dp = 40.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (rowIndex in 0 until rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    for (colIndex in 0 until columns) {
                        val index = rowIndex * columns + colIndex
                        val pedal = slots.getOrNull(index)

                        if (pedal != null) {
                            MiniPedalCard(
                                pedal = pedal,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(slotHeight)
                            )
                        } else {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(slotHeight)
                            )
                        }
                    }
                }
            }
        }

        if (expressionPedal != null) {
            MiniExpressionPedalZone(
                expressionPedal = expressionPedal,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview(name = "2x1 Single Pedal", showBackground = true, widthDp = 200)
@Composable
private fun MiniPreview2x1() {
    ToneStoreTheme {
        MiniPedalBoardPreview(
            slots = listOf(
                Pedal(
                    id = "1",
                    name = "OD",
                    type = PedalType.PRESET,
                    knobs = emptyList(),
                    order = 0,
                    color = 0xFFFF9800
                ),
                null
            ),
            columns = 2,
            rows = 1,
            expressionPedal = null
        )
    }
}

@Preview(name = "4x2 Mixed", showBackground = true, widthDp = 350)
@Composable
private fun MiniPreview4x2Mixed() {
    ToneStoreTheme {
        MiniPedalBoardPreview(
            slots = listOf(
                Pedal(id = "1", name = "OD", type = PedalType.PRESET, knobs = emptyList(), order = 0, color = 0xFFFF9800),
                null,
                Pedal(id = "2", name = "Delay", type = PedalType.PRESET, knobs = emptyList(), order = 1, color = 0xFF2196F3),
                Pedal(id = "3", name = "Reverb", type = PedalType.PRESET, knobs = emptyList(), order = 2, color = 0xFF9C27B0),
                null,
                Pedal(id = "4", name = "Chorus", type = PedalType.PRESET, knobs = emptyList(), order = 3, color = 0xFF00BCD4),
                null,
                null
            ),
            columns = 4,
            rows = 2,
            expressionPedal = null
        )
    }
}

@Preview(name = "8x4 Full", showBackground = true, widthDp = 400)
@Composable
private fun MiniPreview8x4Full() {
    val pedals = (0 until 32).map {
        Pedal(
            id = it.toString(),
            name = "P$it",
            type = PedalType.PRESET,
            knobs = emptyList(),
            order = it,
            color = (0xFF000000 or (0xFF0000 + it * 0x082108).toLong()).toLong()
        )
    }
    ToneStoreTheme {
        MiniPedalBoardPreview(
            slots = pedals,
            columns = 8,
            rows = 4,
            expressionPedal = null
        )
    }
}

@Preview(name = "4x2 Empty Board", showBackground = true, widthDp = 350)
@Composable
private fun MiniPreview4x2Empty() {
    ToneStoreTheme {
        MiniPedalBoardPreview(
            slots = List(8) { null },
            columns = 4,
            rows = 2,
            expressionPedal = null
        )
    }
}

@Preview(name = "With Expression Pedal", showBackground = true, widthDp = 350)
@Composable
private fun MiniPreviewWithExpression() {
    ToneStoreTheme {
        MiniPedalBoardPreview(
            slots = listOf(
                Pedal(id = "1", name = "OD", type = PedalType.PRESET, knobs = emptyList(), order = 0, color = 0xFFFF9800),
                Pedal(id = "2", name = "Dist", type = PedalType.PRESET, knobs = emptyList(), order = 1, color = 0xFFF44336),
                Pedal(id = "3", name = "Delay", type = PedalType.PRESET, knobs = emptyList(), order = 2, color = 0xFF2196F3),
                Pedal(id = "4", name = "Reverb", type = PedalType.PRESET, knobs = emptyList(), order = 3, color = 0xFF9C27B0),
                null,
                null,
                null,
                null
            ),
            columns = 4,
            rows = 2,
            expressionPedal = Pedal(
                id = "wah",
                name = "Wah",
                type = PedalType.PRESET,
                knobs = emptyList(),
                order = 0,
                color = 0xFF4CAF50
            )
        )
    }
}
