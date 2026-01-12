package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.repository.ToneSettingRepository

/**
 * 톤 세팅 즐겨찾기를 토글하는 UseCase
 */
class ToggleFavoriteUseCase(
    private val repository: ToneSettingRepository
) {
    suspend operator fun invoke(id: String) {
        repository.toggleFavorite(id)
    }
}
