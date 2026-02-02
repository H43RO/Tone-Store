package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.repository.AuthRepository
import com.haero.tonestore.domain.repository.SharedToneSettingRepository
import com.haero.tonestore.domain.usecase.GetUserBookmarksUseCase
import com.haero.tonestore.domain.usecase.GetUserLikesUseCase
import com.haero.tonestore.domain.usecase.ToggleBookmarkUseCase
import com.haero.tonestore.domain.usecase.ToggleLikeUseCase
import com.haero.tonestore.presentation.ui.community.CommunityIntent
import com.haero.tonestore.presentation.ui.community.CommunityState
import com.haero.tonestore.presentation.ui.community.CommunityTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val sharedToneSettingRepository: SharedToneSettingRepository,
    private val authRepository: AuthRepository,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val getUserLikesUseCase: GetUserLikesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val getUserBookmarksUseCase: GetUserBookmarksUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CommunityState())
    val state: StateFlow<CommunityState> = _state.asStateFlow()

    init {
        loadPresets()
        loadPopularPresets()
        loadUserLikesAndBookmarks()
    }

    fun handleIntent(intent: CommunityIntent) {
        when (intent) {
            is CommunityIntent.LoadPresets -> loadPresets()
            is CommunityIntent.LoadPopularPresets -> loadPopularPresets()
            is CommunityIntent.SearchByTags -> searchByTags(intent.tags)
            is CommunityIntent.SelectPreset -> selectPreset(intent.presetId)
            is CommunityIntent.ToggleLike -> toggleLike(intent.presetId)
            is CommunityIntent.ToggleBookmark -> toggleBookmark(intent.presetId)
            is CommunityIntent.SetTab -> setTab(intent.tab)
            is CommunityIntent.NavigationHandled -> clearNavigation()
            is CommunityIntent.RefreshPresets -> refreshPresets()
        }
    }

    private fun loadPresets() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            sharedToneSettingRepository.getSharedToneSettings().collect { presets ->
                _state.update { it.copy(presets = presets, isLoading = false) }
            }
        }
    }

    private fun loadPopularPresets() {
        viewModelScope.launch {
            sharedToneSettingRepository.getPopularToneSettings(20).collect { presets ->
                _state.update { it.copy(popularPresets = presets) }
            }
        }
    }

    private fun loadUserLikesAndBookmarks() {
        viewModelScope.launch {
            val userId = authRepository.currentUserId ?: return@launch

            // 좋아요 목록 로드
            getUserLikesUseCase(userId).onSuccess { likedIds ->
                _state.update { it.copy(likedPresetIds = likedIds) }
            }

            // 북마크 목록 로드
            getUserBookmarksUseCase(userId).onSuccess { bookmarkedIds ->
                _state.update { it.copy(bookmarkedPresetIds = bookmarkedIds) }
            }
        }
    }

    private fun searchByTags(tags: List<String>) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            sharedToneSettingRepository.searchByTags(tags).collect { presets ->
                _state.update { it.copy(presets = presets, isLoading = false) }
            }
        }
    }

    private fun selectPreset(presetId: String) {
        _state.update { it.copy(navigateToDetail = presetId) }
    }

    private fun toggleLike(presetId: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUserId
            if (userId == null) {
                _state.update { it.copy(error = "로그인이 필요합니다") }
                return@launch
            }

            // Optimistic update
            val isCurrentlyLiked = _state.value.likedPresetIds.contains(presetId)
            _state.update { currentState ->
                val newLikedIds = if (isCurrentlyLiked) {
                    currentState.likedPresetIds - presetId
                } else {
                    currentState.likedPresetIds + presetId
                }
                currentState.copy(likedPresetIds = newLikedIds)
            }

            // Backend update
            toggleLikeUseCase(userId, presetId).onFailure { e ->
                // Revert on failure
                _state.update { currentState ->
                    val revertedIds = if (isCurrentlyLiked) {
                        currentState.likedPresetIds + presetId
                    } else {
                        currentState.likedPresetIds - presetId
                    }
                    currentState.copy(
                        likedPresetIds = revertedIds,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun toggleBookmark(presetId: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUserId
            if (userId == null) {
                _state.update { it.copy(error = "로그인이 필요합니다") }
                return@launch
            }

            // Optimistic update
            val isCurrentlyBookmarked = _state.value.bookmarkedPresetIds.contains(presetId)
            _state.update { currentState ->
                val newBookmarkedIds = if (isCurrentlyBookmarked) {
                    currentState.bookmarkedPresetIds - presetId
                } else {
                    currentState.bookmarkedPresetIds + presetId
                }
                currentState.copy(bookmarkedPresetIds = newBookmarkedIds)
            }

            // Backend update
            toggleBookmarkUseCase(userId, presetId).onFailure { e ->
                // Revert on failure
                _state.update { currentState ->
                    val revertedIds = if (isCurrentlyBookmarked) {
                        currentState.bookmarkedPresetIds + presetId
                    } else {
                        currentState.bookmarkedPresetIds - presetId
                    }
                    currentState.copy(
                        bookmarkedPresetIds = revertedIds,
                        error = e.message
                    )
                }
            }
        }
    }

    private fun setTab(tab: CommunityTab) {
        _state.update { it.copy(currentTab = tab) }
    }

    private fun clearNavigation() {
        _state.update { it.copy(navigateToDetail = null, error = null) }
    }

    private fun refreshPresets() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            // Flow가 자동으로 최신 데이터를 가져오므로 약간의 딜레이 후 리프레시 상태 해제
            kotlinx.coroutines.delay(500)
            _state.update { it.copy(isRefreshing = false) }
        }
    }
}
