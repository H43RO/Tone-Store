package com.haero.tonestore.domain.repository

import com.haero.tonestore.domain.model.SharedToneSetting
import kotlinx.coroutines.flow.Flow

/**
 * 공유 톤 세팅 Repository 인터페이스
 */
interface SharedToneSettingRepository {

    /**
     * 모든 공유 프리셋 목록 조회 (최신순)
     */
    fun getSharedToneSettings(): Flow<List<SharedToneSetting>>

    /**
     * 특정 사용자의 공유 프리셋 목록 조회
     */
    fun getSharedToneSettingsByUser(userId: String): Flow<List<SharedToneSetting>>

    /**
     * 인기 프리셋 목록 조회 (좋아요 순)
     */
    fun getPopularToneSettings(limit: Int = 20): Flow<List<SharedToneSetting>>

    /**
     * 태그로 프리셋 검색
     */
    fun searchByTags(tags: List<String>): Flow<List<SharedToneSetting>>

    /**
     * 프리셋 상세 조회
     */
    suspend fun getSharedToneSetting(id: String): SharedToneSetting?

    /**
     * 프리셋 업로드
     */
    suspend fun uploadToneSetting(sharedToneSetting: SharedToneSetting): Result<String>

    /**
     * 프리셋 수정
     */
    suspend fun updateToneSetting(sharedToneSetting: SharedToneSetting): Result<Unit>

    /**
     * 프리셋 삭제
     */
    suspend fun deleteToneSetting(id: String): Result<Unit>

    /**
     * 좋아요 토글
     */
    suspend fun toggleLike(presetId: String, userId: String): Result<Boolean>

    /**
     * 다운로드 수 증가
     */
    suspend fun incrementDownloads(id: String): Result<Unit>

    /**
     * 사용자의 모든 프리셋과 댓글의 작성자명 업데이트
     */
    suspend fun updateAuthorName(userId: String, newName: String): Result<Unit>
}
