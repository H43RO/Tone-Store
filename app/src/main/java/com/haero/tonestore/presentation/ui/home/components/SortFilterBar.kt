package com.haero.tonestore.presentation.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.haero.tonestore.R
import com.haero.tonestore.presentation.ui.home.SortOption
import com.haero.tonestore.presentation.ui.home.ViewMode

/**
 * SortFilterBar - 홈 화면의 정렬 및 뷰 모드 토글 바
 *
 * @param viewMode 현재 선택된 뷰 모드 (LIST, GRID)
 * @param sortOption 현재 선택된 정렬 옵션 (FAVORITES_FIRST, DATE_FIRST)
 * @param onViewModeChange 뷰 모드 변경 콜백
 * @param onSortOptionChange 정렬 옵션 변경 콜백
 * @param modifier Compose Modifier
 */
@Composable
fun SortFilterBar(
    viewMode: ViewMode,
    sortOption: SortOption,
    onViewModeChange: (ViewMode) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var sortExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: View toggle (list/grid icons)
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(4.dp)
        ) {
            ViewToggleButton(
                isSelected = viewMode == ViewMode.LIST,
                icon = Icons.Default.ViewList,
                contentDescription = stringResource(R.string.view_list),
                onClick = { onViewModeChange(ViewMode.LIST) }
            )
            ViewToggleButton(
                isSelected = viewMode == ViewMode.GRID,
                icon = Icons.Default.GridView,
                contentDescription = stringResource(R.string.view_grid),
                onClick = { onViewModeChange(ViewMode.GRID) }
            )
        }

        // Right: Sort dropdown
        Box {
            FilterChip(
                selected = true,
                onClick = { sortExpanded = !sortExpanded },
                label = {
                    Text(
                        text = when (sortOption) {
                            SortOption.FAVORITES_FIRST -> stringResource(R.string.sort_favorites_first)
                            SortOption.DATE_FIRST -> stringResource(R.string.sort_date_first)
                        }
                    )
                }
            )
            DropdownMenu(
                expanded = sortExpanded,
                onDismissRequest = { sortExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.sort_favorites_first)) },
                    onClick = {
                        onSortOptionChange(SortOption.FAVORITES_FIRST)
                        sortExpanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.sort_date_first)) },
                    onClick = {
                        onSortOptionChange(SortOption.DATE_FIRST)
                        sortExpanded = false
                    }
                )
            }
        }
    }
}

/**
 * ViewToggleButton - 뷰 모드 토글 버튼 (리스트/그리드)
 */
@Composable
private fun ViewToggleButton(
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "viewToggleBackground"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "viewToggleIcon"
    )

    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview
@Composable
private fun SortFilterBarPreview_ListView_FavoritesFirst() {
    MaterialTheme {
        SortFilterBar(
            viewMode = ViewMode.LIST,
            sortOption = SortOption.FAVORITES_FIRST,
            onViewModeChange = {},
            onSortOptionChange = {}
        )
    }
}

@Preview
@Composable
private fun SortFilterBarPreview_GridView_DateFirst() {
    MaterialTheme {
        SortFilterBar(
            viewMode = ViewMode.GRID,
            sortOption = SortOption.DATE_FIRST,
            onViewModeChange = {},
            onSortOptionChange = {}
        )
    }
}
