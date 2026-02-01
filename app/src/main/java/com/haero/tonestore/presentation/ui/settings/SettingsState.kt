package com.haero.tonestore.presentation.ui.settings

import com.haero.tonestore.domain.model.UserProfile

/**
 * 설정 화면 상태
 */
data class SettingsState(
    val isLoading: Boolean = false,
    val currentUser: UserProfile? = null,
    val isLoggedIn: Boolean = false,
    val nickname: String = "",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val navigateToLogin: Boolean = false
)
