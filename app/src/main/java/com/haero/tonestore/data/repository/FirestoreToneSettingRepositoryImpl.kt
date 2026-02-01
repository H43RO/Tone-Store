package com.haero.tonestore.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.haero.tonestore.data.remote.mapper.toFirestoreMap
import com.haero.tonestore.data.remote.mapper.toToneSetting
import com.haero.tonestore.domain.model.ToneSetting
import com.haero.tonestore.domain.repository.ToneSettingRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firestore 기반 ToneSetting Repository 구현체
 *
 * 데이터 구조: /users/{userId}/toneSettings/{toneId}
 */
class FirestoreToneSettingRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ToneSettingRepository {

    private fun getUserToneSettingsCollection() =
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).collection("toneSettings")
        }

    override fun getAllToneSettings(): Flow<List<ToneSetting>> = callbackFlow {
        val collection = getUserToneSettingsCollection()

        if (collection == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        val listener = collection
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val toneSettings = snapshot?.documents?.mapNotNull { doc ->
                    doc.toToneSetting()
                } ?: emptyList()

                trySend(toneSettings)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getToneSettingById(id: String): ToneSetting? {
        val collection = getUserToneSettingsCollection() ?: return null

        return try {
            val doc = collection.document(id).get().await()
            doc.toToneSetting()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveToneSetting(setting: ToneSetting) {
        val collection = getUserToneSettingsCollection()
            ?: throw IllegalStateException("로그인이 필요합니다")

        val data = setting.toFirestoreMap()
        collection.document(setting.id).set(data).await()
    }

    override suspend fun deleteToneSetting(id: String) {
        val collection = getUserToneSettingsCollection()
            ?: throw IllegalStateException("로그인이 필요합니다")

        collection.document(id).delete().await()
    }

    override suspend fun toggleFavorite(id: String) {
        val collection = getUserToneSettingsCollection()
            ?: throw IllegalStateException("로그인이 필요합니다")

        val doc = collection.document(id).get().await()
        val currentFavorite = doc.getBoolean("isFavorite") ?: false
        collection.document(id).update("isFavorite", !currentFavorite).await()
    }
}
