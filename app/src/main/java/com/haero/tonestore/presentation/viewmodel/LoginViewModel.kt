package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.haero.tonestore.domain.repository.AuthRepository
import com.haero.tonestore.presentation.ui.auth.LoginIntent
import com.haero.tonestore.presentation.ui.auth.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _state.update {
                    it.copy(
                        currentUser = user,
                        isLoggedIn = user != null
                    )
                }
            }
        }
    }

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.SignInWithGoogle -> signInWithGoogle(intent.credential)
            is LoginIntent.SignInAnonymously -> signInAnonymously()
            is LoginIntent.SignOut -> signOut()
            is LoginIntent.ClearError -> clearError()
        }
    }

    private fun signInWithGoogle(credential: AuthCredential) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = authRepository.signInWithGoogle(credential)
            result.onSuccess { user ->
                _state.update { it.copy(isLoading = false, currentUser = user, isLoggedIn = true) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = "로그인 실패: ${e.message}") }
            }
        }
    }

    private fun signInAnonymously() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = authRepository.signInAnonymously()
            result.onSuccess { user ->
                _state.update { it.copy(isLoading = false, currentUser = user, isLoggedIn = true) }
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = "로그인 실패: ${e.message}") }
            }
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _state.update { it.copy(currentUser = null, isLoggedIn = false) }
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
