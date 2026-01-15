package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.domain.usecase.DeleteSavedPedalBoardUseCase
import com.haero.tonestore.domain.usecase.GetAllSavedPedalBoardsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 페달보드 목록 화면 State
 */
data class PedalBoardListState(
    val pedalBoards: List<SavedPedalBoard> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * 페달보드 목록 ViewModel
 */
class PedalBoardListViewModel(
    private val getAllSavedPedalBoardsUseCase: GetAllSavedPedalBoardsUseCase,
    private val deleteSavedPedalBoardUseCase: DeleteSavedPedalBoardUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PedalBoardListState())
    val state: StateFlow<PedalBoardListState> = _state.asStateFlow()

    init {
        loadPedalBoards()
    }

    private fun loadPedalBoards() {
        viewModelScope.launch {
            getAllSavedPedalBoardsUseCase().collect { pedalBoards ->
                _state.update {
                    it.copy(
                        pedalBoards = pedalBoards,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun delete(pedalBoard: SavedPedalBoard) {
        viewModelScope.launch {
            deleteSavedPedalBoardUseCase(pedalBoard.id)
        }
    }
}
