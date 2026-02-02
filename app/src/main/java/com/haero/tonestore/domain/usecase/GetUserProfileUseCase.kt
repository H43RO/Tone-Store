package com.haero.tonestore.domain.usecase

import com.haero.tonestore.domain.model.UserProfile
import com.haero.tonestore.domain.repository.UserProfileRepository

class GetUserProfileUseCase(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<UserProfile> {
        return repository.getUserProfile(userId)
    }
}
