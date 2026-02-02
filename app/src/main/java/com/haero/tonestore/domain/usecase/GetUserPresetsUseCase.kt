package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.model.SharedToneSetting
import com.haero.tonestore.domain.repository.UserProfileRepository

class GetUserPresetsUseCase(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<List<SharedToneSetting>> {
        return repository.getUserPresets(userId)
    }
}
