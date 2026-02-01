package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.model.SavedCustomPedal
import com.haero.tonestore.domain.repository.CustomPedalRepository

/**
 * 커스텀 페달을 저장하는 UseCase
 */
class SaveCustomPedalUseCase(
    private val repository: CustomPedalRepository
) {
    suspend operator fun invoke(pedal: SavedCustomPedal) {
        repository.saveCustomPedal(pedal)
    }
}
