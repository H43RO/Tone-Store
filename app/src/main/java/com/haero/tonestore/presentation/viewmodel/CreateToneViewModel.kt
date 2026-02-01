package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.model.GenreTag
import com.haero.tonestore.domain.model.Knob
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalBoard
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.domain.model.SavedCustomPedal
import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.domain.usecase.GetAllSavedPedalBoardsUseCase
import com.haero.tonestore.domain.usecase.GetAllToneSettingsUseCase
import com.haero.tonestore.domain.usecase.GetPresetPedalsUseCase
import com.haero.tonestore.domain.usecase.GetToneSettingByIdUseCase
import com.haero.tonestore.domain.usecase.SaveCustomPedalUseCase
import com.haero.tonestore.domain.usecase.SaveToneSettingUseCase
import com.haero.tonestore.presentation.ui.create.CreateToneIntent
import com.haero.tonestore.presentation.ui.create.CreateToneState
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateToneViewModel(
    private val getToneSettingByIdUseCase: GetToneSettingByIdUseCase,
    private val saveToneSettingUseCase: SaveToneSettingUseCase,
    private val getPresetPedalsUseCase: GetPresetPedalsUseCase,
    private val getAllSavedPedalBoardsUseCase: GetAllSavedPedalBoardsUseCase,
    private val getAllToneSettingsUseCase: GetAllToneSettingsUseCase,
    private val saveCustomPedalUseCase: SaveCustomPedalUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreateToneState())
    val state: StateFlow<CreateToneState> = _state.asStateFlow()

    private var allToneSettings: List<ToneSetting> = emptyList()

    init {
        loadPresetPedals()
        loadSavedPedalBoards()
        loadAllToneSettings()
    }

    fun handleIntent(intent: CreateToneIntent) {
        when (intent) {
            is CreateToneIntent.LoadToneSetting -> loadToneSetting(intent.id)
            is CreateToneIntent.UpdateSongName -> updateSongName(intent.name)
            is CreateToneIntent.LoadSavedPedalBoard -> loadSavedPedalBoard(intent.pedalBoard)
            is CreateToneIntent.AddPresetPedal -> addPresetPedal(intent.pedal)
            is CreateToneIntent.AddCustomPedal -> addCustomPedal(intent.name, intent.knobNames)
            is CreateToneIntent.RemovePedal -> removePedal(intent.pedalId)
            is CreateToneIntent.UpdatePedalKnob -> updatePedalKnob(intent.pedalId, intent.knobIndex, intent.value)
            is CreateToneIntent.TogglePedalEnabled -> togglePedalEnabled(intent.pedalId)
            is CreateToneIntent.UpdateAmpModel -> updateAmpModel(intent.model)
            is CreateToneIntent.UpdateAmpKnob -> updateAmpKnob(intent.knobName, intent.value)
            is CreateToneIntent.UpdateGuitarModel -> updateGuitarModel(intent.model)
            is CreateToneIntent.UpdatePickupPosition -> updatePickupPosition(intent.position)
            is CreateToneIntent.UpdateGuitarTone -> updateGuitarTone(intent.value)
            is CreateToneIntent.UpdateGuitarVolume -> updateGuitarVolume(intent.value)
            is CreateToneIntent.ToggleTag -> toggleTag(intent.tag)
            is CreateToneIntent.SaveToneSetting -> saveToneSetting()
            is CreateToneIntent.NavigationHandled -> clearNavigation()
            is CreateToneIntent.ClearError -> clearError()
        }
    }

    private fun loadPresetPedals() {
        val presets = getPresetPedalsUseCase()
        _state.update { it.copy(presetPedals = presets) }
    }

    private fun loadSavedPedalBoards() {
        viewModelScope.launch {
            getAllSavedPedalBoardsUseCase().collect { pedalBoards ->
                _state.update { it.copy(savedPedalBoards = pedalBoards) }
            }
        }
    }

    private fun loadSavedPedalBoard(savedPedalBoard: SavedPedalBoard) {
        _state.update { it.copy(pedalBoard = savedPedalBoard.toPedalBoard()) }
    }

    private fun loadToneSetting(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                getToneSettingByIdUseCase(id)
            }.onSuccess { setting ->
                if (setting != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isEditMode = true,
                            editingId = id,
                            songName = setting.songName,
                            pedalBoard = setting.pedalBoard,
                            ampSetting = setting.ampSetting,
                            guitarSetting = setting.guitarSetting,
                            selectedTags = setting.tags
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "톤 세팅을 찾을 수 없습니다") }
                }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun updateSongName(name: String) {
        _state.update { it.copy(songName = name, songNameError = null) }
    }

    private fun addPresetPedal(pedal: Pedal) {
        val currentPedals = _state.value.pedalBoard.pedals
        val newPedal = pedal.copy(
            id = UUID.randomUUID().toString(),
            order = currentPedals.size
        )
        _state.update {
            it.copy(pedalBoard = PedalBoard(currentPedals + newPedal))
        }
    }

    private fun addCustomPedal(name: String, knobNames: List<String>) {
        val currentPedals = _state.value.pedalBoard.pedals
        val knobs = knobNames.map { knobName -> Knob(name = knobName, value = 5f) }
        val newPedal = Pedal(
            id = UUID.randomUUID().toString(),
            name = name,
            type = PedalType.CUSTOM,
            knobs = knobs,
            order = currentPedals.size
        )
        _state.update {
            it.copy(pedalBoard = PedalBoard(currentPedals + newPedal))
        }
    }

    private fun removePedal(pedalId: String) {
        val updatedPedals = _state.value.pedalBoard.pedals
            .filter { it.id != pedalId }
            .mapIndexed { index, pedal -> pedal.copy(order = index) }
        _state.update { it.copy(pedalBoard = PedalBoard(updatedPedals)) }
    }

    private fun updatePedalKnob(pedalId: String, knobIndex: Int, value: Float) {
        val updatedPedals = _state.value.pedalBoard.pedals.map { pedal ->
            if (pedal.id == pedalId) {
                val updatedKnobs = pedal.knobs.mapIndexed { index, knob ->
                    if (index == knobIndex) knob.copy(value = value) else knob
                }
                pedal.copy(knobs = updatedKnobs)
            } else {
                pedal
            }
        }
        _state.update { it.copy(pedalBoard = PedalBoard(updatedPedals)) }
    }

    private fun togglePedalEnabled(pedalId: String) {
        val updatedPedals = _state.value.pedalBoard.pedals.map { pedal ->
            if (pedal.id == pedalId) pedal.copy(isEnabled = pedal.isEnabled.not()) else pedal
        }
        _state.update { it.copy(pedalBoard = PedalBoard(updatedPedals)) }
    }

    private fun updateAmpModel(model: String) {
        _state.update { it.copy(ampSetting = it.ampSetting.copy(ampModel = model.ifBlank { null })) }
    }

    private fun updateAmpKnob(knobName: String, value: Float) {
        _state.update { state ->
            val current = state.ampSetting
            val updated = when (knobName) {
                "gain" -> current.copy(gain = value)
                "bass" -> current.copy(bass = value)
                "middle" -> current.copy(middle = value)
                "treble" -> current.copy(treble = value)
                "presence" -> current.copy(presence = value)
                "reverb" -> current.copy(reverb = value)
                "masterVolume" -> current.copy(masterVolume = value)
                else -> current
            }
            state.copy(ampSetting = updated)
        }
    }

    private fun updateGuitarModel(model: String) {
        _state.update {
            it.copy(guitarSetting = it.guitarSetting.copy(guitarModel = model.ifBlank { null }))
        }
    }

    private fun updatePickupPosition(position: com.haero.tonestore.domain.model.PickupPosition) {
        _state.update { it.copy(guitarSetting = it.guitarSetting.copy(pickupSelector = position)) }
    }

    private fun updateGuitarTone(value: Float) {
        _state.update { it.copy(guitarSetting = it.guitarSetting.copy(toneKnob = value)) }
    }

    private fun updateGuitarVolume(value: Float) {
        _state.update { it.copy(guitarSetting = it.guitarSetting.copy(volumeKnob = value)) }
    }

    private fun toggleTag(tag: GenreTag) {
        _state.update { state ->
            val currentTags = state.selectedTags
            val updatedTags = if (currentTags.contains(tag)) {
                currentTags - tag
            } else {
                currentTags + tag
            }
            state.copy(selectedTags = updatedTags)
        }
    }

    private fun saveToneSetting() {
        val currentState = _state.value

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            // 제목이 비어있으면 자동 생성
            val finalSongName = if (currentState.songName.isBlank()) {
                generateDefaultToneName()
            } else {
                currentState.songName
            }

            val now = System.currentTimeMillis()
            val toneSetting = ToneSetting(
                id = currentState.editingId ?: UUID.randomUUID().toString(),
                songName = finalSongName,
                createdAt = if (currentState.isEditMode) now else now,
                updatedAt = now,
                pedalBoard = currentState.pedalBoard,
                ampSetting = currentState.ampSetting,
                guitarSetting = currentState.guitarSetting,
                tags = currentState.selectedTags
            )
            runCatching {
                // 1. 톤 세팅 저장
                saveToneSettingUseCase(toneSetting)

                // 2. 커스텀 페달 자동 저장
                saveCustomPedalsFromTone(currentState.pedalBoard)
            }.onSuccess {
                _state.update { it.copy(isSaving = false, navigateBack = true, showSaveSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    private suspend fun saveCustomPedalsFromTone(pedalBoard: PedalBoard) {
        val customPedals = pedalBoard.pedals.filter { it.type == PedalType.CUSTOM }
        customPedals.forEach { pedal ->
            val savedPedal = SavedCustomPedal(
                id = pedal.id,
                name = pedal.name,
                knobNames = pedal.knobs.map { it.name },
                color = pedal.color,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            runCatching {
                saveCustomPedalUseCase(savedPedal)
            }
        }
    }

    private fun clearNavigation() {
        _state.update { it.copy(navigateBack = false, showSaveSuccess = false) }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun loadAllToneSettings() {
        viewModelScope.launch {
            getAllToneSettingsUseCase().collect { toneSettings ->
                allToneSettings = toneSettings
            }
        }
    }

    private suspend fun generateDefaultToneName(): String {
        val baseName = "나의 톤"
        val existingNames = allToneSettings.map { it.songName }.toSet()

        // "나의 톤" 시작하는 기존 번호들 추출
        val existingNumbers = existingNames
            .filter { it.startsWith(baseName) }
            .mapNotNull { name ->
                name.removePrefix(baseName).trim().toIntOrNull()
            }

        // 1부터 시작해서 사용 가능한 첫 번째 번호 찾기
        var counter = 1
        while (existingNumbers.contains(counter) || existingNames.contains("$baseName $counter")) {
            counter++
        }

        return "$baseName $counter"
    }
}
