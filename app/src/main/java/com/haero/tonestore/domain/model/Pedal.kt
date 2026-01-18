package com.haero.tonestore.domain.model

/**
 * 이펙터 페달을 나타내는 모델
 *
 * @property id 페달 고유 식별자
 * @property name 페달 이름 (예: Overdrive, Delay)
 * @property type 프리셋 또는 커스텀 여부
 * @property knobs 페달의 노브 목록
 * @property order 페달보드 내 순서 (신호 체인 순서)
 * @property isEnabled 페달 활성화 여부
 * @property color 페달 색상 (ARGB Long 값, null이면 기본 색상 사용)
 */
data class Pedal(
    val id: String,
    val name: String,
    val type: PedalType,
    val knobs: List<Knob>,
    val order: Int,
    val isEnabled: Boolean = true,
    val color: Long? = null
)
