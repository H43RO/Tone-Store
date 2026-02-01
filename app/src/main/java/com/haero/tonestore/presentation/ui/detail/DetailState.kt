package com.haero.tonestore.presentation.ui.detail

import com.haero.tonestore.domain.model.ToneSetting

data class DetailState(
    val isLoading: Boolean = true,
    val toneSetting: ToneSetting? = null,
    val error: String? = null,
    val navigateToEdit: Boolean = false,
    val navigateBack: Boolean = false,
    val showDuplicateSuccess: Boolean = false
)
