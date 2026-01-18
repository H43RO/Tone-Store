package com.haero.tonestore.presentation.ui.home

sealed interface HomeIntent {
    data object LoadToneSettings : HomeIntent

    data class SelectToneSetting(val id: String) : HomeIntent

    data class DeleteToneSetting(val id: String) : HomeIntent

    data object NavigateToCreate : HomeIntent

    data object NavigationHandled : HomeIntent

    data class SetSearchActive(val isActive: Boolean) : HomeIntent

    data class UpdateSearchQuery(val query: String) : HomeIntent

    data class ToggleFavorite(val id: String) : HomeIntent
}
