package com.haero.tonestore.presentation.ui.preset_detail

import com.haero.tonestore.domain.model.Comment
import com.haero.tonestore.domain.model.SharedToneSetting

/**
 * 공유 프리셋 상세 화면 UI State
 */
data class PresetDetailState(
    val isLoading: Boolean = false,
    val preset: SharedToneSetting? = null,
    val comments: List<Comment> = emptyList(),
    val isLiked: Boolean = false,
    val commentText: String = "",
    val isSendingComment: Boolean = false,
    val isDownloading: Boolean = false,
    val downloadSuccess: Boolean = false,
    val error: String? = null,
    val editingCommentId: String? = null,
    val navigateBack: Boolean = false
)
