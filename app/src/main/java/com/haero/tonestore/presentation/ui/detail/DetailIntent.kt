package com.haero.tonestore.presentation.ui.detail

sealed interface DetailIntent {
    data class LoadToneSetting(val id: String) : DetailIntent

    data object NavigateToEdit : DetailIntent

    data object DeleteToneSetting : DetailIntent

    data object NavigationHandled : DetailIntent
}
