package com.haero.tonestore.domain.model

/**
 * 저장된 페달보드 템플릿
 * 사용자가 미리 구성해둔 페달보드를 저장하고 톤 생성시 불러올 수 있음
 *
 * @property id 고유 식별자
 * @property name 페달보드 이름 (예: "메인 보드", "미니멀 보드")
 * @property columns 열 개수 (가로 페달 수)
 * @property rows 행 개수 (세로 줄 수)
 * @property slots 슬롯 배열 (columns x rows 크기) - null이면 빈 슬롯
 * @property createdAt 생성 시간
 * @property updatedAt 수정 시간
 */
data class SavedPedalBoard(
    val id: String,
    val name: String,
    val columns: Int = DEFAULT_COLUMNS,
    val rows: Int = DEFAULT_ROWS,
    val slots: List<Pedal?> = List(DEFAULT_COLUMNS * DEFAULT_ROWS) { null },
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * 실제 페달 목록 (빈 슬롯 제외, 순서 유지)
     */
    fun toPedalBoard(): PedalBoard {
        return PedalBoard(
            pedals = slots.filterNotNull().mapIndexed { index, pedal ->
                pedal.copy(order = index)
            }
        )
    }

    /**
     * 페달 개수
     */
    val pedalCount: Int get() = slots.count { it != null }

    companion object {
        const val DEFAULT_COLUMNS = 5
        const val DEFAULT_ROWS = 2
        const val MIN_COLUMNS = 2
        const val MAX_COLUMNS = 8
        const val MIN_ROWS = 1
        const val MAX_ROWS = 4
    }
}
