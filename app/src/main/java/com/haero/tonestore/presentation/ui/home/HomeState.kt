package com.haero.tonestore.presentation.ui.home

import com.haero.tonestore.domain.model.ToneSetting

/**
 * Home 화면의 상태
 */
data class HomeState(
    val isLoading: Boolean = true,
    val toneSettings: List<ToneSetting> = emptyList(),
    val filteredToneSettings: List<ToneSetting> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val error: String? = null,
    val navigateToDetail: String? = null,
    val navigateToCreate: Boolean = false
)
