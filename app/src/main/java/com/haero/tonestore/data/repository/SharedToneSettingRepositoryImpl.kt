package com.haero.tonestore.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.haero.tonestore.data.remote.mapper.toFirestoreMap
import com.haero.tonestore.data.remote.mapper.toSharedToneSetting
import com.haero.tonestore.domain.model.SharedToneSetting
import com.haero.tonestore.domain.repository.SharedToneSettingRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firebase Firestore 기반 SharedToneSetting Repository 구현체
 */
class SharedToneSettingRepositoryImpl(
    private val firestore: FirebaseFirestore
) : SharedToneSettingRepository {

    private val presetsCollection = firestore.collection("presets")
    private val likesCollection = firestore.collection("likes")

    override fun getSharedToneSettings(): Flow<List<SharedToneSetting>> = callbackFlow {
        val listener = presetsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val presets = snapshot?.documents?.mapNotNull { doc ->
                    doc.toSharedToneSetting()
                } ?: emptyList()
                trySend(presets)
            }
        awaitClose { listener.remove() }
    }

    override fun getSharedToneSettingsByUser(userId: String): Flow<List<SharedToneSetting>> = callbackFlow {
        val listener = presetsCollection
            .whereEqualTo("authorId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val presets = snapshot?.documents?.mapNotNull { doc ->
                    doc.toSharedToneSetting()
                } ?: emptyList()
                trySend(presets)
            }
        awaitClose { listener.remove() }
    }

    override fun getPopularToneSettings(limit: Int): Flow<List<SharedToneSetting>> = callbackFlow {
        val listener = presetsCollection
            .orderBy("likes", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val presets = snapshot?.documents?.mapNotNull { doc ->
                    doc.toSharedToneSetting()
                } ?: emptyList()
                trySend(presets)
            }
        awaitClose { listener.remove() }
    }

    override fun searchByTags(tags: List<String>): Flow<List<SharedToneSetting>> = callbackFlow {
        val listener = presetsCollection
            .whereArrayContainsAny("tags", tags)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val presets = snapshot?.documents?.mapNotNull { doc ->
                    doc.toSharedToneSetting()
                } ?: emptyList()
                trySend(presets)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getSharedToneSetting(id: String): SharedToneSetting? {
        return try {
            val doc = presetsCollection.document(id).get().await()
            doc.toSharedToneSetting()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun uploadToneSetting(sharedToneSetting: SharedToneSetting): Result<String> {
        return try {
            val docRef = presetsCollection.document()
            val dataWithId = sharedToneSetting.copy(id = docRef.id)
            docRef.set(dataWithId.toFirestoreMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateToneSetting(sharedToneSetting: SharedToneSetting): Result<Unit> {
        return try {
            presetsCollection
                .document(sharedToneSetting.id)
                .set(sharedToneSetting.toFirestoreMap())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteToneSetting(id: String): Result<Unit> {
        return try {
            presetsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleLike(presetId: String, userId: String): Result<Boolean> {
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
                Result.success(false)
            } else {
                // 좋아요 추가
                likesCollection.document(likeDocId).set(
                    mapOf(
                        "userId" to userId,
                        "presetId" to presetId,
                        "createdAt" to System.currentTimeMillis()
                    )
                ).await()
                firestore.runTransaction { transaction ->
                    val presetRef = presetsCollection.document(presetId)
                    val preset = transaction.get(presetRef)
                    val currentLikes = preset.getLong("likes") ?: 0
                    transaction.update(presetRef, "likes", currentLikes + 1)
                }.await()
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun incrementDownloads(id: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val presetRef = presetsCollection.document(id)
                val preset = transaction.get(presetRef)
                val currentDownloads = preset.getLong("downloads") ?: 0
                transaction.update(presetRef, "downloads", currentDownloads + 1)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
