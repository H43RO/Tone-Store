package com.haero.tonestore.presentation.ui.community

import com.haero.tonestore.domain.model.SharedToneSetting

/**
 * 커뮤니티 화면 UI State
 */
data class CommunityState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val presets: List<SharedToneSetting> = emptyList(),
    val popularPresets: List<SharedToneSetting> = emptyList(),
    val currentTab: CommunityTab = CommunityTab.LATEST,
    val likedPresetIds: Set<String> = emptySet(),
    val bookmarkedPresetIds: Set<String> = emptySet(),
    val navigateToDetail: String? = null,
    val error: String? = null
) {
    val displayedPresets: List<SharedToneSetting>
        get() = when (currentTab) {
            CommunityTab.LATEST -> presets
            CommunityTab.POPULAR -> popularPresets
        }
}
