package com.haero.tonestore.presentation.ui.create

import com.haero.tonestore.domain.model.GenreTag
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PickupPosition
import com.haero.tonestore.domain.model.SavedPedalBoard

sealed interface CreateToneIntent {
    data class LoadToneSetting(val id: String) : CreateToneIntent

    data class UpdateSongName(val name: String) : CreateToneIntent

    data class LoadSavedPedalBoard(val pedalBoard: SavedPedalBoard) : CreateToneIntent

    data class AddPresetPedal(val pedal: Pedal) : CreateToneIntent

    data class AddCustomPedal(val name: String, val knobNames: List<String>) : CreateToneIntent

    data class RemovePedal(val pedalId: String) : CreateToneIntent

    data class UpdatePedalKnob(
        val pedalId: String,
        val knobIndex: Int,
        val value: Float
    ) : CreateToneIntent

    data class TogglePedalEnabled(val pedalId: String) : CreateToneIntent

    data class UpdateAmpModel(val model: String) : CreateToneIntent

    data class UpdateAmpKnob(val knobName: String, val value: Float) : CreateToneIntent

    data class UpdateGuitarModel(val model: String) : CreateToneIntent

    data class UpdatePickupPosition(val position: PickupPosition) : CreateToneIntent

    data class UpdateGuitarTone(val value: Float) : CreateToneIntent

    data class UpdateGuitarVolume(val value: Float) : CreateToneIntent

    data class ToggleTag(val tag: GenreTag) : CreateToneIntent

    data object SaveToneSetting : CreateToneIntent

    data object NavigationHandled : CreateToneIntent

    data object ClearError : CreateToneIntent
}
