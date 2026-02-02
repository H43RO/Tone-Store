package com.haero.tonestore.presentation.ui.profile

import com.haero.tonestore.domain.model.SharedToneSetting
import com.haero.tonestore.domain.model.UserProfile

data class UserProfileState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val presets: List<SharedToneSetting> = emptyList(),
    val likedPresetIds: Set<String> = emptySet(),
    val bookmarkedPresetIds: Set<String> = emptySet(),
    val error: String? = null,
    val navigateToDetail: String? = null
)
