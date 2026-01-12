package com.haero.tonestore.presentation.ui.create

import com.haero.tonestore.domain.model.Pedal
import com.haero.tonestore.domain.model.PickupPosition

/**
 * Create/Edit 화면의 사용자 인텐트
 */
sealed interface CreateToneIntent {
    /** 기존 톤 세팅 로드 (편집 모드) */
    data class LoadToneSetting(val id: String) : CreateToneIntent
    
    /** 곡 이름 업데이트 */
    data class UpdateSongName(val name: String) : CreateToneIntent
    
    // 페달보드 관련
    /** 프리셋 페달 추가 */
    data class AddPresetPedal(val pedal: Pedal) : CreateToneIntent
    
    /** 커스텀 페달 추가 */
    data class AddCustomPedal(val name: String, val knobNames: List<String>) : CreateToneIntent
    
    /** 페달 삭제 */
    data class RemovePedal(val pedalId: String) : CreateToneIntent
    
    /** 페달 노브 값 업데이트 */
    data class UpdatePedalKnob(
        val pedalId: String, 
        val knobIndex: Int, 
        val value: Float
    ) : CreateToneIntent
    
    /** 페달 활성화/비활성화 토글 */
    data class TogglePedalEnabled(val pedalId: String) : CreateToneIntent
    
    // 앰프 관련
    /** 앰프 모델명 업데이트 */
    data class UpdateAmpModel(val model: String) : CreateToneIntent
    
    /** 앰프 노브 값 업데이트 */
    data class UpdateAmpKnob(val knobName: String, val value: Float) : CreateToneIntent
    
    // 기타 관련
    /** 기타 모델명 업데이트 */
    data class UpdateGuitarModel(val model: String) : CreateToneIntent
    
    /** 픽업 셀렉터 위치 변경 */
    data class UpdatePickupPosition(val position: PickupPosition) : CreateToneIntent
    
    /** 기타 톤 노브 업데이트 */
    data class UpdateGuitarTone(val value: Float) : CreateToneIntent
    
    /** 기타 볼륨 노브 업데이트 */
    data class UpdateGuitarVolume(val value: Float) : CreateToneIntent
    
    // 저장
    /** 톤 세팅 저장 */
    data object SaveToneSetting : CreateToneIntent
    
    /** 네비게이션 이벤트 소비 완료 */
    data object NavigationHandled : CreateToneIntent
}
