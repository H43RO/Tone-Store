package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.domain.repository.ToneSettingRepository

/**
 * 톤 세팅을 저장하는 UseCase
 */
class SaveToneSettingUseCase(
    private val repository: ToneSettingRepository
) {
    suspend operator fun invoke(setting: ToneSetting) {
        repository.saveToneSetting(setting)
    }
}
