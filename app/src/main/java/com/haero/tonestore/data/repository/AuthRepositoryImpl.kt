package com.haero.tonestore.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.haero.tonestore.domain.model.UserProfile
import com.haero.tonestore.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firebase Auth Repository 구현체
 */
class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser?.let { firebaseUser ->
                UserProfile(
                    uid = firebaseUser.uid,
                    displayName = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString()
                )
            }
            trySend(user)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override suspend fun signInWithGoogle(credential: AuthCredential): Result<UserProfile> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("로그인 실패")

            val userProfile = UserProfile(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                photoUrl = firebaseUser.photoUrl?.toString()
            )

            // Firestore에 사용자 정보 저장
            saveUserToFirestore(userProfile)

            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInAnonymously(): Result<UserProfile> {
        return try {
            val result = auth.signInAnonymously().await()
            val firebaseUser = result.user ?: throw Exception("익명 로그인 실패")

            val userProfile = UserProfile(
                uid = firebaseUser.uid,
                displayName = "익명 사용자",
                email = ""
            )

            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun updateProfile(displayName: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("로그인이 필요합니다")

            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                this.displayName = displayName
            }
            user.updateProfile(profileUpdates).await()

            // Firestore 업데이트
            firestore.collection("users")
                .document(user.uid)
                .update("displayName", displayName)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("로그인이 필요합니다")

            // Firestore에서 사용자 데이터 삭제
            firestore.collection("users").document(user.uid).delete().await()

            // Auth 계정 삭제
            user.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveUserToFirestore(userProfile: UserProfile) {
        firestore.collection("users")
            .document(userProfile.uid)
            .set(
                mapOf(
                    "uid" to userProfile.uid,
                    "displayName" to userProfile.displayName,
                    "email" to userProfile.email,
                    "photoUrl" to userProfile.photoUrl,
                    "createdAt" to userProfile.createdAt
                )
            )
            .await()
    }
}
