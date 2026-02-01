package com.haero.tonestore.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.haero.tonestore.domain.model.Comment
import com.haero.tonestore.domain.repository.AuthRepository
import com.haero.tonestore.domain.repository.CommentRepository
import com.haero.tonestore.domain.repository.SharedToneSettingRepository
import com.haero.tonestore.domain.repository.ToneSettingRepository
import com.haero.tonestore.presentation.ui.preset_detail.PresetDetailIntent
import com.haero.tonestore.presentation.ui.preset_detail.PresetDetailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PresetDetailViewModel(
    private val sharedToneSettingRepository: SharedToneSettingRepository,
    private val commentRepository: CommentRepository,
    private val authRepository: AuthRepository,
    private val toneSettingRepository: ToneSettingRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PresetDetailState())
    val state: StateFlow<PresetDetailState> = _state.asStateFlow()

    private var currentPresetId: String? = null

    fun handleIntent(intent: PresetDetailIntent) {
        when (intent) {
            is PresetDetailIntent.LoadPreset -> loadPreset(intent.presetId)
            is PresetDetailIntent.ToggleLike -> toggleLike()
            is PresetDetailIntent.DownloadPreset -> downloadPreset()
            is PresetDetailIntent.UpdateCommentText -> updateCommentText(intent.text)
            is PresetDetailIntent.SendComment -> sendComment()
            is PresetDetailIntent.EditComment -> startEditComment(intent.commentId)
            is PresetDetailIntent.DeleteComment -> deleteComment(intent.commentId)
            is PresetDetailIntent.CancelEditComment -> cancelEditComment()
            is PresetDetailIntent.NavigateBack -> navigateBack()
            is PresetDetailIntent.ClearError -> clearError()
            is PresetDetailIntent.ClearDownloadSuccess -> clearDownloadSuccess()
        }
    }

    private fun loadPreset(presetId: String) {
        currentPresetId = presetId
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // 프리셋 로드
            val preset = sharedToneSettingRepository.getSharedToneSetting(presetId)
            if (preset != null) {
                _state.update { it.copy(preset = preset, isLoading = false) }
            } else {
                _state.update { it.copy(error = "프리셋을 찾을 수 없습니다", isLoading = false) }
                return@launch
            }

            // 댓글 로드
            commentRepository.getComments(presetId).collect { comments ->
                _state.update { it.copy(comments = comments) }
            }
        }
    }

    private fun toggleLike() {
        val presetId = currentPresetId ?: return
        viewModelScope.launch {
            val userId = authRepository.currentUserId
            if (userId == null) {
                _state.update { it.copy(error = "로그인이 필요합니다") }
                return@launch
            }

            val result = sharedToneSettingRepository.toggleLike(presetId, userId)
            result.onSuccess { isLiked ->
                _state.update { currentState ->
                    val updatedPreset = currentState.preset?.let { preset ->
                        preset.copy(likes = if (isLiked) preset.likes + 1 else preset.likes - 1)
                    }
                    currentState.copy(preset = updatedPreset, isLiked = isLiked)
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun downloadPreset() {
        val preset = _state.value.preset ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDownloading = true) }

            try {
                // 로컬 DB에 톤 세팅 저장
                val newToneSetting = preset.toneSetting.copy(
                    id = java.util.UUID.randomUUID().toString(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                toneSettingRepository.saveToneSetting(newToneSetting)

                // 다운로드 수 증가
                sharedToneSettingRepository.incrementDownloads(preset.id)

                _state.update { currentState ->
                    val updatedPreset = currentState.preset?.copy(downloads = currentState.preset.downloads + 1)
                    currentState.copy(
                        preset = updatedPreset,
                        isDownloading = false,
                        downloadSuccess = true
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isDownloading = false, error = "다운로드 실패: ${e.message}") }
            }
        }
    }

    private fun updateCommentText(text: String) {
        _state.update { it.copy(commentText = text) }
    }

    private fun sendComment() {
        val presetId = currentPresetId ?: return
        val commentText = _state.value.commentText.trim()
        if (commentText.isBlank()) return

        viewModelScope.launch {
            val userId = authRepository.currentUserId
            if (userId == null) {
                _state.update { it.copy(error = "로그인이 필요합니다") }
                return@launch
            }

            _state.update { it.copy(isSendingComment = true) }

            val editingId = _state.value.editingCommentId
            if (editingId != null) {
                // 댓글 수정
                val existingComment = _state.value.comments.find { it.id == editingId }
                if (existingComment != null) {
                    val updatedComment = existingComment.copy(
                        content = commentText,
                        updatedAt = System.currentTimeMillis()
                    )
                    commentRepository.updateComment(updatedComment)
                }
            } else {
                // 새 댓글 작성
                authRepository.currentUser.collect { user ->
                    if (user != null) {
                        val comment = Comment(
                            presetId = presetId,
                            authorId = user.uid,
                            authorName = user.displayName,
                            authorPhotoUrl = user.photoUrl,
                            content = commentText
                        )
                        commentRepository.addComment(comment)
                        return@collect
                    }
                }
            }

            _state.update {
                it.copy(
                    commentText = "",
                    isSendingComment = false,
                    editingCommentId = null
                )
            }
        }
    }

    private fun startEditComment(commentId: String) {
        val comment = _state.value.comments.find { it.id == commentId }
        if (comment != null && comment.authorId == authRepository.currentUserId) {
            _state.update { it.copy(editingCommentId = commentId, commentText = comment.content) }
        }
    }

    private fun deleteComment(commentId: String) {
        val presetId = currentPresetId ?: return
        val comment = _state.value.comments.find { it.id == commentId }

        // 본인 댓글만 삭제 가능
        if (comment?.authorId != authRepository.currentUserId) {
            _state.update { it.copy(error = "본인 댓글만 삭제할 수 있습니다") }
            return
        }

        viewModelScope.launch {
            commentRepository.deleteComment(commentId, presetId)
        }
    }

    private fun cancelEditComment() {
        _state.update { it.copy(editingCommentId = null, commentText = "") }
    }

    private fun navigateBack() {
        _state.update { it.copy(navigateBack = true) }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }

    private fun clearDownloadSuccess() {
        _state.update { it.copy(downloadSuccess = false) }
    }
}
