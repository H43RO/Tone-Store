package com.haero.tonestore.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Firestore에서 프로필 정보 가져오기 (닉네임 포함)
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = if (doc.exists()) {
                            UserProfile(
                                uid = firebaseUser.uid,
                                displayName = firebaseUser.displayName ?: "",
                                nickname = doc.getString("nickname") ?: "",
                                email = firebaseUser.email ?: "",
                                photoUrl = doc.getString("photoUrl") ?: firebaseUser.photoUrl?.toString(),
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                        } else {
                            UserProfile(
                                uid = firebaseUser.uid,
                                displayName = firebaseUser.displayName ?: "",
                                nickname = "",
                                email = firebaseUser.email ?: "",
                                photoUrl = firebaseUser.photoUrl?.toString()
                            )
                        }
                        trySend(user)
                    }
                    .addOnFailureListener {
                        trySend(
                            UserProfile(
                                uid = firebaseUser.uid,
                                displayName = firebaseUser.displayName ?: "",
                                email = firebaseUser.email ?: "",
                                photoUrl = firebaseUser.photoUrl?.toString()
                            )
                        )
                    }
            } else {
                trySend(null)
            }
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

            // Firestore에서 기존 프로필 확인
            val existingProfile = getProfile().getOrNull()

            val userProfile = UserProfile(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName ?: "",
                nickname = existingProfile?.nickname ?: "",
                email = firebaseUser.email ?: "",
                photoUrl = existingProfile?.photoUrl ?: firebaseUser.photoUrl?.toString(),
                createdAt = existingProfile?.createdAt ?: System.currentTimeMillis()
            )

            // Firestore에 사용자 정보 저장 (기존 정보 유지)
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
                nickname = "",
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

    override suspend fun updateProfile(nickname: String, photoUrl: String?): Result<UserProfile> {
        return try {
            val user = auth.currentUser ?: throw Exception("로그인이 필요합니다")

            val updates = mutableMapOf<String, Any?>(
                "nickname" to nickname
            )
            if (photoUrl != null) {
                updates["photoUrl"] = photoUrl
            }

            firestore.collection("users")
                .document(user.uid)
                .set(updates, SetOptions.merge())
                .await()

            val updatedProfile = getProfile().getOrNull() ?: throw Exception("프로필 업데이트 실패")
            Result.success(updatedProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfile(): Result<UserProfile?> {
        return try {
            val uid = currentUserId ?: return Result.success(null)
            val firebaseUser = auth.currentUser

            val doc = firestore.collection("users")
                .document(uid)
                .get()
                .await()

            if (doc.exists()) {
                val profile = UserProfile(
                    uid = uid,
                    displayName = firebaseUser?.displayName ?: doc.getString("displayName") ?: "",
                    nickname = doc.getString("nickname") ?: "",
                    email = firebaseUser?.email ?: doc.getString("email") ?: "",
                    photoUrl = doc.getString("photoUrl") ?: firebaseUser?.photoUrl?.toString(),
                    createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                )
                Result.success(profile)
            } else {
                Result.success(null)
            }
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
        val data = mutableMapOf(
            "uid" to userProfile.uid,
            "displayName" to userProfile.displayName,
            "email" to userProfile.email,
            "createdAt" to userProfile.createdAt
        )

        // nickname과 photoUrl은 기존 값이 있으면 유지
        if (userProfile.nickname.isNotBlank()) {
            data["nickname"] = userProfile.nickname
        }
        if (userProfile.photoUrl != null) {
            data["photoUrl"] = userProfile.photoUrl
        }

        firestore.collection("users")
            .document(userProfile.uid)
            .set(data, SetOptions.merge())
            .await()
    }
}
