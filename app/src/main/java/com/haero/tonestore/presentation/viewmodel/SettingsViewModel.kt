package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.repository.AuthRepository
import com.haero.tonestore.domain.repository.SharedToneSettingRepository
import com.haero.tonestore.presentation.ui.settings.SettingsIntent
import com.haero.tonestore.presentation.ui.settings.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val sharedToneSettingRepository: SharedToneSettingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        observeAuthState()
        loadProfile()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _state.update {
                    it.copy(
                        currentUser = user,
                        isLoggedIn = user != null,
                        nickname = user?.nickname ?: ""
                    )
                }
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = authRepository.getProfile()
            result.onSuccess { profile ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        currentUser = profile,
                        nickname = profile?.nickname ?: ""
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.UpdateNickname -> updateNickname(intent.nickname)
            is SettingsIntent.SaveProfile -> saveProfile()
            is SettingsIntent.SignOut -> signOut()
            is SettingsIntent.NavigateToLogin -> navigateToLogin()
            is SettingsIntent.ClearError -> clearError()
            is SettingsIntent.NavigationHandled -> navigationHandled()
        }
    }

    private fun updateNickname(nickname: String) {
        _state.update { it.copy(nickname = nickname) }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val userId = _state.value.currentUser?.uid
            val newNickname = _state.value.nickname

            // 1. 프로필 업데이트
            val result = authRepository.updateProfile(
                nickname = newNickname,
                photoUrl = null
            )

            result.onSuccess { profile ->
                // 2. 기존 프리셋/댓글의 authorName도 업데이트
                if (userId != null && newNickname.isNotBlank()) {
                    sharedToneSettingRepository.updateAuthorName(userId, newNickname)
                }

                _state.update {
                    it.copy(
                        isSaving = false,
                        currentUser = profile,
                        saveSuccess = true
                    )
                }
            }.onFailure { e ->
                _state.update {
                    it.copy(isSaving = false, error = "저장 실패: ${e.message}")
                }
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _state.update {
                it.copy(
                    currentUser = null,
                    isLoggedIn = false,
                    nickname = "",
                    navigateToLogin = true
                )
            }
        }
    }

    private fun navigateToLogin() {
        _state.update { it.copy(navigateToLogin = true) }
    }

    private fun clearError() {
        _state.update { it.copy(error = null, saveSuccess = false) }
    }

    private fun navigationHandled() {
        _state.update { it.copy(navigateToLogin = false) }
    }
}
