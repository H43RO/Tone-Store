package com.haero.tonestore.presentation.ui.pedalboard

import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.SavedPedalBoard

/**
 * 페달보드 관리 화면의 상태
 */
data class PedalBoardState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val editingId: String? = null,
    
    // 페달보드 데이터
    val name: String = "",
    val columns: Int = SavedPedalBoard.DEFAULT_COLUMNS,
    val rows: Int = SavedPedalBoard.DEFAULT_ROWS,
    val slots: List<Pedal?> = List(SavedPedalBoard.DEFAULT_COLUMNS * SavedPedalBoard.DEFAULT_ROWS) { null },
    
    // 프리셋 페달 목록
    val presetPedals: List<Pedal> = emptyList(),
    
    // 페달 편집 상태
    val editingSlotIndex: Int? = null,
    
    // 드래그 상태
    val draggingSlotIndex: Int? = null,
    
    // UI 상태
    val isSaving: Boolean = false,
    val error: String? = null,
    val nameError: String? = null,
    val navigateBack: Boolean = false,
    val showSaveSuccess: Boolean = false,
    val showDeleteConfirm: Boolean = false
) {
    val totalSlots: Int get() = columns * rows
    val pedalCount: Int get() = slots.count { it != null }
    val firstEmptySlotIndex: Int? get() = slots.indexOfFirst { it == null }.takeIf { it >= 0 }
    val editingPedal: Pedal? get() = editingSlotIndex?.let { slots.getOrNull(it) }
}
