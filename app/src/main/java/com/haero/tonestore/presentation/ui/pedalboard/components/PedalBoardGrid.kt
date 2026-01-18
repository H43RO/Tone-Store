package com.haero.tonestore.presentation.ui.pedalboard.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.haero.tonestore.domain.model.Pedal
import kotlin.math.roundToInt

/**
 * 동적 columns x rows 페달보드 그리드 컴포넌트 (드래그 앤 드롭 지원)
 */
@Composable
fun PedalBoardGrid(
    slots: List<Pedal?>,
    columns: Int,
    rows: Int,
    onSlotClick: (Int) -> Unit,
    onAddClick: (Int) -> Unit,
    onSwapSlots: (fromIndex: Int, toIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    isEditable: Boolean = true
) {
    val totalSlots = columns * rows
    require(slots.size >= totalSlots) {
        "slots must have at least $totalSlots elements (got ${slots.size})"
    }

    // 페달 슬롯 너비와 높이
    val slotWidth = 72.dp
    val slotHeight = 120.dp
    val horizontalSpacing = 8.dp
    val verticalSpacing = 12.dp

    // 드래그 상태
    var draggingIndex by remember { mutableIntStateOf(-1) }
    var dragOffsetX by remember { mutableStateOf(0f) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    var targetIndex by remember { mutableIntStateOf(-1) }

    // 슬롯 위치 저장
    val slotPositions = remember { mutableMapOf<Int, Pair<Float, Float>>() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing)
    ) {
        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    val pedal = slots.getOrNull(index)
                    val isDragging = draggingIndex == index
                    val isDropTarget = targetIndex == index && draggingIndex != index

                    val elevation by animateDpAsState(
                        targetValue = if (isDragging) 8.dp else 0.dp,
                        label = "elevation"
                    )

                    Box(
                        modifier = Modifier
                            .width(slotWidth)
                            .zIndex(if (isDragging) 1f else 0f)
                            .onGloballyPositioned { coordinates ->
                                val position = coordinates.positionInParent()
                                slotPositions[index] = Pair(position.x, position.y)
                            }
                            .then(
                                if (isDragging) {
                                    Modifier
                                        .offset { IntOffset(dragOffsetX.roundToInt(), dragOffsetY.roundToInt()) }
                                        .shadow(elevation)
                                        .graphicsLayer {
                                            scaleX = 1.05f
                                            scaleY = 1.05f
                                        }
                                } else {
                                    Modifier
                                }
                            )
                            .then(
                                if (isEditable && pedal != null) {
                                    Modifier.pointerInput(index) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                draggingIndex = index
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                dragOffsetX += dragAmount.x
                                                dragOffsetY += dragAmount.y

                                                // 드래그 중인 슬롯의 현재 중심점 계산
                                                val currentPos = slotPositions[index] ?: return@detectDragGesturesAfterLongPress
                                                val currentCenterX = currentPos.first + dragOffsetX + slotWidth.toPx() / 2
                                                val currentCenterY = currentPos.second + dragOffsetY + slotHeight.toPx() / 2

                                                // 가장 가까운 슬롯 찾기
                                                var closestIndex = -1
                                                var closestDistance = Float.MAX_VALUE

                                                slotPositions.forEach { (slotIdx, pos) ->
                                                    if (slotIdx != index) {
                                                        val slotCenterX = pos.first + slotWidth.toPx() / 2
                                                        val slotCenterY = pos.second + slotHeight.toPx() / 2
                                                        val distance = kotlin.math.sqrt(
                                                            (currentCenterX - slotCenterX) * (currentCenterX - slotCenterX) +
                                                                (currentCenterY - slotCenterY) * (currentCenterY - slotCenterY)
                                                        )
                                                        if (distance < closestDistance && distance < slotWidth.toPx()) {
                                                            closestDistance = distance
                                                            closestIndex = slotIdx
                                                        }
                                                    }
                                                }
                                                targetIndex = closestIndex
                                            },
                                            onDragEnd = {
                                                if (targetIndex != -1 && targetIndex != draggingIndex) {
                                                    onSwapSlots(draggingIndex, targetIndex)
                                                }
                                                draggingIndex = -1
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                                targetIndex = -1
                                            },
                                            onDragCancel = {
                                                draggingIndex = -1
                                                dragOffsetX = 0f
                                                dragOffsetY = 0f
                                                targetIndex = -1
                                            }
                                        )
                                    }
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        PedalSlot(
                            index = index,
                            pedal = pedal,
                            showAddButton = (pedal == null),
                            onAddClick = { onAddClick(index) },
                            onPedalClick = { onSlotClick(index) },
                            isEditable = isEditable,
                            isDragging = isDragging,
                            isDropTarget = isDropTarget
                        )
                    }
                }
            }
        }
    }
}
