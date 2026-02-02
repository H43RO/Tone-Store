package com.haero.tonestore.domain.repository

/**
 * 톤 세팅 북마크 관리 Repository
 */
interface BookmarkRepository {
    /**
     * 북마크 토글
     * @return true if bookmarked, false if unbookmarked
     */
    suspend fun toggleBookmark(userId: String, presetId: String): Result<Boolean>

    /**
     * 사용자가 북마크한 프리셋 ID 목록 조회
     */
    suspend fun getUserBookmarks(userId: String): Result<Set<String>>
}
