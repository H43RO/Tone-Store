package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.repository.BookmarkRepository

/**
 * 사용자 북마크 목록 조회 UseCase
 */
class GetUserBookmarksUseCase(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(userId: String): Result<Set<String>> {
        return bookmarkRepository.getUserBookmarks(userId)
    }
}
