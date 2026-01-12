package com.haero.tonestore.domain.model

/**
 * 이펙터 페달보드를 나타내는 모델
 * 실제 페달보드처럼 여러 이펙터 페달을 순서대로 배치
 *
 * @property pedals 페달보드에 배치된 페달 목록 (신호 체인 순서)
 */
data class PedalBoard(
    val pedals: List<Pedal> = emptyList()
)
