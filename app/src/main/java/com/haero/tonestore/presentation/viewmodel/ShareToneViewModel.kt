package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.model.GenreTag
import com.haero.tonestore.domain.model.SharedToneSetting
import com.haero.tonestore.domain.repository.AuthRepository
import com.haero.tonestore.domain.repository.SharedToneSettingRepository
import com.haero.tonestore.domain.repository.ToneSettingRepository
import com.haero.tonestore.presentation.ui.share.ShareToneIntent
import com.haero.tonestore.presentation.ui.share.ShareToneState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShareToneViewModel(
    private val toneSettingRepository: ToneSettingRepository,
    private val sharedToneSettingRepository: SharedToneSettingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ShareToneState())
    val state: StateFlow<ShareToneState> = _state.asStateFlow()

    fun handleIntent(intent: ShareToneIntent) {
        when (intent) {
            is ShareToneIntent.LoadToneSetting -> loadToneSetting(intent.toneSettingId)
            is ShareToneIntent.UpdateTitle -> updateTitle(intent.title)
            is ShareToneIntent.UpdateDescription -> updateDescription(intent.description)
            is ShareToneIntent.ToggleTag -> toggleTag(intent.tag)
            is ShareToneIntent.Share -> share()
            is ShareToneIntent.ClearError -> clearError()
        }
    }

    private fun loadToneSetting(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val toneSetting = toneSettingRepository.getToneSettingById(id)
                if (toneSetting != null) {
                    _state.update {
                        it.copy(
                            toneSetting = toneSetting,
                            title = toneSetting.songName,
                            selectedTags = toneSetting.tags,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(error = "톤 세팅을 찾을 수 없습니다", isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun updateTitle(title: String) {
        _state.update { it.copy(title = title) }
    }

    private fun updateDescription(description: String) {
        _state.update { it.copy(description = description) }
    }

    private fun toggleTag(tag: GenreTag) {
        _state.update { currentState ->
            val newTags = if (currentState.selectedTags.contains(tag)) {
                currentState.selectedTags - tag
            } else {
                currentState.selectedTags + tag
            }
            currentState.copy(selectedTags = newTags)
        }
    }

    private fun share() {
        val currentState = _state.value
        val toneSetting = currentState.toneSetting ?: return

        if (currentState.title.isBlank()) {
            _state.update { it.copy(error = "제목을 입력해주세요") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val user = authRepository.currentUser.first()
                if (user == null) {
                    _state.update { it.copy(error = "로그인이 필요합니다", isLoading = false) }
                    return@launch
                }

                val sharedToneSetting = SharedToneSetting(
                    authorId = user.uid,
                    authorName = user.displayName,
                    authorPhotoUrl = user.photoUrl,
                    title = currentState.title,
                    description = currentState.description,
                    toneSetting = toneSetting,
                    tags = currentState.selectedTags
                )

                val result = sharedToneSettingRepository.uploadToneSetting(sharedToneSetting)
                result.onSuccess { presetId ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            sharedPresetId = presetId
                        )
                    }
                }.onFailure { e ->
                    _state.update { it.copy(error = "공유 실패: ${e.message}", isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "공유 실패: ${e.message}", isLoading = false) }
            }
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
