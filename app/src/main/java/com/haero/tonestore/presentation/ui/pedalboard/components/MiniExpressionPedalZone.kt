package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.ui.theme.ToneStoreTheme

@Composable
fun MiniExpressionPedalZone(
    expressionPedal: Pedal?,
    modifier: Modifier = Modifier,
    height: Dp = 60.dp
) {
    val footPedalShape = RoundedCornerShape(
        topStart = 5.dp,
        topEnd = 5.dp,
        bottomStart = 1.dp,
        bottomEnd = 1.dp
    )

    Box(
        modifier = modifier
            .width(24.dp)
            .height(height)
            .clip(footPedalShape)
            .then(
                if (expressionPedal != null) {
                    val pedalColor = expressionPedal.color?.let { Color(it) }
                        ?: MaterialTheme.colorScheme.surfaceVariant
                    Modifier
                        .background(pedalColor.copy(alpha = 0.2f))
                        .border(1.dp, pedalColor, footPedalShape)
                } else {
                    Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = footPedalShape
                        )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (expressionPedal != null) {
            Text(
                text = expressionPedal.name,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp, vertical = 4.dp)
            )
        } else {
            Text(
                text = "Wah",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 7.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(name = "Mini Expression - Empty", showBackground = true)
@Composable
private fun MiniExpressionPedalZoneEmptyPreview() {
    ToneStoreTheme {
        MiniExpressionPedalZone(expressionPedal = null)
    }
}

@Preview(name = "Mini Expression - With Wah", showBackground = true)
@Composable
private fun MiniExpressionPedalZoneWithPedalPreview() {
    ToneStoreTheme {
        MiniExpressionPedalZone(
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
