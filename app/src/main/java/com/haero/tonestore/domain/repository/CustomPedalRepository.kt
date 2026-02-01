package com.haero.tonestore.domain.repository

import com.haero.tonestore.domain.model.SavedCustomPedal
import kotlinx.coroutines.flow.Flow

/**
 * 사용자 커스텀 페달 Repository
 */
interface CustomPedalRepository {
    /**
     * 모든 커스텀 페달 조회
     */
    fun getAllCustomPedals(): Flow<List<SavedCustomPedal>>

    /**
     * 커스텀 페달 저장
     */
    suspend fun saveCustomPedal(pedal: SavedCustomPedal)

    /**
     * 커스텀 페달 삭제
     */
    suspend fun deleteCustomPedal(id: String)

    /**
     * ID로 커스텀 페달 조회
     */
    suspend fun getCustomPedalById(id: String): SavedCustomPedal?
}
