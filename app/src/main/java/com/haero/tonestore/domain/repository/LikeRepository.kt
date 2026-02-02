package com.haero.tonestore.domain.repository

/**
 * 톤 세팅 좋아요 관리 Repository
 */
interface LikeRepository {
    /**
     * 좋아요 토글
     * @return true if liked, false if unliked
     */
    suspend fun toggleLike(userId: String, presetId: String): Result<Boolean>

    /**
     * 사용자가 좋아요한 프리셋 ID 목록 조회
     */
    suspend fun getUserLikes(userId: String): Result<Set<String>>

    /**
     * 특정 프리셋의 좋아요 수 조회
     */
    suspend fun getPresetLikeCount(presetId: String): Result<Int>
}
