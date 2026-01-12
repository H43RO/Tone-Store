package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.domain.repository.ToneSettingRepository
import kotlinx.coroutines.flow.Flow

/**
 * 모든 톤 세팅을 조회하는 UseCase
 */
class GetAllToneSettingsUseCase(
    private val repository: ToneSettingRepository
) {
    operator fun invoke(): Flow<List<ToneSetting>> {
        return repository.getAllToneSettings()
    }
}
