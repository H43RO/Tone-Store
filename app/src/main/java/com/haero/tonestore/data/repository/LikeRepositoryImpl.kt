package com.haero.tonestore.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.haero.tonestore.domain.repository.LikeRepository
import kotlinx.coroutines.tasks.await

/**
 * Firestore 기반 좋아요 Repository 구현
 */
class LikeRepositoryImpl(
    private val firestore: FirebaseFirestore
) : LikeRepository {

    private val likesCollection = firestore.collection("likes")
    private val presetsCollection = firestore.collection("presets")

    override suspend fun toggleLike(userId: String, presetId: String): Result<Boolean> {
        return try {
            val likeDocId = "${userId}_$presetId"
            val likeDoc = likesCollection.document(likeDocId).get().await()

            if (likeDoc.exists()) {
                // 좋아요 취소
                likesCollection.document(likeDocId).delete().await()
                firestore.runTransaction { transaction ->
                    val presetRef = presetsCollection.document(presetId)
                    val preset = transaction.get(presetRef)
                    val currentLikes = preset.getLong("likes") ?: 0
                    transaction.update(presetRef, "likes", (currentLikes - 1).coerceAtLeast(0))
                }.await()
                Result.success(false) // unliked
            } else {
                // 좋아요 추가
                likesCollection.document(likeDocId).set(
                    mapOf(
                        "userId" to userId,
                        "presetId" to presetId,
                        "likedAt" to System.currentTimeMillis()
                    )
                ).await()
                firestore.runTransaction { transaction ->
                    val presetRef = presetsCollection.document(presetId)
                    val preset = transaction.get(presetRef)
                    val currentLikes = preset.getLong("likes") ?: 0
                    transaction.update(presetRef, "likes", currentLikes + 1)
                }.await()
                Result.success(true) // liked
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserLikes(userId: String): Result<Set<String>> {
        return try {
            val snapshot = likesCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val likedPresetIds = snapshot.documents.mapNotNull { doc ->
                doc.getString("presetId")
            }.toSet()

            Result.success(likedPresetIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPresetLikeCount(presetId: String): Result<Int> {
        return try {
            val doc = presetsCollection.document(presetId).get().await()
            val likes = doc.getLong("likes")?.toInt() ?: 0
            Result.success(likes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
