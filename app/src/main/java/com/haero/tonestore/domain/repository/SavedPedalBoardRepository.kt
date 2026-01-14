package com.haero.tonestore.domain.repository

import com.haero.tonestore.domain.model.SavedPedalBoard
import kotlinx.coroutines.flow.Flow

/**
 * 저장된 페달보드 Repository 인터페이스
 */
interface SavedPedalBoardRepository {
    fun getAllPedalBoards(): Flow<List<SavedPedalBoard>>
    suspend fun getPedalBoardById(id: String): SavedPedalBoard?
    suspend fun savePedalBoard(pedalBoard: SavedPedalBoard)
    suspend fun deletePedalBoard(id: String)
}
