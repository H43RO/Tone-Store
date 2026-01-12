package com.haero.tonestore.domain.model

/**
 * 톤 세팅의 전체 정보를 담는 모델
 * 하나의 곡에 대한 완전한 톤 세팅 (이펙터 + 앰프 + 기타)
 *
 * @property id 고유 식별자 (UUID)
 * @property songName 곡 이름
 * @property createdAt 생성 시간 (Unix timestamp)
 * @property updatedAt 마지막 수정 시간 (Unix timestamp)
 * @property pedalBoard 이펙터 페달보드 세팅
 * @property ampSetting 앰프 세팅
 * @property guitarSetting 기타 본체 세팅
 */
data class ToneSetting(
    val id: String,
    val songName: String,
    val createdAt: Long,
    val updatedAt: Long,
    val pedalBoard: PedalBoard,
    val ampSetting: AmpSetting,
    val guitarSetting: GuitarSetting
)
