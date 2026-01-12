package com.haero.tonestore.domain.repository

import com.haero.tonestore.domain.model.ToneSetting
import kotlinx.coroutines.flow.Flow

/**
 * 톤 세팅 데이터 접근을 위한 Repository 인터페이스
 */
interface ToneSettingRepository {
    /**
     * 모든 톤 세팅을 Flow로 반환 (최신 수정순 정렬)
     */
    fun getAllToneSettings(): Flow<List<ToneSetting>>

    /**
     * ID로 특정 톤 세팅 조회
     */
    suspend fun getToneSettingById(id: String): ToneSetting?

    /**
     * 톤 세팅 저장 (신규 생성 또는 업데이트)
     */
    suspend fun saveToneSetting(setting: ToneSetting)

    /**
     * ID로 톤 세팅 삭제
     */
    suspend fun deleteToneSetting(id: String)
    
    /**
     * 즐겨찾기 토글
     */
    suspend fun toggleFavorite(id: String)
}
