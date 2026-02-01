package com.haero.tonestore.domain.model

/**
 * 댓글 모델
 *
 * @property id Firestore 문서 ID
 * @property presetId 댓글이 달린 프리셋 ID
 * @property authorId 작성자 UID
 * @property authorName 작성자 닉네임
 * @property authorPhotoUrl 작성자 프로필 사진
 * @property content 댓글 내용
 * @property createdAt 작성 시간
 * @property updatedAt 수정 시간
 */
data class Comment(
    val id: String = "",
    val presetId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String? = null,
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
