package com.haero.tonestore.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.haero.tonestore.data.remote.mapper.toSharedToneSetting
import com.haero.tonestore.domain.model.SharedToneSetting
import com.haero.tonestore.domain.model.UserProfile
import com.haero.tonestore.domain.repository.UserProfileRepository
import kotlinx.coroutines.tasks.await

class UserProfileRepositoryImpl(
    private val firestore: FirebaseFirestore
) : UserProfileRepository {

    private val usersCollection = firestore.collection("users")
    private val presetsCollection = firestore.collection("presets")

    override suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            // 1. 사용자 기본 정보 가져오기
            val userDoc = usersCollection.document(userId).get().await()
            val basicProfile = if (userDoc.exists()) {
                UserProfile(
                    uid = userId,
                    displayName = userDoc.getString("displayName") ?: "",
                    nickname = userDoc.getString("nickname") ?: "",
                    photoUrl = userDoc.getString("photoUrl"),
                    createdAt = userDoc.getLong("createdAt") ?: System.currentTimeMillis()
                )
            } else {
                UserProfile(uid = userId, displayName = "Unknown User")
            }

            // 2. 사용자가 작성한 프리셋 통계 계산
            val presetsSnapshot = presetsCollection
                .whereEqualTo("authorId", userId)
                .get()
                .await()

            val presetsCount = presetsSnapshot.size()
            val totalLikes = presetsSnapshot.documents.sumOf {
                it.getLong("likes")?.toInt() ?: 0
            }

            // 3. 통계가 포함된 프로필 반환
            Result.success(
                basicProfile.copy(
                    totalLikes = totalLikes,
                    sharedPresetsCount = presetsCount
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserPresets(userId: String): Result<List<SharedToneSetting>> {
        return try {
            val snapshot = presetsCollection
                .whereEqualTo("authorId", userId)
                .get()
                .await()

            val presets = snapshot.documents.mapNotNull { it.toSharedToneSetting() }
            // 최신순 정렬 (클라이언트 사이드에서 처리하거나 쿼리에 인덱스 추가 필요)
            // 여기서는 간단히 메모리 정렬
            Result.success(presets.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
