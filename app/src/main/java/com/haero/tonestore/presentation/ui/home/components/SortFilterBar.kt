package com.haero.tonestore.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.presentation.ui.home.SortOption
import com.haero.tonestore.ui.designsystem.Obsidian

/**
 * SortFilterBar - Obsidian 스타일 정렬 옵션 바
 * (그리드/리스트 뷰 토글 제거됨)
 */
@Composable
fun SortFilterBar(
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var sortExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Obsidian.spacing.screenPadding, vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Obsidian Sort dropdown
        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(Obsidian.radius.button))
                    .background(Obsidian.colors.surfaceElevated)
                    .border(
                        1.dp,
                        Obsidian.colors.borderSubtle,
                        RoundedCornerShape(Obsidian.radius.button)
                    )
                    .clickable { sortExpanded = !sortExpanded }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = when (sortOption) {
                        SortOption.FAVORITES_FIRST -> stringResource(R.string.sort_favorites_first)
                        SortOption.DATE_FIRST -> stringResource(R.string.sort_date_first)
                    },
                    style = Obsidian.typography.labelMedium,
                    color = Obsidian.colors.textPrimary
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Obsidian.colors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = sortExpanded,
                onDismissRequest = { sortExpanded = false },
                modifier = Modifier
                    .background(Obsidian.colors.surfaceElevated)
                    .border(1.dp, Obsidian.colors.border, RoundedCornerShape(4.dp))
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(R.string.sort_favorites_first),
                            style = Obsidian.typography.bodyMedium,
                            color = if (sortOption == SortOption.FAVORITES_FIRST) Obsidian.colors.primary else Obsidian.colors.textPrimary
                        )
                    },
                    onClick = {
                        onSortOptionChange(SortOption.FAVORITES_FIRST)
                        sortExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(R.string.sort_date_first),
                            style = Obsidian.typography.bodyMedium,
                            color = if (sortOption == SortOption.DATE_FIRST) Obsidian.colors.primary else Obsidian.colors.textPrimary
                        )
                    },
                    onClick = {
                        onSortOptionChange(SortOption.DATE_FIRST)
                        sortExpanded = false
                    }
                )
            }
        }
    }
}
