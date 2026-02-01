package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.model.SavedCustomPedal
import com.haero.tonestore.domain.repository.CustomPedalRepository
import kotlinx.coroutines.flow.Flow

/**
 * 모든 커스텀 페달을 조회하는 UseCase
 */
class GetAllCustomPedalsUseCase(
    private val repository: CustomPedalRepository
) {
    operator fun invoke(): Flow<List<SavedCustomPedal>> {
        return repository.getAllCustomPedals()
    }
}
