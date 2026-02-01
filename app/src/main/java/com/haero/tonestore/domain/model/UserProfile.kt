package com.haero.tonestore.domain.model

/**
 * 사용자 프로필 모델
 *
 * @property uid Firebase Auth UID
 * @property displayName 표시 이름
 * @property email 이메일
 * @property photoUrl 프로필 사진 URL
 * @property createdAt 가입 시간
 */
data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
