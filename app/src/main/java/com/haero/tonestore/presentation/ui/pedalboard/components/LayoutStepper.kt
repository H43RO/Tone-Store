package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R

/**
 * Layout stepper component for adjusting pedalboard grid dimensions.
 *
 * Provides +/− buttons to increment/decrement columns and rows within defined ranges.
 * Integrated with PedalBoardIntent.UpdateLayout(columns, rows) for state management.
 *
 * @param columns Current number of columns (range: 1-6)
 * @param rows Current number of rows (range: 1-4)
 * @param onColumnsChange Callback invoked when columns value changes
 * @param onRowsChange Callback invoked when rows value changes
 * @param modifier Modifier for the root Row
 */
@Composable
fun LayoutStepper(
    columns: Int,
    rows: Int,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.layout_size),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(60.dp)
        )

        StepperControl(
            value = columns,
            label = stringResource(R.string.columns),
            minValue = 1,
            maxValue = 6,
            onValueChange = onColumnsChange,
            modifier = Modifier.weight(1f)
        )

        Text(
            "×",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        StepperControl(
            value = rows,
            label = stringResource(R.string.rows),
            minValue = 1,
            maxValue = 4,
            onValueChange = onRowsChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StepperControl(
    value: Int,
    label: String,
    minValue: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = { if (value > minValue) onValueChange(value - 1) },
            shape = CircleShape,
            enabled = value > minValue,
            modifier = Modifier.size(36.dp),
            color = if (value > minValue) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ) {
            IconButton(
                onClick = { if (value > minValue) onValueChange(value - 1) },
                enabled = value > minValue,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "$label 감소",
                    tint = if (value > minValue) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$value",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            onClick = { if (value < maxValue) onValueChange(value + 1) },
            shape = CircleShape,
            enabled = value < maxValue,
            modifier = Modifier.size(36.dp),
            color = if (value < maxValue) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ) {
            IconButton(
                onClick = { if (value < maxValue) onValueChange(value + 1) },
                enabled = value < maxValue,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "$label 증가",
                    tint = if (value < maxValue) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
