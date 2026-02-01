package com.haero.tonestore.presentation.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ViewList
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haero.tonestore.R
import com.haero.tonestore.presentation.ui.home.SortOption
import com.haero.tonestore.presentation.ui.home.ViewMode
import com.haero.tonestore.ui.components.LocalEmberGlassTheme

/**
 * SortFilterBar - Glassmorphism 스타일 정렬 및 뷰 모드 토글 바
 */
@Composable
fun SortFilterBar(
    viewMode: ViewMode,
    sortOption: SortOption,
    onViewModeChange: (ViewMode) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current
    var sortExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Glass View toggle
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.1f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            GlassViewToggleButton(
                isSelected = viewMode == ViewMode.LIST,
                icon = Icons.Default.ViewList,
                contentDescription = stringResource(R.string.view_list),
                onClick = { onViewModeChange(ViewMode.LIST) }
            )
            GlassViewToggleButton(
                isSelected = viewMode == ViewMode.GRID,
                icon = Icons.Default.GridView,
                contentDescription = stringResource(R.string.view_grid),
                onClick = { onViewModeChange(ViewMode.GRID) }
            )
        }

        // Glass Sort dropdown
        Box {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(theme.primary.copy(alpha = 0.15f))
                    .border(1.dp, theme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable { sortExpanded = !sortExpanded }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = when (sortOption) {
                            SortOption.FAVORITES_FIRST -> stringResource(R.string.sort_favorites_first)
                            SortOption.DATE_FIRST -> stringResource(R.string.sort_date_first)
                        },
                        color = theme.primary,
                        fontSize = 13.sp
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = theme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            DropdownMenu(
                expanded = sortExpanded,
                onDismissRequest = { sortExpanded = false },
                modifier = Modifier.background(theme.surfaceElevated)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(R.string.sort_favorites_first),
                            color = theme.textPrimary
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
                            color = theme.textPrimary
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

/**
 * GlassViewToggleButton - Glassmorphism 뷰 모드 토글 버튼
 */
@Composable
private fun GlassViewToggleButton(
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val theme = LocalEmberGlassTheme.current

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            theme.primary.copy(alpha = 0.2f)
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
            theme.primary
        } else {
            theme.textSecondary
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "viewToggleIcon"
    )

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (isSelected) {
                    Modifier.border(1.dp, theme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
    }
}
