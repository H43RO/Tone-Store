package com.haero.tonestore.data.repository

import com.haero.tonestore.data.local.dao.ToneSettingDao
import com.haero.tonestore.data.local.mapper.ToneSettingMapper.toDomain
import com.haero.tonestore.data.local.mapper.ToneSettingMapper.toEntity
import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.domain.repository.ToneSettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * ToneSettingRepository의 구현체
 */
class ToneSettingRepositoryImpl(
    private val dao: ToneSettingDao
) : ToneSettingRepository {
    
    override fun getAllToneSettings(): Flow<List<ToneSetting>> {
        return dao.getAllToneSettings().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getToneSettingById(id: String): ToneSetting? {
        return dao.getToneSettingById(id)?.toDomain()
    }
    
    override suspend fun saveToneSetting(setting: ToneSetting) {
        dao.insertToneSetting(setting.toEntity())
    }
    
    override suspend fun deleteToneSetting(id: String) {
        dao.deleteToneSettingById(id)
    }
}
