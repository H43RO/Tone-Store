package com.haero.tonestore.domain.model

/**
 * 사용자 프로필 모델
 *
 * @property uid Firebase Auth UID
 * @property displayName 표시 이름 (Google 계정 이름)
 * @property nickname 커뮤니티에서 사용할 닉네임
 * @property email 이메일
 * @property photoUrl 프로필 사진 URL (커스텀 또는 Google 프로필)
 * @property createdAt 가입 시간
 */
data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val nickname: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * 커뮤니티에서 표시할 이름 (닉네임 우선, 없으면 displayName)
     */
    val communityDisplayName: String
        get() = nickname.ifBlank { displayName.ifBlank { "익명" } }
}
