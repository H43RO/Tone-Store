package com.haero.tonestore.presentation.ui.profile

sealed class UserProfileIntent {
    data class LoadProfile(val userId: String) : UserProfileIntent()
    data class SelectPreset(val presetId: String) : UserProfileIntent()
    data class ToggleLike(val presetId: String) : UserProfileIntent()
    data class ToggleBookmark(val presetId: String) : UserProfileIntent()
    data object NavigationHandled : UserProfileIntent()
}
