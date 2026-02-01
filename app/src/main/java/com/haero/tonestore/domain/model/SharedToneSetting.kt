package com.haero.tonestore.domain.model

/**
 * 공유된 톤 세팅 모델
 * Firebase Firestore에 저장되는 커뮤니티 공유 프리셋
 *
 * @property id Firestore 문서 ID
 * @property authorId 작성자 UID
 * @property authorName 작성자 닉네임
 * @property title 프리셋 제목
 * @property description 프리셋 설명
 * @property toneSetting 톤 세팅 데이터
 * @property likes 좋아요 수
 * @property downloads 다운로드 수
 * @property createdAt 생성 시간 (Unix timestamp)
 * @property updatedAt 수정 시간 (Unix timestamp)
 * @property tags 장르 태그
 */
data class SharedToneSetting(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String? = null,
    val title: String = "",
    val description: String = "",
    val toneSetting: ToneSetting,
    val likes: Int = 0,
    val downloads: Int = 0,
    val commentCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val tags: List<GenreTag> = emptyList()
)
