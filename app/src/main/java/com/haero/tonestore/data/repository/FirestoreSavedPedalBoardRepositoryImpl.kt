package com.haero.tonestore.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.haero.tonestore.data.remote.mapper.toFirestoreMap
import com.haero.tonestore.data.remote.mapper.toSavedPedalBoard
import com.haero.tonestore.domain.model.SavedPedalBoard
import com.haero.tonestore.domain.repository.SavedPedalBoardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firestore 기반 SavedPedalBoard Repository 구현체
 *
 * 데이터 구조: /users/{userId}/pedalBoards/{boardId}
 */
class FirestoreSavedPedalBoardRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : SavedPedalBoardRepository {

    private fun getUserPedalBoardsCollection() =
        auth.currentUser?.uid?.let { uid ->
            firestore.collection("users").document(uid).collection("pedalBoards")
        }

    override fun getAllPedalBoards(): Flow<List<SavedPedalBoard>> = callbackFlow {
        val collection = getUserPedalBoardsCollection()

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

                val pedalBoards = snapshot?.documents?.mapNotNull { doc ->
                    doc.toSavedPedalBoard()
                } ?: emptyList()

                trySend(pedalBoards)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getPedalBoardById(id: String): SavedPedalBoard? {
        val collection = getUserPedalBoardsCollection() ?: return null

        return try {
            val doc = collection.document(id).get().await()
            doc.toSavedPedalBoard()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun savePedalBoard(pedalBoard: SavedPedalBoard) {
        val collection = getUserPedalBoardsCollection()
            ?: throw IllegalStateException("로그인이 필요합니다")

        val data = pedalBoard.toFirestoreMap()
        collection.document(pedalBoard.id).set(data).await()
    }

    override suspend fun deletePedalBoard(id: String) {
        val collection = getUserPedalBoardsCollection()
            ?: throw IllegalStateException("로그인이 필요합니다")

        collection.document(id).delete().await()
    }
}
