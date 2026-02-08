package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianTextField
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 20.dp)
    ) {
        ObsidianTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = stringResource(R.string.pedalboard_name_hint),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (nameError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = nameError,
                style = Obsidian.typography.bodySmall,
                color = Obsidian.colors.error
            )
        }

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
            style = Obsidian.typography.bodyMedium,
            color = Obsidian.colors.textSecondary
        )
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
