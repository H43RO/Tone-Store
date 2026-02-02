package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.usecase.GetUserBookmarksUseCase
import com.haero.tonestore.domain.usecase.GetUserLikesUseCase
import com.haero.tonestore.domain.usecase.GetUserPresetsUseCase
import com.haero.tonestore.domain.usecase.GetUserProfileUseCase
import com.haero.tonestore.domain.usecase.ToggleBookmarkUseCase
import com.haero.tonestore.domain.usecase.ToggleLikeUseCase
import com.haero.tonestore.presentation.ui.profile.UserProfileIntent
import com.haero.tonestore.presentation.ui.profile.UserProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserPresetsUseCase: GetUserPresetsUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    private val getUserLikesUseCase: GetUserLikesUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val getUserBookmarksUseCase: GetUserBookmarksUseCase,
    private val authRepository: com.haero.tonestore.domain.repository.AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    fun handleIntent(intent: UserProfileIntent) {
        when (intent) {
            is UserProfileIntent.LoadProfile -> loadProfile(intent.userId)
            is UserProfileIntent.SelectPreset -> selectPreset(intent.presetId)
            is UserProfileIntent.ToggleLike -> toggleLike(intent.presetId)
            is UserProfileIntent.ToggleBookmark -> toggleBookmark(intent.presetId)
            is UserProfileIntent.NavigationHandled -> clearNavigation()
        }
    }

    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val currentUserId = authRepository.currentUserId

            // 프로필 정보 로드
            launch {
                getUserProfileUseCase(userId).onSuccess { profile ->
                    _state.update { it.copy(profile = profile) }
                }.onFailure { e ->
                    _state.update { it.copy(error = e.message) }
                }
            }

            // 프리셋 목록 로드
            launch {
                getUserPresetsUseCase(userId).onSuccess { presets ->
                    _state.update { it.copy(presets = presets) }
                }
            }

            if (currentUserId != null) {
                // 내 좋아요 목록 로드
                launch {
                    getUserLikesUseCase(currentUserId).onSuccess { likedIds ->
                        _state.update { it.copy(likedPresetIds = likedIds) }
                    }
                }

                // 내 북마크 목록 로드
                launch {
                    getUserBookmarksUseCase(currentUserId).onSuccess { bookmarkedIds ->
                        _state.update { it.copy(bookmarkedPresetIds = bookmarkedIds) }
                    }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun toggleLike(presetId: String) {
        viewModelScope.launch {
            val currentUserId = authRepository.currentUserId ?: return@launch
            val currentLikes = _state.value.likedPresetIds
            val isLiked = currentLikes.contains(presetId)
            val newLikes = if (isLiked) currentLikes - presetId else currentLikes + presetId

            // Optimistic update
            _state.update {
                it.copy(
                    likedPresetIds = newLikes,
                    presets = it.presets.map { preset ->
                        if (preset.id == presetId) {
                            preset.copy(likes = if (isLiked) preset.likes - 1 else preset.likes + 1)
                        } else {
                            preset
                        }
                    }
                )
            }

            toggleLikeUseCase(currentUserId, presetId).onFailure {
                // Revert on failure
                _state.update { it.copy(likedPresetIds = currentLikes) }
            }
        }
    }

    private fun toggleBookmark(presetId: String) {
        viewModelScope.launch {
            val currentUserId = authRepository.currentUserId ?: return@launch
            val currentBookmarks = _state.value.bookmarkedPresetIds
            val isBookmarked = currentBookmarks.contains(presetId)
            val newBookmarks = if (isBookmarked) currentBookmarks - presetId else currentBookmarks + presetId

            // Optimistic update
            _state.update { it.copy(bookmarkedPresetIds = newBookmarks) }

            toggleBookmarkUseCase(currentUserId, presetId).onFailure {
                // Revert on failure
                _state.update { it.copy(bookmarkedPresetIds = currentBookmarks) }
            }
        }
    }

    private fun selectPreset(presetId: String) {
        _state.update { it.copy(navigateToDetail = presetId) }
    }

    private fun clearNavigation() {
        _state.update { it.copy(navigateToDetail = null, error = null) }
    }
}
