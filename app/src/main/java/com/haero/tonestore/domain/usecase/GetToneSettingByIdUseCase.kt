package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.domain.repository.ToneSettingRepository

/**
 * ID로 특정 톤 세팅을 조회하는 UseCase
 */
class GetToneSettingByIdUseCase(
    private val repository: ToneSettingRepository
) {
    suspend operator fun invoke(id: String): ToneSetting? {
        return repository.getToneSettingById(id)
    }
}
