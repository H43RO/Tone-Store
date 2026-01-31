package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.haero.tonestore.domain.model.Pedal

@Composable
fun ExpressionPedalZone(
    expressionPedal: Pedal?,
    onSelectPedal: () -> Unit,
    onRemovePedal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val footPedalShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 4.dp,
        bottomEnd = 4.dp
    )

    Box(
        modifier = modifier
            .width(80.dp)
            .height(200.dp)
            .clip(footPedalShape)
            .then(
                if (expressionPedal != null) {
                    val pedalColor = expressionPedal.color?.let { Color(it) }
                        ?: MaterialTheme.colorScheme.surfaceVariant
                    Modifier
                        .background(pedalColor.copy(alpha = 0.2f))
                        .border(3.dp, pedalColor, footPedalShape)
                } else {
                    Modifier
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = footPedalShape
                        )
                }
            )
            .clickable(onClick = onSelectPedal),
        contentAlignment = Alignment.Center
    ) {
        if (expressionPedal != null) {
            val pedalColor = expressionPedal.color?.let { Color(it) }
                ?: MaterialTheme.colorScheme.surfaceVariant

            Canvas(modifier = Modifier.fillMaxSize()) {
                val stripeWidth = 6.dp.toPx()
                val stripeSpacing = 4.dp.toPx()
                val totalWidth = size.width

                var x = 0f
                while (x < totalWidth) {
                    drawLine(
                        color = pedalColor.copy(alpha = 0.3f),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = stripeWidth
                    )
                    x += stripeWidth + stripeSpacing
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            if (expressionPedal != null) {
                Text(
                    text = expressionPedal.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                IconButton(
                    onClick = onRemovePedal,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove",
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Text(
                    text = "Wah\n/\nWhammy",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                )
            }
        }
    }
}
