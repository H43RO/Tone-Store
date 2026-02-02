package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.repository.LikeRepository

/**
 * 좋아요 토글 UseCase
 */
class ToggleLikeUseCase(
    private val likeRepository: LikeRepository
) {
    suspend operator fun invoke(userId: String, presetId: String): Result<Boolean> {
        return likeRepository.toggleLike(userId, presetId)
    }
}
