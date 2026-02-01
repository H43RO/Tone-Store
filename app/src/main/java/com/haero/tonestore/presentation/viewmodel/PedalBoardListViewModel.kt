package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.domain.repository.AuthRepository
import com.haero.tonestore.domain.usecase.DeleteSavedPedalBoardUseCase
import com.haero.tonestore.domain.usecase.GetAllSavedPedalBoardsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PedalBoardListState(
    val pedalBoards: List<SavedPedalBoard> = emptyList(),
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val navigateToLogin: Boolean = false,
    val error: String? = null
)

class PedalBoardListViewModel(
    private val getAllSavedPedalBoardsUseCase: GetAllSavedPedalBoardsUseCase,
    private val deleteSavedPedalBoardUseCase: DeleteSavedPedalBoardUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PedalBoardListState())
    val state: StateFlow<PedalBoardListState> = _state.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                val isLoggedIn = user != null
                _state.update { it.copy(isLoggedIn = isLoggedIn) }

                if (isLoggedIn) {
                    loadPedalBoards()
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            pedalBoards = emptyList()
                        )
                    }
                }
            }
        }
    }

    private fun loadPedalBoards() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllSavedPedalBoardsUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { pedalBoards ->
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

    fun navigateToLogin() {
        _state.update { it.copy(navigateToLogin = true) }
    }

    fun navigationHandled() {
        _state.update { it.copy(navigateToLogin = false) }
    }
}
