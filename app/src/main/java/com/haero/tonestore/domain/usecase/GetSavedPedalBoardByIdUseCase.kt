package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.domain.repository.SavedPedalBoardRepository

/**
 * ID로 저장된 페달보드 조회 UseCase
 */
class GetSavedPedalBoardByIdUseCase(
    private val repository: SavedPedalBoardRepository
) {
    suspend operator fun invoke(id: String): SavedPedalBoard? {
        return repository.getPedalBoardById(id)
    }
}
