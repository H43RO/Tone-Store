package com.haero.tonestore.presentation.ui.create

import com.haero.tonestore.domain.model.AmpSetting
import com.haero.tonestore.domain.model.GenreTag
import com.haero.tonestore.domain.model.GuitarSetting
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalBoard
import com.haero.tonestore.domain.model.SavedPedalBoard

/**
 * Create/Edit 화면의 상태
 */
data class CreateToneState(
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val editingId: String? = null,
    
    // 입력 데이터
    val songName: String = "",
    val pedalBoard: PedalBoard = PedalBoard(),
    val ampSetting: AmpSetting = AmpSetting(),
    val guitarSetting: GuitarSetting = GuitarSetting(),
    val selectedTags: List<GenreTag> = emptyList(),
    
    // 프리셋 페달 목록
    val presetPedals: List<Pedal> = emptyList(),
    
    // 저장된 페달보드 목록
    val savedPedalBoards: List<SavedPedalBoard> = emptyList(),
    
    // UI 상태
    val isSaving: Boolean = false,
    val error: String? = null,
    val songNameError: String? = null,
    val navigateBack: Boolean = false,
    val showSaveSuccess: Boolean = false
)
