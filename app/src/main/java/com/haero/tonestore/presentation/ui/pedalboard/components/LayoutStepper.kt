package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.ui.designsystem.Obsidian

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
            style = Obsidian.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(60.dp),
            color = Obsidian.colors.textSecondary
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
            style = Obsidian.typography.titleLarge,
            color = Obsidian.colors.textMuted
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
        val canDecrease = value > minValue
        val canIncrease = value < maxValue

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = if (canDecrease) Obsidian.colors.primaryMuted else Obsidian.colors.surfaceHighlight,
                    shape = CircleShape
                )
                .clickable(enabled = canDecrease) { if (canDecrease) onValueChange(value - 1) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "$label 감소",
                tint = if (canDecrease) Obsidian.colors.primary else Obsidian.colors.textMuted,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "$value",
            style = Obsidian.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Obsidian.colors.textPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = if (canIncrease) Obsidian.colors.primaryMuted else Obsidian.colors.surfaceHighlight,
                    shape = CircleShape
                )
                .clickable(enabled = canIncrease) { if (canIncrease) onValueChange(value + 1) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "$label 증가",
                tint = if (canIncrease) Obsidian.colors.primary else Obsidian.colors.textMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LayoutStepperPreview() {
    MaterialTheme {
        LayoutStepper(
            columns = 3,
            rows = 2,
            onColumnsChange = {},
            onRowsChange = {}
        )
    }
}
