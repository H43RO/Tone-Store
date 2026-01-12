package com.haero.tonestore.domain.model

/**
 * 기타 픽업 셀렉터 위치
 * 3-way 및 5-way 스위치 모두 지원
 */
enum class PickupPosition {
    /** 넥 픽업 (따뜻하고 둥근 톤) */
    NECK,
    /** 미들 픽업 (밸런스 잡힌 톤) */
    MIDDLE,
    /** 브릿지 픽업 (밝고 날카로운 톤) */
    BRIDGE,
    /** 넥 + 미들 믹스 (5-way 2번 포지션) */
    NECK_MIDDLE,
    /** 미들 + 브릿지 믹스 (5-way 4번 포지션) */
    MIDDLE_BRIDGE
}
