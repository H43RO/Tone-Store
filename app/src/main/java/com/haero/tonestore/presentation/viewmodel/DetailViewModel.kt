package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.usecase.DeleteToneSettingUseCase
import com.haero.tonestore.domain.usecase.GetToneSettingByIdUseCase
import com.haero.tonestore.presentation.ui.detail.DetailIntent
import com.haero.tonestore.presentation.ui.detail.DetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Detail 화면의 ViewModel (MVI 패턴)
 */
class DetailViewModel(
    private val getToneSettingByIdUseCase: GetToneSettingByIdUseCase,
    private val deleteToneSettingUseCase: DeleteToneSettingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private var currentId: String? = null

    fun handleIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadToneSetting -> loadToneSetting(intent.id)
            is DetailIntent.NavigateToEdit -> navigateToEdit()
            is DetailIntent.DeleteToneSetting -> deleteToneSetting()
            is DetailIntent.NavigationHandled -> clearNavigation()
        }
    }

    private fun loadToneSetting(id: String) {
        currentId = id
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                getToneSettingByIdUseCase(id)
            }.onSuccess { setting ->
                _state.update {
                    it.copy(isLoading = false, toneSetting = setting, error = null)
                }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun navigateToEdit() {
        _state.update { it.copy(navigateToEdit = true) }
    }

    private fun deleteToneSetting() {
        val id = currentId ?: return
        viewModelScope.launch {
            runCatching {
                deleteToneSettingUseCase(id)
            }.onSuccess {
                _state.update { it.copy(navigateBack = true) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun clearNavigation() {
        _state.update { it.copy(navigateToEdit = false, navigateBack = false) }
    }
}
