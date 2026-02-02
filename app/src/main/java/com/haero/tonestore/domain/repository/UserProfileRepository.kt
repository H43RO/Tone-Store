package com.haero.tonestore.domain.repository

import com.haero.tonestore.domain.model.SharedToneSetting
import com.haero.tonestore.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun getUserProfile(userId: String): Result<UserProfile>
    suspend fun getUserPresets(userId: String): Result<List<SharedToneSetting>>
}
