package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.usecase.DeleteToneSettingUseCase
import com.haero.tonestore.domain.usecase.GetToneSettingByIdUseCase
import com.haero.tonestore.domain.usecase.SaveToneSettingUseCase
import com.haero.tonestore.presentation.ui.detail.DetailIntent
import com.haero.tonestore.presentation.ui.detail.DetailState
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val getToneSettingByIdUseCase: GetToneSettingByIdUseCase,
    private val deleteToneSettingUseCase: DeleteToneSettingUseCase,
    private val saveToneSettingUseCase: SaveToneSettingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private var currentId: String? = null

    fun handleIntent(intent: DetailIntent) {
        when (intent) {
            is DetailIntent.LoadToneSetting -> loadToneSetting(intent.id)
            is DetailIntent.NavigateToEdit -> navigateToEdit()
            is DetailIntent.DeleteToneSetting -> deleteToneSetting()
            is DetailIntent.DuplicateToneSetting -> duplicateToneSetting()
            is DetailIntent.NavigationHandled -> clearNavigation()
            is DetailIntent.ClearDuplicateSuccess -> clearDuplicateSuccess()
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

    private fun duplicateToneSetting() {
        val original = _state.value.toneSetting ?: return
        viewModelScope.launch {
            runCatching {
                val duplicated = original.copy(
                    id = UUID.randomUUID().toString(),
                    songName = original.songName + " (Copy)",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                saveToneSettingUseCase(duplicated)
            }.onSuccess {
                _state.update { it.copy(showDuplicateSuccess = true) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun clearNavigation() {
        _state.update { it.copy(navigateToEdit = false, navigateBack = false) }
    }

    private fun clearDuplicateSuccess() {
        _state.update { it.copy(showDuplicateSuccess = false) }
    }
}
