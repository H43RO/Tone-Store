package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.ui.theme.ToneStoreTheme

/**
 * Pedalboard information editor component.
 * Displays name TextField, layout stepper controls, and pedal count.
 */
@Composable
fun PedalboardInfoEditor(
    name: String,
    columns: Int,
    rows: Int,
    pedalCount: Int,
    totalSlots: Int,
    nameError: String?,
    onNameChange: (String) -> Unit,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.pedalboard_name)) },
                placeholder = { Text(stringResource(R.string.pedalboard_name_hint)) },
                singleLine = true,
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            LayoutStepper(
                columns = columns,
                rows = rows,
                onColumnsChange = onColumnsChange,
                onRowsChange = onRowsChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            val pedalCountText = stringResource(R.string.pedal_count, pedalCount)
            val slotsText = stringResource(R.string.slots)
            Text(
                text = "$pedalCountText / $totalSlots $slotsText",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PedalboardInfoEditorPreview() {
    ToneStoreTheme {
        PedalboardInfoEditor(
            name = "My Pedalboard",
            columns = 4,
            rows = 2,
            pedalCount = 5,
            totalSlots = 8,
            nameError = null,
            onNameChange = {},
            onColumnsChange = {},
            onRowsChange = {}
        )
    }
}
