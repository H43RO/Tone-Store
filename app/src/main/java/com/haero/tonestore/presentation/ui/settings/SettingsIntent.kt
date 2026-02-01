package com.haero.tonestore.presentation.ui.settings

/**
 * 설정 화면 Intent
 */
sealed class SettingsIntent {
    data class UpdateNickname(val nickname: String) : SettingsIntent()
    data object SaveProfile : SettingsIntent()
    data object SignOut : SettingsIntent()
    data object NavigateToLogin : SettingsIntent()
    data object ClearError : SettingsIntent()
    data object NavigationHandled : SettingsIntent()
}
