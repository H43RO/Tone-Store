package com.haero.tonestore.domain.model

/**
 * 사용자가 저장한 커스텀 페달
 *
 * @property id 커스텀 페달 고유 식별자
 * @property name 페달 이름
 * @property knobNames 노브 이름 목록
 * @property color 페달 색상 (ARGB Long 값, null이면 기본 색상)
 * @property createdAt 생성 시간 (timestamp)
 * @property updatedAt 수정 시간 (timestamp)
 */
data class SavedCustomPedal(
    val id: String,
    val name: String,
    val knobNames: List<String>,
    val color: Long? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    /**
     * Pedal 모델로 변환
     */
    fun toPedal(order: Int = 0): Pedal {
        return Pedal(
            id = id,
            name = name,
            type = PedalType.CUSTOM,
            knobs = knobNames.map { Knob(name = it, value = 5f) },
            order = order,
            isEnabled = true,
            color = color
        )
    }
}
