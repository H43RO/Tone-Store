package com.haero.tonestore.presentation.ui.auth

import com.haero.tonestore.domain.model.UserProfile

/**
 * 로그인 화면 UI State
 */
data class LoginState(
    val isLoading: Boolean = false,
    val currentUser: UserProfile? = null,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)
