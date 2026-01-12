package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.repository.ToneSettingRepository

/**
 * 톤 세팅을 삭제하는 UseCase
 */
class DeleteToneSettingUseCase(
    private val repository: ToneSettingRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteToneSetting(id)
    }
}
