package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.repository.BookmarkRepository

/**
 * 북마크 토글 UseCase
 */
class ToggleBookmarkUseCase(
    private val bookmarkRepository: BookmarkRepository
) {
    suspend operator fun invoke(userId: String, presetId: String): Result<Boolean> {
        return bookmarkRepository.toggleBookmark(userId, presetId)
    }
}
