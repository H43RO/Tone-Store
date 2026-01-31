package com.haero.tonestore.presentation.ui.pedalboard

import com.haero.tonestore.domain.model.Knob
import com.haero.tonestore.domain.model.Pedal

sealed interface PedalBoardIntent {
    data class LoadPedalBoard(val id: String) : PedalBoardIntent

    data class UpdateName(val name: String) : PedalBoardIntent

    data class UpdateLayout(val columns: Int, val rows: Int) : PedalBoardIntent

    data class AddPedalToSlot(val slotIndex: Int, val pedal: Pedal) : PedalBoardIntent

    data class AddCustomPedalToSlot(
        val slotIndex: Int,
        val name: String,
        val knobNames: List<String>
    ) : PedalBoardIntent

    data class RemovePedalFromSlot(val slotIndex: Int) : PedalBoardIntent

    data class SwapSlots(val fromIndex: Int, val toIndex: Int) : PedalBoardIntent

    data class MovePedalToSlot(val fromIndex: Int, val toIndex: Int) : PedalBoardIntent

    data class UpdatePedalKnob(
        val slotIndex: Int,
        val knobIndex: Int,
        val value: Float
    ) : PedalBoardIntent

    data class TogglePedalEnabled(val slotIndex: Int) : PedalBoardIntent

    data class OpenPedalEditor(val slotIndex: Int) : PedalBoardIntent

    data object ClosePedalEditor : PedalBoardIntent

    data class UpdatePedalColor(val slotIndex: Int, val color: Long?) : PedalBoardIntent

    data class UpdatePedalKnobs(val slotIndex: Int, val knobs: List<Knob>) : PedalBoardIntent

    data object SavePedalBoard : PedalBoardIntent

    data object DeletePedalBoard : PedalBoardIntent

    data object NavigationHandled : PedalBoardIntent
}
