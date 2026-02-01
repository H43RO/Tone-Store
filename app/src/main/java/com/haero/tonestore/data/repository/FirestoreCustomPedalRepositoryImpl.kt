package com.haero.tonestore.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.haero.tonestore.domain.model.SavedCustomPedal
import com.haero.tonestore.domain.repository.CustomPedalRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firestore 기반 CustomPedal Repository 구현체
 *
 * 데이터 구조: /users/{userId}/customPedals/{pedalId}
 */
class FirestoreCustomPedalRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CustomPedalRepository {

    private fun getUserCustomPedalsCollection() =
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).collection("customPedals")
        }

    override fun getAllCustomPedals(): Flow<List<SavedCustomPedal>> = callbackFlow {
        val collection = getUserCustomPedalsCollection()

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

                val pedals = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        SavedCustomPedal(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            knobNames = (doc.get("knobNames") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                            color = doc.getLong("color"),
                            createdAt = doc.getLong("createdAt") ?: 0L,
                            updatedAt = doc.getLong("updatedAt") ?: 0L
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(pedals)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getCustomPedalById(id: String): SavedCustomPedal? {
        val collection = getUserCustomPedalsCollection() ?: return null

        return try {
            val doc = collection.document(id).get().await()
            SavedCustomPedal(
                id = doc.id,
                name = doc.getString("name") ?: "",
                knobNames = (doc.get("knobNames") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                color = doc.getLong("color"),
                createdAt = doc.getLong("createdAt") ?: 0L,
                updatedAt = doc.getLong("updatedAt") ?: 0L
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveCustomPedal(pedal: SavedCustomPedal) {
        val collection = getUserCustomPedalsCollection()
            ?: throw IllegalStateException("로그인이 필요합니다")

        val data = hashMapOf(
            "name" to pedal.name,
            "knobNames" to pedal.knobNames,
            "color" to pedal.color,
            "createdAt" to pedal.createdAt,
            "updatedAt" to pedal.updatedAt
        )

        collection.document(pedal.id).set(data).await()
    }

    override suspend fun deleteCustomPedal(id: String) {
        val collection = getUserCustomPedalsCollection()
            ?: throw IllegalStateException("로그인이 필요합니다")

        collection.document(id).delete().await()
    }
}
