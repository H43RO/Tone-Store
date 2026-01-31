package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.haero.tonestore.data.preset.PresetPedals

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpressionPedalSelectionDialog(
    onDismiss: () -> Unit,
    onSelectWah: () -> Unit,
    onSelectWhammy: () -> Unit
) {
    val presetPedals = remember { PresetPedals.getPresetPedals() }
    val wahPedal = remember { presetPedals.find { it.name == "Wah" } }
    val whammyPedal = remember { presetPedals.find { it.name == "Whammy" } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Expression Pedal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                wahPedal?.let { pedal ->
                    ExpressionPedalOption(
                        pedalName = pedal.name,
                        pedalColor = pedal.color?.let { Color(it) },
                        onClick = onSelectWah
                    )
                }

                whammyPedal?.let { pedal ->
                    ExpressionPedalOption(
                        pedalName = pedal.name,
                        pedalColor = pedal.color?.let { Color(it) },
                        onClick = onSelectWhammy
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ExpressionPedalOption(
    pedalName: String,
    pedalColor: Color?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (pedalColor != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(pedalColor)
                )
            }

            Text(
                text = pedalName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
