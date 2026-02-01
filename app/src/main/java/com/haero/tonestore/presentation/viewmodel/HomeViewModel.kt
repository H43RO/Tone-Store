package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.domain.repository.AuthRepository
import com.haero.tonestore.domain.usecase.DeleteToneSettingUseCase
import com.haero.tonestore.domain.usecase.GetAllToneSettingsUseCase
import com.haero.tonestore.domain.usecase.ToggleFavoriteUseCase
import com.haero.tonestore.presentation.ui.home.HomeIntent
import com.haero.tonestore.presentation.ui.home.HomeState
import com.haero.tonestore.presentation.ui.home.SortOption
import com.haero.tonestore.presentation.ui.home.ViewMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllToneSettingsUseCase: GetAllToneSettingsUseCase,
    private val deleteToneSettingUseCase: DeleteToneSettingUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                val isLoggedIn = user != null
                _state.update { it.copy(isLoggedIn = isLoggedIn) }

                if (isLoggedIn) {
                    loadToneSettings()
                } else {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            toneSettings = emptyList(),
                            filteredToneSettings = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadToneSettings -> loadToneSettings()
            is HomeIntent.SelectToneSetting -> navigateToDetail(intent.id)
            is HomeIntent.DeleteToneSetting -> deleteToneSetting(intent.id)
            is HomeIntent.NavigateToCreate -> navigateToCreate()
            is HomeIntent.NavigateToLogin -> navigateToLogin()
            is HomeIntent.NavigationHandled -> clearNavigation()
            is HomeIntent.SetSearchActive -> setSearchActive(intent.isActive)
            is HomeIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
            is HomeIntent.ToggleFavorite -> toggleFavorite(intent.id)
            is HomeIntent.ScrollToTopHandled -> clearScrollToTop()
            is HomeIntent.SetViewMode -> setViewMode(intent.viewMode)
            is HomeIntent.SetSortOption -> setSortOption(intent.sortOption)
        }
    }

    private fun loadToneSettings() {
        if (!_state.value.isLoggedIn) {
            _state.update { it.copy(isLoading = false) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAllToneSettingsUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { settings ->
                    _state.update { state ->
                        val filtered = filterToneSettings(settings, state.searchQuery)
                        val sorted = applySorting(filtered, state.sortOption)
                        state.copy(
                            isLoading = false,
                            toneSettings = settings,
                            filteredToneSettings = sorted,
                            error = null
                        )
                    }
                }
        }
    }

    private fun setSearchActive(isActive: Boolean) {
        _state.update { state ->
            if (isActive.not()) {
                val filtered = filterToneSettings(state.toneSettings, "")
                val sorted = applySorting(filtered, state.sortOption)
                state.copy(
                    isSearchActive = false,
                    searchQuery = "",
                    filteredToneSettings = sorted
                )
            } else {
                state.copy(isSearchActive = true)
            }
        }
    }

    private fun updateSearchQuery(query: String) {
        _state.update { state ->
            val filtered = filterToneSettings(state.toneSettings, query)
            val sorted = applySorting(filtered, state.sortOption)
            state.copy(
                searchQuery = query,
                filteredToneSettings = sorted
            )
        }
    }

    private fun filterToneSettings(settings: List<ToneSetting>, query: String): List<ToneSetting> {
        if (query.isBlank()) return settings
        return settings.filter { it.songName.contains(query, ignoreCase = true) }
    }

    private fun applySorting(settings: List<ToneSetting>, sortOption: SortOption): List<ToneSetting> {
        return when (sortOption) {
            SortOption.FAVORITES_FIRST -> {
                settings.sortedWith(
                    compareByDescending<ToneSetting> { it.isFavorite }.thenByDescending { it.updatedAt }
                )
            }
            SortOption.DATE_FIRST -> {
                settings.sortedByDescending { it.updatedAt }
            }
        }
    }

    private fun navigateToDetail(id: String) {
        _state.update { it.copy(navigateToDetail = id) }
    }

    private fun navigateToCreate() {
        if (!_state.value.isLoggedIn) {
            _state.update { it.copy(navigateToLogin = true) }
            return
        }
        _state.update { it.copy(navigateToCreate = true) }
    }

    private fun navigateToLogin() {
        _state.update { it.copy(navigateToLogin = true) }
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
                _state.update { it.copy(scrollToTop = true) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun clearNavigation() {
        _state.update {
            it.copy(
                navigateToDetail = null,
                navigateToCreate = false,
                navigateToLogin = false
            )
        }
    }

    private fun clearScrollToTop() {
        _state.update { it.copy(scrollToTop = false) }
    }

    private fun setViewMode(viewMode: ViewMode) {
        _state.update { it.copy(viewMode = viewMode) }
    }

    private fun setSortOption(sortOption: SortOption) {
        _state.update { state ->
            val filtered = filterToneSettings(state.toneSettings, state.searchQuery)
            val sorted = applySorting(filtered, sortOption)
            state.copy(
                sortOption = sortOption,
                filteredToneSettings = sorted
            )
        }
    }
}
