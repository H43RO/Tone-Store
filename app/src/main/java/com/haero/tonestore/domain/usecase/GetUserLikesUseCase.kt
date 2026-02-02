package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.repository.LikeRepository

/**
 * 사

용자 좋아요 목록 조회 UseCase
 */
class GetUserLikesUseCase(
    private val likeRepository: LikeRepository
) {
    suspend operator fun invoke(userId: String): Result<Set<String>> {
        return likeRepository.getUserLikes(userId)
    }
}
