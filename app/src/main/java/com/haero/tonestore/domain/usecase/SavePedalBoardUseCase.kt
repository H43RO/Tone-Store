package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.domain.repository.SavedPedalBoardRepository

/**
 * 페달보드 저장 UseCase
 */
class SavePedalBoardUseCase(
    private val repository: SavedPedalBoardRepository
) {
    suspend operator fun invoke(pedalBoard: SavedPedalBoard) {
        repository.savePedalBoard(pedalBoard)
    }
}
