package com.haero.tonestore.domain.model

/**
 * 기타 본체 세팅을 나타내는 모델
 *
 * @property guitarModel 기타 모델명 (예: Fender Stratocaster, Gibson Les Paul)
 * @property pickupSelector 픽업 셀렉터 위치
 * @property toneKnob 톤 노브 값 (0-10), 높을수록 밝은 톤
 * @property volumeKnob 볼륨 노브 값 (0-10)
 */
data class GuitarSetting(
    val guitarModel: String? = null,
    val pickupSelector: PickupPosition = PickupPosition.BRIDGE,
    val toneKnob: Float = 10f,
    val volumeKnob: Float = 10f
)
