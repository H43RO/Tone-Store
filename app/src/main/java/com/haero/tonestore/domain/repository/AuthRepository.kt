package com.haero.tonestore.domain.repository

import com.google.firebase.auth.AuthCredential
import com.haero.tonestore.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * 인증 Repository 인터페이스
 */
interface AuthRepository {

    /**
     * 현재 로그인된 사용자 Flow
     */
    val currentUser: Flow<UserProfile?>

    /**
     * 현재 사용자 UID (동기)
     */
    val currentUserId: String?

    /**
     * 로그인 여부
     */
    val isLoggedIn: Boolean

    /**
     * Google 로그인
     */
    suspend fun signInWithGoogle(credential: AuthCredential): Result<UserProfile>

    /**
     * 익명 로그인
     */
    suspend fun signInAnonymously(): Result<UserProfile>

    /**
     * 로그아웃
     */
    suspend fun signOut()

    /**
     * 사용자 프로필 업데이트 (닉네임)
     */
    suspend fun updateProfile(nickname: String, photoUrl: String? = null): Result<UserProfile>

    /**
     * Firestore에서 프로필 가져오기
     */
    suspend fun getProfile(): Result<UserProfile?>

    /**
     * 계정 삭제
     */
    suspend fun deleteAccount(): Result<Unit>
}
