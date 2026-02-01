package com.haero.tonestore.presentation.ui.preset_detail

/**
 * 공유 프리셋 상세 화면 Intent
 */
sealed class PresetDetailIntent {
    data class LoadPreset(val presetId: String) : PresetDetailIntent()
    data object ToggleLike : PresetDetailIntent()
    data object DownloadPreset : PresetDetailIntent()
    data class UpdateCommentText(val text: String) : PresetDetailIntent()
    data object SendComment : PresetDetailIntent()
    data class EditComment(val commentId: String) : PresetDetailIntent()
    data class DeleteComment(val commentId: String) : PresetDetailIntent()
    data object CancelEditComment : PresetDetailIntent()
    data object NavigateBack : PresetDetailIntent()
    data object ClearError : PresetDetailIntent()
    data object ClearDownloadSuccess : PresetDetailIntent()
}
