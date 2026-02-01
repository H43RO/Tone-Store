package com.haero.tonestore.presentation.ui.share

import com.haero.tonestore.domain.model.GenreTag
import com.haero.tonestore.domain.model.ToneSetting

/**
 * 톤 공유 화면 UI State
 */
data class ShareToneState(
    val toneSetting: ToneSetting? = null,
    val title: String = "",
    val description: String = "",
    val selectedTags: List<GenreTag> = emptyList(),
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val sharedPresetId: String? = null,
    val error: String? = null
)
