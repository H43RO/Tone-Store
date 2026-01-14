package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.repository.SavedPedalBoardRepository

/**
 * 페달보드 삭제 UseCase
 */
class DeleteSavedPedalBoardUseCase(
    private val repository: SavedPedalBoardRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deletePedalBoard(id)
    }
}
