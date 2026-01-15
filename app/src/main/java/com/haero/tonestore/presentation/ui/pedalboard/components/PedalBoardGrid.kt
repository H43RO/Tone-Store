package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.haero.tonestore.domain.model.Pedal

/**
 * 동적 columns x rows 페달보드 그리드 컴포넌트
 */
@Composable
fun PedalBoardGrid(
    slots: List<Pedal?>,
    columns: Int,
    rows: Int,
    onSlotClick: (Int) -> Unit,
    onAddClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    val totalSlots = columns * rows
    require(slots.size >= totalSlots) { 
        "slots must have at least $totalSlots elements (got ${slots.size})" 
    }
    
    // 페달 슬롯 너비
    val slotWidth = 72.dp
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    val pedal = slots.getOrNull(index)
                    PedalSlot(
                        index = index,
                        pedal = pedal,
                        showAddButton = (pedal == null),
                        onAddClick = { onAddClick(index) },
                        onPedalClick = { onSlotClick(index) },
                        isEditable = isEditable,
                        modifier = Modifier.width(slotWidth)
                    )
                }
            }
        }
    }
}
