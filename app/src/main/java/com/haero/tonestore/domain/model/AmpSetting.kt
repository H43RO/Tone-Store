package com.haero.tonestore.domain.model

/**
 * 앰프 세팅을 나타내는 모델
 *
 * @property ampModel 앰프 모델명 (예: Fender Twin Reverb, Marshall JCM800)
 * @property gain 게인/드라이브 (0-10)
 * @property bass 저음역 EQ (0-10)
 * @property middle 중음역 EQ (0-10)
 * @property treble 고음역 EQ (0-10)
 * @property presence 프레젠스 (0-10), 고역대 명료함 조절
 * @property reverb 내장 리버브 양 (0-10)
 * @property masterVolume 마스터 볼륨 (0-10)
 */
data class AmpSetting(
    val ampModel: String? = null,
    val gain: Float = 5f,
    val bass: Float = 5f,
    val middle: Float = 5f,
    val treble: Float = 5f,
    val presence: Float = 5f,
    val reverb: Float = 0f,
    val masterVolume: Float = 5f
)
