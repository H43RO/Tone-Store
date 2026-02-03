package com.haero.tonestore.presentation.ui.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.domain.model.GenreTag
import com.haero.tonestore.presentation.ui.components.SectionHeader
import com.haero.tonestore.ui.designsystem.Obsidian
import com.haero.tonestore.ui.designsystem.ObsidianChip
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSection(selectedTags: List<GenreTag>, onTagToggle: (GenreTag) -> Unit, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(true) }
    val isKorean = LocalConfiguration.current.locales[0].language == Locale.KOREAN.language

    SectionHeader(
        title = stringResource(R.string.genre_tags),
        isExpanded = isExpanded,
        onToggle = { isExpanded = isExpanded.not() },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Obsidian.spacing.screenPadding)
        ) {
            Text(
                text = stringResource(R.string.select_tags_hint),
                style = Obsidian.typography.bodySmall,
                color = Obsidian.colors.textMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GenreTag.entries.forEach { tag ->
                    val isSelected = selectedTags.contains(tag)
                    ObsidianChip(
                        label = if (isKorean) tag.displayNameKo else tag.displayName,
                        selected = isSelected,
                        onClick = { onTagToggle(tag) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
