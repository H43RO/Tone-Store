package com.haero.tonestore.presentation.ui.pedalboard

import com.haero.tonestore.domain.model.Pedal

/**
 * 페달보드 관리 화면의 사용자 인텐트
 */
sealed interface PedalBoardIntent {
    /** 기존 페달보드 로드 (편집 모드) */
    data class LoadPedalBoard(val id: String) : PedalBoardIntent

    /** 페달보드 이름 업데이트 */
    data class UpdateName(val name: String) : PedalBoardIntent

    /** 레이아웃 크기 변경 */
    data class UpdateLayout(val columns: Int, val rows: Int) : PedalBoardIntent

    /** 슬롯에 프리셋 페달 추가 */
    data class AddPedalToSlot(val slotIndex: Int, val pedal: Pedal) : PedalBoardIntent

    /** 슬롯에 커스텀 페달 추가 */
    data class AddCustomPedalToSlot(
        val slotIndex: Int,
        val name: String,
        val knobNames: List<String>
    ) : PedalBoardIntent

    /** 슬롯에서 페달 제거 */
    data class RemovePedalFromSlot(val slotIndex: Int) : PedalBoardIntent

    /** 두 슬롯 위치 스왑 */
    data class SwapSlots(val fromIndex: Int, val toIndex: Int) : PedalBoardIntent

    /** 슬롯을 다른 위치로 이동 */
    data class MovePedalToSlot(val fromIndex: Int, val toIndex: Int) : PedalBoardIntent

    /** 페달 노브 값 업데이트 */
    data class UpdatePedalKnob(
        val slotIndex: Int,
        val knobIndex: Int,
        val value: Float
    ) : PedalBoardIntent

    /** 페달 활성화/비활성화 토글 */
    data class TogglePedalEnabled(val slotIndex: Int) : PedalBoardIntent

    /** 페달 편집 다이얼로그 열기 */
    data class OpenPedalEditor(val slotIndex: Int) : PedalBoardIntent

    /** 페달 편집 다이얼로그 닫기 */
    data object ClosePedalEditor : PedalBoardIntent

    /** 페달보드 저장 */
    data object SavePedalBoard : PedalBoardIntent

    /** 페달보드 삭제 */
    data object DeletePedalBoard : PedalBoardIntent

    /** 네비게이션 이벤트 소비 완료 */
    data object NavigationHandled : PedalBoardIntent
}
