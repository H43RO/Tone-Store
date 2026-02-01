package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.model.Knob
import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PedalType
import com.haero.tonestore.domain.model.SavedCustomPedal
import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.domain.usecase.DeleteSavedPedalBoardUseCase
import com.haero.tonestore.domain.usecase.GetAllSavedPedalBoardsUseCase
import com.haero.tonestore.domain.usecase.GetPresetPedalsUseCase
import com.haero.tonestore.domain.usecase.GetSavedPedalBoardByIdUseCase
import com.haero.tonestore.domain.usecase.SaveCustomPedalUseCase
import com.haero.tonestore.domain.usecase.SavePedalBoardUseCase
import com.haero.tonestore.presentation.ui.pedalboard.PedalBoardIntent
import com.haero.tonestore.presentation.ui.pedalboard.PedalBoardState
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PedalBoardViewModel(
    private val getSavedPedalBoardByIdUseCase: GetSavedPedalBoardByIdUseCase,
    private val savePedalBoardUseCase: SavePedalBoardUseCase,
    private val deleteSavedPedalBoardUseCase: DeleteSavedPedalBoardUseCase,
    private val getPresetPedalsUseCase: GetPresetPedalsUseCase,
    private val getAllSavedPedalBoardsUseCase: GetAllSavedPedalBoardsUseCase,
    private val saveCustomPedalUseCase: SaveCustomPedalUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PedalBoardState())
    val state: StateFlow<PedalBoardState> = _state.asStateFlow()

    private var allPedalBoards: List<SavedPedalBoard> = emptyList()

    init {
        loadPresetPedals()
        loadAllPedalBoards()
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
            is PedalBoardIntent.UpdatePedalColor -> updatePedalColor(intent.slotIndex, intent.color)
            is PedalBoardIntent.UpdatePedalKnobs -> updatePedalKnobs(intent.slotIndex, intent.knobs)
            is PedalBoardIntent.UpdatePedalName -> updatePedalName(intent.slotIndex, intent.name)
            is PedalBoardIntent.UpdateKnobName -> updateKnobName(intent.slotIndex, intent.knobIndex, intent.name)
            is PedalBoardIntent.SelectExpressionPedal -> selectExpressionPedal(intent.pedal)
            is PedalBoardIntent.RemoveExpressionPedal -> removeExpressionPedal()
            is PedalBoardIntent.SavePedalBoard -> savePedalBoard()
            is PedalBoardIntent.DeletePedalBoard -> deletePedalBoard()
            is PedalBoardIntent.NavigationHandled -> clearNavigation()
            is PedalBoardIntent.ClearError -> clearError()
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
                            slots = pedalBoard.slots,
                            expressionPedal = pedalBoard.expressionPedal
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

        if (newTotalSlots < currentSlots.size) {
            val pedalsToLose = currentSlots.drop(newTotalSlots).filterNotNull()
            if (pedalsToLose.isNotEmpty()) {
                _state.update {
                    it.copy(
                        error = "레이아웃을 축소하려면 먼저 ${pedalsToLose.size}개의 페달을 제거하세요"
                    )
                }
                return
            }
        }

        val newSlots = if (newTotalSlots >= currentSlots.size) {
            currentSlots + List(newTotalSlots - currentSlots.size) { null }
        } else {
            currentSlots.take(newTotalSlots)
        }

        _state.update {
            it.copy(
                columns = newColumns,
                rows = newRows,
                slots = newSlots,
                error = null
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

        if (updatedSlots[toIndex] == null) {
            updatedSlots[fromIndex] = null
            updatedSlots[toIndex] = pedal.copy(order = toIndex)
            _state.update { it.copy(slots = updatedSlots) }
        } else {
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

    private fun updatePedalColor(slotIndex: Int, color: Long?) {
        if (slotIndex !in 0 until _state.value.totalSlots) return

        val pedal = _state.value.slots[slotIndex] ?: return
        val updatedPedal = pedal.copy(color = color)
        val updatedSlots = _state.value.slots.toMutableList()
        updatedSlots[slotIndex] = updatedPedal
        _state.update { it.copy(slots = updatedSlots) }
    }

    private fun updatePedalKnobs(slotIndex: Int, knobs: List<Knob>) {
        if (slotIndex !in 0 until _state.value.totalSlots) return

        val pedal = _state.value.slots[slotIndex] ?: return
        val updatedPedal = pedal.copy(knobs = knobs)
        val updatedSlots = _state.value.slots.toMutableList()
        updatedSlots[slotIndex] = updatedPedal
        _state.update { it.copy(slots = updatedSlots) }
    }

    private fun updatePedalName(slotIndex: Int, name: String) {
        if (slotIndex !in 0 until _state.value.totalSlots) return

        val pedal = _state.value.slots[slotIndex] ?: return
        val updatedPedal = pedal.copy(name = name)
        val updatedSlots = _state.value.slots.toMutableList()
        updatedSlots[slotIndex] = updatedPedal
        _state.update { it.copy(slots = updatedSlots) }
    }

    private fun updateKnobName(slotIndex: Int, knobIndex: Int, name: String) {
        if (slotIndex !in 0 until _state.value.totalSlots) return

        val pedal = _state.value.slots[slotIndex] ?: return
        if (knobIndex !in pedal.knobs.indices) return

        val updatedKnobs = pedal.knobs.mapIndexed { index, knob ->
            if (index == knobIndex) knob.copy(name = name) else knob
        }
        val updatedPedal = pedal.copy(knobs = updatedKnobs)
        val updatedSlots = _state.value.slots.toMutableList()
        updatedSlots[slotIndex] = updatedPedal
        _state.update { it.copy(slots = updatedSlots) }
    }

    private fun selectExpressionPedal(pedal: Pedal) {
        _state.update { it.copy(expressionPedal = pedal) }
    }

    private fun removeExpressionPedal() {
        _state.update { it.copy(expressionPedal = null) }
    }

    private fun savePedalBoard() {
        val currentState = _state.value

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            // 제목이 비어있으면 자동 생성
            val finalName = if (currentState.name.isBlank()) {
                generateDefaultPedalBoardName()
            } else {
                currentState.name
            }

            val now = System.currentTimeMillis()
            val pedalBoard = SavedPedalBoard(
                id = currentState.editingId ?: UUID.randomUUID().toString(),
                name = finalName,
                columns = currentState.columns,
                rows = currentState.rows,
                slots = currentState.slots,
                expressionPedal = currentState.expressionPedal,
                createdAt = now,
                updatedAt = now
            )
            runCatching {
                // 1. 페달보드 저장
                savePedalBoardUseCase(pedalBoard)

                // 2. 커스텀 페달 자동 저장
                saveCustomPedalsFromBoard(currentState.slots)
            }.onSuccess {
                _state.update { it.copy(isSaving = false, navigateBack = true, showSaveSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(isSaving = false, error = e.message) }
            }
        }
    }

    private suspend fun saveCustomPedalsFromBoard(slots: List<Pedal?>) {
        val customPedals = slots.filterNotNull().filter { it.type == PedalType.CUSTOM }
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

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun loadAllPedalBoards() {
        viewModelScope.launch {
            getAllSavedPedalBoardsUseCase().collect { pedalBoards ->
                allPedalBoards = pedalBoards
            }
        }
    }

    private suspend fun generateDefaultPedalBoardName(): String {
        val baseName = "나의 페달보드"
        val existingNames = allPedalBoards.map { it.name }.toSet()

        // "나의 페달보드" 시작하는 기존 번호들 추출
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
