package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.domain.repository.SavedPedalBoardRepository
import kotlinx.coroutines.flow.Flow

/**
 * 모든 저장된 페달보드 조회 UseCase
 */
class GetAllSavedPedalBoardsUseCase(
    private val repository: SavedPedalBoardRepository
) {
    operator fun invoke(): Flow<List<SavedPedalBoard>> {
        return repository.getAllPedalBoards()
    }
}
