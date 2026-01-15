package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.model.Knob
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.domain.usecase.DeleteSavedPedalBoardUseCase
import com.haero.tonestore.domain.usecase.GetPresetPedalsUseCase
import com.haero.tonestore.domain.usecase.GetSavedPedalBoardByIdUseCase
import com.haero.tonestore.domain.usecase.SavePedalBoardUseCase
import com.haero.tonestore.presentation.ui.pedalboard.PedalBoardIntent
import com.haero.tonestore.presentation.ui.pedalboard.PedalBoardState
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 페달보드 관리 화면의 ViewModel (MVI 패턴)
 */
class PedalBoardViewModel(
    private val getSavedPedalBoardByIdUseCase: GetSavedPedalBoardByIdUseCase,
    private val savePedalBoardUseCase: SavePedalBoardUseCase,
    private val deleteSavedPedalBoardUseCase: DeleteSavedPedalBoardUseCase,
    private val getPresetPedalsUseCase: GetPresetPedalsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PedalBoardState())
    val state: StateFlow<PedalBoardState> = _state.asStateFlow()

    init {
        loadPresetPedals()
    }

    fun handleIntent(intent: PedalBoardIntent) {
        when (intent) {
            is PedalBoardIntent.LoadPedalBoard -> loadPedalBoard(intent.id)
            is PedalBoardIntent.UpdateName -> updateName(intent.name)
            is PedalBoardIntent.UpdateLayout -> updateLayout(intent.columns, intent.rows)
            is PedalBoardIntent.AddPedalToSlot -> addPedalToSlot(intent.slotIndex, intent.pedal)
            is PedalBoardIntent.AddCustomPedalToSlot -> addCustomPedalToSlot(
                intent.slotIndex,
                intent.name,
                intent.knobNames
            )
            is PedalBoardIntent.RemovePedalFromSlot -> removePedalFromSlot(intent.slotIndex)
            is PedalBoardIntent.SwapSlots -> swapSlots(intent.fromIndex, intent.toIndex)
            is PedalBoardIntent.MovePedalToSlot -> movePedalToSlot(intent.fromIndex, intent.toIndex)
            is PedalBoardIntent.UpdatePedalKnob -> updatePedalKnob(
                intent.slotIndex,
                intent.knobIndex,
                intent.value
            )
            is PedalBoardIntent.TogglePedalEnabled -> togglePedalEnabled(intent.slotIndex)
            is PedalBoardIntent.OpenPedalEditor -> openPedalEditor(intent.slotIndex)
            is PedalBoardIntent.ClosePedalEditor -> closePedalEditor()
            is PedalBoardIntent.SavePedalBoard -> savePedalBoard()
            is PedalBoardIntent.DeletePedalBoard -> deletePedalBoard()
            is PedalBoardIntent.NavigationHandled -> clearNavigation()
        }
    }

    private fun loadPresetPedals() {
        val presets = getPresetPedalsUseCase()
        _state.update { it.copy(presetPedals = presets) }
    }

    private fun loadPedalBoard(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                getSavedPedalBoardByIdUseCase(id)
            }.onSuccess { pedalBoard ->
                if (pedalBoard != null) {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isEditMode = true,
                            editingId = id,
                            name = pedalBoard.name,
                            columns = pedalBoard.columns,
                            rows = pedalBoard.rows,
                            slots = pedalBoard.slots
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "페달보드를 찾을 수 없습니다") }
                }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun updateName(name: String) {
        _state.update { it.copy(name = name, nameError = null) }
    }

    private fun updateLayout(columns: Int, rows: Int) {
        val newColumns = columns.coerceIn(SavedPedalBoard.MIN_COLUMNS, SavedPedalBoard.MAX_COLUMNS)
        val newRows = rows.coerceIn(SavedPedalBoard.MIN_ROWS, SavedPedalBoard.MAX_ROWS)
        val newTotalSlots = newColumns * newRows
        val currentSlots = _state.value.slots

        // 새로운 슬롯 크기에 맞게 조정
        val newSlots = if (newTotalSlots >= currentSlots.size) {
            // 크기가 늘어나면 기존 슬롯 유지 + 빈 슬롯 추가
            currentSlots + List(newTotalSlots - currentSlots.size) { null }
        } else {
            // 크기가 줄어들면 앞에서부터 유지 (뒤의 페달들은 잘림)
            currentSlots.take(newTotalSlots)
        }

        _state.update {
            it.copy(
                columns = newColumns,
                rows = newRows,
                slots = newSlots
            )
        }
    }

    private fun addPedalToSlot(slotIndex: Int, pedal: Pedal) {
        if (slotIndex !in 0 until _state.value.totalSlots) return

        val newPedal = pedal.copy(
            id = UUID.randomUUID().toString(),
            order = slotIndex
        )
        val updatedSlots = _state.value.slots.toMutableList()
        updatedSlots[slotIndex] = newPedal
        _state.update { it.copy(slots = updatedSlots) }
    }

    private fun addCustomPedalToSlot(slotIndex: Int, name: String, knobNames: List<String>) {
        if (slotIndex !in 0 until _state.value.totalSlots) return

        val knobs = knobNames.map { knobName -> Knob(name = knobName, value = 5f) }
        val newPedal = Pedal(
            id = UUID.randomUUID().toString(),
            name = name,
            type = PedalType.CUSTOM,
            knobs = knobs,
            order = slotIndex
        )
        val updatedSlots = _state.value.slots.toMutableList()
        updatedSlots[slotIndex] = newPedal
        _state.update { it.copy(slots = updatedSlots) }
    }

    private fun removePedalFromSlot(slotIndex: Int) {
        if (slotIndex !in 0 until _state.value.totalSlots) return

        val updatedSlots = _state.value.slots.toMutableList()
        updatedSlots[slotIndex] = null
        _state.update { it.copy(slots = updatedSlots, editingSlotIndex = null) }
    }

    private fun swapSlots(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in 0 until _state.value.totalSlots) return
        if (toIndex !in 0 until _state.value.totalSlots) return
        if (fromIndex == toIndex) return

        val updatedSlots = _state.value.slots.toMutableList()
        val temp = updatedSlots[fromIndex]
        updatedSlots[fromIndex] = updatedSlots[toIndex]
        updatedSlots[toIndex] = temp

        // order 업데이트
        updatedSlots[fromIndex] = updatedSlots[fromIndex]?.copy(order = fromIndex)
        updatedSlots[toIndex] = updatedSlots[toIndex]?.copy(order = toIndex)

        _state.update { it.copy(slots = updatedSlots) }
    }

    private fun movePedalToSlot(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in 0 until _state.value.totalSlots) return
        if (toIndex !in 0 until _state.value.totalSlots) return
        if (fromIndex == toIndex) return

        val updatedSlots = _state.value.slots.toMutableList()
        val pedal = updatedSlots[fromIndex] ?: return

        // 대상 슬롯이 비어있을 때만 이동
        if (updatedSlots[toIndex] == null) {
            updatedSlots[fromIndex] = null
            updatedSlots[toIndex] = pedal.copy(order = toIndex)
            _state.update { it.copy(slots = updatedSlots) }
        } else {
            // 비어있지 않으면 스왑
            swapSlots(fromIndex, toIndex)
        }
    }

    private fun updatePedalKnob(slotIndex: Int, knobIndex: Int, value: Float) {
        if (slotIndex !in 0 until _state.value.totalSlots) return

        val pedal = _state.value.slots[slotIndex] ?: return
        val updatedKnobs = pedal.knobs.mapIndexed { index, knob ->
            if (index == knobIndex) knob.copy(value = value) else knob
        }
        val updatedPedal = pedal.copy(knobs = updatedKnobs)
        val updatedSlots = _state.value.slots.toMutableList()
        updatedSlots[slotIndex] = updatedPedal
        _state.update { it.copy(slots = updatedSlots) }
    }

    private fun togglePedalEnabled(slotIndex: Int) {
        if (slotIndex !in 0 until _state.value.totalSlots) return

        val pedal = _state.value.slots[slotIndex] ?: return
        val updatedPedal = pedal.copy(isEnabled = pedal.isEnabled.not())
        val updatedSlots = _state.value.slots.toMutableList()
        updatedSlots[slotIndex] = updatedPedal
        _state.update { it.copy(slots = updatedSlots) }
    }

    private fun openPedalEditor(slotIndex: Int) {
        if (slotIndex !in 0 until _state.value.totalSlots) return
        _state.update { it.copy(editingSlotIndex = slotIndex) }
    }

    private fun closePedalEditor() {
        _state.update { it.copy(editingSlotIndex = null) }
    }

    private fun savePedalBoard() {
        val currentState = _state.value

        // Validation
        if (currentState.name.isBlank()) {
            _state.update { it.copy(nameError = "페달보드 이름을 입력해주세요") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val now = System.currentTimeMillis()
            val pedalBoard = SavedPedalBoard(
                id = currentState.editingId ?: UUID.randomUUID().toString(),
                name = currentState.name,
                columns = currentState.columns,
                rows = currentState.rows,
                slots = currentState.slots,
                createdAt = now,
                updatedAt = now
            )
            runCatching {
                savePedalBoardUseCase(pedalBoard)
            }.onSuccess {
                _state.update { it.copy(isSaving = false, navigateBack = true, showSaveSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    private fun deletePedalBoard() {
        val editingId = _state.value.editingId ?: return

        viewModelScope.launch {
            runCatching {
                deleteSavedPedalBoardUseCase(editingId)
            }.onSuccess {
                _state.update { it.copy(navigateBack = true) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun clearNavigation() {
        _state.update { it.copy(navigateBack = false, showSaveSuccess = false) }
    }
}
