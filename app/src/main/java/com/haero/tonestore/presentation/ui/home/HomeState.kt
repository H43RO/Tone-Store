package com.haero.tonestore.presentation.ui.home

import com.haero.tonestore.domain.model.ToneSetting

data class HomeState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val toneSettings: List<ToneSetting> = emptyList(),
    val filteredToneSettings: List<ToneSetting> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val error: String? = null,
    val navigateToDetail: String? = null,
    val navigateToCreate: Boolean = false,
    val navigateToLogin: Boolean = false,
    val scrollToTop: Boolean = false,
    val viewMode: ViewMode = ViewMode.LIST,
    val sortOption: SortOption = SortOption.FAVORITES_FIRST
)
