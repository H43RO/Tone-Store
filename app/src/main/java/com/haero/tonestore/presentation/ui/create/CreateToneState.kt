package com.haero.tonestore.presentation.ui.create

import com.haero.tonestore.domain.model.AmpSetting
import com.haero.tonestore.domain.model.GenreTag
import com.haero.tonestore.domain.model.GuitarSetting
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalBoard
import com.haero.tonestore.domain.model.SavedPedalBoard

data class CreateToneState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val editingId: String? = null,

    val songName: String = "",
    val pedalBoard: PedalBoard = PedalBoard(),
    val ampSetting: AmpSetting = AmpSetting(),
    val guitarSetting: GuitarSetting = GuitarSetting(),
    val selectedTags: List<GenreTag> = emptyList(),

    val presetPedals: List<Pedal> = emptyList(),

    val savedPedalBoards: List<SavedPedalBoard> = emptyList(),

    val isSaving: Boolean = false,
    val error: String? = null,
    val songNameError: String? = null,
    val navigateBack: Boolean = false,
    val showSaveSuccess: Boolean = false
)
