package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.usecase.DeleteToneSettingUseCase
import com.haero.tonestore.domain.usecase.GetAllToneSettingsUseCase
import com.haero.tonestore.presentation.ui.home.HomeIntent
import com.haero.tonestore.presentation.ui.home.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Home 화면의 ViewModel (MVI 패턴)
 */
class HomeViewModel(
    private val getAllToneSettingsUseCase: GetAllToneSettingsUseCase,
    private val deleteToneSettingUseCase: DeleteToneSettingUseCase
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
        }
    }
    
    private fun loadToneSettings() {
        viewModelScope.launch {
            getAllToneSettingsUseCase()
                .catch { e ->
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { settings ->
                    _state.update { 
                        it.copy(isLoading = false, toneSettings = settings, error = null) 
                    }
                }
        }
    }
    
    private fun navigateToDetail(id: String) {
        _state.update { it.copy(navigateToDetail = id) }
    }
    
    private fun navigateToCreate() {
        _state.update { it.copy(navigateToCreate = true) }
    }
    
    private fun deleteToneSetting(id: String) {
        viewModelScope.launch {
            try {
                deleteToneSettingUseCase(id)
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
    
    private fun clearNavigation() {
        _state.update { it.copy(navigateToDetail = null, navigateToCreate = false) }
    }
}
