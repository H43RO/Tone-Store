package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.domain.usecase.DeleteToneSettingUseCase
import com.haero.tonestore.domain.usecase.GetAllToneSettingsUseCase
import com.haero.tonestore.domain.usecase.ToggleFavoriteUseCase
import com.haero.tonestore.presentation.ui.home.HomeIntent
import com.haero.tonestore.presentation.ui.home.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllToneSettingsUseCase: GetAllToneSettingsUseCase,
    private val deleteToneSettingUseCase: DeleteToneSettingUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        handleIntent(HomeIntent.LoadToneSettings)
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadToneSettings -> loadToneSettings()
            is HomeIntent.SelectToneSetting -> navigateToDetail(intent.id)
            is HomeIntent.DeleteToneSetting -> deleteToneSetting(intent.id)
            is HomeIntent.NavigateToCreate -> navigateToCreate()
            is HomeIntent.NavigationHandled -> clearNavigation()
            is HomeIntent.SetSearchActive -> setSearchActive(intent.isActive)
            is HomeIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
            is HomeIntent.ToggleFavorite -> toggleFavorite(intent.id)
        }
    }

    private fun loadToneSettings() {
        viewModelScope.launch {
            getAllToneSettingsUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { settings ->
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            toneSettings = settings,
                            filteredToneSettings = filterToneSettings(settings, state.searchQuery),
                            error = null
                        )
                    }
                }
        }
    }

    private fun setSearchActive(isActive: Boolean) {
        _state.update { state ->
            if (isActive.not()) {
                state.copy(
                    isSearchActive = false,
                    searchQuery = "",
                    filteredToneSettings = state.toneSettings
                )
            } else {
                state.copy(isSearchActive = true)
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        _state.update { state ->
            state.copy(
                searchQuery = query,
                filteredToneSettings = filterToneSettings(state.toneSettings, query)
            )
        }
    }

    private fun filterToneSettings(settings: List<ToneSetting>, query: String): List<ToneSetting> {
        if (query.isBlank()) return settings
        return settings.filter { it.songName.contains(query, ignoreCase = true) }
    }

    private fun navigateToDetail(id: String) {
        _state.update { it.copy(navigateToDetail = id) }
    }

    private fun navigateToCreate() {
        _state.update { it.copy(navigateToCreate = true) }
    }

    private fun deleteToneSetting(id: String) {
        viewModelScope.launch {
            runCatching {
                deleteToneSettingUseCase(id)
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun toggleFavorite(id: String) {
        viewModelScope.launch {
            runCatching {
                toggleFavoriteUseCase(id)
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun clearNavigation() {
        _state.update { it.copy(navigateToDetail = null, navigateToCreate = false) }
    }
}
