package com.haero.tonestore.presentation.ui.pedalboard

import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.SavedCustomPedal
import com.haero.tonestore.domain.model.SavedPedalBoard

data class PedalBoardState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val editingId: String? = null,

    val name: String = "",
    val columns: Int = SavedPedalBoard.DEFAULT_COLUMNS,
    val rows: Int = SavedPedalBoard.DEFAULT_ROWS,
    val slots: List<Pedal?> = List(SavedPedalBoard.DEFAULT_COLUMNS * SavedPedalBoard.DEFAULT_ROWS) { null },
    val expressionPedal: Pedal? = null,

    val presetPedals: List<Pedal> = emptyList(),
    val customPedals: List<SavedCustomPedal> = emptyList(),

    val editingSlotIndex: Int? = null,

    val draggingSlotIndex: Int? = null,

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
