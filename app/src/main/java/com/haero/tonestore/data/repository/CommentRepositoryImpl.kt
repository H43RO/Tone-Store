package com.haero.tonestore.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.haero.tonestore.domain.model.Comment
import com.haero.tonestore.domain.repository.CommentRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firebase Firestore 기반 Comment Repository 구현체
 */
class CommentRepositoryImpl(
    private val firestore: FirebaseFirestore
) : CommentRepository {

    private fun commentsCollection(presetId: String) =
        firestore.collection("presets").document(presetId).collection("comments")

    override fun getComments(presetId: String): Flow<List<Comment>> = callbackFlow {
        val listener = commentsCollection(presetId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val comments = snapshot?.documents?.mapNotNull { doc ->
                    doc.toComment()
                } ?: emptyList()
                trySend(comments)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addComment(comment: Comment): Result<String> {
        return try {
            val presetId = comment.presetId
            val docRef = commentsCollection(presetId).document()
            val commentWithId = comment.copy(id = docRef.id)
            docRef.set(commentWithId.toFirestoreMap()).await()

            // 댓글 수 증가
            updateCommentCount(presetId, 1)

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateComment(comment: Comment): Result<Unit> {
        return try {
            commentsCollection(comment.presetId)
                .document(comment.id)
                .update(
                    mapOf(
                        "content" to comment.content,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteComment(commentId: String, presetId: String): Result<Unit> {
        return try {
            commentsCollection(presetId).document(commentId).delete().await()

            // 댓글 수 감소
            updateCommentCount(presetId, -1)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCommentCount(presetId: String): Int {
        return try {
            val snapshot = commentsCollection(presetId).get().await()
            snapshot.size()
        } catch (e: Exception) {
            0
        }
    }

    private suspend fun updateCommentCount(presetId: String, delta: Int) {
        try {
            firestore.runTransaction { transaction ->
                val presetRef = firestore.collection("presets").document(presetId)
                val preset = transaction.get(presetRef)
                val currentCount = preset.getLong("commentCount") ?: 0
                transaction.update(presetRef, "commentCount", (currentCount + delta).coerceAtLeast(0))
            }.await()
        } catch (e: Exception) {
            // 실패해도 댓글 작성/삭제는 성공으로 처리
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toComment(): Comment? {
        return try {
            Comment(
                id = this.id,
                presetId = getString("presetId") ?: "",
                authorId = getString("authorId") ?: "",
                authorName = getString("authorName") ?: "",
                authorPhotoUrl = getString("authorPhotoUrl"),
                content = getString("content") ?: "",
                createdAt = getLong("createdAt") ?: 0L,
                updatedAt = getLong("updatedAt") ?: 0L
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Comment.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "presetId" to presetId,
            "authorId" to authorId,
            "authorName" to authorName,
            "authorPhotoUrl" to authorPhotoUrl,
            "content" to content,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}
