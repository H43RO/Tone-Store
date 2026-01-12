package com.haero.tonestore.domain.model

/**
 * 이펙터/앰프/기타의 노브를 나타내는 모델
 *
 * @property name 노브의 이름 (예: Gain, Tone, Level)
 * @property value 현재 노브 값 (0-10 범위)
 * @property minValue 최소값 (기본 0)
 * @property maxValue 최대값 (기본 10)
 */
data class Knob(
    val name: String,
    val value: Float,
    val minValue: Float = 0f,
    val maxValue: Float = 10f
)
