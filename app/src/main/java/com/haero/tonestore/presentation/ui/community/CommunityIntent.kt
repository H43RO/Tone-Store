package com.haero.tonestore.presentation.ui.community

/**
 * 커뮤니티 화면 Intent
 */
sealed class CommunityIntent {
    data object LoadPresets : CommunityIntent()
    data object LoadPopularPresets : CommunityIntent()
    data class SearchByTags(val tags: List<String>) : CommunityIntent()
    data class SelectPreset(val presetId: String) : CommunityIntent()
    data class ToggleLike(val presetId: String) : CommunityIntent()
    data class SetTab(val tab: CommunityTab) : CommunityIntent()
    data object NavigationHandled : CommunityIntent()
    data object RefreshPresets : CommunityIntent()
}

/**
 * 커뮤니티 탭
 */
enum class CommunityTab {
    LATEST,
    POPULAR
}
