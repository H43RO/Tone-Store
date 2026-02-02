package com.haero.tonestore.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.haero.tonestore.domain.repository.BookmarkRepository
import kotlinx.coroutines.tasks.await

/**
 * Firestore 기반 북마크 Repository 구현
 */
class BookmarkRepositoryImpl(
    private val firestore: FirebaseFirestore
) : BookmarkRepository {

    private val bookmarksCollection = firestore.collection("bookmarks")

    override suspend fun toggleBookmark(userId: String, presetId: String): Result<Boolean> {
        return try {
            val bookmarkDocId = "${userId}_$presetId"
            val bookmarkDoc = bookmarksCollection.document(bookmarkDocId).get().await()

            if (bookmarkDoc.exists()) {
                // 북마크 취소
                bookmarksCollection.document(bookmarkDocId).delete().await()
                Result.success(false) // unbookmarked
            } else {
                // 북마크 추가
                bookmarksCollection.document(bookmarkDocId).set(
                    mapOf(
                        "userId" to userId,
                        "presetId" to presetId,
                        "bookmarkedAt" to System.currentTimeMillis()
                    )
                ).await()
                Result.success(true) // bookmarked
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserBookmarks(userId: String): Result<Set<String>> {
        return try {
            val snapshot = bookmarksCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val bookmarkedPresetIds = snapshot.documents.mapNotNull { doc ->
                doc.getString("presetId")
            }.toSet()

            Result.success(bookmarkedPresetIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
