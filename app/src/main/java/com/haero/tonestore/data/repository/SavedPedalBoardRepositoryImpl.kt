package com.haero.tonestore.data.repository

import com.haero.tonestore.data.local.dao.SavedPedalBoardDao
import com.haero.tonestore.data.local.mapper.SavedPedalBoardMapper
import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.domain.repository.SavedPedalBoardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 저장된 페달보드 Repository 구현체
 */
class SavedPedalBoardRepositoryImpl(
    private val dao: SavedPedalBoardDao
) : SavedPedalBoardRepository {

    override fun getAllPedalBoards(): Flow<List<SavedPedalBoard>> {
        return dao.getAllPedalBoards().map { entities ->
            entities.map { SavedPedalBoardMapper.toDomain(it) }
        }
    }

    override suspend fun getPedalBoardById(id: String): SavedPedalBoard? {
        return dao.getPedalBoardById(id)?.let { SavedPedalBoardMapper.toDomain(it) }
    }

    override suspend fun savePedalBoard(pedalBoard: SavedPedalBoard) {
        dao.insertPedalBoard(SavedPedalBoardMapper.toEntity(pedalBoard))
    }

    override suspend fun deletePedalBoard(id: String) {
        dao.deletePedalBoardById(id)
    }
}
