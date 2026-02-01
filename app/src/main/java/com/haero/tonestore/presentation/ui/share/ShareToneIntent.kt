package com.haero.tonestore.presentation.ui.share

import com.haero.tonestore.domain.model.GenreTag

/**
 * 톤 공유 화면 Intent
 */
sealed class ShareToneIntent {
    data class LoadToneSetting(val toneSettingId: String) : ShareToneIntent()
    data class UpdateTitle(val title: String) : ShareToneIntent()
    data class UpdateDescription(val description: String) : ShareToneIntent()
    data class ToggleTag(val tag: GenreTag) : ShareToneIntent()
    data object Share : ShareToneIntent()
    data object ClearError : ShareToneIntent()
}
